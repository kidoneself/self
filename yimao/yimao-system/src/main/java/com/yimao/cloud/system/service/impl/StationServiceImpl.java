package com.yimao.cloud.system.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.baideApi.utils.BaideApiUtil;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.PermissionTypeEnum;
import com.yimao.cloud.base.enums.StationAreaServiceTypeEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.BeanHelper;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.GetLatAndLngUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import com.yimao.cloud.pojo.dto.system.StationExportDTO;
import com.yimao.cloud.pojo.dto.system.StationServiceAreaDTO;
import com.yimao.cloud.pojo.dto.system.UpdateEngineerServiceAreaDataInfo;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.query.system.StationQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.feign.HraFeign;
import com.yimao.cloud.system.feign.StationFeign;
import com.yimao.cloud.system.feign.UserFeign;
import com.yimao.cloud.system.mapper.StationCompanyMapper;
import com.yimao.cloud.system.mapper.StationCompanyStationMapper;
import com.yimao.cloud.system.mapper.StationMapper;
import com.yimao.cloud.system.mapper.StationServiceAreaMapper;
import com.yimao.cloud.system.po.Station;
import com.yimao.cloud.system.po.StationCompanyStation;
import com.yimao.cloud.system.po.StationServiceArea;
import com.yimao.cloud.system.service.AreaService;
import com.yimao.cloud.system.service.StationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Lizhqiang
 * @date 2019/1/22
 */
@Slf4j
@Service
public class StationServiceImpl implements StationService {

    @Resource
    private UserCache userCache;
    @Resource
    private StationMapper stationMapper;
    @Resource
    private StationServiceAreaMapper stationServiceAreaMapper;
    @Resource
    private StationCompanyStationMapper stationCompanyStationMapper;
    @Resource
    private AreaService areaService;
    @Resource
    private UserFeign userFeign;
    @Resource
    private HraFeign hraFeign;
    @Resource
    private StationCompanyMapper companyMapper;
    @Resource
    private RedisCache redisCache;
    @Resource
    private StationFeign stationFeign;
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 新增服务站门店
     *
     * @param station           服务站门店
     * @param areaIds
     * @param serviceAreaLists  服务站门店服务区域
     * @param stationCompanyIds 区县级公司ID
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void saveStation(Station station, List<Integer> areaIds, List<StationServiceAreaDTO> serviceAreaLists, List<Integer> stationCompanyIds) {
        //校验区域是否有选择重复的
        this.hasSame(areaIds);
        //基本信息校验
        this.checkStation(station);
        //校验服务站名字是否已经存在
        Station query = new Station();
        query.setName(station.getName());
        Station record = stationMapper.selectOne(query);
        if (record != null) {
            throw new YimaoException("服务站已经存在");
        }
        //校验服务区域的服务类型有没有被其他服务站选择
        if (CollectionUtil.isNotEmpty(serviceAreaLists)) {
            this.checkStationServiceArea(serviceAreaLists, null);
        }
        //1-保存服务站门店基本信息
        String creator = userCache.getCurrentAdminRealName();
        Date now = new Date();
        station.setCreator(creator);
        station.setCreateTime(now);
        station.setId(null);
        Object[] o;
        try {
            o = GetLatAndLngUtil.getCoordinate(station.getAddress());
        } catch (IOException e) {
            throw new YimaoException("获取地址经纬度失败");
        }
        station.setLatitude(Double.parseDouble(o[1].toString()));
        station.setLongitude(Double.parseDouble(o[0].toString()));
        //未设置的默认设置为未XX
        station.setOnline(0);
        station.setContract(false);
        station.setRecommend(false);
        station.setContract(false);
        stationMapper.insert(station);

        Station update = new Station();
        update.setId(station.getId());
        update.setOldId(station.getId() + "");//设置oldId
        stationMapper.updateByPrimaryKeySelective(update);
        //将经销商id所对应的是否站长字段变更为true
        if (Objects.nonNull(station.getMasterDistributorId())) {
            DistributorDTO updateDistributor = new DistributorDTO();
            updateDistributor.setId(station.getMasterDistributorId());
            updateDistributor.setStationMaster(true);
            userFeign.updateDistributorCommon(updateDistributor);
        }


        //插入公司后才能获取id通过id来设置服务站公司编号
        SimpleDateFormat df = new SimpleDateFormat("yyMM");
        String format = df.format(new Date());
        String str = leftPad(station.getId(), 4, '0');
        //公司编号
        str = format + str;
        Station updateBody = new Station();
        updateBody.setId(station.getId());
        updateBody.setCode(str);
        stationMapper.updateByPrimaryKeySelective(updateBody);
        if (CollectionUtil.isNotEmpty(serviceAreaLists)) {
            //2-保存服务站门店服务区域信息
            this.saveStationServiceArea(serviceAreaLists, station, creator, now);
        }
        //3-保存公司和门店的关联关系
        this.saveStationCompanyStation(stationCompanyIds, station.getId());

//        try {
//            //同步售后
//            BaideApiUtil.syncServiceSiteAddOrUpdate(BaideApiUtil.INSERT, station.getId().toString(), station.getName(), station.getMasterName(), station.getMasterPhone(), station.getMasterIdCard(), station.getProvince(), station.getCity(), station.getRegion(), station.getAddress());
//        } catch (Exception e) {
//            log.error("==========新增服务站同步售后失败====" + e.getMessage());
//            throw new YimaoException("新增服务站失败[同步售后失败]");
//        }
    }

