package com.yimao.cloud.water.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.AreaTypeEnum;
import com.yimao.cloud.base.enums.ConnectionTypeEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.ExcelUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.framework.aop.OperationType;
import com.yimao.cloud.framework.aop.annotation.EnableOperationLog;
import com.yimao.cloud.framework.aop.parser.DefaultContentParse;
import com.yimao.cloud.pojo.dto.out.DeviceQuery;
import com.yimao.cloud.pojo.dto.system.AreaDTO;
import com.yimao.cloud.pojo.dto.water.ConditionalAdvertisingAreaDTO;
import com.yimao.cloud.pojo.dto.water.ConditionalAdvertisingDTO;
import com.yimao.cloud.pojo.dto.water.FilterAdvertisingDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.enums.DeviceGroupEnum;
import com.yimao.cloud.water.enums.DeviceTypeEnum;
import com.yimao.cloud.water.enums.PlatformEnum;
import com.yimao.cloud.water.enums.PlayStatusEnum;
import com.yimao.cloud.water.enums.ProductModelEnum;
import com.yimao.cloud.water.enums.ScreenLocationEnum;
import com.yimao.cloud.water.handler.SystemFeignHandler;
import com.yimao.cloud.water.mapper.ConditionalAdvertisingAreaMapper;
import com.yimao.cloud.water.mapper.ConditionalAdvertisingMapper;
import com.yimao.cloud.water.mapper.ConditionalAdvertisingModelMapper;
import com.yimao.cloud.water.mapper.MaterialsMapper;
import com.yimao.cloud.water.po.ConditionalAdvertising;
import com.yimao.cloud.water.po.ConditionalAdvertisingArea;
import com.yimao.cloud.water.po.ConditionalAdvertisingModel;
import com.yimao.cloud.water.po.Materials;
import com.yimao.cloud.water.service.ConditionalAdvertisingService;
import com.yimao.cloud.water.service.ServiceStationWaterDeviceService;
import com.yimao.cloud.water.service.WaterDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 描述：广告条件投放。
 *
 * @Author Zhang Bo
 * @Date 2019/1/30 14:50
 * @Version 1.0
 */
@Service
@Slf4j
public class ConditionalAdvertisingServiceImpl implements ConditionalAdvertisingService {

    @Resource
    private ConditionalAdvertisingMapper conditionalAdvertisingMapper;
    @Resource
    private ConditionalAdvertisingAreaMapper conditionalAdvertisingAreaMapper;
    @Resource
    private ConditionalAdvertisingModelMapper conditionalAdvertisingModelMapper;
    @Resource
    private SystemFeignHandler systemFeignHandler;
    @Resource
    private UserCache userCache;
    @Resource
    private MaterialsMapper materialsMapper;
    @Resource
    private ServiceStationWaterDeviceService serviceStationWaterDeviceService;
    @Resource
    private WaterDeviceService waterDeviceService;

