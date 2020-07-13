package com.yimao.cloud.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.StationAreaServiceTypeEnum;
import com.yimao.cloud.base.enums.TransferOperateTypeEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.mail.MailSender;
import com.yimao.cloud.base.utils.BeanHelper;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.yunSignUtil.YunSignApi;
import com.yimao.cloud.base.utils.yunSignUtil.YunSignResult;
import com.yimao.cloud.pojo.dto.system.*;
import com.yimao.cloud.pojo.query.system.StationCompanyQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.feign.OrderFeign;
import com.yimao.cloud.system.feign.StationFeign;
import com.yimao.cloud.system.feign.UserFeign;
import com.yimao.cloud.system.mapper.StationCompanyMapper;
import com.yimao.cloud.system.mapper.StationCompanyServiceAreaMapper;
import com.yimao.cloud.system.mapper.StationMapper;
import com.yimao.cloud.system.mapper.StationServiceAreaMapper;
import com.yimao.cloud.system.po.Station;
import com.yimao.cloud.system.po.StationCompany;
import com.yimao.cloud.system.po.StationCompanyServiceArea;
import com.yimao.cloud.system.po.StationServiceArea;
import com.yimao.cloud.system.service.AreaService;
import com.yimao.cloud.system.service.StationCompanyService;
import com.yimao.cloud.system.service.StationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Lizhqiang
 * @date 2019/1/17
 */
@Service
@Slf4j
public class StationCompanyServiceImpl implements StationCompanyService {

    @Resource
    private YunSignApi yunSignApi;
    @Resource
    private UserCache userCache;
    @Resource
    private StationCompanyMapper stationCompanyMapper;
    @Resource
    private StationCompanyServiceAreaMapper stationCompanyServiceAreaMapper;
    @Resource
    private AreaService areaService;
    @Resource
    private StationService stationService;
    @Resource
    private StationServiceAreaMapper stationServiceAreaMapper;
    @Resource
    private StationMapper stationMapper;
    @Resource
    private AmqpTemplate amqpTemplate;

    @Resource
    private OrderFeign orderFeign;
    @Resource
    private UserFeign userFeign;
    @Resource
    private StationFeign stationFeign;
    @Resource
    private MailSender mailSender;

    @Override
    public List<StationCompanyDTO> getStationCompanyByLocation(String province, String city, String region) {
        return stationCompanyMapper.getStationCompanyByLocation(province, city, region);
    }

    @Override
    public List<StationCompanyServiceAreaDTO> getStationCompanyServiceAreaAndServiceType(Integer id) {
        StationCompanyServiceArea query = new StationCompanyServiceArea();
        query.setStationCompanyId(id);
        List<StationCompanyServiceArea> stationCompanyServiceAreas = stationCompanyServiceAreaMapper.select(query);
        List<StationCompanyServiceAreaDTO> stationCompanyServiceAreaDTOS = null;
        if (CollectionUtil.isNotEmpty(stationCompanyServiceAreas)) {
            stationCompanyServiceAreaDTOS = BeanHelper.copyWithCollection(stationCompanyServiceAreas, StationCompanyServiceAreaDTO.class);
            //一个服务站公司对应一个服务区域最多只会存在一条数据
            for (StationCompanyServiceAreaDTO stationCompanyServiceAreaDTO : stationCompanyServiceAreaDTOS) {
                stationCompanyServiceAreaDTO.setUsablePreSale(false); //暂设售前不可选
                stationCompanyServiceAreaDTO.setUsableAfterSale(false); //暂设售后不可选
                if (StationAreaServiceTypeEnum.havePreSale(stationCompanyServiceAreaDTO.getServiceType())) {
                    stationCompanyServiceAreaDTO.setUsablePreSale(true); //售前可选
                }
                if (StationAreaServiceTypeEnum.haveAfterSale(stationCompanyServiceAreaDTO.getServiceType())) {
                    stationCompanyServiceAreaDTO.setUsableAfterSale(true); //售后可选
                }
            }
            ListIterator<StationCompanyServiceAreaDTO> listIterator = stationCompanyServiceAreaDTOS.listIterator();
            while (listIterator.hasNext()) {
                StationCompanyServiceAreaDTO stationCompanyServiceAreaDTO = listIterator.next();
                StationServiceArea queryStationServiceArea = new StationServiceArea();
                queryStationServiceArea.setAreaId(stationCompanyServiceAreaDTO.getAreaId());
                // 查询已被其它服务站门店占用的服务地区及其服务权限
                List<StationServiceArea> beOccupiedStationServiceAreas = stationServiceAreaMapper.select(queryStationServiceArea);
                if (CollectionUtil.isNotEmpty(beOccupiedStationServiceAreas)) {
                    for (StationServiceArea beOccupiedStationServiceArea : beOccupiedStationServiceAreas) {
                        if (StationAreaServiceTypeEnum.havePreSale(beOccupiedStationServiceArea.getServiceType())) {
                            stationCompanyServiceAreaDTO.setUsablePreSale(false);
                        }
                        if (StationAreaServiceTypeEnum.haveAfterSale(beOccupiedStationServiceArea.getServiceType())) {
                            stationCompanyServiceAreaDTO.setUsableAfterSale(false);
                        }
                    }
                }
                if (stationCompanyServiceAreaDTO.getUsableAfterSale() && stationCompanyServiceAreaDTO.getUsablePreSale()) {
                    //售前售后都可选，serviceType设为 0
                    stationCompanyServiceAreaDTO.setServiceType(StationAreaServiceTypeEnum.ALL.value);
                } else if (!stationCompanyServiceAreaDTO.getUsableAfterSale() && stationCompanyServiceAreaDTO.getUsablePreSale()) {
                    //仅售前可选，serviceType设为 1
                    stationCompanyServiceAreaDTO.setServiceType(StationAreaServiceTypeEnum.PRE_SALE.value);
                } else if (stationCompanyServiceAreaDTO.getUsableAfterSale() && !stationCompanyServiceAreaDTO.getUsablePreSale()) {
                    //仅售后可选，serviceType设为 2
                    stationCompanyServiceAreaDTO.setServiceType(StationAreaServiceTypeEnum.AFTER_SALE.value);
                } else if (!stationCompanyServiceAreaDTO.getUsablePreSale() && !stationCompanyServiceAreaDTO.getUsableAfterSale()) {
                    //售前售后都不可选，剔除！
                    listIterator.remove();
                }
            }
        }
        return stationCompanyServiceAreaDTOS;
    }