    private Boolean existStationOldId(String oldId) {
        Station query = new Station();
        query.setOldId(oldId);
        int count = stationMapper.selectCount(query);
        return count > 0;
    }

    /**
     * 修改服务站门店
     *
     * @param station         服务站门店
     * @param serviceAreaList 服务站门店服务区域dto
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void updateStation(Station station, List<Integer> areaIds, List<StationServiceAreaDTO> serviceAreaList) {
        //必填字段校验
        this.checkStation(station);
        //校验服务区域有没有被其它区县级公司选择
        if (CollectionUtil.isNotEmpty(serviceAreaList)) {
            this.checkStationServiceArea(serviceAreaList, station.getId());
        }
        //查询原先的服务站
        Station origStation = stationMapper.selectByPrimaryKey(station.getId());
        if (Objects.isNull(origStation)) {
            throw new YimaoException("服务站不存在");
        }

        DistributorDTO updateDistributor = new DistributorDTO();
        //判断站长是否变更
        if (origStation.getMasterDistributorId() != null && !origStation.getMasterDistributorId().equals(station.getMasterDistributorId())) {
            //查询该站长是否存在，不存在无需更新(后台存在该站长对应经销商数据删除，而导致更新站长报该站长不存在而无法变更问题)
            DistributorDTO originMaster = userFeign.getDistributorById(origStation.getMasterDistributorId());
            if (Objects.nonNull(originMaster)) {

                updateDistributor.setId(origStation.getMasterDistributorId());
                updateDistributor.setStationMaster(false);
                userFeign.updateDistributorCommon(updateDistributor);
            }

            //将更换的经销商的是否为站长标识为是
            updateDistributor.setId(station.getMasterDistributorId());
            updateDistributor.setStationMaster(true);
            userFeign.updateDistributorCommon(updateDistributor);

        } else if (origStation.getMasterDistributorId() == null && station.getMasterDistributorId() != null) {
            //本未设服务站站长，编辑时设置了服务站站长，将该经销商是否为站长标识设为是
            updateDistributor.setId(station.getMasterDistributorId());
            updateDistributor.setStationMaster(true);
            userFeign.updateDistributorCommon(updateDistributor);
        }
        //判断服务站门店名称是否更变，若更变则同步更新安装工表的服务站门店名称
        if (!station.getName().equals(origStation.getName())) {
            Map<String, Object> stationInfo = new HashMap<>();
            stationInfo.put("stationId", station.getId());
            stationInfo.put("newStationName", station.getName());
            rabbitTemplate.convertAndSend(RabbitConstant.ENGINEER_UPDATE_STATION_NAME,stationInfo);
        }

        //重新设置经纬度
        if (StringUtil.isNotEmpty(station.getAddress())) {
            Object[] o;
            try {
                o = GetLatAndLngUtil.getCoordinate(station.getAddress());
            } catch (IOException e) {
                throw new YimaoException("获取地址经纬度失败");
            }
            station.setLatitude(Double.parseDouble(o[1].toString()));
            station.setLongitude(Double.parseDouble(o[0].toString()));
        }
        //1-修改服务站门店基本信息
        String updater = userCache.getCurrentAdminRealName();
        Date now = new Date();
        station.setUpdater(updater);
        station.setUpdateTime(now);
        stationMapper.updateByPrimaryKeySelective(station);
        //2-修改服务站门店服务区域信息
        StationServiceArea record = new StationServiceArea();
        record.setStationId(station.getId());
        stationServiceAreaMapper.delete(record);
        if (CollectionUtil.isNotEmpty(serviceAreaList)) {
            //绑定门店与服务区域关系
            this.saveStationServiceArea(serviceAreaList, station, updater, now);
            //修改服务站门店的安装工的服务区域
            this.updateEngineerServiceArea(serviceAreaList, station.getId());
        }

        //站务系统3.0.1 后不支持编辑服务站门店更改绑定的服务站公司
//        //3-保存公司和门店的关联关系
//        this.saveStationCompanyStation(stationCompanyIds, station.getId());
//        try {
//            //同步售后
//            BaideApiUtil.syncServiceSiteAddOrUpdate(BaideApiUtil.UPDATE, station.getId().toString(), station.getName(), station.getMasterName(), station.getMasterPhone(), station.getMasterIdCard(), station.getProvince(), station.getCity(), station.getRegion(), station.getAddress());
//        } catch (Exception e) {
//            log.error("==========修改服务站(服务站id=" + station.getId().toString() + ")同步售后失败====" + e.getMessage());
//        }
    }

    private void updateEngineerServiceArea(List<StationServiceAreaDTO> serviceAreaList, Integer stationId) {
        Iterator<StationServiceAreaDTO> iterator = serviceAreaList.iterator();
        while (iterator.hasNext()) {
            StationServiceAreaDTO stationServiceArea = iterator.next();
            if (!StationAreaServiceTypeEnum.haveAfterSale(stationServiceArea.getServiceType())) {
                //仅保留有售后权限的区域，若无则剔除
                iterator.remove();
            }
        }
        if (CollectionUtil.isNotEmpty(serviceAreaList)) {
            UpdateEngineerServiceAreaDataInfo updateEngineerServiceAreaDataInfo = new UpdateEngineerServiceAreaDataInfo();
            updateEngineerServiceAreaDataInfo.setServiceAreaList(serviceAreaList);
            updateEngineerServiceAreaDataInfo.setStationId(stationId);
            userFeign.updateEngineerServiceArea(updateEngineerServiceAreaDataInfo);
        }
    }

    /**
     * 校验区域是否选择重复
     *
     * @param list
     * @return
     */
    private void hasSame(List<Integer> list) {
        if (CollectionUtil.isNotEmpty(list)) {
            boolean isRepeat = list.size() != new HashSet<Integer>(list).size();
            if (isRepeat) {
                throw new BadRequestException("区域选择重复");
            }
        }
    }