    /**
     * 创建广告条件投放配置
     *
     * @param advertising 广告条件投放配置
     */
    @EnableOperationLog(
            name = "创建广告条件投放配置",
            type = OperationType.SAVE,
            daoClass = ConditionalAdvertisingMapper.class,
            parseClass = DefaultContentParse.class,
            fields = {"ownAdslotId", "platform"},
            index = 0,
            queue = RabbitConstant.WATER_OPERATION_LOG
    )
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void save(ConditionalAdvertising advertising) {
        //校验入参
        this.checkParameter(advertising, 1);
        advertising.setEffective(true);
        advertising.setForbidden(false);
        advertising.setDeleted(false);
        advertising.setCreator(userCache.getCurrentAdminRealName());
        advertising.setCreateTime(new Date());
        //保存之前 校验策略是否重复
        Set<AreaDTO> areaSet = new HashSet<>();
        Set<String> modelSet = new HashSet<>();
        Set<FilterAdvertisingDTO> aBoolean = checkAdvertisingExists(advertising, areaSet, modelSet);
        if (CollectionUtil.isNotEmpty(aBoolean)) {
            //校验不通过
            throw new BadRequestException("本次配置的投放策略和现有配置存在重复部分，请重新筛选。");
        }
        //保存广告投放策略配置
        conditionalAdvertisingMapper.insertSelective(advertising);

        //保存广告投放策略配置关联区域信息
        if (areaSet.size() > 0) {
            List<ConditionalAdvertisingArea> caaList = new ArrayList<>();
            for (AreaDTO dto : areaSet) {
                ConditionalAdvertisingArea aa = new ConditionalAdvertisingArea();
                aa.setAdvertisingId(advertising.getId());
                aa.setAreaId(dto.getId());
                aa.setAreaLevel(dto.getLevel());
                aa.setAreaName(dto.getName());
                aa.setPid(dto.getPid());
                caaList.add(aa);
            }
            //保存广告投放策略配置关联区域信息
            conditionalAdvertisingAreaMapper.batchInsert(caaList);
        }
        //保存广告投放策略配置关联水机型号信息
        if (modelSet.size() > 0) {
            List<ConditionalAdvertisingModel> camList = new ArrayList<>();
            for (String model : modelSet) {
                ConditionalAdvertisingModel am = new ConditionalAdvertisingModel();
                am.setAdvertisingId(advertising.getId());
                am.setModel(model);
                camList.add(am);
            }
            conditionalAdvertisingModelMapper.batchInsert(camList);
        }

    }

    /**
     * 条件投放---筛选水机
     *
     * @param dtoAd
     * @return
     */
    @Override
    public Map<String, Object> filter(ConditionalAdvertisingDTO dtoAd) {
        //校验入参
        ConditionalAdvertising advertising = new ConditionalAdvertising(dtoAd);
        this.checkParameter(advertising, 2);
        advertising.setEffective(true);
        advertising.setForbidden(false);
        advertising.setDeleted(false);
        // 校验策略是否重复
        List<Map<String, String>> areaList = new ArrayList<>();
        Set<String> modelSet = new HashSet<>();
        Set<FilterAdvertisingDTO> list = check(advertising, areaList, modelSet);
        Map<String, Object> map = new HashMap<>();
        if (CollectionUtil.isNotEmpty(list)) {
            //遍历，查出具体重复的数据
            Set<Map<String, Object>> setTime = new HashSet();
            Map<String, Object> mapTime = new HashMap<>();
            Set<String> setModel = new HashSet();
            Set<String> setArea = new HashSet();
            Set<Integer> setConnectionType = new HashSet();
            Set<Integer> setAfterConnectionType = new HashSet();
            Set<Integer> setDeviceGroup = new HashSet();

            for (FilterAdvertisingDTO dto : list) {
                mapTime.put("effectiveBeginTime", dto.getEffectiveBeginTime());
                mapTime.put("effectiveEndTime", dto.getEffectiveEndTime());
                setTime.add(mapTime);
                setModel.add(dto.getModel());
                if (StringUtil.isEmpty(dto.getAreaName())) {
                    setArea.add("全国");
                } else {
                    setArea.add(dto.getAreaName());
                }
                setConnectionType.add(dto.getConnectionType());
                setAfterConnectionType.add(dto.getAdvertisingType());
                setDeviceGroup.add(dto.getDeviceGroup());
            }
            map.put("deviceGroup", setDeviceGroup);
            map.put("time", setTime);
            map.put("model", setModel);
            map.put("area", setArea);
            map.put("connectionType", setConnectionType);
            map.put("afterConnectionType", setAfterConnectionType);
            //存在重复策略，筛选结果为0
            map.put("count", 0);
        } else {
            //不存在重复策略，查询可用的设备
            List<String> modelList = new ArrayList<>();
            if (StringUtil.isNotEmpty(advertising.getModels())) {
                modelList = Arrays.asList(advertising.getModels().split(","));
            } else {
                modelList.add(ProductModelEnum.MODEL_1601T.value);
                modelList.add(ProductModelEnum.MODEL_1602T.value);
                modelList.add(ProductModelEnum.MODEL_1603T.value);
                modelList.add(ProductModelEnum.MODEL_1601L.value);
            }
            if (Objects.nonNull(dtoAd.getDeviceGroup())) {
                if (Objects.equals(dtoAd.getDeviceGroup(), DeviceTypeEnum.USER.value)) {
                    //  用户组数据
                    DeviceQuery query = new DeviceQuery();
                    if (Objects.nonNull(advertising.getConnectionType())) {
                        query.setConnectionType(advertising.getConnectionType());
                    }
                    query.setModels(modelList);
                    //设置省市区
                    query.setPcr(areaList);
                    //位置标签
                    if (StringUtil.isNotEmpty(dtoAd.getLocationTab())) {
                        query.setLocation(dtoAd.getLocationTab());
                    }
                    // Long count = outFeign.countDevice(query);
                    int count = waterDeviceService.countDevice(query);
                    map.put("count", count);
                } else if (Objects.equals(dtoAd.getDeviceGroup(), DeviceTypeEnum.STATION.value)) {
                    //服务站设备数据
                    int count = serviceStationWaterDeviceService.count(areaList, modelList, advertising.getConnectionType());
                    map.put("count", count);
                }
            } else {
                //====当选择全部数据的时候，求两个数据库设备的总和===
                //  用户组数据
                DeviceQuery query = new DeviceQuery();
                if (Objects.nonNull(advertising.getConnectionType())) {
                    query.setConnectionType(advertising.getConnectionType());
                }
                query.setModels(modelList);
                //设置省市区
                query.setPcr(areaList);
                //位置标签
                if (StringUtil.isNotEmpty(dtoAd.getLocationTab())) {
                    query.setLocation(dtoAd.getLocationTab());
                }
                // Long userCount = outFeign.countDevice(query);
                int userCount = waterDeviceService.countDevice(query);

                //服务站设备数据
                int serviceCount = serviceStationWaterDeviceService.count(areaList, modelList, advertising.getConnectionType());

                map.put("count", userCount + serviceCount);
            }
        }

        return map;
    }