    @Override
    public List<StationCompanyDTO> getStationCompanyByStationId(Integer stationId) {
        List<StationCompanyDTO> list = stationCompanyMapper.getStationCompanyByStationId(stationId);
        if (CollectionUtil.isEmpty(list)) {
            throw new YimaoException("该服务站门店没有对应的服务站公司！");
        }
        return list;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String registerCompanyUser(Integer stationCompanyId) {
        StationCompany stationCompany = stationCompanyMapper.selectByPrimaryKey(stationCompanyId);
        String yunSignId = stationCompany.getYunSignId();
        Boolean flag = false;
        if (yunSignId != null && yunSignId.trim().length() > 5) {
            //该区县级公司是否已注册过云签企业用户
            flag = yunSignApi.checkUserExisted(yunSignId).isSuccess();
        }
        if (flag) {
            //该公司已注册过云签企业用户，无需再次注册
            throw new YimaoException("该服务站公司已注册过云签，无需再次注册");
        }
        //查询该服务站公司所对应的服务站门店
        List<Station> stations = stationService.getStationByStationCompanyId(stationCompanyId);
        if (CollectionUtil.isEmpty(stations)) {
            throw new YimaoException("服务站门店不存在");
        }
        //获取其中一个门店信息
        Station station = stations.get(0);
        String sMobile = station.getMasterPhone();
        String sUserName = station.getMasterName();
        String sIdentityNumber = station.getMasterIdCard();
        String sLicenseNo = stationCompany.getCreditCode();
        String sCompany = stationCompany.getName();
        String sEmail = stationCompany.getEmail();
        String sUserId = stationCompany.getYunSignId();
        try {
            //校验站长信息是否完善
            checkStationMasterInfo(sMobile, sUserName, sIdentityNumber);
        } catch (Exception e) {
            throw new YimaoException("站长信息不完善，请完善后注册云签");
        }
        if (sLicenseNo == null) {
            throw new YimaoException("服务站公司统一社会信用代码不能为空");
        }
        //判断服务站公司的联系人的姓名、身份证是否能通过云签数据校验
        /*if(!YunSignApi.checkidentityNumber(sIdentityNumber,sUserName).isSuccess()){
            throw new YimaoException("服务站站长姓名、身份证 无法通过云签数据校验！");
        }*/

        if (sUserId == null) {
            sUserId = stationCompany.getId() + "";
        }
        YunSignResult serviceRegResult = yunSignApi.registerCommpanyUser(sLicenseNo, sCompany, sIdentityNumber, sUserName, sMobile, sEmail, sUserId, true);
        log.info("云签服务站注册结果 ： " + serviceRegResult.getResultData() + " " + serviceRegResult.getCode() + " > " + serviceRegResult.getDesc() + " " + sUserId);
        if (!serviceRegResult.isSuccess()) {
            throw new YimaoException("云签注册失败,请刷新页面后重试" + serviceRegResult.getResultData() + " " + serviceRegResult.getDesc());
        }
        if (serviceRegResult.isSuccess()) {
            //给区县级公司添加云签用户id
            StationCompany newStationCompany = new StationCompany();
            newStationCompany.setYunSignId(sUserId);
            newStationCompany.setYunSignTime(new Date());
            newStationCompany.setSignUp(1);
            newStationCompany.setId(stationCompany.getId());
            int count = stationCompanyMapper.updateByPrimaryKeySelective(newStationCompany);
            if (count != 1) {
                throw new YimaoException("添加云签用户id失败");
            }
        }
        return null;
    }

    /**
     * 校验服务站站长信息
     *
     * @param sMobile
     * @param sUserName
     * @param sIdentityNumber
     */
    private void checkStationMasterInfo(String sMobile, String sUserName, String sIdentityNumber) {
        if (StringUtil.isEmpty(sMobile)) {
            throw new YimaoException("服务站站长手机号不能为空");
        }
        if (StringUtil.isEmpty(sUserName)) {
            throw new YimaoException("服务站站长姓名不能为空。");
        }
        if (StringUtil.isEmpty(sIdentityNumber)) {
            throw new YimaoException("服务站站长身份证号不能为空。");
        }
    }

    /**
     * 新增区县级公司
     *
     * @param dto     区县级公司
     * @param areaIds 区县级公司服务区域ID
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void saveStationCompany(StationCompanyDTO dto, List<Integer> areaIds) {
        StationCompany stationCompany = new StationCompany(dto);
        //区域选择校验
        this.hasSame(areaIds);
        //必填字段校验
        this.checkStationCompany(stationCompany);
        //校验公司名字是否已经存在
        StationCompany query = new StationCompany();
        query.setName(stationCompany.getName());
        StationCompany dbquery = stationCompanyMapper.selectOne(query);
        if (dbquery != null) {
            throw new YimaoException("公司已经存在。");
        }
        if (CollectionUtil.isNotEmpty(dto.getServiceAreas())) {
            //校验服务区域的服务权限有没有被其它区县级公司选择
            this.checkStationCompanyServiceArea(dto.getServiceAreas(), null);
        }
        //1-保存区县级公司基本信息
        String creator = userCache.getCurrentAdminRealName();
        Date now = new Date();
        stationCompany.setCreator(creator);
        stationCompany.setCreateTime(now);
        stationCompany.setId(null);
        if (stationCompany.getOnline()) {
            stationCompany.setOnlineTime(new Date());
        }
        stationCompany.setSignUp(0); //是否已注册云签，默认为 0 - 未注册
        //插入服务站公司
        stationCompanyMapper.insert(stationCompany);
        //插入公司后才能获取id通过id来设置服务站公司编号
        SimpleDateFormat df = new SimpleDateFormat("yyMM");
        String format = df.format(new Date());
        String str = leftPad(stationCompany.getId(), 4, '0');
        //公司编号
        str = format + str;
        StationCompany updateBody = new StationCompany();
        updateBody.setId(stationCompany.getId());
        updateBody.setCode(str);
        stationCompanyMapper.updateByPrimaryKeySelective(updateBody);
        if (CollectionUtil.isNotEmpty(dto.getServiceAreas())) {
            //2-保存区县级公司服务区域信息
            this.saveStationCompanyServiceArea(dto.getServiceAreas(), stationCompany, creator, now);
        }
    }

    /**
     * 修改区县级公司
     *
     * @param dto     区县级公司
     * @param areaIds 区县级公司服务区域ID
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void updateStationCompany(StationCompanyDTO dto, List<Integer> areaIds) {
        StationCompany stationCompany = new StationCompany(dto);
        //必填字段校验
        this.checkStationCompany(stationCompany);
        if (CollectionUtil.isNotEmpty(dto.getServiceAreas())) {
            //校验服务区域的服务权限有没有被其它区县级公司选择
            this.checkStationCompanyServiceArea(dto.getServiceAreas(), stationCompany.getId());
        }
        //1-更新区县级公司基本信息
        String updater = userCache.getCurrentAdminRealName();
        Date now = new Date();
        stationCompany.setUpdater(updater);
        stationCompany.setUpdateTime(now);
        stationCompanyMapper.updateByPrimaryKeySelective(stationCompany);
        //2-更新区县级公司服务区域信息
        StationCompanyServiceArea record = new StationCompanyServiceArea();
        record.setStationCompanyId(stationCompany.getId());
        stationCompanyServiceAreaMapper.delete(record);
        if (CollectionUtil.isNotEmpty(dto.getServiceAreas())) {
            this.saveStationCompanyServiceArea(dto.getServiceAreas(), stationCompany, updater, now);
        }
    }

    /**
     * 保存区县级公司服务区域信息
     *
     * @param stationCompanyServiceAreas 区县级公司服务区域信息
     * @param stationCompany             区县级公司
     * @param adminName                  操作管理员姓名
     * @param now                        操作时间
     */
    private void saveStationCompanyServiceArea(List<StationCompanyServiceAreaDTO> stationCompanyServiceAreas, StationCompany stationCompany, String adminName, Date now) {
        StationCompanyServiceArea query = new StationCompanyServiceArea();
        for (StationCompanyServiceAreaDTO serviceArea : stationCompanyServiceAreas) {
            query.setStationCompanyId(stationCompany.getId());
            query.setAreaId(serviceArea.getAreaId());
            //一个服务站公司对应一个服务区域只会有一条数据（0-售前+售后；1-售前；2-售后）
            StationCompanyServiceArea exist = stationCompanyServiceAreaMapper.selectOne(query);
            if (exist == null) {
                //不存在则新增
                serviceArea.setStationCompanyId(stationCompany.getId());
                serviceArea.setStationCompanyName(stationCompany.getName());
                serviceArea.setCreator(adminName);
                serviceArea.setCreateTime(now);
                stationCompanyServiceAreaMapper.insertSelective(BeanHelper.copyProperties(serviceArea, StationCompanyServiceArea.class));
            } else {
                //存在则说明该公司拥有该地区另一种服务权限（售前或售后），直接将存在的服务权限类型改为0（售前+售后）
                exist.setServiceType(StationAreaServiceTypeEnum.ALL.value);
                exist.setUpdater(adminName);
                exist.setUpdateTime(now);
                stationCompanyServiceAreaMapper.updateByPrimaryKeySelective(exist);
            }
        }
    }

    /**
     * 查询区县级公司（分页）
     *
     * @param pageNum  页码
     * @param pageSize 页面大小
     * @param query    查询条件
     */
    @Override
    public PageVO<StationCompanyDTO> pageStationCompany(Integer pageNum, Integer pageSize, StationCompanyQuery query) {
        //开始分页
        PageHelper.startPage(pageNum, pageSize);
        //查询服务站公司信息
        Page<StationCompanyDTO> page = stationCompanyMapper.listStationCompany(query);

        //信息封装，服务区域展示处理
        massagePack(page);

        return new PageVO<>(pageNum, page);
    }

    private void massagePack(Page<StationCompanyDTO> page) {
        if (CollectionUtil.isEmpty(page.getResult())) {
            return;
        }
        for (StationCompanyDTO stationCompanyDTO : page.getResult()) {
            stationCompanyDTO.setAllowTransfer(false);//暂设不允许承包转让操作，如果有售后服务权限设为true
            //服务地区及权限
            List<StationCompanyServiceAreaDTO> stationCompanyServiceAreaDTOList = stationCompanyDTO.getServiceAreas();
            if (CollectionUtil.isNotEmpty(stationCompanyServiceAreaDTOList)) {
                //服务区域去重（保留一条数据）
                Set<Integer> areaIds = new HashSet<>();
                for (StationCompanyServiceAreaDTO stationCompanyServiceArea : stationCompanyServiceAreaDTOList) {
                    areaIds.add(stationCompanyServiceArea.getAreaId());
                }

                List<StationCompanyServiceAreaDTO> results = new ArrayList<>();
                for (Integer areaId : areaIds) {
                    StationCompanyServiceAreaDTO result = new StationCompanyServiceAreaDTO();
                    result.setAreaId(areaId);
                    result.setHavePreSale(false); //拥有售前权限暂设不拥有
                    result.setHaveAfterSale(false);//拥有售后权限暂设不拥有
                    for (StationCompanyServiceAreaDTO stationCompanyServiceArea : stationCompanyServiceAreaDTOList) {
                        if (stationCompanyServiceArea.getAreaId().intValue() == areaId) {
                            result.setProvince(stationCompanyServiceArea.getProvince());
                            result.setCity(stationCompanyServiceArea.getCity());
                            result.setRegion(stationCompanyServiceArea.getRegion());
                            result.setServiceType(stationCompanyServiceArea.getServiceType());
                            if (StationAreaServiceTypeEnum.havePreSale(stationCompanyServiceArea.getServiceType())) {
                                //拥有售前权限
                                result.setHavePreSale(true);
                            }
                            if (StationAreaServiceTypeEnum.haveAfterSale(stationCompanyServiceArea.getServiceType())) {
                                //拥有售后权限
                                result.setHaveAfterSale(true);
                                //拥有售后服务权限则允许承包转让操作
                                stationCompanyDTO.setAllowTransfer(true);
                            }
                        }
                    }
                    results.add(result);
                }
                stationCompanyDTO.setServiceAreas(results);
            }
        }
    }

    /**
     * 查询区县级公司（列表）
     *
     * @param query 查询条件
     */
    @Override
    public List<StationCompanyDTO> listStationCompany(StationCompanyQuery query) {
        return stationCompanyMapper.listStationCompany(query);
    }

    /**
     * 区县级公司上下线
     *
     * @param id 区县级公司ID
     */
    @Override
    public void onlineOffline(Integer id) {
        StationCompany stationCompany = stationCompanyMapper.selectByPrimaryKey(id);
        if (stationCompany != null) {
            StationCompany record = new StationCompany();
            record.setId(id);
            if (stationCompany.getOnline()) {
                record.setOnline(false);
            } else {
                //查询服务站公司是否有服务区域，若没有则不允许上线
                StationCompanyServiceArea query = new StationCompanyServiceArea();
                query.setStationCompanyId(id);
                List<StationCompanyServiceArea> stationCompanyServiceAreas = stationCompanyServiceAreaMapper.select(query);
                if (CollectionUtil.isEmpty(stationCompanyServiceAreas)) {
                    throw new BadRequestException("服务站公司没有设置服务地区，无法上线！");
                }
                record.setOnlineTime(new Date());
                record.setOnline(true);
            }
            stationCompanyMapper.updateByPrimaryKeySelective(record);
        }
    }

    @Override
    public boolean isAreaUsed(String area) {
        Integer areaUsed = stationCompanyMapper.isAreaUsed(area);
        if (areaUsed >= 1) {
            return true;
        }
        return false;
    }

    @Override
    public StationCompanyDTO getStationCompanyByPCR(String province, String city, String region, Integer type) {
        return stationCompanyMapper.getStationCompanyByPCR(province, city, region, type);
    }

    /**
     * 校验必填字段
     *
     * @param stationCompany 区县级公司
     */
    private void checkStationCompany(StationCompany stationCompany) {
        if (stationCompany.getName() == null) {
            throw new BadRequestException("公司名称不能为空。");
        }
        if (stationCompany.getType() == null) {
            throw new BadRequestException("公司类型不能为空。");
        }
        if (StringUtil.isEmpty(stationCompany.getContact())) {
            throw new BadRequestException("联系人不能为空。");
        }
        if (StringUtil.isEmpty(stationCompany.getContactPhone())) {
            throw new BadRequestException("联系人手机号不能为空。");
        }
        if (StringUtil.isEmpty(stationCompany.getCreditCode())) {
            throw new BadRequestException("社会统一信用代码不能为空。");
        }
        if (StringUtil.isEmpty(stationCompany.getLegalPerson())) {
            throw new BadRequestException("法人代表姓名不能为空。");
        }
        if (StringUtil.isEmpty(stationCompany.getEmail())) {
            throw new BadRequestException("公司邮箱不能为空。");
        }

    }


    /**
     * 校验服务区域有没有被其它区县级公司选择
     *
     * @param serviceAreaDTOList 服务区域集合
     * @param stationCompanyId   区县级公司ID
     */
    private void checkStationCompanyServiceArea(List<StationCompanyServiceAreaDTO> serviceAreaDTOList, Integer stationCompanyId) {
        for (StationCompanyServiceAreaDTO stationCompanyServiceAreaDTO : serviceAreaDTOList) {
            Integer serviceType = stationCompanyServiceAreaDTO.getServiceType();
            if (serviceType == null) {
                throw new BadRequestException("请选择服务地区的服务类型！");
            }
            Example example = new Example(StationCompanyServiceArea.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("areaId", stationCompanyServiceAreaDTO.getAreaId());
            List<Integer> serviceTypes = new ArrayList<>();//服务类型
            serviceTypes.add(stationCompanyServiceAreaDTO.getServiceType());//前端传回的服务类型
            serviceTypes.add(StationAreaServiceTypeEnum.ALL.value);//0-售前+售后 （一个服务区域最多绑定一个售前或者售后的服务站公司）
            criteria.andIn("serviceType", serviceTypes);
            if (stationCompanyId != null) {
                criteria.andNotEqualTo("stationCompanyId", stationCompanyId);
            }
            List<StationCompanyServiceArea> list = stationCompanyServiceAreaMapper.selectByExample(example);
            if (list.size() > 0) {
                throw new BadRequestException("该区域的" + StationAreaServiceTypeEnum.getServiceTypeName(serviceType) + "服务权限已被【" + list.get(0).getStationCompanyName() + "】服务，不能选择。");
            }
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
     * 获取四位数公司id
     *
     * @param num
     * @param maxLen
     * @param filledChar
     * @return
     */

    private static String leftPad(int num, final int maxLen, char filledChar) {
        StringBuffer sb = new StringBuffer();
        String str = String.valueOf(num);
        for (int i = str.length(); i < maxLen; i++) {
            sb.append(filledChar);
        }
        return sb.append(str).toString();
    }

    @Override
    public void updateContactInfo(StationCompanyDTO update) {
        StationCompany stationCompany = new StationCompany();
        stationCompany.setId(update.getId());
        stationCompany.setContact(update.getContact());
        stationCompany.setContactPhone(update.getContactPhone());
        stationCompany.setEmail(update.getEmail());

        stationCompanyMapper.updateByPrimaryKeySelective(stationCompany);

    }

    @Override
    public StationCompanyServiceAreaDTO getStationCompanyServiceAreaByPCR(String province, String city, String region) {
        //查询区域id
        Integer areaId = areaService.getRegionIdByPCR(province, city, region);
        if (areaId == null) {
            throw new BadRequestException("该地区不存在。");
        }
        //查询该服务区域服务权限被服务站公司占用情况
        StationCompanyServiceArea query = new StationCompanyServiceArea();
        query.setAreaId(areaId);
        List<StationCompanyServiceArea> queryList = stationCompanyServiceAreaMapper.select(query);

        StationCompanyServiceAreaDTO result = new StationCompanyServiceAreaDTO();
        //暂设售前售后都可选
        result.setUsablePreSale(true); //售前可选标识
        result.setUsableAfterSale(true); //售后可选标识
        if (CollectionUtil.isEmpty(queryList)) {
            //未有服务站公司占用该地区权限
            result.setProvince(province);
            result.setCity(city);
            result.setRegion(region);
            result.setAreaId(areaId);
        } else {
            result.setProvince(province);
            result.setCity(city);
            result.setRegion(region);
            result.setAreaId(areaId);
            //已有服务站公司占用服务权限，将已被占用的服务权限剔除
            for (StationCompanyServiceArea stationCompanyServiceArea : queryList) {
                if (stationCompanyServiceArea.getServiceType() == StationAreaServiceTypeEnum.ALL.value) {
                    result.setUsablePreSale(false); //售前不可选标识
                    result.setUsableAfterSale(false); //售后不可选标识
                } else if (stationCompanyServiceArea.getServiceType() == StationAreaServiceTypeEnum.PRE_SALE.value) {
                    result.setUsablePreSale(false);//售前不可选标识
                } else if (stationCompanyServiceArea.getServiceType() == StationAreaServiceTypeEnum.AFTER_SALE.value) {
                    result.setUsableAfterSale(false);//售后不可选标识
                }
            }
            if (!result.getUsablePreSale() && !result.getUsableAfterSale()) {
                //售前、售后都已被占用，则提示用户该区域服务权限都已被占用
                throw new YimaoException("该地区服务权限已都被占用！");
            }
        }
        return result;
    }

    @Override
    public Set<StationCompanyServiceAreaDTO> getAfterSaleServiceArea(Integer id) {

        return stationCompanyServiceAreaMapper.selectAfterServiceAreaByStationCompanyId(id);
    }

    /**
     * 服务站公司服务区域承包转让
     *
     * @param originalStationCompanyServiceAreaList 被承包服务站公司信息以及服务区域信息
     * @param stationCompanyId                      承包方服务站公司id
     * @param stationId                             承包方服务站门店id
     * @param engineerId                            承包方服务站门店安装工
     * @param type                                  转让标识（1-转让方转了所有售后服务区域；2-转让方仅转让部分售后服务区域）
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void serviceAreaContractMakeOver(List<StationCompanyServiceAreaDTO> originalStationCompanyServiceAreaList, Integer stationCompanyId, Integer stationId, Integer engineerId, Integer type) {
        //定义当前时间
        Date now = new Date();
        List<TransferAreaInfoDTO> transferAreaInfoDTOList = new ArrayList<>();
        Set<Integer> updateStationIds = new HashSet<>();//定义即将修改的转让方服务站门店id集合
        for (StationCompanyServiceAreaDTO originalStationCompanyServiceArea : originalStationCompanyServiceAreaList) {
            if (originalStationCompanyServiceArea.getServiceType() != StationAreaServiceTypeEnum.AFTER_SALE.value) {
                throw new BadRequestException("只能承包转让售后的服务权限！");
            }
            //被承包服务地区原属服务站公司id
            Integer originalStationCompanyId = originalStationCompanyServiceArea.getStationCompanyId();
            if (originalStationCompanyId == null) {
                throw new BadRequestException("被承包服务地区原属服务站公司id不能为空！");
            }

            Integer originalStationId = null;//定义转让方服务站门店id
            TransferOperationLogDTO transferOperationLog = new TransferOperationLogDTO();//定义服务区域承包转让操作日志
            StringBuffer describe = new StringBuffer(); //定义转让描述

            //查询该区域权限的实际信息
            StationCompanyServiceArea query = new StationCompanyServiceArea();
            query.setAreaId(originalStationCompanyServiceArea.getAreaId());
            query.setStationCompanyId(originalStationCompanyId);
            List<StationCompanyServiceArea> stationCompanyServiceAreaList = stationCompanyServiceAreaMapper.select(query);
            if (CollectionUtil.isEmpty(stationCompanyServiceAreaList)) {
                throw new BadRequestException("该服务站公司没有设置服务区域！");
            }

            //待删除的服务站公司对该地区的售后权限信息
            StationCompanyServiceArea awaitDeleteStationCompanyServiceAreaInfo = new StationCompanyServiceArea();
            Boolean flag = false;
            for (StationCompanyServiceArea stationCompanyServiceArea : stationCompanyServiceAreaList) {
                if (StationAreaServiceTypeEnum.haveAfterSale(stationCompanyServiceArea.getServiceType())) {
                    describe.append("将【" + stationCompanyServiceArea.getStationCompanyName() + "】");
                    //只要有售后权限，将标识改为true
                    flag = true;
                    awaitDeleteStationCompanyServiceAreaInfo.setId(stationCompanyServiceArea.getId());
                    awaitDeleteStationCompanyServiceAreaInfo.setServiceType(stationCompanyServiceArea.getServiceType());
                    break;
                }
            }
            if (!flag) {
                //该服务站公司没有该地区的售后服务权限
                throw new BadRequestException("该服务站公司没有该地区的售后服务权限！");
            }

            StationServiceArea queryStationServiceArea = new StationServiceArea();
            queryStationServiceArea.setAreaId(originalStationCompanyServiceArea.getAreaId());
            List<StationServiceArea> stationServiceAreaList = stationServiceAreaMapper.select(queryStationServiceArea);
            //待删除的服务站门店对该地区的售后权限信息
            StationServiceArea awaitDeleteStationServiceAreaInfo = null;
            if (CollectionUtil.isNotEmpty(stationServiceAreaList)) {
                for (StationServiceArea stationServiceArea : stationServiceAreaList) {
                    if (StationAreaServiceTypeEnum.haveAfterSale(stationServiceArea.getServiceType())) {
                        describe.append("--【" + stationServiceArea.getStationName() + "】");
                        originalStationId = stationServiceArea.getStationId();
                        updateStationIds.add(originalStationId);
                        awaitDeleteStationServiceAreaInfo = new StationServiceArea();
                        awaitDeleteStationServiceAreaInfo.setId(stationServiceArea.getId());
                        awaitDeleteStationServiceAreaInfo.setServiceType(stationServiceArea.getServiceType());
                        break;
                    }
                }
            }
            if (awaitDeleteStationServiceAreaInfo != null) {
                if (awaitDeleteStationServiceAreaInfo.getServiceType() == StationAreaServiceTypeEnum.AFTER_SALE.value) {
                    //仅有售后权限，直接删除
                    int count = stationServiceAreaMapper.deleteByPrimaryKey(awaitDeleteStationServiceAreaInfo.getId());
                    if (count != 1) {
                        throw new YimaoException("服务站门店删除该地区售后服务权限失败！服务区域id：" + originalStationCompanyServiceArea.getAreaId());
                    }
                } else if (awaitDeleteStationServiceAreaInfo.getServiceType() == StationAreaServiceTypeEnum.ALL.value) {
                    //将该服务站门店对该地区的售后权限删除，保留其售前的权限
                    StationServiceArea update = new StationServiceArea();
                    update.setId(awaitDeleteStationServiceAreaInfo.getId());
                    update.setServiceType(StationAreaServiceTypeEnum.PRE_SALE.value);
                    update.setUpdater(userCache.getCurrentAdminRealName());
                    update.setUpdateTime(now);
                    int count = stationServiceAreaMapper.updateByPrimaryKeySelective(update);
                    if (count != 1) {
                        throw new YimaoException("服务站门店删除该地区售后服务权限失败！服务区域id：" + originalStationCompanyServiceArea.getAreaId());
                    }
                }
            }

            //删除该公司对该地区的售后服务权限
            if (awaitDeleteStationCompanyServiceAreaInfo.getServiceType() == StationAreaServiceTypeEnum.AFTER_SALE.value) {
                //直接删除
                int count = stationCompanyServiceAreaMapper.deleteByPrimaryKey(awaitDeleteStationCompanyServiceAreaInfo.getId());
                if (count != 1) {
                    throw new YimaoException("服务站公司删除该地区售后服务权限失败！服务区域id：" + originalStationCompanyServiceArea.getAreaId());
                }
            } else if (awaitDeleteStationCompanyServiceAreaInfo.getServiceType() == StationAreaServiceTypeEnum.ALL.value) {
                //将该服务站公司对该地区的售后权限删除，保留其售前的权限
                StationCompanyServiceArea update = new StationCompanyServiceArea();
                update.setId(awaitDeleteStationCompanyServiceAreaInfo.getId());
                update.setServiceType(StationAreaServiceTypeEnum.PRE_SALE.value);
                update.setUpdater(userCache.getCurrentAdminRealName());
                update.setUpdateTime(now);
                int count = stationCompanyServiceAreaMapper.updateByPrimaryKeySelective(update);
                if (count != 1) {
                    throw new YimaoException("服务站公司删除该地区售后服务权限失败！服务区域id：" + originalStationCompanyServiceArea.getAreaId());
                }
            }

            //获取承包方服务站公司信息
            StationCompany stationCompany = stationCompanyMapper.selectByPrimaryKey(stationCompanyId);
            if (stationCompany == null) {
                throw new BadRequestException("承包方服务站公司不存在！");
            }

            query = new StationCompanyServiceArea();
            query.setStationCompanyId(stationCompanyId);
            query.setAreaId(originalStationCompanyServiceArea.getAreaId());
            //一个服务站公司对应一个服务区域只会有一条数据（0-售前+售后；1-售前；2-售后），如果存在的话这边只会存在售前权限
            StationCompanyServiceArea existStationCompanyServiceArea = stationCompanyServiceAreaMapper.selectOne(query);
            if (existStationCompanyServiceArea == null) {
                //不存在则说明承包方服务站公司没有该区域的售前权限，直接新增售后权限
                StationCompanyServiceArea insertStationCompanyServiceArea = new StationCompanyServiceArea();
                insertStationCompanyServiceArea.setAreaId(originalStationCompanyServiceArea.getAreaId());
                insertStationCompanyServiceArea.setServiceType(StationAreaServiceTypeEnum.AFTER_SALE.value);
                insertStationCompanyServiceArea.setStationCompanyId(stationCompanyId);
                insertStationCompanyServiceArea.setProvince(originalStationCompanyServiceArea.getProvince());
                insertStationCompanyServiceArea.setCity(originalStationCompanyServiceArea.getCity());
                insertStationCompanyServiceArea.setRegion(originalStationCompanyServiceArea.getRegion());
                insertStationCompanyServiceArea.setStationCompanyName(stationCompany.getName());
                insertStationCompanyServiceArea.setCreator(userCache.getCurrentAdminRealName());
                insertStationCompanyServiceArea.setCreateTime(now);
                int count = stationCompanyServiceAreaMapper.insertSelective(insertStationCompanyServiceArea);
                if (count != 1) {
                    throw new YimaoException("承包方服务站公司新增该地区售后权限失败！服务区域id：" + originalStationCompanyServiceArea.getAreaId());
                }
            } else {
                if (StationAreaServiceTypeEnum.haveAfterSale(existStationCompanyServiceArea.getServiceType())) {
                    throw new YimaoException("承包方服务站公司已存在该区域的售后权限，传参有误！");
                }
                //承包方服务站公司已拥有该区域的售前权限，则将承包方服务站公司对该区域的服务类型更变为0（售前+售后）
                existStationCompanyServiceArea.setServiceType(StationAreaServiceTypeEnum.ALL.value);
                existStationCompanyServiceArea.setUpdater(userCache.getCurrentAdminRealName());
                existStationCompanyServiceArea.setUpdateTime(now);
                int count = stationCompanyServiceAreaMapper.updateByPrimaryKeySelective(existStationCompanyServiceArea);
                if (count != 1) {
                    throw new YimaoException("承包方服务站公司新增该地区售后权限失败！服务区域id：" + originalStationCompanyServiceArea.getAreaId());
                }
            }

            //获取承包方服务站门店信息
            Station station = stationMapper.selectByPrimaryKey(stationId);
            if (station == null) {
                throw new BadRequestException("承包方服务站门店不存在");
            }
            queryStationServiceArea = new StationServiceArea();
            queryStationServiceArea.setStationId(stationId);
            queryStationServiceArea.setAreaId(originalStationCompanyServiceArea.getAreaId());
            //一个服务站门店对应一个服务区域只会有一条数据（0-售前+售后；1-售前；2-售后），如果存在的话这边只会存在售前权限
            StationServiceArea existStationServiceArea = stationServiceAreaMapper.selectOne(queryStationServiceArea);
            if (existStationServiceArea == null) {
                //不存在则说明承包方服务站门店没有该区域的售前权限，直接新增售后权限
                StationServiceArea insertStationServiceArea = new StationServiceArea();
                insertStationServiceArea.setAreaId(originalStationCompanyServiceArea.getAreaId());
                insertStationServiceArea.setServiceType(StationAreaServiceTypeEnum.AFTER_SALE.value);
                insertStationServiceArea.setStationId(stationId);
                insertStationServiceArea.setProvince(originalStationCompanyServiceArea.getProvince());
                insertStationServiceArea.setCity(originalStationCompanyServiceArea.getCity());
                insertStationServiceArea.setRegion(originalStationCompanyServiceArea.getRegion());
                insertStationServiceArea.setStationName(station.getName());
                insertStationServiceArea.setCreator(userCache.getCurrentAdminRealName());
                insertStationServiceArea.setCreateTime(now);
                int count = stationServiceAreaMapper.insertSelective(insertStationServiceArea);
                if (count != 1) {
                    throw new YimaoException("承包方服务站门店新增该地区售后权限失败！服务区域id：" + originalStationCompanyServiceArea.getAreaId());
                }
            } else {
                if (StationAreaServiceTypeEnum.haveAfterSale(existStationServiceArea.getServiceType())) {
                    throw new YimaoException("承包方服务站门店已存在该区域的售后权限，传参有误！");
                }
                //承包方服务站门店已拥有该区域的售前权限，则将承包方服务站门店对该区域的服务类型更变为0（售前+售后）
                existStationServiceArea.setServiceType(StationAreaServiceTypeEnum.ALL.value);
                existStationServiceArea.setUpdater(userCache.getCurrentAdminRealName());
                existStationServiceArea.setUpdateTime(now);
                int count = stationServiceAreaMapper.updateByPrimaryKeySelective(existStationServiceArea);
                if (count != 1) {
                    throw new YimaoException("承包方服务站门店新增该地区售后权限失败！服务区域id：" + originalStationCompanyServiceArea.getAreaId());
                }
            }

            TransferAreaInfoDTO transferAreaInfoDTO = new TransferAreaInfoDTO();
            if (originalStationId != null) {
                //若该属性不为空，则之上逻辑删除了该服务站对当前区域的售后管理权限（若为空则仅将服务站公司对该区域的售后服务权限转让给承包方服务站门店，不需要做数据转让操作）
                transferAreaInfoDTO.setAreaId(originalStationCompanyServiceArea.getAreaId());
                transferAreaInfoDTO.setProvince(originalStationCompanyServiceArea.getProvince());
                transferAreaInfoDTO.setCity(originalStationCompanyServiceArea.getCity());
                transferAreaInfoDTO.setRegion(originalStationCompanyServiceArea.getRegion());
                transferAreaInfoDTO.setStationId(stationId);
                if (type == 1) {
                    //转让方转让了所有售后服务区域
                    transferAreaInfoDTOList.add(transferAreaInfoDTO);
                } else if (type == 2) {
                    //仅转让部分售后服务区域，将该服务区域的工单以及设备转让给指定安装工，保留原服务站安装工
                    if (engineerId == null) {
                        throw new BadRequestException("请选择服务人员。");
                    }
                    transferAreaInfoDTO.setEngineerId(engineerId);
                    transferAreaInfoDTOList.add(transferAreaInfoDTO);
                }

                // 查询转让方服务站门店是否还有服务区域，若无则使服务站门店下线
                queryStationServiceArea = new StationServiceArea();
                queryStationServiceArea.setStationId(originalStationId);
                stationServiceAreaList = stationServiceAreaMapper.select(queryStationServiceArea);
                if (CollectionUtil.isEmpty(stationServiceAreaList)) {
                    //无服务区域，使该服务站门店下线
                    Station update = new Station();
                    update.setId(originalStationId);
                    update.setOnline(0); //下线
                    int count = stationMapper.updateByPrimaryKeySelective(update);
                    if (count != 1) {
                        throw new YimaoException("服务站门店下线失败！");
                    }
                }
            }
            // 查询转让方服务站公司是否还有服务区域，若无则使服务站门店下线
            query = new StationCompanyServiceArea();
            query.setStationCompanyId(originalStationCompanyId);
            stationCompanyServiceAreaList = stationCompanyServiceAreaMapper.select(query);
            if (CollectionUtil.isEmpty(stationCompanyServiceAreaList)) {
                //无服务区域，使该服务站公司下线
                StationCompany update = new StationCompany();
                update.setId(originalStationCompanyId);
                update.setOnline(false); //下线
                int count = stationCompanyMapper.updateByPrimaryKeySelective(update);
                if (count != 1) {
                    throw new YimaoException("服务站门店下线失败！");
                }
            }

            // 记录操作日志
            describe.append("对【" + originalStationCompanyServiceArea.getProvince() + "/" +
                    originalStationCompanyServiceArea.getCity() +
                    "/" + originalStationCompanyServiceArea.getRegion() + "】的【售后】服务权限承包转让给" + "【" +
                    stationCompany.getName() + "】--【" + station.getName() + "】");
            if (type == 2 && engineerId != null) {
                describe.append("，承包方安装工的id为【" + engineerId + "】");
            }
            transferOperationLog.setDescription(describe.toString());
            transferOperationLog.setTransferorId(originalStationCompanyId);
            transferOperationLog.setReceiverId(stationCompanyId);
            transferOperationLog.setOperateType(TransferOperateTypeEnum.AFTER_SALE_TRANSFER.value);
            transferOperationLog.setOperator(userCache.getCurrentAdminRealName());
            transferOperationLog.setCreateTime(now);
            amqpTemplate.convertAndSend(RabbitConstant.TRANSFER_OPERATION_LOG, transferOperationLog);
        }

        //避免以上循环报错产生的分布式事务无法回滚问题，数据的转让在区域转让完成之后进行
        if (CollectionUtil.isNotEmpty(transferAreaInfoDTOList)) {
            // 转让方安装工账号、工单、设备转让给承包方服务站门店
            try {
                //转让当前区域下未完成和退单中的工单和订单所给指定安装工
                orderFeign.transferOrderDevice(transferAreaInfoDTOList);
            } catch (Exception e) {
                throw new YimaoException("转让当前区域下的设备以及未完成和退单中的工单和订单异常！");
            }
            //安装工服务站信息、服务区域变更
            for (TransferAreaInfoDTO transferAreaInfoDTO : transferAreaInfoDTOList) {
                try {
                    if (transferAreaInfoDTO.getEngineerId() != null) {
                        //仅转让部分售后服务区域，将该服务区域的工单以及设备转让给指定安装工，保留原服务站安装工
                        //将原服务于该地区的安装工对该区域的服务权限删除，给指定安装工新增对该地区的服务权限
                        userFeign.engineerUpdateServiceArea(transferAreaInfoDTO);
                    } else {
                        //转让方转让了所有售后服务区域
                        // 转让所有安装工程师给指定服务站
                        userFeign.transferEngineerToNewStationByServiceArea(transferAreaInfoDTO);
                    }
                } catch (Exception e) {
                    log.error("安装工服务站信息或服务区域变更失败! 异常：{}", e.getMessage());
                    // 发送邮箱提示安装工服务站信息、服务区域变更失败
                    String subject = "安装工服务站信息或服务区域变更失败";
                    String content = "安装工服务站信息或服务区域变更失败，更变相关信息传参：" + JSON.toJSONString(transferAreaInfoDTO);
                    mailSender.send(null, subject, content);
                }
            }
        }

        // -----------------------转让完成后更新承包方服务站门店下的安装工服务区域----------------------
        //查询承包方服务站门店的售后服务区域
        List<StationServiceAreaDTO> afterServiceAreas = stationServiceAreaMapper.getAfterServiceAreaByStationId(stationId);
        if (CollectionUtil.isNotEmpty(afterServiceAreas)) {
            //修改服务站门店的安装工的服务区域
            UpdateEngineerServiceAreaDataInfo updateEngineerServiceAreaDataInfo = new UpdateEngineerServiceAreaDataInfo();
            updateEngineerServiceAreaDataInfo.setServiceAreaList(afterServiceAreas);
            updateEngineerServiceAreaDataInfo.setStationId(stationId);
            try {
                userFeign.updateEngineerServiceArea(updateEngineerServiceAreaDataInfo);
            } catch (Exception e) {
                log.error("【远程调用失败】服务区域承包转让后同步承包方服务站门店的安装工的服务区域失败! 异常：{}", e.getMessage());
                // 发送邮箱提示安装工服务区域同步失败
                String subject = "【远程调用失败】服务区域承包转让后同步承包方服务站门店的安装工的服务区域失败";
                String content = "【远程调用失败】服务区域承包转让后同步承包方服务站门店的安装工的服务区域失败，传参：" + JSON.toJSONString(updateEngineerServiceAreaDataInfo);
                mailSender.send(null, subject, content);
            }
        }

        // -----------------------转让完成后修改站务系统绑定该服务站区域的用户--------------------------
        if (CollectionUtil.isNotEmpty(updateStationIds)) {
            for (Integer updateStationId : updateStationIds) {
                //查询当前服务站门店转让服务区域之后的服务区域
                StationServiceArea query = new StationServiceArea();
                query.setStationId(updateStationId);
                List<StationServiceArea> stationServiceAreas = stationServiceAreaMapper.select(query);
                try {
                    //修改站务系统绑定该服务站区域的用户
                    stationFeign.stationAreaChange(updateStationId, BeanHelper.copyWithCollection(stationServiceAreas, StationServiceAreaDTO.class));
                } catch (Exception e) {
                    log.error("【远程调用失败】服务区域承包转让后同步承包方服务站门店的安装工的服务区域失败！异常：{}", e.getMessage());
                    // 发送邮箱提示服务区域承包转让后同步承包方服务站门店的安装工的服务区域失败
                    String subject = "【远程调用失败】服务区域承包转让后同步承包方服务站门店的安装工的服务区域失败";
                    String content = "【远程调用失败】服务区域承包转让后同步承包方服务站门店的安装工的服务区域失败，传参：stationId：" + updateStationId + "stationServiceAreas:" + JSON.toJSONString(stationServiceAreas);
                    mailSender.send(null, subject, content);
                }
            }
        }
    }

    /**
     * 根据省市区查询售前售后门店集合
     */
    public List<StationCompanyDTO> getStationCompanyListByPCR(String province, String city, String region) {

        return stationCompanyMapper.getStationCompanyListByPCR(province, city, region);
    }

}