    /**
     * 保存区县级公司服务区域信息
     *
     * @param serviceAreaLists 服务站门店服务区域列表
     * @param station          服务站门店
     * @param adminName        操作管理员姓名
     * @param now              操作时间
     */
    private void saveStationServiceArea(List<StationServiceAreaDTO> serviceAreaLists, Station station, String adminName, Date now) {
        StationServiceArea query = new StationServiceArea();
        for (StationServiceAreaDTO serviceArea : serviceAreaLists) {
            query.setStationId(station.getId());
            query.setAreaId(serviceArea.getAreaId());
            //一个服务站门店对应一个服务区域只会有一条数据（0-售前+售后；1-售前；2-售后）
            StationServiceArea exist = stationServiceAreaMapper.selectOne(query);
            if (exist == null) {
                //不存在则新增
                serviceArea.setStationId(station.getId());
                serviceArea.setStationName(station.getName());
                serviceArea.setCreator(adminName);
                serviceArea.setCreateTime(now);
                stationServiceAreaMapper.insertSelective(BeanHelper.copyProperties(serviceArea, StationServiceArea.class));
            } else {
                //存在则说明该公司拥有该地区另一种服务权限（售前或售后），直接将存在的服务权限类型改为0（售前+售后）
                exist.setServiceType(StationAreaServiceTypeEnum.ALL.value);
                exist.setUpdater(adminName);
                exist.setUpdateTime(now);
                stationServiceAreaMapper.updateByPrimaryKeySelective(exist);
            }
        }
    }