    /**
     * 查询是否有已经生效或者将要生效的配置
     *
     * @param platform
     * @param ownAdslotId
     * @param adslotId
     * @return
     */
    @Override
    public Boolean judgeAdvertsingExists(Integer platform, String ownAdslotId, String adslotId) {
        Boolean aBoolean = conditionalAdvertisingMapper.selectAdvertsing(platform, ownAdslotId, adslotId);
        return aBoolean;
    }

    /**
     * 导出自有投放列表数据
     *
     * @param platform
     * @param screenLocation
     * @param startTime
     * @param endTime
     * @param name
     * @param response
     */
    @Override
    public void exportHave(Integer platform, Integer screenLocation, Date startTime, Date endTime, String name, String type, HttpServletResponse response) {
        Date effectiveTime = null;
        Date invalidTime = null;
        String header = "";
        if (type.equals("1")) {
            //查询未超过时限的投放策略
            effectiveTime = new Date();
            header = "自有投放列表";
        } else if (type.equals("3")) {
            //查询已经超过时限的投放策略
            invalidTime = new Date();
            header = "自有投放记录";
        }
        Page<ConditionalAdvertisingDTO> page = conditionalAdvertisingMapper.pageOwnList(screenLocation, startTime, endTime, effectiveTime, invalidTime, name);
        if (CollectionUtil.isEmpty(page)) {
            throw new BadRequestException("没有查到投放数据！");
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        long time = new Date().getTime();
        for (ConditionalAdvertisingDTO dto : page) {
            //投放位置翻译
            if (Objects.nonNull(dto.getScreenLocation())) {
                if (Objects.equals(dto.getScreenLocation(), ScreenLocationEnum.ONE.value)) {
                    dto.setScreenLocationName(ScreenLocationEnum.ONE.name);
                } else if (Objects.equals(dto.getScreenLocation(), ScreenLocationEnum.TWO.value)) {
                    dto.setScreenLocationName(ScreenLocationEnum.TWO.name);
                }
            }
            String beginTimeFormat = format.format(dto.getEffectiveBeginTime());
            String endTimeFormat = format.format(dto.getEffectiveEndTime());
            String createTimeFormat = simpleDateFormat.format(dto.getCreateTime());
            dto.setBeginTimeFormat(beginTimeFormat);
            dto.setEndTimeFormat(endTimeFormat);
            dto.setCreateTimeFormat(createTimeFormat);
            //对播放状态进行处理
            if (type.equals("1")) {
                if (dto.getEffectiveBeginTime().getTime() > time) {
                    dto.setStatus(PlayStatusEnum.ONE.name);
                } else if (dto.getEffectiveBeginTime().getTime() <= time && dto.getEffectiveEndTime().getTime() >= time) {
                    dto.setStatus(PlayStatusEnum.TWO.name);
                }
            } else if (type.equals("3")) {
                if (dto.getForbidden()) {
                    dto.setStatus(PlayStatusEnum.FOUR.name);
                } else {
                    dto.setStatus(PlayStatusEnum.THREE.name);
                }
            }
        }

        String[] beanPropertys = new String[]{"id", "materialsId", "screenLocationName", "materialsName", "advertisers", "status",
                "deviceCount", "ownAdslotId", "beginTimeFormat", "endTimeFormat", "createTimeFormat"};
        String[] titles = new String[]{"投放编号", "物料标识", "投放位置", "文件名", "广告主", "状态", "投放设备数量", "广告位", "播放开始时间", "播放结束时间", "投放时间"};
        ExcelUtil.exportSXSSF(page, header, titles.length - 1, titles, beanPropertys, response);

    }

    /**
     * 根据投放ID查询投放详情
     *
     * @param id
     * @return
     */
    @Override
    public ConditionalAdvertisingDTO selectAdById(Integer id) {
        ConditionalAdvertising conditionalAdvertising = conditionalAdvertisingMapper.selectByPrimaryKey(id);
        ConditionalAdvertisingDTO dto =new ConditionalAdvertisingDTO();
        if (Objects.nonNull(conditionalAdvertising)){
            conditionalAdvertising.convert(dto);
        }
        return dto;
    }


    /**
     * 校验条件投放策略是否存在重复的具体数据
     *
     * @param ad
     * @param areaList
     * @param modelSet
     * @return
     */
    public Set<FilterAdvertisingDTO> check(ConditionalAdvertising ad, List<Map<String, String>> areaList, Set<String> modelSet) {
        Set<Integer> areaIdSet = null;
        if (StringUtil.isNotBlank(ad.getAreas())) {
            String[] areaIds = ad.getAreas().split(",");
            areaIdSet = new HashSet<>();
            for (String s : areaIds) {
                Map<String, String> map = new HashMap<>();
                Integer areaId = Integer.parseInt(s);
                AreaDTO area = systemFeignHandler.getAreaById(areaId);
                if (Objects.nonNull(area)) {
                    if (area.getLevel() == AreaTypeEnum.PROVINCE.value) {
                        areaIdSet.add(area.getId());
                        map.put("province", area.getName());
                    } else if (area.getLevel() == AreaTypeEnum.CITY.value) {
                        AreaDTO province = systemFeignHandler.getAreaById(area.getPid());
                        areaIdSet.add(area.getId());
                        areaIdSet.add(province.getId());
                        map.put("province", province.getName());
                        map.put("city", area.getName());
                    } else if (area.getLevel() == AreaTypeEnum.REGION.value) {
                        AreaDTO city = systemFeignHandler.getAreaById(area.getPid());
                        AreaDTO province = systemFeignHandler.getAreaById(city.getPid());
                        areaIdSet.add(area.getId());
                        areaIdSet.add(city.getId());
                        areaIdSet.add(province.getId());
                        map.put("province", province.getName());
                        map.put("city", city.getName());
                        map.put("region", area.getName());
                    }
                    areaList.add(map);
                }
            }
        }
        if (StringUtil.isNotBlank(ad.getModels())) {
            String[] models = ad.getModels().split(",");
            for (String model : models) {
                modelSet.add(model);
            }
        }
        Set<FilterAdvertisingDTO> list = conditionalAdvertisingMapper.checkAdvertisingExists(areaIdSet, modelSet,
                ad.getPlatform(), ad.getOwnAdslotId(), ad.getAdslotId(), ad.getScreenLocation(),
                ad.getAdvertisingType(), ad.getConnectionType(), ad.getAfterConnectionType(), ad.getEffectiveBeginTime(), ad.getEffectiveEndTime(), ad.getId(), ad.getLocationTab(), ad.getDeviceGroup());
        return list;
    }


    /**
     * 分页查询第三方投放列表
     *
     * @param platform 第三方广告平台
     * @param pageNum  页码
     * @param pageSize 页数
     * @return
     */
    @Override
    public PageVO<ConditionalAdvertisingDTO> page(Integer platform, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<ConditionalAdvertisingDTO> page = conditionalAdvertisingMapper.pageQueryList(platform);
        for (ConditionalAdvertisingDTO dto : page) {
            List<ConditionalAdvertisingAreaDTO> areas = conditionalAdvertisingAreaMapper.selectListByCondition(dto.getId());
            dto.setAreaList(areas);
        }
        return new PageVO<>(pageNum, page);
    }

    /**
     * 查询自有投放列表
     *
     * @param screenLocation
     * @param startTime
     * @param endTime
     * @param name
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageVO<ConditionalAdvertisingDTO> pageOwnList(Integer screenLocation, Date startTime, Date endTime, String name, String type, Integer pageNum, Integer pageSize) {
        Date effectiveTime = null;
        Date invalidTime = null;
        if (type.equals("1")) {
            //查询未超过时限的投放策略
            effectiveTime = new Date();
        } else if (type.equals("3")) {
            //查询已经超过时限的投放策略
            invalidTime = new Date();
        }
        PageHelper.startPage(pageNum, pageSize);
        Page<ConditionalAdvertisingDTO> page = conditionalAdvertisingMapper.pageOwnList(screenLocation, startTime, endTime, effectiveTime, invalidTime, name);
        long time = new Date().getTime();
        for (ConditionalAdvertisingDTO dto : page) {
            //对播放状态进行处理
            if (type.equals("1")) {
                if (dto.getEffectiveBeginTime().getTime() > time) {
                    dto.setStatus(PlayStatusEnum.ONE.name);
                } else if (dto.getEffectiveBeginTime().getTime() <= time && dto.getEffectiveEndTime().getTime() >= time) {
                    dto.setStatus(PlayStatusEnum.TWO.name);
                }
            } else if (type.equals("3")) {
                if (dto.getForbidden()) {
                    dto.setStatus(PlayStatusEnum.FOUR.name);
                } else {
                    dto.setStatus(PlayStatusEnum.THREE.name);
                }
            }
            //封装投放配置对应的区域信息
            List<ConditionalAdvertisingAreaDTO> areas = conditionalAdvertisingAreaMapper.selectListByCondition(dto.getId());
            dto.setAreaList(areas);
        }
        return new PageVO<>(pageNum, page);
    }

    /**
     * 更新投放配置
     *
     * @param advertising
     */
    @EnableOperationLog(
            name = "更新条件投放配置",
            type = OperationType.UPDATE,
            daoClass = ConditionalAdvertisingMapper.class,
            parseClass = DefaultContentParse.class,
            index = 0,
            queue = RabbitConstant.WATER_OPERATION_LOG
    )
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void update(ConditionalAdvertising advertising) {
        this.checkParameter(advertising, 1);
        if (Objects.isNull(advertising.getId())) {
            throw new BadRequestException("投放配置ID不能为空。");
        }
        //保存之前 校验策略是否重复
        Set<AreaDTO> areaSet = new HashSet<>();
        Set<String> modelSet = new HashSet<>();
        Set<FilterAdvertisingDTO> aBoolean = checkAdvertisingExists(advertising, areaSet, modelSet);
        if (CollectionUtil.isNotEmpty(aBoolean)) {
            //校验不通过
            throw new BadRequestException("本次配置的投放策略和现有配置存在重复部分，请重新筛选。");
        }
        advertising.setUpdateTime(new Date());
        advertising.setUpdater(userCache.getCurrentAdminRealName());
        conditionalAdvertisingMapper.update(advertising);

        //保存广告投放策略配置关联区域信息
        if (advertising.getAreas() == null || areaSet.size() > 0) {
            // 删除关联区域信息
            Example example = new Example(ConditionalAdvertisingArea.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("advertisingId", advertising.getId());
            conditionalAdvertisingAreaMapper.deleteByExample(example);
            List<ConditionalAdvertisingArea> caaList = new ArrayList<>();
            for (AreaDTO dto : areaSet) {
                ConditionalAdvertisingArea aa = new ConditionalAdvertisingArea();
                aa.setAdvertisingId(advertising.getId());
                aa.setAreaId(dto.getId());
                aa.setAreaLevel(dto.getLevel());
                aa.setAreaName(dto.getName());
                aa.setPid(dto.getPid());
                caaList.add(aa);
            }
            if (CollectionUtil.isNotEmpty(caaList)) {
                //保存广告投放策略配置关联区域信息
                conditionalAdvertisingAreaMapper.batchInsert(caaList);
            }
        }
        //保存广告投放策略配置关联水机型号信息
        if (advertising.getModels() == null || modelSet.size() > 0) {
            // 删除关联型号信息
            Example example = new Example(ConditionalAdvertisingModel.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("advertisingId", advertising.getId());
            conditionalAdvertisingModelMapper.deleteByExample(example);
            // 删除关联水机型号信息
            List<ConditionalAdvertisingModel> camList = new ArrayList<>();
            for (String model : modelSet) {
                ConditionalAdvertisingModel am = new ConditionalAdvertisingModel();
                am.setAdvertisingId(advertising.getId());
                am.setModel(model);
                camList.add(am);
            }
            if (CollectionUtil.isNotEmpty(camList)) {
                conditionalAdvertisingModelMapper.batchInsert(camList);
            }
        }

    }

    /**
     * 删除投放配置
     *
     * @param advertising 配置ID
     */
    @EnableOperationLog(
            name = "删除条件投放配置",
            type = OperationType.DELETE,
            daoClass = ConditionalAdvertisingMapper.class,
            parseClass = DefaultContentParse.class,
            index = 0,
            queue = RabbitConstant.WATER_OPERATION_LOG
    )
    @Override
    public void deleteConditionalAdvertising(ConditionalAdvertising advertising) {
        if (Objects.isNull(advertising.getId())) {
            throw new BadRequestException("投放配置ID不能为空。");
        }
        advertising.setDeleted(true);
        advertising.setUpdateTime(new Date());
        advertising.setUpdater(userCache.getCurrentAdminRealName());
        conditionalAdvertisingMapper.updateByPrimaryKeySelective(advertising);

    }

    /**
     * 下架投放配置
     *
     * @param advertising 配置ID
     */
    @EnableOperationLog(
            name = "下架投放配置",
            type = OperationType.UPDATE,
            daoClass = ConditionalAdvertisingMapper.class,
            parseClass = DefaultContentParse.class,
            index = 0,
            queue = RabbitConstant.WATER_OPERATION_LOG
    )
    @Override
    public void lowerAdvertising(ConditionalAdvertising advertising) {
        if (Objects.isNull(advertising.getId())) {
            throw new BadRequestException("投放配置ID不能为空。");
        }
        advertising.setForbidden(true);
        advertising.setUpdateTime(new Date());
        advertising.setUpdater(userCache.getCurrentAdminRealName());
        conditionalAdvertisingMapper.updateByPrimaryKeySelective(advertising);

    }

    public void checkParameter(ConditionalAdvertising advertising, int type) {
        if (Objects.isNull(advertising)) {
            throw new BadRequestException("投放配置不能为空。");
        }
        if (Objects.isNull(advertising.getPlatform())) {
            throw new BadRequestException("请选择广告平台。");
        }
        if (Objects.isNull(advertising.getAdvertisingType())) {
            throw new BadRequestException("请选择正确的投放类型。");
        }
        if (!Objects.equals(advertising.getAdvertisingType(), 1)) {
            throw new BadRequestException("投放类型只能是条件投放。");
        }

        if (advertising.getPlatform() != PlatformEnum.YIMAO.value
                && advertising.getPlatform() != PlatformEnum.BAIDU.value
                && advertising.getPlatform() != PlatformEnum.JINGDONG.value
                && advertising.getPlatform() != PlatformEnum.JIANSHI.value
                && advertising.getPlatform() != PlatformEnum.IFLYTEK.value
                && advertising.getPlatform() != PlatformEnum.YISHOU.value) {
            throw new BadRequestException("请正确选择广告平台。");
        }
        if (Objects.isNull(advertising.getOwnAdslotId())) {
            throw new BadRequestException("请选择广告位。");
        }
        if (Objects.equals(type, 1)) {
            if (Objects.isNull(advertising.getAdslotId())) {
                if (advertising.getPlatform() == PlatformEnum.YIMAO.value) {
                    throw new BadRequestException("请填写物料ID。");
                } else if (advertising.getPlatform() == PlatformEnum.BAIDU.value) {
                    throw new BadRequestException("请选择广告位ID。");
                }
            } else {
                if (advertising.getPlatform() == PlatformEnum.YIMAO.value) {
                    Materials materials = new Materials();
                    materials.setId(Integer.valueOf(advertising.getAdslotId()));
                    materials.setDeleted(false);
                    Materials one = materialsMapper.selectOne(materials);
                    if (Objects.isNull(one)) {
                        throw new BadRequestException("请填写正确的物料ID。");
                    }
                }
            }
        }
        if (Objects.nonNull(advertising.getConnectionType())
                && !Objects.equals(advertising.getConnectionType(), ConnectionTypeEnum.WIFI.value)
                && !Objects.equals(advertising.getConnectionType(), ConnectionTypeEnum.GPRS2.value)
                && !Objects.equals(advertising.getConnectionType(), ConnectionTypeEnum.GPRS3.value)
                && !Objects.equals(advertising.getConnectionType(), ConnectionTypeEnum.GPRS4.value)
                && !Objects.equals(advertising.getConnectionType(), ConnectionTypeEnum.GPRS5.value)) {
            throw new BadRequestException("请正确选择网络类型。");
        }
        if (Objects.nonNull(advertising.getAfterConnectionType())
                && !Objects.equals(advertising.getAfterConnectionType(), ConnectionTypeEnum.WIFI.value)
                && !Objects.equals(advertising.getAfterConnectionType(), ConnectionTypeEnum.GPRS2.value)
                && !Objects.equals(advertising.getAfterConnectionType(), ConnectionTypeEnum.GPRS3.value)
                && !Objects.equals(advertising.getAfterConnectionType(), ConnectionTypeEnum.GPRS4.value)
                && !Objects.equals(advertising.getAfterConnectionType(), ConnectionTypeEnum.GPRS5.value)) {
            throw new BadRequestException("请正确选择后续网络类型。");
        }
        if (Objects.isNull(advertising.getEffectiveBeginTime())) {
            throw new BadRequestException("请选择开始时间。");
        }
        if (Objects.isNull(advertising.getEffectiveEndTime())) {
            throw new BadRequestException("请选择结束时间。");
        }
        if (advertising.getEffectiveEndTime().before(advertising.getEffectiveBeginTime())) {
            throw new BadRequestException("结束时间不能早于开始时间。");
        }
        if (advertising.getEffectiveEndTime().before(new Date())) {
            throw new BadRequestException("结束时间不能早于当前时间。");
        }

        if (StringUtil.isNotEmpty(advertising.getModels())) {
            String[] modelList = advertising.getModels().split(",");
            List<String> modelListTemp = new ArrayList<>();
            modelListTemp.add(ProductModelEnum.MODEL_1601T.value);
            modelListTemp.add(ProductModelEnum.MODEL_1602T.value);
            modelListTemp.add(ProductModelEnum.MODEL_1603T.value);
            modelListTemp.add(ProductModelEnum.MODEL_1601L.value);
            for (String model : modelList) {
                if (!modelListTemp.contains(model.toUpperCase())) {
                    throw new BadRequestException("请选择正确的水机型号。");
                }
            }
        }
        if (Objects.nonNull(advertising.getDeviceGroup())
                && !Objects.equals(advertising.getDeviceGroup(), DeviceGroupEnum.USER.value)
                && !Objects.equals(advertising.getDeviceGroup(), DeviceGroupEnum.STATION.value)) {
            throw new BadRequestException("请选择正确的设备组。");
        }
    }

    /**
     * 查询本次配置信息和现有配置信息是否有交集。
     *
     * @param ad
     * @param areaSet
     * @param modelSet
     * @return
     */
    @Override
    public Set<FilterAdvertisingDTO> checkAdvertisingExists(ConditionalAdvertising ad, Set<AreaDTO> areaSet, Set<String> modelSet) {
        Set<Integer> areaIdSet = null;
        if (StringUtil.isNotBlank(ad.getAreas())) {
            String[] areaIds = ad.getAreas().split(",");
            areaIdSet = new HashSet<>();
            for (String s : areaIds) {
                Integer areaId = Integer.parseInt(s);
                AreaDTO area = systemFeignHandler.getAreaById(areaId);
                if (Objects.nonNull(area)) {
                    if (area.getLevel() == AreaTypeEnum.PROVINCE.value) {
                        areaIdSet.add(area.getId());
                        areaSet.add(area);
                    } else if (area.getLevel() == AreaTypeEnum.CITY.value) {
                        AreaDTO province = systemFeignHandler.getAreaById(area.getPid());
                        areaIdSet.add(area.getId());
                        areaIdSet.add(province.getId());
                        areaSet.add(area);
                        areaSet.add(province);
                    } else if (area.getLevel() == AreaTypeEnum.REGION.value) {
                        AreaDTO city = systemFeignHandler.getAreaById(area.getPid());
                        AreaDTO province = systemFeignHandler.getAreaById(city.getPid());
                        areaIdSet.add(area.getId());
                        areaIdSet.add(city.getId());
                        areaIdSet.add(province.getId());
                        areaSet.add(area);
                        areaSet.add(city);
                        areaSet.add(province);
                    }
                }
            }
        }
        if (StringUtil.isNotBlank(ad.getModels())) {
            String[] models = ad.getModels().split(",");
            for (String model : models) {
                modelSet.add(model);
            }
        }
        Set<FilterAdvertisingDTO> list = conditionalAdvertisingMapper.checkAdvertisingExists(areaIdSet, modelSet,
                ad.getPlatform(), ad.getOwnAdslotId(), ad.getAdslotId(), ad.getScreenLocation(),
                ad.getAdvertisingType(), ad.getConnectionType(),ad.getAfterConnectionType(), ad.getEffectiveBeginTime(), ad.getEffectiveEndTime(), ad.getId(), ad.getLocationTab(), ad.getDeviceGroup());
        return list;
    }

}
