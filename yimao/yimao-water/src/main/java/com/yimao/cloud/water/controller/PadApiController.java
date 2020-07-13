package com.yimao.cloud.water.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.baideApi.utils.BaideApiUtil;
import com.yimao.cloud.base.constant.AliConstant;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.constant.WechatConstant;
import com.yimao.cloud.base.context.SpringContextHolder;
import com.yimao.cloud.base.enums.ConnectionTypeEnum;
import com.yimao.cloud.base.enums.DeviceFaultFilterType;
import com.yimao.cloud.base.enums.DeviceFaultState;
import com.yimao.cloud.base.enums.DeviceFaultType;
import com.yimao.cloud.base.enums.EnvironmentEnum;
import com.yimao.cloud.base.enums.MaintenanceWorkOrderSourceEnum;
import com.yimao.cloud.base.enums.MessageFilterTypeEnum;
import com.yimao.cloud.base.enums.MessageMechanismEnum;
import com.yimao.cloud.base.enums.MessageModelTypeEnum;
import com.yimao.cloud.base.enums.MessagePushModeEnum;
import com.yimao.cloud.base.enums.MessagePushObjectEnum;
import com.yimao.cloud.base.enums.OrderType;
import com.yimao.cloud.base.enums.PayPlatform;
import com.yimao.cloud.base.enums.PayReceiveType;
import com.yimao.cloud.base.enums.PayTerminal;
import com.yimao.cloud.base.enums.PayType;
import com.yimao.cloud.base.enums.ProductCostTypeEnum;
import com.yimao.cloud.base.enums.RenewStatus;
import com.yimao.cloud.base.enums.RepairOrderSourceType;
import com.yimao.cloud.base.enums.StatusEnum;
import com.yimao.cloud.base.enums.SystemType;
import com.yimao.cloud.base.enums.WaterDeviceRenewStatus;
import com.yimao.cloud.base.enums.WorkOrderStatusEnum;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.DESUtil;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.MD5Util;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.UUIDUtil;
import com.yimao.cloud.base.utils.yun.YunOldIdUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.order.AliPayRequest;
import com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderDTO;
import com.yimao.cloud.pojo.dto.order.OrderRenewDTO;
import com.yimao.cloud.pojo.dto.order.WechatPayRequest;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import com.yimao.cloud.pojo.dto.order.WorkRepairOrderDTO;
import com.yimao.cloud.pojo.dto.product.ProductCostDTO;
import com.yimao.cloud.pojo.dto.system.OnlineAreaDTO;
import com.yimao.cloud.pojo.dto.system.SmsMessageDTO;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.dto.user.WaterDeviceUserDTO;
import com.yimao.cloud.pojo.dto.water.EffectStatisticsForAppDTO;
import com.yimao.cloud.pojo.dto.water.IntegralDetailDTO;
import com.yimao.cloud.pojo.dto.water.IntegralRuleDTO;
import com.yimao.cloud.pojo.dto.water.TrafficStatisticsDTO;
import com.yimao.cloud.pojo.dto.water.VersionDetailStatisticsDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDurationDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFaultDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFilterChangeRecordDTO;
import com.yimao.cloud.pojo.dto.water.WaterDevicePlaceChangeRecordDTO;
import com.yimao.cloud.pojo.vo.water.MaintenanceFilterVO;
import com.yimao.cloud.water.feign.OrderFeign;
import com.yimao.cloud.water.feign.ProductFeign;
import com.yimao.cloud.water.feign.SystemFeign;
import com.yimao.cloud.water.feign.UserFeign;
import com.yimao.cloud.water.handler.ProductFeignHandler;
import com.yimao.cloud.water.mapper.FilterMarksMapper;
import com.yimao.cloud.water.mapper.ManualPadCostMapper;
import com.yimao.cloud.water.mapper.PadEventMapper;
import com.yimao.cloud.water.mapper.PretreatmentDeviceMapper;
import com.yimao.cloud.water.mapper.TdsUploadRecordMapper;
import com.yimao.cloud.water.mapper.WaterDeviceConfigMapper;
import com.yimao.cloud.water.mapper.WaterDeviceRenewConfigMapper;
import com.yimao.cloud.water.po.DeviceLocation;
import com.yimao.cloud.water.po.FilterData;
import com.yimao.cloud.water.po.FilterMarks;
import com.yimao.cloud.water.po.FilterSetting;
import com.yimao.cloud.water.po.FlowRule;
import com.yimao.cloud.water.po.ManualPadCost;
import com.yimao.cloud.water.po.PadEvent;
import com.yimao.cloud.water.po.PretreatmentDevice;
import com.yimao.cloud.water.po.Tds;
import com.yimao.cloud.water.po.TdsUploadRecord;
import com.yimao.cloud.water.po.WaterDevice;
import com.yimao.cloud.water.po.WaterDeviceConfig;
import com.yimao.cloud.water.po.WaterDeviceConsumable;
import com.yimao.cloud.water.po.WaterDeviceDynamicCipherConfig;
import com.yimao.cloud.water.po.WaterDeviceDynamicCipherRecord;
import com.yimao.cloud.water.po.WaterDevicePlaceChangeRecord;
import com.yimao.cloud.water.po.WaterDeviceRenewConfig;
import com.yimao.cloud.water.po.WaterDeviceReplaceRecord;
import com.yimao.cloud.water.service.DeviceLocationService;
import com.yimao.cloud.water.service.DevicePasswordService;
import com.yimao.cloud.water.service.FilterDataService;
import com.yimao.cloud.water.service.FilterSettingService;
import com.yimao.cloud.water.service.FlowRuleService;
import com.yimao.cloud.water.service.GetAdslotConfigService;
import com.yimao.cloud.water.service.IntegralDetailService;
import com.yimao.cloud.water.service.IntegralRuleService;
import com.yimao.cloud.water.service.ManualPadCostService;
import com.yimao.cloud.water.service.PadApiService;
import com.yimao.cloud.water.service.StatisticsService;
import com.yimao.cloud.water.service.TdsService;
import com.yimao.cloud.water.service.TrafficStatisticsService;
import com.yimao.cloud.water.service.WaterDeviceConsumableService;
import com.yimao.cloud.water.service.WaterDeviceDurationService;
import com.yimao.cloud.water.service.WaterDeviceDynamicCipherService;
import com.yimao.cloud.water.service.WaterDeviceFilterChangeRecordService;
import com.yimao.cloud.water.service.WaterDevicePlaceChangeRecordService;
import com.yimao.cloud.water.service.WaterDeviceService;
import com.yimao.cloud.water.service.impl.SimCardService;
import com.yimao.cloud.water.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

/**
 * 描述：与水机APP端交互接口
 *
 * @Author Zhang Bo
 * @Date 2019/2/20 16:35
 * @Version 1.0
 */
@RestController
@Slf4j
@Api(tags = "PadApiController")
public class PadApiController {

    @Resource
    private GetAdslotConfigService getAdslotConfigService;
    @Resource
    private StatisticsService statisticsService;
    @Resource
    private TrafficStatisticsService trafficStatisticsService;
    @Resource
    private WaterDeviceDurationService waterDeviceDurationService;
    @Resource
    private SystemFeign systemFeign;
    @Resource
    private UserFeign userFeign;
    @Resource
    private OrderFeign orderFeign;
    @Resource
    private ProductFeign productFeign;
    @Resource
    private ProductFeignHandler productFeignHandler;
    @Resource
    private PadApiService padApiService;
    @Resource
    private DeviceLocationService deviceLocationService;
    @Resource
    private WaterDeviceService waterDeviceService;
    @Resource
    private WaterDevicePlaceChangeRecordService waterDevicePlaceChangeRecordService;
    @Resource
    private DevicePasswordService devicePasswordService;
    @Resource
    private WaterDeviceConsumableService waterDeviceConsumableService;
    @Resource
    private FilterDataService filterDataService;
    @Resource
    private TdsService tdsService;
    @Resource
    private SimCardService simCardService;
    @Resource
    private ManualPadCostService manualPadCostService;
    @Resource
    private ManualPadCostMapper manualPadCostMapper;
    @Resource
    private PadEventMapper padEventMapper;
    @Resource
    private TdsUploadRecordMapper tdsUploadRecordMapper;
    @Resource
    private FilterMarksMapper filterMarksMapper;
    @Resource
    private FilterSettingService filterSettingService;
    @Resource
    private PretreatmentDeviceMapper pretreatmentDeviceMapper;
    @Resource
    private WaterDeviceRenewConfigMapper waterDeviceRenewConfigMapper;
    @Resource
    private WaterDeviceConfigMapper waterDeviceConfigMapper;
    @Resource
    private WaterDeviceDynamicCipherService waterDeviceDynamicCipherService;
    @Resource
    private IntegralDetailService integralDetailService;
    @Resource
    private IntegralRuleService integralRuleService;
    @Resource
    private WaterDeviceFilterChangeRecordService waterDeviceFilterChangeRecordService;
    @Resource
    private FlowRuleService flowRuleService;
    @Resource
    private AmqpTemplate rabbitTemplate;

    @Resource
    private DomainProperties domainProperties;
    
    @Resource
    private RedisCache redisCache;

    /**
     * 获取流量规则
     */
    @GetMapping(value = "/pad/flowRule")
    @ApiOperation(value = "获取流量规则")
    public Object getFlowRule() {
        FlowRule rule = flowRuleService.getFlowRule();
        return ResponseEntity.ok(rule);
    }

    /**
     * @param sncode
     * @return
     * @description 根据SN查询维护工单
     * @author Liu Yi
     */
    @GetMapping(value = "/pad/order/maintenanceWorkOrder/sn")
    @ApiOperation(value = "根据SN查询维护工单", notes = "根据SN查询维护工单")
    @ApiImplicitParam(name = "sncode", value = "SN", required = true, dataType = "String", paramType = "query")
    public Object getWorkOrderMaintenanceBySnCode(@RequestParam(value = "sncode") String sncode) {
        List<MaintenanceWorkOrderDTO> dtoList = orderFeign.getWorkOrderMaintenanceBySnCode(sncode, null, StatusEnum.NO.value(), MaintenanceWorkOrderSourceEnum.SYSTEM_INCOME.value);
        MaintenanceFilterVO vo = null;
        for (MaintenanceWorkOrderDTO order : dtoList) {
            //有总部未完成的维护工单则返回给水机端
            vo = new MaintenanceFilterVO();
            vo.setOrderId(order.getId());
            vo.setMaterielDetailName(order.getMaterielDetailName());
            vo.setDeviceScope(order.getKindName());
            return ResponseEntity.ok(vo);
        }

        return vo;
    }