    /**
     * 保存区县级公司和服务站门店关联关系
     *
     * @param stationCompanyIds 区县级公司ID
     * @param stationId         服务站门店ID
     */
    private void saveStationCompanyStation(List<Integer> stationCompanyIds, Integer stationId) {
        //1-先删除
        StationCompanyStation record = new StationCompanyStation();
        record.setStationId(stationId);
        stationCompanyStationMapper.delete(record);

        //2-再保存
        List<StationCompanyStation> list = new ArrayList<>();
        for (Integer stationCompanyId : stationCompanyIds) {
            StationCompanyStation scs = new StationCompanyStation();
            scs.setStationCompanyId(stationCompanyId);
            scs.setStationId(stationId);
            list.add(scs);
        }
        stationCompanyStationMapper.batchInsert(list);
    }

    /**
     * 服务站门店基本信息校验
     *
     * @param station 服务站门店
     */
    private void checkStation(Station station) {
        if (StringUtil.isEmpty(station.getName())) {
            throw new BadRequestException("请填写服务站门店名称。");
        }
        if (station.getType() == null) {
            throw new BadRequestException("请选择服务站门店类型。");
        }
        if (StringUtil.isEmpty(station.getProvince())) {
            throw new BadRequestException("请选择服务站门店所属区域。");
        }
        if (StringUtil.isEmpty(station.getCity())) {
            throw new BadRequestException("请选择服务站门店所属区域。");
        }
        if (StringUtil.isEmpty(station.getRegion())) {
            throw new BadRequestException("请选择服务站门店所属区域。");
        }
        if (StringUtil.isEmpty(station.getAddress())) {
            throw new BadRequestException("请填写服务站门店详细地址。");
        }
        if (StringUtil.isEmpty(station.getContact())) {
            throw new BadRequestException("请填写服务站联系人姓名。");
        }
        if (StringUtil.isEmpty(station.getContactPhone())) {
            throw new BadRequestException("请填写服务站联系人手机号。");
        }

    }

    /**
     * 校验服务区域有没有被其它服务站门店选择
     *
     * @param serviceAreaLists 服务区域ID
     * @param stationId        服务站门店ID
     */
    private void checkStationServiceArea(List<StationServiceAreaDTO> serviceAreaLists, Integer stationId) {
        for (StationServiceAreaDTO dto : serviceAreaLists) {
            if (dto.getServiceType() == null) {
                throw new BadRequestException("请选择服务区域服务类型！");
            }
            Example example = new Example(StationServiceArea.class);
            Example.Criteria criteria = example.createCriteria();
            Integer areaId = dto.getAreaId();
            Integer serviceType = dto.getServiceType();
            criteria.andEqualTo("areaId", areaId);
            List<Integer> serviceTypes = new ArrayList<>();
            serviceTypes.add(serviceType);//用户选择的服务权限
            serviceTypes.add(StationAreaServiceTypeEnum.ALL.value);//如果表中存在某个区域售前和售后都被同一个服务站门店管理则其它门店不可再选择管理该区域权限
            criteria.andIn("serviceType", serviceTypes);
            if (stationId != null) {
                criteria.andNotEqualTo("stationId", stationId);
            }
            List<StationServiceArea> list = stationServiceAreaMapper.selectByExample(example);
            if (list.size() > 0) {
                Integer type = list.get(0).getServiceType();
                if (type == StationAreaServiceTypeEnum.ALL.value) {
                    throw new BadRequestException("该区域的" + StationAreaServiceTypeEnum.ALL.name + "服务权限已被【" + list.get(0).getStationName() + "】服务，不能选择。");
                }
                if (type == StationAreaServiceTypeEnum.PRE_SALE.value) {
                    throw new BadRequestException("该区域的" + StationAreaServiceTypeEnum.PRE_SALE.name + "服务权限已被【" + list.get(0).getStationName() + "】服务，不能选择。");
                }
                if (type == StationAreaServiceTypeEnum.AFTER_SALE.value) {
                    throw new BadRequestException("该区域的" + StationAreaServiceTypeEnum.AFTER_SALE.name + "服务权限已被【" + list.get(0).getStationName() + "】服务，不能选择。");
                }
            }
        }


    }

    /**
     * 修改服务站门店承包信息
     *
     * @param station 承包信息
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void updateStationContractInfo(Station station) {
        Station record = new Station();
        record.setId(station.getId());
        record.setContractor(station.getContractor());
        record.setContractorPhone(station.getContractorPhone());
        record.setContractorIdCard(station.getContractorIdCard());
        record.setContractStartTime(station.getContractStartTime());
        record.setContractEndTime(station.getContractEndTime());
        record.setContract(station.getContract());
        //修改服务站门店承包信息
        String updater = userCache.getCurrentAdminRealName();
        Date now = new Date();
        record.setUpdater(updater);
        record.setUpdateTime(now);
        stationMapper.updateByPrimaryKeySelective(station);
    }

    /**
     * 修改服务站门店经营信息
     *
     * @param station 经营信息
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void updateStationManagementInfo(Station station) {
        Station record = new Station();
        record.setId(station.getId());
        record.setEstablishedTime(station.getEstablishedTime());
        record.setSatisfaction(station.getSatisfaction());
        record.setEmployeeNum(station.getEmployeeNum());
        record.setBusinessHoursStart(station.getBusinessHoursStart());
        record.setBusinessHoursEnd(station.getBusinessHoursEnd());
        record.setImgs(station.getImgs());
        record.setCoverImage(station.getCoverImage());
        record.setStationArea(station.getStationArea());
        record.setPurpose(station.getPurpose());
        record.setAptitude(station.getAptitude());
        record.setIntroduction(station.getIntroduction());
        record.setSorts(station.getSorts());
        //修改服务站门店经营信息
        String updater = userCache.getCurrentAdminRealName();
        Date now = new Date();
        record.setUpdater(updater);
        record.setUpdateTime(now);
        stationMapper.updateByPrimaryKeySelective(station);
    }

    @Override
    public void showStation(Integer id) {
        Station station = stationMapper.selectByPrimaryKey(id);
        if (station == null) {
            throw new BadRequestException("服务站门店不存在！");
        }

        Station update = new Station();
        update.setId(id);
        update.setUpdater(userCache.getCurrentAdminRealName());
        update.setUpdateTime(new Date());
        if (station.getDisplay() == null || station.getDisplay() == 0) {
            update.setDisplay(1);
        } else if (station.getDisplay() == 1) {
            update.setDisplay(0);
        }
        stationMapper.updateByPrimaryKeySelective(update);
    }

    /**
     * 服务站门店上下线
     *
     * @param id 服务站门店ID
     */
    @Override
    public void onlineOffline(Integer id) {
        Station station = stationMapper.selectByPrimaryKey(id);
        if (station != null) {
            Station record = new Station();
            record.setId(id);
            if (station.getOnline() != null && station.getOnline() == 1) {
                record.setOnline(0);
//                try {
//                    //下线同步售后
//                    BaideApiUtil.syncServiceSiteDelete(BaideApiUtil.DELETE, id.toString());
//                } catch (Exception e) {
//                }
            } else {
                StationServiceArea query = new StationServiceArea();
                query.setStationId(id);
                List<StationServiceArea> stationServiceAreas = stationServiceAreaMapper.select(query);
                if (CollectionUtil.isEmpty(stationServiceAreas)) {
                    throw new BadRequestException("服务站未设置服务地区，不能进行上线操作！");
                }
                if (station.getMasterDistributorId() == null) {
                    throw new BadRequestException("服务站门店未设置站长信息，不能进行上线操作！");
                }
                record.setOnline(1);
                record.setOnlineTime(new Date());
//                try {
//                    //同步售后 上线服务站
//                    BaideApiUtil.syncServiceSiteAddOrUpdate(BaideApiUtil.INSERT, station.getId().toString(), station.getName(), station.getMasterName(), station.getMasterPhone(), station.getMasterIdCard(), station.getProvince(), station.getCity(), station.getRegion(), station.getAddress());
//                } catch (Exception e) {
//                    log.error("==========修改服务站(服务站id=" + station.getId().toString() + ")同步售后失败====" + e.getMessage());
//                }
            }
            stationMapper.updateByPrimaryKeySelective(record);
        } else {
            throw new BadRequestException("服务站门店不存在！");
        }
    }

    /**
     * 服务站推荐
     *
     * @param id 服务站门店ID
     */
    @Override
    public void recommend(Integer id) {
        Station station = stationMapper.selectByPrimaryKey(id);
        if (station != null) {
            Station record = new Station();
            record.setId(id);
            if (station.getRecommend() != null && station.getRecommend()) {
                record.setRecommend(false);
            } else {
                record.setRecommend(true);
            }
            stationMapper.updateByPrimaryKeySelective(record);
        }
    }