    /***
     * 功能描述 新增用户自助水机滤芯更换记录
     *
     * @param: [dto]
     * @auther: liu yi
     * @date: 2019/5/15 14:24
     * @return: java.lang.Object
     */
    @PostMapping(value = "/pad/waterdevice/filterChangeRecord")
    @ApiOperation(value = "新增用户自助水机滤芯更换", notes = "新增用户自助水机滤芯更换")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "filterNames", value = "滤芯，多个逗号隔开", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "deviceGroup", value = "用户组-1,服务站组-2", defaultValue = "1", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "source", value = "来源：1-安装工提交 2-客户提交", required = true, defaultValue = "2", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "orderId", value = "维护工单id", required = true, dataType = "String", paramType = "query")
    })
    public Object create(@RequestParam(value = "filterNames") String filterNames,
                         @RequestParam(value = "deviceGroup", defaultValue = "1") Integer deviceGroup,
                         @RequestParam(value = "source", defaultValue = "2") Integer source,
                         @RequestParam(value = "orderId") String orderId) {
        waterDeviceFilterChangeRecordService.createByClient(filterNames, deviceGroup, source, orderId);
        return ResponseEntity.noContent().build();
    }

    /***
     * 功能描述:pad创建积分记录信息
     *
     * @param: [ruleId, sn]
     * @auther: liu yi
     * @date: 2019/6/4 15:22
     * @return: java.lang.Object
     */
    @PostMapping(value = "/pad/integralDetail")
    @ApiOperation(value = "pad创建积分记录信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dto", value = "新增积分详情list", dataType = "IntegralDetailDTO", paramType = "body")
    })
    public Object addPadIntegral(@RequestBody IntegralDetailDTO dto) {
        integralDetailService.savePadIntegralDetail(dto);
        return ResponseEntity.noContent().build();
    }

    /***
     * 功能描述:查询设备统计积分
     *
     * @param: [sn, startTime, endTime]
     * @auther: liu yi
     * @date: 2019/6/3 17:29
     * @return: java.lang.Object
     */
    @GetMapping(value = "/pad/integralDetail/sn")
    @ApiOperation(value = "查询设备有效总积分")
    @ApiImplicitParam(name = "sn", value = "sn", required = true, dataType = "String", paramType = "query")
    public Object getPadEffectiveTotalIntegralBySn(@RequestParam(value = "sn") String sn) {
        int count = integralDetailService.getPadEffectiveTotalIntegralBySn(sn);
        return ResponseEntity.ok(count);
    }

    /**
     * 获取当前生效的积分规则
     */
    @GetMapping(value = "/pad/integralRule/effectiveIntegralRule")
    @ApiOperation(value = "获取当前生效的积分规则")
    public Object getEffectiveIntegralRule() {
        IntegralRuleDTO dto = integralRuleService.getEffectiveIntegralRule();
        return ResponseEntity.ok(dto);
    }

    /**
     * 客户端获取所有广告位配置信息。
     *
     * @return
     */
    @RequestMapping(value = "/pad/adslot/config", method = RequestMethod.GET)
    @ApiOperation(value = "获取所有广告位配置信息", notes = "获取所有广告位配置信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "snCode", value = "SN码", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "deviceGroup", value = "设备组类型，1-用户组，2服务站组", dataType = "Long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "currentTime", value = "当入参位空时取本地当前时间；如果有值，则根据入参进行查询", dataType = "Date", paramType = "query")
    })
    public ResponseEntity<Map<String, Object>> getAdslotConfig(@RequestParam(value = "snCode") String snCode,
                                                               @RequestParam(value = "deviceGroup") Integer deviceGroup,
                                                               @RequestParam(value = "currentTime", required = false) Date currentTime) {
        //获取所有广告位配置信息
        return ResponseEntity.ok(getAdslotConfigService.listBySnCode(snCode, currentTime, deviceGroup));
    }


    @RequestMapping(value = "/pad/device/duration", method = RequestMethod.GET)
    @ApiOperation(value = "根据设备SN获取亮灭屏时长")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "snCode", value = "设备SN", dataType = "String", required = true, paramType = "query")
    })
    public ResponseEntity<WaterDeviceDurationDTO> getDurationBySn(@RequestParam(value = "snCode") String snCode) {
        return ResponseEntity.ok(waterDeviceDurationService.getDurationBySn(snCode));
    }

    /**
     * 创建效果统计
     *
     * @param list
     * @return
     */
    @PostMapping(value = "/pad/statistics/effect")
    @ApiOperation(value = "创建效果统计", notes = "创建效果统计")
    @ApiImplicitParam(name = "list", value = "效果统计实体类", required = true, allowMultiple = true, dataType = "EffectStatisticsForAppDTO", paramType = "body")
    public Object saveEffect(@RequestBody List<EffectStatisticsForAppDTO> list) {
        if (CollectionUtil.isNotEmpty(list)) {
            statisticsService.saveEffect(list);
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * 创建流量统计
     *
     * @param list
     * @return
     */
    @PostMapping(value = "/pad/statistics/traffic")
    @ApiOperation(value = "创建流量统计", notes = "创建流量统计")
    @ApiImplicitParam(name = "list", value = "流量统计实体类", required = true, allowMultiple = true, dataType = "TrafficStatisticsDTO", paramType = "body")
    public Object saveTraffic(@RequestBody List<TrafficStatisticsDTO> list) {
        if (CollectionUtil.isNotEmpty(list)) {
            trafficStatisticsService.saveTraffic(list);
        }
        return ResponseEntity.noContent().build();
    }


    /**
     * 创建版本统计
     *
     * @param dto
     * @return
     */
    @PostMapping(value = "/pad/statistics/version")
    @ApiOperation(value = "创建版本统计", notes = "创建版本统计")
    @ApiImplicitParam(name = "dto", value = "版本统计实体类", required = true, dataType = "VersionDetailStatisticsDTO", paramType = "body")
    public Object saveTraffic(@RequestBody VersionDetailStatisticsDTO dto) {
        statisticsService.saveVersion(dto);
        return ResponseEntity.noContent().build();
    }


    //*************************************************************原PAD端访问云平台的接口*********************************************************

    /**
     * 2.5水机摆放位置
     */
    @GetMapping(value = "/api/pad/location")
    @ApiOperation(value = "水机摆放位置")
    public Object padLocation() {
        log.info("2.5水机摆放位置");
        String currentTime = DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
        log.info("调用时间={}", currentTime);
        List<DeviceLocation> list = deviceLocationService.listAll();
        List<Map<String, String>> locationMap = new ArrayList<>();
        for (DeviceLocation dl : list) {
            Map<String, String> map = new HashMap<>();
            map.put("location", dl.getName());
            locationMap.add(map);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("list", locationMap);
        return result;
    }

    /**
     * 2.6并发访问下载数量，下载视频或者图片的时候执行
     */
    @GetMapping(value = "/api/pad/available")
    @ApiOperation(value = "并发访问下载数量")
    public Object available() {
        log.info("2.6并发访问下载数量，下载视频或者图片的时候执行");
        String currentTime = DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
        log.info("调用时间={}", currentTime);
        Map<String, Object> result = new HashMap<>();
        ResultUtil.success(result);
        return result;
    }

    /**
     * 2.8通过SN码获取水机的用户信息
     *
     * @param sn SN码
     */
    @GetMapping(value = "/api/pad/sncode")
    @ApiOperation(value = "通过SN码获取水机的用户信息")
    public Object user(@RequestParam String sncode) {
        sncode = sncode.trim();
        log.info("2.8通过SN码获取水机的用户信息，sn={}", sncode);
        String currentTime = DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
        log.info("调用时间={}", currentTime);
        Map<String, Object> ru = new HashMap<>();
        WaterDevice waterDevice = waterDeviceService.getBySnCode(sncode);
        if (waterDevice == null) {
            ResultUtil.error(ru, "19", "sn码不存在");
            return ru;
        }
        // WaterDeviceUser deviceUser = waterDeviceUserMapper.selectBasicInfoById(waterDevice.getDeviceUserId());
        WaterDeviceUserDTO deviceUser = userFeign.getDeviceUserById(waterDevice.getDeviceUserId());
        if (deviceUser == null) {
            ResultUtil.error(ru, "44", "无此水机用户信息");
            return ru;
        }
        ru.put("name", deviceUser.getRealName());
        ru.put("phone", deviceUser.getPhone());
        ru.put("province", deviceUser.getProvince());
        ru.put("city", deviceUser.getCity());
        ru.put("region", deviceUser.getRegion());
        ru.put("simcard", waterDevice.getIccid());
        ResultUtil.success(ru);
        return ru;
    }

    /**
     * 2.9改变水机位置信息，亮灭屏的时候执行，并且在mcu数据查询时候执行
     *
     * @param sncode SN码
     */
    @PostMapping(value = "/api/pad/place")
    @ApiOperation(value = "改变水机位置信息")
    public Object locationChange(@RequestParam String sncode, @RequestParam String province, @RequestParam String city, @RequestParam String region,
                                 @RequestParam(required = false, defaultValue = "") String longitude,
                                 @RequestParam(required = false, defaultValue = "") String latitude) {
        sncode = sncode.trim();
        log.info("2.9 改变水机位置信息，亮灭屏的时候执行，并且在mcu数据查询时候执行: sncode={} province={} city={} region={} longitude={} latitude={}", sncode, province, city, region, longitude, latitude);
        String currentTime = DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
        log.info("调用时间={}", currentTime);
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        WaterDevice waterDevice = waterDeviceService.getBySnCode(sncode);
        if (waterDevice == null) {
            ResultUtil.error(ru, "19", "sn码不存在");
            return ru;
        }
        String oldProvince = waterDevice.getProvince();
        String oldCity = waterDevice.getCity();
        String oldRegion = waterDevice.getRegion();
        WaterDevice update = new WaterDevice();
        update.setId(waterDevice.getId());
        update.setProvince(province);
        update.setCity(city);
        update.setRegion(region);
        update.setLockState(waterDevice.getLockState());
        if (StringUtil.isNotEmpty(longitude) && StringUtil.isNotEmpty(latitude)) {
            update.setLongitude(longitude);
            update.setLatitude(latitude);
        }

        boolean isLockDevice = false;
        //位置变更
        if (!Objects.equals(oldProvince, province) || !Objects.equals(oldCity, city) || !Objects.equals(oldRegion, region)) {
            //保存设备位置更换记录信息
            WaterDevicePlaceChangeRecord placeChangeRecord = new WaterDevicePlaceChangeRecord();
            placeChangeRecord.setSn(waterDevice.getSn());
            placeChangeRecord.setOldPlace(oldProvince + oldCity + oldRegion);
            placeChangeRecord.setNewPlace(province + city + region);
            placeChangeRecord.setCreateTime(new Date());
            String lockState = waterDevice.getLockState();
            boolean b = this.checkDevicePlace(waterDevice.getWorkOrderId(), waterDevice.getSn(), province, city, region);
            if (!b) {
                //设备上报位置不一致.且设备锁机状态为未锁机
                isLockDevice = true;
                if (StringUtil.isEmpty(lockState) || StatusEnum.isNo(lockState)) {
                    update.setLockState(StatusEnum.YES.value());
                }
            } else {
                if (StringUtil.isEmpty(lockState) || StatusEnum.isYes(lockState)) {
                    update.setLockState(StatusEnum.NO.value());
                }
            }
            if (isLockDevice) {
                placeChangeRecord.setLockState(StatusEnum.YES.value());
            } else {
                placeChangeRecord.setLockState(StatusEnum.NO.value());
            }
            waterDevicePlaceChangeRecordService.save(placeChangeRecord);
        }
        waterDeviceService.update(update);

        ru.put("lockState", StatusEnum.NO.value());
        if (StatusEnum.isYes(update.getLockState())) {
            ru.put("lockState", StatusEnum.YES.value());
        }
        return ru;
    }

    private boolean checkDevicePlace(String workOrderId, String sn, String province, String city, String region) {
        if (StringUtil.isEmpty(region)) {
            return false;
        } else {
            WorkOrderDTO workOrder = orderFeign.getWorkOrderById(workOrderId);
            if (workOrder == null) {
                return false;
            } else {
                String oldProvince = workOrder.getProvince();
                String oldCity = workOrder.getCity();
                String oldRegion = workOrder.getRegion();
                if (oldProvince == null) {
                    oldProvince = "";
                }
                if (oldCity == null) {
                    oldCity = "";
                }
                if (oldRegion == null) {
                    oldRegion = "";
                }
                if (Objects.equals(oldProvince, province) && Objects.equals(oldCity, city) && Objects.equals(oldRegion, region)) {
                    return true;
                } else {
                    //设备移位.进行关联地区判断
                    log.info("设备sncode: {} 上报位置发生改变.开始判断是否关联地址", sn);
                    // boolean existsResult = this.commonAreaDao.query().is("name", region).is("refArea", oldRegion).exists();
                    // return existsResult;
                    return false;
                }
            }
        }
    }

    /**
     * 2.10水机清零
     */
    @GetMapping(value = "/api/pad/clear")
    @ApiOperation(value = "2.10水机清零")
    public Map<String, Object> clear(@RequestParam String sncode, @RequestParam Integer flow) {
        sncode = sncode.trim();
        log.info("2.10水机清零");
        String currentTime = DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
        log.info("调用时间={}", currentTime);

        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        WaterDevice device = waterDeviceService.getBySnCode(sncode);
        if (null == device) {
            ResultUtil.error(ru, "19", "sn码不存在");
            return ru;
        }
        Integer time = device.getCurrentTotalTime();
        time = time == null ? 0 : time;
        Integer lastTotalTime = device.getLastTotalTime();
        Integer lastTotalFlow = device.getLastTotalFlow();

        WaterDevice update = new WaterDevice();
        update.setId(device.getId());
        update.setLastTotalTime(time + lastTotalTime);
        update.setLastTotalFlow(flow + lastTotalFlow);
        waterDeviceService.update(update);
        return ru;
    }

    /**
     * 2.11同步密码
     */
    @GetMapping(value = "/api/pad/pwd")
    @ApiOperation(value = "同步密码")
    public Object syncPwd() {
        log.info("2.11同步密码");
        String currentTime = DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
        log.info("调用时间={}", currentTime);
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        String password = devicePasswordService.getPwd();
        if (StringUtil.isNotEmpty(password)) {
            ru.put("pwd", password);
        }
        return ru;
    }

    /**
     * 2.12控制板数据查询的时候触发本接口
     *
     * @param sn         SN码
     * @param type       type为1代表余额不足，type为2代表制水故障，type为3代表TDS异常，type为4代表滤芯更换
     * @param filterName 不定参数，在type为4时传,filterName的值为：PP、CTO、UDF、T33、RO
     */
    @GetMapping(value = "/api/pad/message/new")
    @ApiOperation(value = "控制板数据查询的时候触发本接口")
    public Object messageTrigger(@RequestParam String sn, @RequestParam Integer type, @RequestParam(required = false, defaultValue = "") String filterName) {
        sn = sn.trim();
        log.info("2.12 控制板数据查询的时候触发本接口，sn={} type={} filterName={}", sn, type, filterName);
        String currentTime = DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
        log.info("调用时间={}", currentTime);
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        WaterDevice device = waterDeviceService.getBySnCode(sn);
        if (Objects.isNull(device)) {
            ResultUtil.error(ru, "19", "sn码不存在");
            return ru;
        }
        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(device.getWorkOrderId());
        if (Objects.isNull(workOrder)) {
            ResultUtil.error(ru, "设备没有安装工单信息！");
            return ru;
        }
        if (WorkOrderStatusEnum.COMPLETED.value != workOrder.getStatus()) {
            ResultUtil.error(ru, "设备安装工单未完成，请联系客服！");
            return ru;
        }
        String deviceUserPhone = device.getDeviceUserPhone();
        //经销商姓名
        //经销商手机型号
        int distributorAppType = 0;
        //安装工姓名
        String engineerName;
        //安装工手机号
        String engineerPhone;
        //安装工手机型号
        int engineerAppType;
        EngineerDTO engineer = userFeign.getEngineerBasicInfoByIdForMsgPushInfo(device.getEngineerId());
        if (Objects.isNull(engineer)) {
            ResultUtil.error(ru, "该设备没有关联的工程师!");
            return ru;
        }
        engineerName = engineer.getRealName();
        engineerPhone = engineer.getPhone();
        engineerAppType = engineer.getAppType();

        DistributorDTO distributor = userFeign.getDistributorBasicInfoByIdForMsgPushInfo(device.getDistributorId());
        if (!Objects.isNull(distributor)) {
            distributorAppType = distributor.getAppType();
        }
        OnlineAreaDTO onlineArea = systemFeign.getOnlineAreaByPCR(engineer.getProvince(), engineer.getCity(), engineer.getRegion());

        //临时处理  后续删除   预处理设备
        PretreatmentDevice preDevice = pretreatmentDeviceMapper.selectBySn(sn);
        if (!Objects.isNull(preDevice)) {
            //滤芯报警
            if (type == DeviceFaultType.FILTER.value) {
                // int pushType = 0;
                if (checkIfPush()) { // 可以发推送
                    String fault = "";
                    Map<String, Object> checkFilterTime = getExpiredFilterNames(sn);
                    List<String> filterList = (List<String>) checkFilterTime.get("outdatedFilters");

                    for (String name : filterList) {
                        if (Objects.equals(name, "PP")) {
                            filterName = DeviceFaultFilterType.PP.name;
                            fault = DeviceFaultFilterType.PP.faultText;
                        } else if (Objects.equals(name, "CTO")) {
                            filterName = DeviceFaultFilterType.CTO.name;
                            fault = DeviceFaultFilterType.CTO.faultText;
                        } else if (Objects.equals(name, "UDF")) {
                            filterName = DeviceFaultFilterType.UDF.name;
                            fault = DeviceFaultFilterType.UDF.faultText;
                        } else if (Objects.equals(name, "T33")) {
                            filterName = DeviceFaultFilterType.T33.name;
                            fault = DeviceFaultFilterType.T33.faultText;
                        }

                        //推送消息给安装工APP
                        Map<String, String> appMessage = new HashMap<>();
                        appMessage.put("#code#", sn);
                        appMessage.put("#code1#", filterName);
                        appMessage.put("#code2#", device.getDeviceUserName());
                        appMessage.put("#code3#", device.getDeviceUserPhone());

                        //1-推送给安装工；
                        padApiService.pushMsgToApp(device, MessageModelTypeEnum.WARM_PUSH.value, MessageMechanismEnum.FILTER_CHANGE_BEFORE.value, MessagePushModeEnum.YIMAO_ENGINEER_APP_NOTICE.value, MessageModelTypeEnum.WARM_PUSH.name, engineer.getId(), engineerName, engineerAppType, appMessage, null);
                        //保存水机故障信息
                        createWaterDeviceFault(device.getId(), sn, fault, DeviceFaultType.FILTER.value, filterName);
                    }

                    if (!filterList.isEmpty()) {
                        //上线新流程校验
                        if (onlineArea != null && StatusEnum.YES.value().equals(onlineArea.getSyncState())) {
                            //向售后推送维护工单
                            Map<String, Object> map = new HashMap<>();
                            WaterDeviceDTO deviceDTO = new WaterDeviceDTO();
                            device.convert(deviceDTO);
                            StringBuffer materielTypeNamesStr = new StringBuffer();
                            for (String filter : filterList) {
                                materielTypeNamesStr.append(filter + ",");
                            }
                            String materielTypeNames = materielTypeNamesStr.substring(0, materielTypeNamesStr.length() - 1);
                            map.put("waterDevice", deviceDTO);
                            map.put("workOrder", workOrder);
                            map.put("filterName", materielTypeNames);
                            rabbitTemplate.convertAndSend(RabbitConstant.SYNC_MAINTENANCE_WORK_ORDER, map);
                        } else {
                            //非上线地区
                            log.info("该设备关联的工程师不在上线地区,未创建维护工单，sn：" + sn);
                        }
                    }
                    pretreatmentDeviceMapper.deleteByPrimaryKey(preDevice.getId());
                }
            }
        } else {
            String fault = "";
            if (StringUtil.isNotEmpty(deviceUserPhone)) {
                if (type == DeviceFaultType.MONEY.value) {
                    //余额为0情况的短信
                    fault = "余额不足";
                    //工单未支付，或者支付状态不是已支付
                    if (!workOrder.getPay() || workOrder.getPayStatus() != 3) {
                        ResultUtil.error(ru, "工单还未支付");
                        return ru;
                    }
                    //1-判断是否需要推送消息
                    boolean flag = padApiService.pushMsgToAppByMessageFilter(device, type, MessageFilterTypeEnum.MONEY.value, null);
                    if (flag) {
                        Map<String, String> engineerMessage = new HashMap<>();
                        engineerMessage.put("#code#", sn);
                        engineerMessage.put("#code1#", device.getDeviceUserName());
                        engineerMessage.put("#code2#", device.getDeviceUserPhone());
                        padApiService.pushMsgToApp(device, MessageModelTypeEnum.WARM_PUSH.value, MessageMechanismEnum.MONEY_NULL.value, MessagePushModeEnum.YIMAO_ENGINEER_APP_NOTICE.value, MessageModelTypeEnum.WARM_PUSH.name, engineer.getId(), engineerName, engineerAppType, engineerMessage, MessageFilterTypeEnum.MONEY.value);

                        //2-推送消息给经销商
                        WaterDeviceRenewConfig config = waterDeviceRenewConfigMapper.selectByType(WaterDeviceRenewStatus.WAIT.value);
                        int postponeDay = config == null ? 0 : config.getPostponeDay();

                        //设备安装地址
                        String deviceAddress;
                        //水机更换位置信息查询
                        WaterDevicePlaceChangeRecordDTO placeChangeRecord = waterDevicePlaceChangeRecordService.getBySn(sn);
                        if (Objects.nonNull(placeChangeRecord)) {
                            deviceAddress = placeChangeRecord.getNewPlace() + placeChangeRecord.getDetailAddress();
                        } else {
                            deviceAddress = device.getProvince() + device.getCity() + device.getRegion() + device.getAddress();
                        }
                        Map<String, String> distributorMessage = new HashMap<>();
                        distributorMessage.put("#code#", sn);
                        distributorMessage.put("#code1#", String.valueOf(postponeDay));
                        distributorMessage.put("#code2#", device.getDeviceUserName());
                        distributorMessage.put("#code3#", device.getDeviceUserPhone());
                        distributorMessage.put("#code4#", deviceAddress);
                        padApiService.pushMsgToApp(device, MessageModelTypeEnum.WARM_PUSH.value, MessageMechanismEnum.MONEY_NULL.value, MessagePushModeEnum.YIMAO_APP_NOTICE.value, MessageModelTypeEnum.WARM_PUSH.name, distributor.getUserId(), distributor.getUserName(), distributorAppType, distributorMessage, null);

                        //3-推送消息给站务系统
                     	//根据工单的省市区查询区域id
               		 	Integer areaId = redisCache.get(Constant.AREA_CACHE + workOrder.getProvince() + "_" + workOrder.getCity() + "_" + workOrder.getRegion(), Integer.class);
                        if (areaId == null) {
                            areaId = systemFeign.getRegionIdByPCR(workOrder.getProvince(), workOrder.getCity(), workOrder.getRegion());
                        }
                        Map<String, String> stationMessage = new HashMap<>();
                        stationMessage.put("#code#", sn);
                        stationMessage.put("#code1#", device.getDeviceUserName());
                        stationMessage.put("#code2#", device.getDeviceUserPhone());
                        stationMessage.put("#code3#", engineerName);
                        stationMessage.put("#code4#", engineerPhone);
                        if(Objects.isNull(distributor)) {
                        	 stationMessage.put("#code5#", "");
                             stationMessage.put("#code6#", "");
                        }else {
                        	 stationMessage.put("#code5#", distributor.getRealName());
                             stationMessage.put("#code6#", distributor.getPhone());
                        }
                       
                        padApiService.pushMsgToStation(device,MessageModelTypeEnum.WARM_PUSH.value,MessageMechanismEnum.MONEY_NULL.value, "余额为0",areaId,stationMessage,null);
                        
                        //发送短信给用户
                        SmsMessageDTO smsMessage = new SmsMessageDTO();
                        smsMessage.setType(MessageModelTypeEnum.WARM_PUSH.value);
                        smsMessage.setPushObject(MessagePushObjectEnum.WATER_USER.value);
                        smsMessage.setPhone(device.getDeviceUserPhone());
                        smsMessage.setMechanism(MessageMechanismEnum.MONEY_NULL.value);
                        smsMessage.setPushMode(MessagePushModeEnum.YIMAO_SMS.value);
                        Map<String, String> messageMap = new HashMap<>();
                        messageMap.put("#code#", sn);
                        smsMessage.setContentMap(messageMap);
                        rabbitTemplate.convertAndSend(RabbitConstant.SMS_MESSAGE_PUSH, smsMessage);
                    }
                } else if (DeviceFaultType.FILTER.value == type) {
                    fault = filterName + "滤芯需要更换";

                    int filterType = 0;
                    if (Objects.equals(filterName, DeviceFaultFilterType.PP.name)) {
                        filterType = MessageFilterTypeEnum.PP.value;
                    } else if (Objects.equals(filterName, DeviceFaultFilterType.CTO.name)) {
                        filterType = MessageFilterTypeEnum.CTO.value;
                    } else if (Objects.equals(filterName, DeviceFaultFilterType.UDF.name)) {
                        filterType = MessageFilterTypeEnum.UDF.value;
                    } else if (Objects.equals(filterName, DeviceFaultFilterType.T33.name)) {
                        filterType = MessageFilterTypeEnum.T33.value;
                    }
                    //1-判断是否需要推送消息
                    boolean flag = padApiService.pushMsgToAppByMessageFilter(device, type, filterType, filterName);
                    if (flag) {
                        //推送消息给安装工APP
                        Map<String, String> appMessage = new HashMap<>();
                        appMessage.put("#code#", sn);
                        appMessage.put("#code1#", filterName);
                        appMessage.put("#code2#", device.getDeviceUserName());
                        appMessage.put("#code3#", device.getDeviceUserPhone());
                        //1-推送给安装工；
                        padApiService.pushMsgToApp(device, MessageModelTypeEnum.WARM_PUSH.value, MessageMechanismEnum.FILTER_CHANGE_BEFORE.value, MessagePushModeEnum.YIMAO_ENGINEER_APP_NOTICE.value, MessageModelTypeEnum.WARM_PUSH.name, engineer.getId(), engineerName, engineerAppType, appMessage, filterType);
                        
                        //推送消息给站务系统
                        //根据工单的省市区查询区域id
               		 	Integer areaId = redisCache.get(Constant.AREA_CACHE + workOrder.getProvince() + "_" + workOrder.getCity() + "_" + workOrder.getRegion(), Integer.class);
                        if (areaId == null) {
                            areaId = systemFeign.getRegionIdByPCR(workOrder.getProvince(), workOrder.getCity(), workOrder.getRegion());
                        }
                        Map<String, String> stationMessage = new HashMap<>();
                        stationMessage.put("#code#", sn);
                        stationMessage.put("#code1#", device.getDeviceUserName());
                        stationMessage.put("#code2#", device.getDeviceUserPhone());
                        stationMessage.put("#code3#", engineerName);
                        stationMessage.put("#code4#", engineerPhone);
                        if(Objects.isNull(distributor)) {
                        	 stationMessage.put("#code5#", "");
                             stationMessage.put("#code6#", "");
                        }else {
                        	 stationMessage.put("#code5#", distributor.getRealName());
                             stationMessage.put("#code6#", distributor.getPhone());
                        }
                        stationMessage.put("#code7#", filterName);
                       
                        padApiService.pushMsgToStation(device, MessageModelTypeEnum.WARM_PUSH.value, MessageMechanismEnum.FILTER_CHANGE_BEFORE.value, "滤芯更换提醒", areaId, stationMessage,filterType);
                    
                    }
                    //上线新流程校验
                    if (onlineArea != null && StatusEnum.YES.value().equals(onlineArea.getSyncState())) {
                        //创建水机维护工单
                        Map<String, Object> map = new HashMap<>();
                        WaterDeviceDTO deviceDTO = new WaterDeviceDTO();
                        device.convert(deviceDTO);
                        map.put("waterDevice", deviceDTO);
                        map.put("workOrder", workOrder);
                        map.put("filterName", filterName);

                        rabbitTemplate.convertAndSend(RabbitConstant.SYNC_MAINTENANCE_WORK_ORDER, map);
                    } else {
                        log.info("该设备关联的工程师不在上线地区,未创建维护工单，sn：" + sn);
                    }
                } else if (DeviceFaultType.TDS.value == type) {//设备tds异常,制水故障信息推送给售后
                    fault = "TDS值异常";
                    
                    WorkRepairOrderDTO dto=setRepairParam(device,DeviceFaultType.TDS.name);
                    //创建水机推送故障                   
                    rabbitTemplate.convertAndSend(RabbitConstant.WATERDEVICE_PUSH_REPAIRORDER_CREATE, dto);
//                    Map<String, Object> map = setParamMap(type, fault, workOrder);
//                    rabbitTemplate.convertAndSend(RabbitConstant.SYNC_DEVICE_FAULT, map);//推送设备故障信息到百得(设备tds异常,制水故障信息推送)
                    //1-判断是否需要推送消息
                    boolean flag = padApiService.pushMsgToAppByMessageFilter(device, type, MessageFilterTypeEnum.TDS.value, null);
                    if (flag) {
                        //1-推送消息给安装工
                        Map<String, String> engineerMessage = new HashMap<>();
                        engineerMessage.put("#code#", sn);
                        engineerMessage.put("#code1#", device.getDeviceUserName());
                        engineerMessage.put("#code2#", device.getDeviceUserPhone());
                        padApiService.pushMsgToApp(device, MessageModelTypeEnum.WARM_PUSH.value, MessageMechanismEnum.TDS_FAULT.value, MessagePushModeEnum.YIMAO_ENGINEER_APP_NOTICE.value, MessageModelTypeEnum.WARM_PUSH.name, engineer.getId(), engineerName, engineerAppType, engineerMessage, MessageFilterTypeEnum.TDS.value);

                        //推送消息给站务系统
                        //根据工单的省市区查询区域id
               		 	Integer areaId = redisCache.get(Constant.AREA_CACHE + workOrder.getProvince() + "_" + workOrder.getCity() + "_" + workOrder.getRegion(), Integer.class);
                        if (areaId == null) {
                            areaId = systemFeign.getRegionIdByPCR(workOrder.getProvince(), workOrder.getCity(), workOrder.getRegion());
                        }
                        Map<String, String> stationMessage = new HashMap<>();
                        stationMessage.put("#code#", sn);
                        stationMessage.put("#code1#", device.getDeviceUserName());
                        stationMessage.put("#code2#", device.getDeviceUserPhone());
                        stationMessage.put("#code3#", engineerName);
                        stationMessage.put("#code4#", engineerPhone);
                        if(Objects.isNull(distributor)) {
                        	 stationMessage.put("#code5#", "");
                             stationMessage.put("#code6#", "");
                        }else {
                        	 stationMessage.put("#code5#", distributor.getRealName());
                             stationMessage.put("#code6#", distributor.getPhone());
                        }
                        padApiService.pushMsgToStation(device, MessageModelTypeEnum.WARM_PUSH.value, MessageMechanismEnum.TDS_FAULT.value, "TDS异常", areaId, stationMessage, MessageFilterTypeEnum.TDS.value);
                        
                        //2-发送短信给用户
                       /* SmsMessageDTO smsMessage = new SmsMessageDTO();
                        smsMessage.setType(MessageModelTypeEnum.WARM_PUSH.value);
                        smsMessage.setPushObject(MessagePushObjectEnum.WATER_USER.value);
                        smsMessage.setPhone(device.getDeviceUserPhone());
                        smsMessage.setMechanism(MessageMechanismEnum.TDS_FAULT.value);
                        smsMessage.setPushMode(MessagePushModeEnum.YIMAO_SMS.value);
                        Map<String, String> messageMap = new HashMap<>();
                        messageMap.put("#code#", sn);
                        messageMap.put("#code1#", engineerName);
                        messageMap.put("#code2#", engineerPhone);
                        smsMessage.setContentMap(messageMap);
                        // 发送短信
                        rabbitTemplate.convertAndSend(RabbitConstant.SMS_MESSAGE_PUSH, smsMessage);*/
                    }
                } else if (DeviceFaultType.WATER.value == type) { //设备制水故障,制水故障信息推送给售后
                    fault = "制水故障";
                    
                    WorkRepairOrderDTO dto=setRepairParam(device,DeviceFaultType.WATER.name);
                    rabbitTemplate.convertAndSend(RabbitConstant.WATERDEVICE_PUSH_REPAIRORDER_CREATE, dto);
//                    Map<String, Object> map = setParamMap(type, fault, workOrder);
//                    rabbitTemplate.convertAndSend(RabbitConstant.SYNC_DEVICE_FAULT, map);//推送设备故障信息到百得(设备tds异常,制水故障信息推送)
                    //1-判断是否需要推送消息
                    boolean flag = padApiService.pushMsgToAppByMessageFilter(device, type, MessageFilterTypeEnum.WATER.value, null);
                    if (flag) {
                        //1-推送消息给安装工
                        Map<String, String> engineerMessage = new HashMap<>();
                        engineerMessage.put("#code#", sn);
                        engineerMessage.put("#code1#", device.getDeviceUserName());
                        engineerMessage.put("#code2#", device.getDeviceUserPhone());
                        padApiService.pushMsgToApp(device, MessageModelTypeEnum.WARM_PUSH.value, MessageMechanismEnum.DEVICE_FAULT.value, MessagePushModeEnum.YIMAO_ENGINEER_APP_NOTICE.value, MessageModelTypeEnum.WARM_PUSH.name, engineer.getId(), engineerName, engineerAppType, engineerMessage, MessageFilterTypeEnum.WATER.value);

                        //推送消息给站务系统
                        //根据工单的省市区查询区域id
               		 	Integer areaId = redisCache.get(Constant.AREA_CACHE + workOrder.getProvince() + "_" + workOrder.getCity() + "_" + workOrder.getRegion(), Integer.class);
                        if (areaId == null) {
                            areaId = systemFeign.getRegionIdByPCR(workOrder.getProvince(), workOrder.getCity(), workOrder.getRegion());
                        }
                        Map<String, String> stationMessage = new HashMap<>();
                        stationMessage.put("#code#", sn);
                        stationMessage.put("#code1#", device.getDeviceUserName());
                        stationMessage.put("#code2#", device.getDeviceUserPhone());
                        stationMessage.put("#code3#", engineerName);
                        stationMessage.put("#code4#", engineerPhone);
                        if(Objects.isNull(distributor)) {
                        	 stationMessage.put("#code5#", "");
                             stationMessage.put("#code6#", "");
                        }else {
                        	 stationMessage.put("#code5#", distributor.getRealName());
                             stationMessage.put("#code6#", distributor.getPhone());
                        }
                        padApiService.pushMsgToStation(device, MessageModelTypeEnum.WARM_PUSH.value, MessageMechanismEnum.DEVICE_FAULT.value, "设备故障", areaId, stationMessage,MessageFilterTypeEnum.WATER.value);
                        
                        //2-发送短信给用户
                        SmsMessageDTO smsMessage = new SmsMessageDTO();
                        smsMessage.setType(MessageModelTypeEnum.WARM_PUSH.value);
                        smsMessage.setPushObject(MessagePushObjectEnum.WATER_USER.value);
                        smsMessage.setPhone(device.getDeviceUserPhone());
                        smsMessage.setMechanism(MessageMechanismEnum.DEVICE_FAULT.value);
                        smsMessage.setPushMode(MessagePushModeEnum.YIMAO_SMS.value);
                        Map<String, String> messageMap = new HashMap<>();
                        messageMap.put("#code#", sn);
                        messageMap.put("#code1#", engineerName);
                        messageMap.put("#code2#", engineerPhone);
                        smsMessage.setContentMap(messageMap);
                        rabbitTemplate.convertAndSend(RabbitConstant.SMS_MESSAGE_PUSH, smsMessage);
                    }
                }

                //保存水机故障信息
                createWaterDeviceFault(device.getId(), sn, fault, type, filterName);
            }
        }

        return ru;
    }

    
    private WorkRepairOrderDTO setRepairParam(WaterDevice device, String faultName) {
    	WorkRepairOrderDTO dto =new WorkRepairOrderDTO();
    	dto.setSourceType(RepairOrderSourceType.WATER_DEVICE_PUSH.value);
    	dto.setDeviceId(device.getId());
    	dto.setSn(device.getSn());
    	dto.setEngineerId(device.getEngineerId());   	
    	dto.setFaultName(faultName);
    	//设置服务时间,为第二天的八点到九点
    	Date now =new Date();
    	Calendar calendar =Calendar.getInstance();
    	calendar.setTime(now);
    	calendar.add(calendar.DATE,1);
    	calendar.set(calendar.HOUR, 8);
    	calendar.set(calendar.MINUTE, 0);
    	calendar.set(calendar.SECOND,0);
    	calendar.set(calendar.MILLISECOND,0);
    	Date startTime=calendar.getTime();	
    	calendar.add(calendar.HOUR,1);
    	Date endTime=calendar.getTime();
    	
    	dto.setServiceStartTime(startTime);
    	dto.setServiceEndTime(endTime);
		return dto;
	}

	public void createWaterDeviceFault(Integer deviceId, String snCode, String fault, Integer type, String filterName) {
        WaterDeviceFaultDTO deviceFault = new WaterDeviceFaultDTO();
        deviceFault.setDeviceId(deviceId);
        deviceFault.setSn(snCode);
        deviceFault.setType(type);
        deviceFault.setFilterType(filterName);
        deviceFault.setFault(fault);
        deviceFault.setState(DeviceFaultState.FAULT.value);
        rabbitTemplate.convertAndSend(RabbitConstant.DEVICE_FAULT, deviceFault);
    }

    /***
     * 功能描述:传参设置
     *
     * @param: [device, faultType, faultInfo]
     * @auther: liu yi
     * @date: 2019/5/14 17:30
     * @return: java.util.Map
     */
    private Map<String, Object> setParamMap(Integer faultType, String faultInfo, WorkOrderDTO workOrder) {
        Map<String, Object> params = new HashMap<>();
        params.put("sncode", workOrder.getSn());
        params.put("faultType", faultType);
        params.put("faultInfo", faultInfo);
        params.put("serviceSiteId", workOrder.getOldStationId());
        params.put("serviceSiteName", workOrder.getStationName());
        params.put("engineerId", workOrder.getOldEngineerId());
        params.put("engineerName", workOrder.getEngineerName());
        params.put("consumerId", workOrder.getOldUserId());
        params.put("consumerName", workOrder.getUserName());
        params.put("consumerPhone", workOrder.getUserPhone());
        return params;
    }

    /**
     * 2.13PAD定时执行设备在/离线（开机后两秒执行，之后每两小时执行）
     */
    @GetMapping(value = "/api/pad/online")
    @ApiOperation(value = "PAD定时执行设备在/离线")
    public Object syncOnline(@RequestParam String sn) {
        sn = sn.trim();
        log.info("2.13 PAD定时执行设备在/离线，sn={}", sn);
        String currentTime = DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
        log.info("调用时间={}", currentTime);
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        WaterDevice device = waterDeviceService.getBySnCode(sn);
        if (null != device) {
            Boolean unbundling = device.getUnbundling();
            WaterDevice update = new WaterDevice();
            update.setId(device.getId());
            update.setOnline(true);
            update.setLastOnlineTime(new Date());
            waterDeviceService.update(update);
            ru.put("unbundling", unbundling);
        } else {
            ResultUtil.error(ru, "19", "sn码不存在");
        }
        return ru;
    }

    /**
     * 2.15PAD定时执行广告播放记录
     */
    @PostMapping(value = "/api/pad/record")
    @ApiOperation(value = "PAD定时执行广告播放记录")
    public Object syncPlayRecord(@RequestParam String sncode, @RequestParam String record) {
        sncode = sncode.trim();
        log.info("2.15 PAD定时执行广告播放记录，sncode={} record={}", sncode, record);
        String currentTime = DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
        log.info("调用时间={}", currentTime);
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        return ru;
    }

    /**
     * 2.16PAD定时同步安装工信息到本地
     */
    @PostMapping(value = "/api/pad/customer")
    @ApiOperation(value = "PAD定时同步安装工信息到本地")
    public Object syncEngineer(@RequestParam String sncode) {
        sncode = sncode.trim();
        log.info("2.16PAD定时同步安装工信息到本地，sncode={}", sncode);
        String currentTime = DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
        log.info("调用时间={}", currentTime);

        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        WaterDevice device = waterDeviceService.getBySnCode(sncode);
        if (device != null) {
            ru.put("name", device.getEngineerName());
            ru.put("phone", device.getEngineerPhone());
        }
        return ru;
    }

    /**
     * 2.17广告配置下载成功记录（广告配置下载成功执行）
     */
    @PostMapping(value = "/api/pad/video/success")
    @ApiOperation(value = "广告配置下载成功记录")
    public Object adSuccess(@RequestParam String sncode, @RequestParam Integer advertisingId) {
        sncode = sncode.trim();
        log.info("2.17广告配置下载成功记录（广告配置下载成功执行），sncode={} advertisingId={}", sncode, advertisingId);
        String currentTime = DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
        log.info("调用时间={}", currentTime);

        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        return ru;
    }

    /**
     * 2.18查看SN码是否绑定<br/>
     * （1） sim为不定参数，在水机app版本1.2.0修改增加的参数
     * （2）在水机app清除数据再次提交sn码绑定的情况下，通过判断水机app提交的simcard参数与服务端存储的simcard比较，一致通过，不一致返回错误
     * （3）如果提交sn码未绑定的情况，按照sn码为初次提交，进行绑定。
     * （4）state字段表示返回sn与iccid绑定的状态
     * 1：代表未绑定
     * 2：代表已绑定且已绑定的simcard与提交simcard一致
     * 3：代表已绑定且已绑定的simcard与提交simcard不一致
     * （5） reset 为不定参数，boolean类型， 提供给水机app重置电控板后绑定sn数据使用，reset为true，服务端绑定sn数据
     *
     * @param sncode  SN码
     * @param simcard sim为不定参数，在水机app版本1.2.0修改增加的参数
     * @param reset   是否重置
     */
    @GetMapping(value = "/api/pad/valid/sncode")
    @ApiOperation(value = "查看SN码是否绑定")
    public Object snValid(@RequestParam String sncode, @RequestParam(required = false, defaultValue = "") String simcard, @RequestParam(required = false) Boolean reset) {
        sncode = sncode.trim();
        log.info("2.18查看SN码是否绑定，sn={} sim={} reset={}", sncode, simcard, reset);
        String currentTime = DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
        log.info("调用时间={}", currentTime);

        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        WaterDevice device = waterDeviceService.getBySnCode(sncode);
        if (null == device) {
            ResultUtil.error(ru, "19", "sn码不存在");
            return ru;
        }
        WaterDevice update = new WaterDevice();
        update.setId(device.getId());
        if (StringUtil.isNotEmpty(simcard)) {
            Boolean bind = device.getBind();
            if (bind != null && bind) {
                String iccid = device.getIccid();
                if (!Objects.equals(iccid, simcard)) {
                    ru.put("state", 3);
                    ResultUtil.error(ru, "25", "iccid与sn码不匹配，请确认信息后再重试！");
                } else {
                    ru.put("state", 2);
                }
            } else {
                //需重置
                if (reset) {
                    update.setBind(true);
                    waterDeviceService.update(device);
                }
                ru.put("state", 1);
            }
        } else {
            update.setBind(true);
            waterDeviceService.update(device);
        }
        return ru;
    }

    /**
     * 2.19获取滤芯参数配置
     *
     * @param sncode SN码
     */
    @RequestMapping(value = "/api/pad/getFilter")
    @ApiOperation(value = "获取滤芯参数配置")
    public Object filterConfig(@RequestParam String sncode) {
        sncode = sncode.trim();
        log.info("2.19获取滤芯参数配置，sncode={}", sncode);
        String currentTime = DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
        log.info("调用时间={}", currentTime);

        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        WaterDevice waterDevice = waterDeviceService.getBySnCode(sncode);
        if (waterDevice == null) {
            ResultUtil.error(ru, "19", "sn码不存在");
            return ru;
        }
        List<WaterDeviceConsumable> list = waterDeviceConsumableService.listByDeviceModel(waterDevice.getDeviceModel());
        if (CollectionUtil.isNotEmpty(list)) {
            Map<String, String> map = new HashMap<>();
            for (WaterDeviceConsumable consumable : list) {
                map.put(consumable.getName(), String.valueOf(consumable.getFlow()));
            }
            ru.put("filterStdFlow", map);
        }
        return ru;
    }

    /**
     * 2.20获取180完款支付二维码链接地址 TODO
     *
     * @param sn      SN码
     * @param payType 1-微信；2-支付宝
     * @param costId  计费方式ID
     */
    @GetMapping(value = "/api/pad/pay/geturl")
    @ApiOperation(value = "获取支付二维码链接地址")
    public Object payQrcode(@RequestParam String sncode, @RequestParam Integer payType, @RequestParam String costId) {
        sncode = sncode.trim();
        log.info("2.20获取180完款支付二维码链接地址，sncode={} payType={} costId={}", sncode, payType, costId);
        String currentTime = DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
        log.info("调用时间={}", currentTime);
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        return ru;
    }

    /**
     * 2.21是否续费成功<br/>
     * 续费成功设备自发请求《设备提交数据》接口
     *
     * @param reneworderId 续费订单id
     */
    @GetMapping(value = "/api/pad/pay/isrenew")
    @ApiOperation(value = "是否续费成功")
    public Object renewSuccess(@RequestParam String reneworderId) {
        log.info("2.21是否续费成功，renewOrderId={}", reneworderId);
        String currentTime = DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
        log.info("调用时间={}", currentTime);

        Map<String, Object> ru = new HashMap<>();
        OrderRenewDTO renew = orderFeign.getRenewWorkOrder(reneworderId);
        if (renew != null && renew.getPay()) {
            ResultUtil.success(ru);
        } else {
            boolean exists = orderFeign.checkWorkOrder180ComplatePay(reneworderId);
            if (exists) {
                ResultUtil.success(ru);
            } else {
                ResultUtil.error(ru, "46", "续费未成功");
            }
        }
        return ru;
    }

    /**
     * 2.22设备提交数据
     *
     * @param sncode        SN码
     * @param restFilterMap 各级滤芯的流量
     * @param flow          累计总流量
     * @param time          累计时长
     * @param netType       1-包流量；2-包年
     */
    @PostMapping(value = "/api/pad/sync/data")
    @ApiOperation(value = "设备提交数据")
    public Object syncData(@RequestParam String sncode,
                           @RequestParam(required = false) String iccid,
                           @RequestParam(required = false) String restFilterMap,
                           @RequestParam(required = false, defaultValue = "-1") Integer flow,
                           @RequestParam(required = false, defaultValue = "-1") Integer time,
                           @RequestParam(required = false, defaultValue = "1") Integer netType) {
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        try {
            sncode = sncode.trim();
            log.info("2.22设备提交数据，sncode={} iccid={} restFilterMap={} flow={} time={} netType={}", sncode, iccid, restFilterMap, flow, time, netType);
            String currentTime = DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
            log.info("调用时间={}", currentTime);

            WaterDevice device = waterDeviceService.getBySnCode(sncode);
            if (device == null) {
                log.error("2.22设备提交数据失败-sn码不存在。");
                ResultUtil.error(ru, "19", "sn码不存在");
                return ru;
            }
            WaterDevice update = new WaterDevice();
            update.setId(device.getId());
            update.setOnline(true);
            Date now = new Date();
            update.setLastOnlineTime(now);
            update.setConnectionType(ConnectionTypeEnum.WIFI.value);
            if (netType != null && netType == 2) {
                //SIM卡连接
                update.setConnectionType(ConnectionTypeEnum.GPRS3.value);
            }
            WorkOrderDTO workOrder = orderFeign.getWorkOrderById(device.getWorkOrderId());
            if (workOrder == null) {
                log.error("2.22设备提交数据失败-工单不存在。");
                ResultUtil.error(ru, "19", "工单不存在");
                return ru;
            }

            List<WaterDeviceConsumable> consumableList = waterDeviceConsumableService.listByDeviceModel(device.getDeviceModel());

            if (device.getSnEntryTime() == null && workOrder.getPay()) {
                WaterDevice updateSnEntryTime = new WaterDevice();
                updateSnEntryTime.setId(device.getId());
                updateSnEntryTime.setSnEntryTime(now);
                waterDeviceService.update(updateSnEntryTime);
                try {
                    BaideApiUtil.changeSncodeTime(workOrder.getId(), DateUtil.transferDateToString(now, "yyyy-MM-dd HH:mm:ss"), device.getIccid());
                } catch (Exception e) {
                    log.error("/sync/data 同步sncode修改时间带售后出错 sncode={}", sncode);
                }
            }
            Map<String, Map<String, Object>> filterMap = padApiService.getFilterMap(device, consumableList);
            ru.put("filterInfo", filterMap);

            FilterData filterData = filterDataService.save(device.getId(), sncode, restFilterMap);
            if (StringUtil.isEmpty(restFilterMap)) {
                Map<String, Integer> returnMap = padApiService.getFilterFlow(filterData, consumableList);
                ru.put("restFilterMap", returnMap);
            }
            if (flow == -1) {
                ru.put("flow", device.getUseFlow());
                // update.setCurrentTotalFlow(device.getCurrentTotalFlow());
                // update.setUseFlow(device.getUseFlow());
            } else {
                update.setCurrentTotalFlow(flow);
                update.setUseFlow(flow);
            }
            //通过设备创建时间计算包年计费的套餐使用情况
            Date startTime = device.getCreateTime();
            if (workOrder.getPayTerminal() != null && workOrder.getPayTerminal() == PayTerminal.USER.value) {
                //如果是货到付款（即用户支付）的话，包年计费的开始时间取工单实际支付时间，因为有可能水机安装后用户过了一段时间才支付
                startTime = workOrder.getPayTime();
            }
            if (startTime == null) {
                log.error("2.22设备提交数据---startTime为空---sncode={}", sncode);
                ResultUtil.error(ru, "设备提交数据失败，数据异常");
                return ru;
            }
            int days = DateUtil.betweenDays(startTime, new Date());
            update.setCurrentTotalTime(days);
            if (time == -1) {
                ru.put("time", device.getUseTime());
            } else {
                update.setUseTime(time);
            }

            log.info("2.22设备提交数据---走到计算设备金额的前面了");

            //余额计算，阀值提醒等
            padApiService.caculateMoney(device, workOrder, flow, days, ru, update);
            if (!(boolean) ru.get("success")) {
                //如果遇到错误，直接返回，不往下执行了
                log.info("2.22设备提交数据---走到计算设备金额的前面了");
                return ru;
            }

            log.info("2.22设备提交数据---更新到数据库的信息为---{}", JSONObject.toJSONString(update));
            if (update.getCurrentCostType() != null) {
                if (update.getCurrentCostType() == ProductCostTypeEnum.FLOW.value) {
                    update.setCurrentCostName(ProductCostTypeEnum.FLOW.name);
                } else {
                    update.setCurrentCostName(ProductCostTypeEnum.TIME.name);
                }
            }
            waterDeviceService.updateForPadSyncData(update);

            //根据计算的金额.核验设备的续费状态
            waterDeviceService.checkDeviceRenewTypeAndPutMir(device, update.getMoney(), workOrder);

            ru.put("lockState", StatusEnum.NO.value());
            if (StatusEnum.isYes(device.getLockState())) {
                ru.put("lockState", StatusEnum.YES.value());
            }

            log.info("水机同步结束：金额：{}，time：{}，sncode：{}", update.getMoney(), DateUtil.getDateToString(new Date(), "yyyy-MM-dd HH:mm:ss"), sncode);
            return ru;
        } catch (Exception e) {
            log.error("2.22设备提交数据---发生错误---sncode=" + sncode + "---" + e.getMessage(), e);
            ResultUtil.error(ru, "设备提交数据发生错误");
            return ru;
        }
    }

    /**
     * 2.23设备事件信息提交
     *
     * @param sncode  SN码
     * @param type    1代表人为造成  2代表设备自发造成
     * @param msg     设备事件信息描述
     * @param version app版本号
     */
    @PostMapping(value = "/api/pad/event/data")
    @ApiOperation(value = "设备事件信息提交")
    public Object event(@RequestParam String sncode, @RequestParam Integer type, @RequestParam String msg, @RequestParam String version) {
        sncode = sncode.trim();
        log.info("2.23设备事件信息提交，sncode={} type={} msg={} version={}", sncode, type, msg, version);
        String currentTime = DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
        log.info("调用时间={}", currentTime);

        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);

        WaterDevice device = waterDeviceService.getBySnCode(sncode);
        if (device == null) {
            log.error("2.23设备事件信息提交-sn码不存在。");
            ResultUtil.error(ru, "19", "sn码不存在");
            return ru;
        }
        WaterDevice update = new WaterDevice();
        update.setId(device.getId());
        update.setVersion(version);
        update.setLastOnlineTime(new Date());
        waterDeviceService.update(update);

        //TODO 设备事件信息提交，数据量大，后期需要采用mongodb存储
        PadEvent event = new PadEvent();
        event.setSn(sncode);
        event.setType(type);
        event.setDescription(msg);
        event.setVersion(version);
        event.setCreateTime(new Date());
        padEventMapper.insert(event);
        return ru;
    }

    /**
     * 2.24在更换设备时上传设备中的TDS参数值
     *
     * @param sncode SN码
     * @param k      时间参数
     * @param t      流量参数
     */
    @PostMapping(value = "/api/pad/uploadTds")
    @ApiOperation(value = "在更换设备时上传设备中的TDS参数值")
    public Object tdsUpload(@RequestParam String sncode, @RequestParam String K, @RequestParam String T) {
        sncode = sncode.trim();
        log.info("2.24在更换设备时上传设备中的TDS参数值，sncode={} k={} t={} version={}", sncode, K, T);
        String currentTime = DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
        log.info("调用时间={}", currentTime);

        Map<String, Object> ru = new HashMap<>();
        double k = 0D;
        double t = 0D;
        try {
            k = Double.parseDouble(K);
            t = Double.parseDouble(T);
        } catch (NumberFormatException nfe) {
            ResultUtil.error(ru, "39", "非法请求参数");
            return ru;
        }
        if (k <= 0.0D || t <= 0.0D) {
            ResultUtil.error(ru, "k,t值异常!无法进行提交!");
            return ru;
        }
        WaterDevice device = waterDeviceService.getBySnCode(sncode);
        if (device == null) {
            ResultUtil.error(ru, "19", "sn码不存在");
            return ru;
        }
        Tds tds = new Tds(k, t);
        Tds currentTds = null;
        if (device.getTdsId() != null) {
            currentTds = tdsService.getById(device.getTdsId());
        }
        if (currentTds == null) {
            FilterSetting setting = filterSettingService.getByPCR(device.getProvince(), device.getCity(), device.getRegion(), device.getDeviceModel());
            if (setting != null) {
                currentTds = new Tds(setting.getK(), setting.getT());
            } else {
                currentTds = new Tds(1.0D, 1.0D);
            }
        }
        if (tds.isSame(currentTds)) {
            ResultUtil.error(ru, "相同的k,t值，无法进行提交。");
            return ru;
        }
        //保存设备TDS值上传记录
        Date date = new Date();
        TdsUploadRecord record = new TdsUploadRecord();
        record.setDeviceId(device.getId());
        record.setSn(device.getSn());
        record.setEngineerId(device.getEngineerId());
        record.setEngineerName(device.getEngineerName());
        record.setType(2);
        record.setTypeName("修改");
        record.setK(currentTds.getK());
        record.setT(currentTds.getT());
        record.setCurrentK(tds.getK());
        record.setCurrentT(tds.getT());
        record.setCreateTime(date);
        record.setVerifyStatus(StatusEnum.NO.value());
        tdsUploadRecordMapper.insert(record);
        // ResultUtil.error(ru, "提交成功,等待系统审核");
        ResultUtil.success(ru, "提交成功,等待系统审核");
        return ru;
    }

    /**
     * 2.25 获取滤芯TDS配置
     *
     * @param sncode SN码
     * @param change 是否移机 0：未移机，1：移机
     */
    @GetMapping(value = "/api/pad/getTds")
    @ApiOperation(value = "获取滤芯TDS配置")
    public Object tdsConfig(@RequestParam String sncode, @RequestParam(defaultValue = "0") String change) {
        sncode = sncode.trim();
        log.info("2.25 获取滤芯TDS配置，sncode={} change={}", sncode, change);
        String currentTime = DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
        log.info("调用时间={}", currentTime);

        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        WaterDevice device = waterDeviceService.getBySnCode(sncode);
        if (device == null) {
            ResultUtil.error(ru, "19", "sn码不存在");
            return ru;
        }
        String province = device.getProvince();
        String city = device.getCity();
        String region = device.getRegion();
        String deviceModel = device.getDeviceModel();
        Tds tds = null;
        if (device.getTdsId() != null) {
            tds = tdsService.getById(device.getTdsId());
        }
        DecimalFormat df = new DecimalFormat("#0.000");
        Map<String, Object> tdsDefault = new HashMap<>();
        if ("0".equals(change)) {
            if (tds != null) {
                tdsDefault.put("K", df.format(tds.getK()));
                tdsDefault.put("T", df.format(tds.getT()));
                ru.put("tds", tdsDefault);
                return ru;
            }
        } else {
            //TODO 2020-03-20 这里不能直接删除TDS数据，正确的做法应该是将device上的tds_id设置为空
            // if (tds != null) {
            //     log.info("设备移机，删除设备上传的TDS参数。");
            //     tdsService.remove(tds.getId());
            // }
        }
        log.info("水机获取对应地区设置过TDS参数。");
        FilterSetting setting = filterSettingService.getByPCR(province, city, region, deviceModel);
        if (setting != null) {
            tdsDefault.put("K", df.format(setting.getK()));
            tdsDefault.put("T", df.format(setting.getT()));
            ru.put("tds", tdsDefault);
            return ru;
        }
        log.info("水机对应地区没有配置TDS参数，获取默认值。");
        tdsDefault.put("K", "1.000");
        tdsDefault.put("T", "1.000");
        ru.put("tds", tdsDefault);
        return ru;
    }

    /**
     * 2.26 同步设备配置信息<br/>
     *
     * @param sncode    SN码
     * @param simcard   sim卡号
     * @param place     水机摆放位置
     * @param longitude 经度
     * @param latitude  纬度
     * @return 返回样例：
     * {
     * "name":"张三",		    //客服人员
     * "phone":"15121244456",	//客服联系方式
     * "type":"家用1型",         //产品型号
     * "functionFlag":1,		//功能标记字段
     * "screenTime":{			//屏幕工作时间
     * "on":"09:00:00",	        //亮屏时间
     * "off":"20:00:00" 	    //灭屏时间
     * },
     * "success":true
     * }
     * 说明：
     * (1)经度、纬度设置为可传可不传
     * (2) 功能标记字段，其值为整数，目前（3.0版）定义：
     * a、二进制最低位为【亮灭屏是否由本地控制】标志位，若此位为0表示由本地控制亮灭屏幕动作（故screenTime不会返回），若此位为1表示按照screenTime执行亮灭屏幕（故screenTime必须返回）；
     * b、其他二进制位暂无定义，默认为0
     */
    @PostMapping(value = "/api/pad/sync/settings")
    @ApiOperation(value = "同步设备配置信息")
    public Object syncSettings(@RequestParam String sncode,
                               @RequestParam(required = false, defaultValue = "") String simcard,
                               @RequestParam(required = false, defaultValue = "") String place,
                               @RequestParam(required = false, defaultValue = "") String longitude,
                               @RequestParam(required = false, defaultValue = "") String latitude) {
        sncode = sncode.trim();
        log.info("2.26 同步设备配置信息，sncode={} simcard={} place={} longitude={} latitude={}", sncode, simcard, place, longitude, latitude);
        String currentTime = DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
        log.info("调用时间={}", currentTime);

        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        WaterDevice device = waterDeviceService.getBySnCode(sncode);
        if (device == null) {
            ResultUtil.error(ru, "19", "sn码不存在");
            return ru;
        }
        WaterDevice update = new WaterDevice();
        update.setId(device.getId());
        Date now = new Date();
        Boolean simActivating = device.getSimActivating();
        if (simActivating != null && !simActivating) {
            //激活设备SIM卡
            boolean boo = simCardService.callEditTerminal(device.getId(), device.getSimAccountId(), simcard);
            if (boo) {
                update.setSimActivating(true);
            }
        }
        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(device.getWorkOrderId());
        if (workOrder != null) {
            if (device.getSnEntryTime() == null && workOrder.getPay() && workOrder.getPayStatus() == 3) {
                update.setSnEntryTime(now);
                try {
                    BaideApiUtil.changeSncodeTime(workOrder.getId(), DateUtil.transferDateToString(now, "yyyy-MM-dd HH:mm:ss"), device.getIccid());
                } catch (Exception e) {
                    log.error("/sync/data 同步sncode修改时间带售后出错 sncode={}", sncode);
                }
            }
        }
        String deviceIccid;
        if (StringUtil.isNotEmpty(simcard)) {
            deviceIccid = device.getIccid();
            if (StringUtil.isEmpty(deviceIccid)) {
                ResultUtil.error(ru, "49", "请先在客服APP上提交SIM卡信息");
                return ru;
            }
            if (!Objects.equals(simcard, deviceIccid)) {
                //设备提交的iccid如果和SN不匹配，并且SN是绑定状态，则提示不匹配，不能提交
                //TODO 这里有一种情况是正常的设备没有识别到iccid，所以不能直接判断不相等，后续再做优化，这版先注释
                // if (device.getBind() != null && device.getBind()) {
                //     ResultUtil.error(ru, "25", "iccid与sn码不匹配，请确认信息后再重试！");
                //     return ru;
                // }
                update.setBind(true);
                update.setIccid(simcard);
                update.setPlace(place);
                update.setLastOnlineTime(now);
            }
        }
        update.setPlace(place);
        update.setLastOnlineTime(now);
        if (!"".equals(longitude) && !"".equals(latitude)) {
            update.setLongitude(longitude);
            update.setLatitude(latitude);
        }
        waterDeviceService.update(update);

        ru.put("type", YunOldIdUtil.getProductName(device.getDeviceModel()));
        ru.put("name", device.getEngineerName());
        ru.put("phone", device.getEngineerPhone());
        //亮灭屏使用默认配置
        WaterDeviceConfig config = waterDeviceConfigMapper.selectByDeviceModel(device.getDeviceModel());
        if (config != null) {
            ru.put("type", config.getName());
            if (config.getSwitchAble()) {
                Map<String, String> screenTime = new HashMap<>();
                screenTime.put("on", config.getOntime());
                screenTime.put("off", config.getOfftime());
                ru.put("functionFlag", 1);
                ru.put("screenTime", screenTime);
            }
        }
        return ru;
    }

    /**
     * 2.28 获取对应机型套餐信息列表
     *
     * @param sncode SN码
     */
    @GetMapping(value = "/api/pad/get/costList")
    @ApiOperation(value = "获取对应机型套餐信息列表")
    public Object tdsConfig(@RequestParam String sncode) {
        sncode = sncode.trim();
        log.info("2.28 获取对应机型套餐信息列表，sncode={}", sncode);
        String currentTime = DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
        log.info("调用时间={}", currentTime);

        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        WaterDevice device = waterDeviceService.getBySnCode(sncode);
        if (device == null) {
            ResultUtil.error(ru, "19", "sn码不存在");
            return ru;
        }
        List<ProductCostDTO> costList = productFeignHandler.listProductCostByOldCostId(device.getCostId());
        JSONArray array = new JSONArray();
        if (CollectionUtil.isNotEmpty(costList)) {
            for (ProductCostDTO cost : costList) {
                JSONObject o = new JSONObject();
                if (StringUtil.isNotEmpty(cost.getOldId())) {
                    o.put("id", cost.getOldId());
                } else {
                    o.put("id", cost.getId());
                }
                o.put("name", cost.getName());
                o.put("price", cost.getRentalFee());
                array.add(o);
            }
        }
        ru.put("data", array);
        return ru;
    }

    /**
     * 2.14 更换滤芯（旧版本）
     *
     * @param sncode SN码
     * @param name   滤芯名称
     */
    @PostMapping(value = "/api/pad/changefilter")
    @ApiOperation(value = "更换滤芯（旧版本）")
    public Object changefilter(@RequestParam String sncode, @RequestParam String name) {
        sncode = sncode.trim();
        log.info("2.14 更换滤芯（旧版本），sncode={} name={}", sncode, name);
        String currentTime = DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
        log.info("调用时间={}", currentTime);

        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        WaterDevice device = waterDeviceService.getBySnCode(sncode);
        if (device == null) {
            ResultUtil.error(ru, "19", "sn码不存在");
            return ru;
        }
        WaterDevice update = new WaterDevice();
        update.setId(device.getId());
        Date now = new Date();
        if ("PP".equals(name)) {
            device.setLastPpChangeTime(now);
        }
        if ("CTO".equals(name)) {
            device.setLastCtoChangeTime(now);
        }
        if ("UDF".equals(name)) {
            device.setLastUdfChangeTime(now);
        }
        if ("T33".equals(name)) {
            device.setLastT33ChangeTime(now);
        }
        WaterDeviceFilterChangeRecordDTO record = new WaterDeviceFilterChangeRecordDTO();
        record.setCreateTime(now);
        record.setSn(sncode);
        record.setFilterName(name);
        record.setProvince(device.getProvince());
        record.setCity(device.getCity());
        record.setRegion(device.getRegion());
        record.setAddress(device.getAddress());
        record.setSource(1);//安装工生成
        record.setEffective(1);//此条滤芯更换记录的“是否有效”设为“是”
        record.setActivatingTime(device.getCreateTime());//自动审核通过  改变设备更换时间

        waterDeviceService.update(device);
        waterDeviceFilterChangeRecordService.save(record);
        return ru;
    }

    /**
     * 2.29 安装工在设备端提交更换滤芯
     *
     * @param sncode SN码
     * @param names  滤芯名称集合
     */
    @PostMapping(value = "/api/pad/changefilters")
    @ApiOperation(value = "更换滤芯")
    public Object changefilters(@RequestParam String sncode, @RequestParam String names) {
        sncode = sncode.trim();
        log.info("2.29 安装工在设备端提交更换滤芯，sncode={} names={}", sncode, names);
        String currentTime = DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
        log.info("调用时间={}", currentTime);

        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        WaterDevice device = waterDeviceService.getBySnCode(sncode);
        if (device == null) {
            ResultUtil.error(ru, "19", "sn码不存在");
            return ru;
        }
        JSONArray ja = JSONArray.parseArray(names);
        //更换了几个滤芯
        int len = ja.size();
        Date now = new Date();
        List<WaterDeviceFilterChangeRecordDTO> list = new ArrayList<>(len);
        // List<WaterDeviceConsumable> consumableList = waterDeviceConsumableService.listByDeviceModel(device.getDeviceModel());
        //StringBuffer sb = new StringBuffer();

        //查询安装工信息同于服务站消息推送内容
        EngineerDTO engineer = userFeign.getEngineerBasicInfoByIdForMsgPushInfo(device.getEngineerId());
        //查询经销商信息同于服务站消息推送内容
        DistributorDTO distributor = userFeign.getDistributorBasicInfoByIdForMsgPushInfo(device.getDistributorId());
        //查询工单信息用于设置服务站消息推送区域id（根据工单的安装省市区）
        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(device.getWorkOrderId());

        for (int i = 0; i < len; i++) {
            String filter = ja.getString(i);
            // Optional<WaterDeviceConsumable> consumableOptional = consumableList.stream().filter(consumable -> filter.equals(consumable.getName())).findAny();
            // if (consumableOptional.isPresent()) {
            //     WaterDeviceConsumable consumable = consumableOptional.get();
            // }
            if ("PP".equals(filter)) {
                device.setLastPpChangeTime(now);
            }
            if ("CTO".equals(filter)) {
                device.setLastCtoChangeTime(now);
            }
            if ("UDF".equals(filter)) {
                device.setLastUdfChangeTime(now);
            }
            if ("T33".equals(filter)) {
                device.setLastT33ChangeTime(now);
            }

            //3-发送短信给用户
            SmsMessageDTO smsMessage = new SmsMessageDTO();
            smsMessage.setType(MessageModelTypeEnum.WARM_PUSH.value);
            smsMessage.setPushObject(MessagePushObjectEnum.WATER_USER.value);
            smsMessage.setPhone(device.getDeviceUserPhone());
            smsMessage.setMechanism(MessageMechanismEnum.FILTER_CHANGE_AFTER.value);
            smsMessage.setPushMode(MessagePushModeEnum.YIMAO_SMS.value);
            //发送短信
            Map<String, String> messageMap = new HashMap<>();
            messageMap.put("#code#", sncode);
            messageMap.put("#code1#", filter);
            smsMessage.setContentMap(messageMap);
            // 发送短信
            rabbitTemplate.convertAndSend(RabbitConstant.SMS_MESSAGE_PUSH, smsMessage);
            //解除设备报警
            WaterDeviceFaultDTO deviceFault = new WaterDeviceFaultDTO();
            deviceFault.setDeviceId(device.getId());
            deviceFault.setSn(sncode);
            deviceFault.setType(DeviceFaultType.FILTER.value);
            deviceFault.setFilterType(filter);
            deviceFault.setState(DeviceFaultState.RESOLVE.value);
            rabbitTemplate.convertAndSend(RabbitConstant.DEVICE_FAULT, deviceFault);

            WaterDeviceFilterChangeRecordDTO record = new WaterDeviceFilterChangeRecordDTO();
            record.setCreateTime(now);
            record.setSn(sncode);
            record.setFilterName(filter);
            record.setProvince(device.getProvince());
            record.setCity(device.getCity());
            record.setRegion(device.getRegion());
            record.setAddress(device.getAddress());
            record.setSource(1);//安装工生成
            record.setEffective(1);//此条滤芯更换记录的“是否有效”设为“是”
            record.setActivatingTime(device.getCreateTime());//自动审核通过  改变设备更换时间
            list.add(record);


            //推送消息到站务系统        
            if(Objects.nonNull(workOrder)) {
            	//根据工单的省市区查询区域id
            	Integer areaId = redisCache.get(Constant.AREA_CACHE + workOrder.getProvince() + "_" + workOrder.getCity() + "_" + workOrder.getRegion(), Integer.class);
                if (areaId == null) {
                    areaId = systemFeign.getRegionIdByPCR(workOrder.getProvince(), workOrder.getCity(), workOrder.getRegion());
                }
                if(Objects.nonNull(areaId) && Objects.nonNull(engineer)) {
                	Map<String, String> stationMessage = new HashMap<>();
                    stationMessage.put("#code#", sncode);
                    stationMessage.put("#code1#", filter);
                    stationMessage.put("#code2#", device.getDeviceUserName());
                    stationMessage.put("#code3#", device.getDeviceUserPhone());
                    stationMessage.put("#code4#", engineer.getRealName());
                    stationMessage.put("#code5#", engineer.getPhone());
                    if(Objects.isNull(distributor)) {
                    	 stationMessage.put("#code6#", "");
                         stationMessage.put("#code7#", "");
                    }else {
                    	 stationMessage.put("#code6#", distributor.getRealName());
                         stationMessage.put("#code7#", distributor.getPhone());
                    }
                    padApiService.pushMsgToStation(device, MessageModelTypeEnum.WARM_PUSH.value, MessageMechanismEnum.FILTER_CHANGE_AFTER.value, "滤芯更换后", areaId, stationMessage,null);

                }
            }
   		 	

        }

        waterDeviceService.update(device);
        waterDeviceFilterChangeRecordService.batchSave(list);


        return ru;
    }


    /**
     * 2.30 获取滤芯初始工作标记数据<br/>
     * 调用策略：查询mcu控制板数据的时候
     *
     * @param sncode SN码
     */
    @GetMapping(value = "/api/pad/get/filterMarks")
    @ApiOperation(value = "获取滤芯初始工作标记数据")
    public Object getFilterMarks(@RequestParam String sncode) {
        sncode = sncode.trim();
        log.info("2.30 获取滤芯初始工作标记数据，sncode={} ", sncode);
        String currentTime = DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
        log.info("调用时间={}", currentTime);

        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        WaterDevice device = waterDeviceService.getBySnCode(sncode);
        if (device == null) {
            ResultUtil.error(ru, "19", "sn码不存在");
            return ru;
        }
        int pp = 0;
        int cto = 0;
        int udf = 0;
        int t33 = 0;
        int ro = 0;
        FilterMarks query = new FilterMarks();
        query.setSn(sncode);
        query.setDeviceId(device.getId());
        FilterMarks filterMarks = filterMarksMapper.selectOne(query);
        if (filterMarks != null) {
            pp = filterMarks.getPp();
            udf = filterMarks.getUdf();
            cto = filterMarks.getCto();
            t33 = filterMarks.getThree();
            ro = filterMarks.getRo();
        }
        JSONObject json = new JSONObject();
        json.put("PP", pp);
        json.put("CTO", cto);
        json.put("UDF", udf);
        json.put("T33", t33);
        json.put("RO", ro);
        ru.put("filterMarks", json);
        return ru;
    }

    /**
     * 2.31 设置滤芯初始工作标记数据<br/>
     * 调用策略：查询mcu控制板数据的时候
     *
     * @param sncode SN码
     */
    @PostMapping(value = "/api/pad/set/filterMarks")
    @ApiOperation(value = "设置滤芯初始工作标记数据")
    public Object setFilterMarks(@RequestParam String sncode, @RequestParam String filterMarks) {
        sncode = sncode.trim();
        log.info("2.31 设置滤芯初始工作标记数据，sncode={} filterMarks={}", sncode, filterMarks);
        String currentTime = DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
        log.info("调用时间={}", currentTime);

        Map<String, Object> ru = new HashMap<>();
        if (StringUtil.isBlank(filterMarks)) {
            return ru;
        }
        ResultUtil.success(ru);
        WaterDevice device = waterDeviceService.getBySnCode(sncode);
        if (device == null) {
            ResultUtil.error(ru, "19", "sn码不存在");
            return ru;
        }
        JSONObject json = JSONObject.parseObject(filterMarks);
        FilterMarks query = new FilterMarks();
        query.setSn(sncode);
        query.setDeviceId(device.getId());
        FilterMarks marks = filterMarksMapper.selectOne(query);
        if (marks == null) {
            marks = new FilterMarks();
            marks.setSn(sncode);
            marks.setDeviceId(device.getId());
            marks.setCreateTime(new Date());
            marks.setPp(json.getInteger("PP"));
            marks.setCto(json.getInteger("CTO"));
            marks.setUdf(json.getInteger("UDF"));
            marks.setThree(json.getInteger("T33"));
            marks.setRo(json.getInteger("RO"));
            filterMarksMapper.insert(marks);
        } else {
            marks.setPp(json.getInteger("PP"));
            marks.setCto(json.getInteger("CTO"));
            marks.setUdf(json.getInteger("UDF"));
            marks.setThree(json.getInteger("T33"));
            marks.setRo(json.getInteger("RO"));
            filterMarksMapper.updateByPrimaryKey(marks);
        }
        return ru;
    }

    /**
     * 2.32 查询时间过期滤芯列表<br/>
     * 调用策略：开机后三秒连接，之后每八小时连接
     *
     * @param sncode SN码
     */
    @GetMapping(value = "/api/pad/get/outdatedFilters")
    @ApiOperation(value = "查询时间过期滤芯列表")
    public Map<String, Object> getExpiredFilterNames(@RequestParam String sncode) {
        sncode = sncode.trim();
        log.info("2.32 查询时间过期滤芯列表，sncode={}", sncode);
        String currentTime = DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
        log.info("调用时间={}", currentTime);

        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        WaterDevice device = waterDeviceService.getBySnCode(sncode);
        if (device == null) {
            ResultUtil.error(ru, "19", "sn码不存在");
            return ru;
        }
        Tds tds = null;
        if (device.getTdsId() != null) {
            tds = tdsService.getById(device.getTdsId());
        }
        if (tds == null) {
            FilterSetting setting = filterSettingService.getByPCR(device.getProvince(), device.getCity(), device.getRegion(), device.getDeviceModel());
            if (setting != null) {
                tds = new Tds(setting.getK(), setting.getT());
            } else {
                tds = new Tds(1.0D, 1.0D);
            }
        }
        List<WaterDeviceConsumable> consumableList = waterDeviceConsumableService.listByDeviceModel(device.getDeviceModel());
        if (CollectionUtil.isEmpty(consumableList)) {
            ResultUtil.error(ru, "19", "滤芯配置不存在");
            return ru;
        }
        List<String> filters = new ArrayList<>();
        Date now = new Date();
        WaterDeviceReplaceRecord replaceRecord = padApiService.getReplaceRecordBySnCode(sncode);
        for (WaterDeviceConsumable consumable : consumableList) {
            Date startDate = padApiService.getFilterChangeDate(sncode, device.getSnEntryTime(), consumable.getName(), replaceRecord);
            double betweenHours = DateUtil.betweenHours(startDate, now);
            if (consumable.getTime() * tds.getK() * 24.0D - betweenHours <= 0.0D) {
                filters.add(consumable.getName());
                log.info("水机,SN码:" + sncode + " " + consumable.getName() + " 滤芯使用时间到期");
            }
        }
        ru.put("outdatedFilters", filters);
        return ru;
    }

    /**
     * 2.34 查询水机金额异常后台处理记录<br/>
     * 调用策略：每八小时连接一次，并且在水机控制板数据改变或查询的时候连接
     *
     * @param sncode SN码
     */
    @GetMapping(value = "/api/pad/getManualPadCost")
    @ApiOperation(value = "查询水机金额异常后台处理记录")
    public Object getManualPadCost(@RequestParam String sncode) {
        sncode = sncode.trim();
        log.info("2.34 查询水机金额异常后台处理记录，sncode={}", sncode);
        String currentTime = DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
        log.info("调用时间={}", currentTime);

        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        // WaterDevice device = waterDeviceService.getBySnCode(sncode);
        // if (device == null) {
        //     ResultUtil.error(ru, "19", "sn码不存在");
        //     return ru;
        // }
        List<ManualPadCost> list = manualPadCostService.listBySn(sncode);
        if (CollectionUtil.isEmpty(list)) {
            ResultUtil.error(ru, "暂无异常数据");
            return ru;
        }
        ManualPadCost padCost = list.get(0);
        JSONObject data = new JSONObject();
        data.put("id", padCost.getId());
        data.put("sncode", padCost.getSn());
        data.put("flow", padCost.getDischarge());
        ru.put("data", data);
        return ru;
    }

    /**
     * 2.35 水机同步确认<br/>
     * 调用策略：在mcu查询到有状态返回则执行
     *
     * @param id      手动同步记录id
     * @param pcbFlow pcb控制板上记录的水机流量
     */
    @GetMapping(value = "/api/pad/checkManualPadCost")
    @ApiOperation(value = "水机同步确认")
    public Object checkManualPadCost(@RequestParam String id, @RequestParam Integer pcbFlow) {
        log.info("2.35 水机同步确，id={} pcbFlow={}", id, pcbFlow);
        String currentTime = DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
        log.info("调用时间={}", currentTime);

        Date now = new Date();
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        ManualPadCost padCost = manualPadCostMapper.selectByPrimaryKey(Integer.parseInt(id));
        if (padCost == null) {
            ResultUtil.error(ru, "修改失败");
            return ru;
        }

        if (pcbFlow - 1000 > padCost.getDischarge()) {
            padCost.setSyncFailReason("水机记录流量为：" + pcbFlow + "，已超出正常范围");
            padCost.setSyncStatus(2);
            padCost.setSyncTime(now);
            manualPadCostMapper.updateByPrimaryKeySelective(padCost);
            ResultUtil.error(ru, "水机记录流量为：" + pcbFlow + "，已超出正常范围");
            return ru;
        } else if (pcbFlow < padCost.getDischarge()) {
            padCost.setSyncFailReason("水机记录流量为：" + pcbFlow + "，小于修改流量");
            padCost.setSyncStatus(2);
            padCost.setSyncTime(now);
            manualPadCostMapper.updateByPrimaryKeySelective(padCost);
            ResultUtil.error(ru, "水机记录流量为：" + pcbFlow + "，小于修改流量");
            return ru;
        } else {
            WaterDevice device = waterDeviceService.getBySnCode(padCost.getSn());
            if (device == null) {
                ResultUtil.error(ru, "19", "sn码不存在");
                return ru;
            }
            WaterDevice update = new WaterDevice();
            update.setId(device.getId());
            update.setCurrentTotalFlow(padCost.getDischarge());
            update.setMoney(padCost.getBalance());
            waterDeviceService.update(update);

            padCost.setSyncStatus(1);
            padCost.setSyncTime(now);
            manualPadCostMapper.updateByPrimaryKeySelective(padCost);

            log.info("修改余额同步成功 时间:" + DateUtil.getDateToString(now, "yyyy-MM-dd HH:mm:ss.SSS") + "   sncode:" + padCost.getSn());
            return ru;
        }
    }

    /**
     * 2.36 判断水机是续费还是完款<br/>
     * 调用策略：开机后两秒执行，之后两小时执行，查询mcu数据时执行
     *
     * @param sncode SN码
     */
    @GetMapping(value = "/api/pad/renew/checkState")
    @ApiOperation(value = "判断水机是续费还是完款")
    public Object renewCheck(@RequestParam String sncode) {
        sncode = sncode.trim();
        log.info("2.36 判断水机是续费还是完款，sncode={}", sncode);
        String currentTime = DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
        log.info("调用时间={}", currentTime);

        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        WaterDevice device = waterDeviceService.getBasicInfoBySnCode(sncode);
        if (device == null) {
            ResultUtil.error(ru, "19", "sn码不存在");
            return ru;
        }
        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(device.getWorkOrderId());
        if (workOrder == null) {
            ResultUtil.error(ru, "45", "无此水机对应工单");
            return ru;
        } else if (workOrder.getPay()) {
            //是否续费，true为续费，false为完款,且必须要存在对应的套餐才能使用新续费方式，业务系统新版不做完款功能
            ru.put("isRenew", true);
            return ru;
        } else {
            ResultUtil.error(ru, "45", "工单未支付无法续费");
            return ru;
        }
    }

    /**
     * 2.37 续费套餐列表<br/>
     * 调用策略：
     *
     * @param sncode SN码
     */
    @GetMapping(value = "/api/pad/renew/renewPackage")
    @ApiOperation(value = "续费套餐列表")
    public Object listRenewPackage(@RequestParam String sncode) {
        sncode = sncode.trim();
        log.info("2.37 续费套餐列表，sncode={}", sncode);
        String currentTime = DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
        log.info("调用时间={}", currentTime);

        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        WaterDevice device = waterDeviceService.getBasicInfoBySnCode(sncode);
        if (device == null) {
            ResultUtil.error(ru, "19", "sn码不存在");
            return ru;
        }
        List<ProductCostDTO> costList = productFeign.listProductRenewCostByOldCostId(device.getCostId());
        if (CollectionUtil.isEmpty(costList)) {
            ResultUtil.error(ru, "19", "续费套餐不存在");
            return ru;
        }
        costList.sort((pc1, pc2) -> pc1.getType() > pc2.getType() ? 1 : -1);

        Set<String> set = new HashSet<>();
        for (ProductCostDTO dto : costList) {
            set.add(dto.getName());
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (String name : set) {
            Map<String, Object> data = new HashMap<>();
            List<Map<String, Object>> deductionList = new ArrayList<>();
            for (ProductCostDTO cost : costList) {
                if (Objects.equals(name, cost.getName())) {
                    // data.put("id", "00000000");
                    data.put("id", name);
                    data.put("name", cost.getName());
                    data.put("price", cost.getRentalFee());
                    data.put("discount", 10.0D);
                    data.put("afterDiscountPrice", cost.getRentalFee());
                    data.put("showDisCount", false);

                    Map<String, Object> deduction = new HashMap<>();
                    deduction.put("type", cost.getType());
                    if (ProductCostTypeEnum.isTime(cost.getType())) {
                        // deduction.put("remark", "按时间计费（包年）");
                        deduction.put("remark", cost.getRenewRemark());
                    } else {
                        deduction.put("remark", cost.getRenewRemark());
                    }
                    if (data.get("packageDeductions") == null) {
                        deductionList.add(deduction);
                        data.put("packageDeductions", deductionList);
                    } else {
                        ((List<Map<String, Object>>) data.get("packageDeductions")).add(deduction);
                    }
                }

            }
            list.add(data);
        }
        log.info("PAD端获取到的续费套餐数据为：{}", JSONObject.toJSONString(list));
        // ProductCostDTO cost = costList.get(0);
        // List<Map<String, Object>> list = new ArrayList<>();
        // Map<String, Object> data = new HashMap<>();
        // //新版设计ID没有实际意义
        // data.put("id", "00000000");
        // data.put("name", cost.getName());
        // data.put("price", cost.getRentalFee());
        // data.put("discount", 10.0D);
        // data.put("afterDiscountPrice", cost.getRentalFee());
        // data.put("showDisCount", false);
        // List<Map<String, Object>> deductionList = new ArrayList<>();
        // for (ProductCostDTO dto : costList) {
        //     Map<String, Object> deduction = new HashMap<>();
        //     deduction.put("type", dto.getType());
        //     if (ProductCostTypeEnum.isTime(dto.getType())) {
        //         deduction.put("remark", "按时间计费（包年）");
        //     } else {
        //         deduction.put("remark", "按流量计费");
        //     }
        //     deductionList.add(deduction);
        // }
        // data.put("packageDeductions", deductionList);
        // list.add(data);
        ru.put("data", list);
        return ru;
    }

    /**
     * 2.38 获取续费支付二维码地址<br/>
     * 调用策略：
     *
     * @param sncode    SN码
     * @param packageId 计费方式名称（新系统采用）
     */
    @PostMapping(value = "/api/pad/renew/pay/geturl")
    @ApiOperation(value = "获取续费支付二维码地址")
    public Object getRenewPayUrl(@RequestParam String sncode, @RequestParam Integer payType, @RequestParam String packageId, @RequestParam Integer deductionsType) {
        sncode = sncode.trim();
        log.info("2.38 获取续费支付二维码地址，sncode={} payType={} packageId={} deductionsType={}", sncode, payType, packageId, deductionsType);
        String currentTime = DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
        log.info("调用时间={}", currentTime);

        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        WaterDevice device = waterDeviceService.getBasicInfoBySnCode(sncode);
        if (device == null) {
            ResultUtil.error(ru, "19", "sn码不存在");
            return ru;
        }
        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(device.getWorkOrderId());
        if (workOrder == null) {
            ResultUtil.error(ru, "45", "无此水机对应工单");
            return ru;
        } else if (workOrder.getNeedCompletePay()) {
            ResultUtil.error(ru, "00", "设备请先完款再续费");
            return ru;
        }
        List<ProductCostDTO> costList = productFeign.listProductRenewCostByOldCostId(device.getCostId());
        ProductCostDTO cost = null;
        for (ProductCostDTO dto : costList) {
            if (Objects.equals(dto.getName(), packageId) && Objects.equals(dto.getType(), deductionsType)) {
                cost = dto;
            }
        }
        if (cost == null) {
            ResultUtil.error(ru, "00", "错误的套餐信息");
            return ru;
        }
        BigDecimal payMoney = cost.getRentalFee();
        OrderRenewDTO renewOrder = createOrderRenewDTO(device, cost, payType);
        //保存续费订单
        try {
            orderFeign.saveRenewWorkOrder(renewOrder);
        } catch (Exception e) {
            log.error("获取续费二维码失败，创建续费订单时出错。" + e.getMessage(), e);
            ResultUtil.error(ru, "00", "无法获取二维码，创建续费订单时出错");
            return ru;
        }
        String payurl = null;
        //微信支付
        if (payType == PayType.WECHAT.value) {
            WechatPayRequest payRequest = new WechatPayRequest();
            payRequest.setOut_trade_no(renewOrder.getId());
            //单位是：分，所以乘100
            if (Constant.PRO_ENVIRONMENT) {
                payRequest.setTotal_fee(payMoney.doubleValue());
            } else {
                payRequest.setTotal_fee(0.01D);
            }
            payRequest.setTradeType(WechatConstant.NATIVE);
            payRequest.setNotifyUrl(domainProperties.getApi() + WechatConstant.WXPAY_NOTIFY_URL_TWO);
            payRequest.setOrderType(OrderType.RENEW.value);
            payRequest.setPlatform(PayPlatform.WECHAT.value);
            payRequest.setClientType(SystemType.PAD.value);
            payRequest.setReceiveType(PayReceiveType.ONE.value);
            if (SpringContextHolder.getEnvironment().equalsIgnoreCase(EnvironmentEnum.PRO.code)) {
                payRequest.setBody("净水设备续费" + payMoney + "元");
            } else {
                payRequest.setBody("净水设备续费" + payMoney + "元测试");
            }
            payurl = orderFeign.wechatScanCodePay(payRequest);
        } else {
            AliPayRequest payRequest = new AliPayRequest();
            payRequest.setOutTradeNo(renewOrder.getId());
            //单位是：分，所以乘100
            payRequest.setTotalAmount(payMoney.doubleValue());
            payRequest.setBody("净水设备续费" + payMoney + "元");
            payRequest.setNotifyUrl(domainProperties.getApi() + AliConstant.ALIPAY_NOTIFY_URL_TWO);
            payRequest.setOrderType(OrderType.RENEW.value);
            payRequest.setPlatform(PayPlatform.ALI.value);
            payRequest.setClientType(SystemType.PAD.value);
            payRequest.setReceiveType(PayReceiveType.ONE.value);
            if (SpringContextHolder.getEnvironment().equalsIgnoreCase(EnvironmentEnum.PRO.code)) {
                payRequest.setSubject("翼猫水机续费");
            } else {
                payRequest.setSubject("翼猫水机续费测试");
            }
            payurl = orderFeign.aliScanCodePay(payRequest);
        }
        if (StringUtil.isEmpty(payurl)) {
            ResultUtil.error(ru, "22", "生成二维码失败");
        } else {
            ru.put("reneworderId", renewOrder.getId());
            ru.put("payurl", payurl);
        }
        return ru;
    }

    /**
     * 2.41 设备获取动态密码，不存在则新建
     *
     * @param sncode SN码
     */
    @GetMapping(value = "/api/pad/getDeviceDynamicCipher")
    @ApiOperation(value = "设备获取动态密码，不存在则新建")
    public Object getDeviceDynamicCipher(@RequestParam String sncode) {
        sncode = sncode.trim();
        log.info("2.41 设备获取动态密码，sncode={}", sncode);
        String currentTime = DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
        log.info("调用时间={}", currentTime);

        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        if (StringUtil.isBlank(sncode)) {
            ResultUtil.error(ru, "39", "非法请求参数");
            return ru;
        }
        WaterDeviceDynamicCipherRecord record = waterDeviceDynamicCipherService.getDynamicCipherRecordBySnCode(sncode);
        if (record == null) {
            WaterDeviceDynamicCipherConfig config = waterDeviceDynamicCipherService.getDynamicCipherConfig();
            if (config == null) {
                ResultUtil.error(ru, "配置项不存在!请联系管理员处理");
                return ru;
            }
            //没有则新建
            WaterDeviceDynamicCipherRecord newRecord = new WaterDeviceDynamicCipherRecord();
            newRecord.setEngineerId(-1);
            newRecord.setEngineerName(null);
            newRecord.setTerminal("system");
            newRecord.setSn(sncode);
            String password = new Random().nextInt(899999) + 100000 + "";
            newRecord.setPassword(password);
            newRecord.setPasswordDesStr(DESUtil.encrypt(password, DESUtil.KEY));
            newRecord.setValidStatus(StatusEnum.YES.value());
            Calendar cal = Calendar.getInstance();
            newRecord.setCreateTime(cal.getTime());
            cal.add(Calendar.MINUTE, config.getValidMinute());
            newRecord.setValidTime(cal.getTime());
            waterDeviceDynamicCipherService.saveDynamicCipherRecord(newRecord);
            Map<String, Object> data = new HashMap<>();
            data.put("pwd", newRecord.getPasswordDesStr());
            data.put("validTime", newRecord.getValidTime().getTime());
            ru.put("data", data);
            return ru;
        } else {
            Map<String, Object> data = new HashMap<>();
            data.put("pwd", record.getPasswordDesStr());
            data.put("validTime", record.getValidTime().getTime());
            ru.put("data", data);
            return ru;
        }
    }


    /**
     * 创建续费订单对象
     */
    private OrderRenewDTO createOrderRenewDTO(WaterDevice device, ProductCostDTO newCost, Integer payType) {
        OrderRenewDTO renewOrder = new OrderRenewDTO();
        //22位数字
        renewOrder.setId(UUIDUtil.buildRenewWorkOrderId());
        renewOrder.setSnCode(device.getSn());
        renewOrder.setDeviceId(device.getId());
        renewOrder.setDeviceModel(device.getDeviceModel());
        renewOrder.setDeviceAddress(device.getAddress());

        renewOrder.setCostId(newCost.getId());
        renewOrder.setCostName(newCost.getName());
        renewOrder.setCostType(newCost.getType());
        renewOrder.setCostTypeName(ProductCostTypeEnum.getName(newCost.getType()));
        renewOrder.setLastCostId(device.getCostId());
        renewOrder.setLastCostName(device.getCostName());
        renewOrder.setLastCostType(device.getCostType());
        renewOrder.setLastCostTypeName(ProductCostTypeEnum.getName(device.getCostType()));

        //续费时要支付的是租赁费（不含安装费）
        renewOrder.setAmountFee(newCost.getRentalFee());

        renewOrder.setPay(false);
        //支付类型：1-微信；2-支付宝；3-POS机；4-转账；
        renewOrder.setPayType(payType);
        renewOrder.setPayTypeName(PayType.find(payType).name);
        //支付状态：0-待支付，1-待审核，2-支付成功，3-支付失败
        if (payType == PayType.WECHAT.value || payType == PayType.ALIPAY.value) {
            renewOrder.setStatus(RenewStatus.WAITING_PAY.value);
            renewOrder.setStatusName(RenewStatus.WAITING_PAY.name);
        } else {
            renewOrder.setStatus(RenewStatus.AUDITED.value);
            renewOrder.setStatusName(RenewStatus.AUDITED.name);
        }
        Date date = new Date();
        renewOrder.setCreateTime(date);
        renewOrder.setTerminal(PayTerminal.PAD.value);
        renewOrder.setTerminalName(PayTerminal.PAD.name);
        //续费订单的经销商信息要从水机设备上取
        renewOrder.setDistributorId(device.getDistributorId());
        //续费订单的安装工信息要从水机设备上取
        renewOrder.setEngineerId(device.getEngineerId());
        //设备安装时间
        renewOrder.setDeviceInstallationTime(device.getSimActivatingTime());
        return renewOrder;
    }

    /**
     * 临时处理推送代码 ，到以后 删除
     */
    public static int send_count = 15;
    public static Long last_update_count_time = System.currentTimeMillis();


    private synchronized boolean checkIfPush() {
        if (System.currentTimeMillis() - last_update_count_time < 3600000) { //last_update_count_time 是否是这个小时内的 的 值
            --send_count;
        } else {
            send_count = 15;
            last_update_count_time = System.currentTimeMillis();
        }
        return send_count > 0;
    }

    @GetMapping(value = "/api/pad/password")
    @ApiOperation(value = "设备获取动态密码")
    public Map<String, Object> password(@RequestParam String password) {
        Map<String, Object> ru = new HashMap();
        String pwd = devicePasswordService.getPwd();
        String pwdTemp = MD5Util.encodeMD5(pwd);
        if (pwdTemp.equalsIgnoreCase(password)) {
            ResultUtil.success(ru);
        } else {
            ResultUtil.error(ru, "14", "密码输入错误");
        }

        return ru;
    }
}