    /**
     * 查询服务站门店信息（分页）
     *
     * @param pageNum  页码
     * @param pageSize 页面大小
     * @param query    查询条件
     */
    @Override
    public PageVO<StationDTO> pageStation(Integer pageNum, Integer pageSize, StationQuery query) {
        if (query.getHraIsOnline() == null) {
            PageHelper.startPage(pageNum, pageSize);
            Page<StationDTO> page = stationMapper.listStation(query);
            return new PageVO<>(pageNum, page);
        } else {
            List<Integer> hraIds = hraFeign.getHraStationIds();
            HashSet set = new HashSet<>(hraIds);
            hraIds.clear();
            hraIds.addAll(set);
            List<Integer> stationIds = stationMapper.getStationIds();
            if (CollectionUtil.isEmpty(hraIds)) {
                return new PageVO<>(pageNum, null);
            }
            if (query.getHraIsOnline()) {
                PageHelper.startPage(pageNum, pageSize);
                query.setIds(new HashSet<>(hraIds));
                Page<StationDTO> page = stationMapper.getFinalStations(query);
                return new PageVO<>(pageNum, page);
            } else {
                if (CollectionUtil.isEmpty(stationIds)) {
                    return new PageVO<>(pageNum, null);
                }
                stationIds.removeAll(hraIds);
                query.setIds(new HashSet<>(stationIds));
                PageHelper.startPage(pageNum, pageSize);
                Page<StationDTO> page = stationMapper.getFinalStations(query);
                return new PageVO<>(pageNum, page);
            }
        }

    }

    /**
     * 查询服务站门店信息（列表）
     *
     * @param query 查询条件
     */
    @Override
    public List<StationDTO> listStation(StationQuery query) {
        return stationMapper.listStation(query);
    }

    /**
     * 服务站附近门店
     *
     * @param lng
     * @param lat
     */
    @Override
    public List<StationDTO> findStationByLngAndLat(Double lng, Double lat, Integer online, Boolean hraIsOnline) {
        //获取所有服务站列表
        StationQuery query = new StationQuery();
        if (online != null) {
            query.setDisplay(online);
        }

        List<StationDTO> stationDTOList = stationMapper.listStation(query);

        if (hraIsOnline != null) {
            for (int i = 0; i < stationDTOList.size(); i++) {
                Integer id = stationDTOList.get(i).getId();
                boolean hraDeviceStatus = hraFeign.getHraDeviceStatus(id);
                if (hraIsOnline && !hraDeviceStatus || !hraIsOnline && hraDeviceStatus) {
                    stationDTOList.remove(i);
                }
            }
        }


        ListIterator<StationDTO> listIterator = stationDTOList.listIterator();
        while (listIterator.hasNext()) {
            StationDTO stationDTO = listIterator.next();
            Double slng = stationDTO.getLongitude();
            Double slat = stationDTO.getLatitude();
            //数据库中服务站位置为空
            if (slng == null || slat == null) {
                listIterator.remove();
            } else {
                double distance = getDistance(lng, lat, slng, slat);
                stationDTO.setDistance(distance);
            }
        }
        if (CollectionUtil.isEmpty(stationDTOList)) {
            return null;
        }
        Collections.sort(stationDTOList, new Comparator<StationDTO>() {
            @Override
            public int compare(StationDTO o1, StationDTO o2) {
                return o1.getDistance() >= o2.getDistance() ? 1 : -1;
            }
        });
        if (stationDTOList.size() > 5) {
            return stationDTOList.subList(0, 4);
        }
        return stationDTOList;
    }

    /**
     * 根据省市区获取服务站ID和NAME
     *
     * @param province 省
     * @param city     市
     * @param region   区
     */
    @Override
    public StationDTO getStationByPCR(String province, String city, String region, Integer type) {
        List<StationDTO> list = stationMapper.selectStationByPCR(province, city, region, type);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }


    /**
     * 获取销售主体
     *
     * @param
     * @param
     * @return
     */
    @Override
    public StationDTO referrerStation() {
        Integer userId = userCache.getUserId();
        UserDTO user = userFeign.getBasicUserById(userId);
        Integer distributorId = user.getDistributorId();
        if (distributorId == null) {
            List<StationDTO> stationDTOList = stationMapper.selectStationByPCR("上海市", "上海市", "总部区", PermissionTypeEnum.PRE_SALE.value);
            if (CollectionUtil.isNotEmpty(stationDTOList)) {
                return stationDTOList.get(0);
            }
            return null;
        } else {
            DistributorDTO distributorById = userFeign.getExpansionInfoById(distributorId);
            List<StationDTO> stationDTOList = stationMapper.selectStationByPCR(distributorById.getProvince(), distributorById.getCity(), distributorById.getRegion(), PermissionTypeEnum.PRE_SALE.value);
            if (CollectionUtil.isNotEmpty(stationDTOList)) {
                return stationDTOList.get(0);
            }
            return null;
        }
    }

    /**
     * 服务站导出
     *
     * @param
     * @return
     */
    @Override
    public List<StationExportDTO> getStationInfoToExport(StationQuery query) {
        List<StationExportDTO> list = stationMapper.getStationInfoToExport(query);
        return list;
    }

    @Override
    public List<StationServiceAreaDTO> getStationNameByIds(Set<Integer> stationIds) {
        return stationMapper.getStationNameByIds(stationIds);
    }

    @Override
    public String getStationCompanyNameById(Integer stationId) {
        return stationMapper.getStationCompanyNameByStationId(stationId);
    }

    @Override
    public List<Integer> findStationIdsByPCR(String province, String city, String region, String stationName) {
        if (StringUtil.isNotEmpty(province) || StringUtil.isNotEmpty(city) || StringUtil.isNotEmpty(region) || StringUtil.isNotEmpty(stationName)) {
            return stationMapper.findStationIdsByPCR(province, city, region, stationName);
        }
        return null;
    }

    /**
     * 服务站附近门店
     *
     * @param long1
     * @param lat1
     * @param long2
     * @param lat2
     * @return
     */
    private double getDistance(double long1, double lat1, double long2, double lat2) {
        double a, b, d, sa2, sb2;
        lat1 = rad(lat1);
        lat2 = rad(lat2);
        a = lat1 - lat2;
        b = rad(long1 - long2);

        sa2 = Math.sin(a / 2.0);
        sb2 = Math.sin(b / 2.0);
        d = 2 * Constant.EARTH_RADIUS
                * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)
                * Math.cos(lat2) * sb2 * sb2));
        return d;
    }

    private double rad(double d) {
        return d * Math.PI / 180.0;
    }

    private static String leftPad(int num, final int maxLen, char filledChar) {
        StringBuffer sb = new StringBuffer();
        String str = String.valueOf(num);
        for (int i = str.length(); i < maxLen; i++) {
            sb.append(filledChar);
        }
        return sb.append(str).toString();
    }

    @Override
    public List<Station> getStationByStationCompanyId(Integer stationCompanyId) {
        return stationMapper.getStationByStationCompanyId(stationCompanyId);
    }

    @Override
    public StationDTO getStationByDistributorId(Integer recommendId) {
        Station station = new Station();
        station.setMasterDistributorId(recommendId);
        Station selectOne = stationMapper.selectOne(station);
        if (selectOne == null) {
            return null;
        }
        StationDTO stationDTO = new StationDTO();
        selectOne.convert(stationDTO);
        return stationDTO;
    }


    @Override
    public Boolean getStationStatusByDistributorId(Integer distributorId) {
        return stationMapper.getStationStatusByDistributorId(distributorId);
    }

    @Override
    public List<StationServiceAreaDTO> getServiceAreaByStationId(Integer stationId) {
        return stationServiceAreaMapper.getServiceAreaByStationId(stationId);
    }

    @Override
    public List<StationDTO> getAllStation() {
        return stationMapper.getAllStation();
    }

    @Override
    public void updateContactInfo(StationDTO update) {
        Station station = new Station();
        station.setId(update.getId());
        station.setContact(update.getContact());
        station.setContactPhone(update.getContactPhone());
        int count = stationMapper.updateByPrimaryKeySelective(station);
        if (count < 1) {
            throw new YimaoException("修改服务站联系人异常");
        }
    }

    @Override
    public List<StationDTO> getStationListByIds(StationQuery stationQuery) {

        return stationMapper.getStationListByIds(stationQuery);
    }
}
