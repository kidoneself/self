package com.yimao.cloud.water.service.impl;

import com.google.common.collect.Maps;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.DeviceFaultState;
import com.yimao.cloud.base.enums.DeviceFaultType;
import com.yimao.cloud.base.enums.MessageMechanismEnum;
import com.yimao.cloud.base.enums.MessageModelTypeEnum;
import com.yimao.cloud.base.enums.MessagePushModeEnum;
import com.yimao.cloud.base.enums.MessagePushObjectEnum;
import com.yimao.cloud.base.enums.PayTerminal;
import com.yimao.cloud.base.enums.ProductCostTypeEnum;
import com.yimao.cloud.base.enums.WaterDeviceRenewStatus;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import com.yimao.cloud.pojo.dto.product.ProductCostDTO;
import com.yimao.cloud.pojo.dto.station.StationMessageDTO;
import com.yimao.cloud.pojo.dto.system.AppMessageDTO;
import com.yimao.cloud.pojo.dto.system.MessagePushDTO;
import com.yimao.cloud.pojo.dto.system.SmsMessageDTO;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFaultDTO;
import com.yimao.cloud.water.feign.SystemFeign;
import com.yimao.cloud.water.feign.UserFeign;
import com.yimao.cloud.water.handler.ProductFeignHandler;
import com.yimao.cloud.water.mapper.MessagePushFilterMapper;
import com.yimao.cloud.water.mapper.WaterDeviceFilterChangeRecordMapper;
import com.yimao.cloud.water.mapper.WaterDeviceReplaceRecordMapper;
import com.yimao.cloud.water.po.DeductionPlan;
import com.yimao.cloud.water.po.FilterData;
import com.yimao.cloud.water.po.FilterSetting;
import com.yimao.cloud.water.po.Tds;
import com.yimao.cloud.water.po.WaterDevice;
import com.yimao.cloud.water.po.WaterDeviceConsumable;
import com.yimao.cloud.water.po.WaterDeviceFilterChangeRecord;
import com.yimao.cloud.water.po.WaterDeviceReplaceRecord;
import com.yimao.cloud.water.service.DeductionPlanService;
import com.yimao.cloud.water.service.FilterSettingService;
import com.yimao.cloud.water.service.PadApiService;
import com.yimao.cloud.water.service.TdsService;
import com.yimao.cloud.water.service.WaterDeviceFaultService;
import com.yimao.cloud.water.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 描述：TODO
 *
 * @Author Zhang Bo
 * @Date 2019/5/8
 */
@Service
@Slf4j
public class PadApiServiceImpl implements PadApiService {

    @Resource
    private ProductFeignHandler productFeignHandler;
    @Resource
    private SystemFeign systemFeign;
    @Resource
    private UserFeign userFeign;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private WaterDeviceReplaceRecordMapper waterDeviceReplaceRecordMapper;
    @Resource
    private WaterDeviceFilterChangeRecordMapper waterDeviceFilterChangeRecordMapper;
    @Resource
    private MessagePushFilterMapper messagePushFilterMapper;

    @Resource
    private DeductionPlanService deductionPlanService;
    @Resource
    private WaterDeviceFaultService waterDeviceFaultService;
    @Resource
    private FilterSettingService filterSettingService;
    @Resource
    private TdsService tdsService;
    @Resource
    private RedisCache redisCache;

    @Override
    public Map<String, Map<String, Object>> getFilterMap(WaterDevice device, List<WaterDeviceConsumable> consumableList) {
        String province = device.getProvince();
        String city = device.getCity();
        String region = device.getRegion();
        FilterSetting setting = filterSettingService.getByPCR(province, city, region, device.getDeviceModel());
        Date now = new Date();
        Map<String, Map<String, Object>> info = Maps.newHashMap();
        Tds tds = new Tds();
        if (setting == null && device.getTdsId() == null) {
            tds.setK(1.0D);
            tds.setT(1.0D);
        } else if (setting != null) {
            tds.setK(setting.getK());
            tds.setT(setting.getT());
        }
        if (device.getTdsId() != null) {
            tds = tdsService.getById(device.getTdsId());
        }

        if (CollectionUtil.isNotEmpty(consumableList)) {
            for (WaterDeviceConsumable consumable : consumableList) {
                Date startDate = this.getFilterChangeDate(device, consumable.getName());
                int outOfDate = this.isOutOfDate(startDate, now, tds.getK(), consumable.getTime());
                if (outOfDate == 1) {
                    log.warn("水机,SN码:" + device.getSn() + " " + consumable.getName() + " 滤芯使用时间到期");
                }
                Map<String, Object> map = new HashMap<>();
                map.put("outdated", outOfDate);
                map.put("threshold", 0);
                info.put(consumable.getName(), map);
            }
        }
        return info;
    }

    private Date getFilterChangeDate(WaterDevice device, String filterName) {
        String sncode = device.getSn();
        WaterDeviceReplaceRecord query = new WaterDeviceReplaceRecord();
        query.setNewSn(sncode);
        WaterDeviceReplaceRecord replaceRecord = waterDeviceReplaceRecordMapper.selectOneByNewSn(sncode);
        List<WaterDeviceFilterChangeRecord> filterChangeRecordList = this.listFilterChangeRecordBySnAndFilterName(sncode, filterName);
        if (CollectionUtil.isNotEmpty(filterChangeRecordList)) {
            WaterDeviceFilterChangeRecord filterChange = filterChangeRecordList.get(0);
            return filterChange.getCreateTime();
        } else if (replaceRecord != null) {
            return replaceRecord.getCreateTime();
        } else {
            return device.getSnEntryTime();
        }
    }

    private int isOutOfDate(Date startDate, Date endDate, double k, Integer useDays) {
        double betweenHours = 0.0D;

        try {
            betweenHours = DateUtil.betweenHours(startDate, endDate);
        } catch (Exception e) {
        }
        return useDays * k * 24.0D - betweenHours <= 0.0D ? 1 : 0;
    }

    @Override
    public Map<String, Integer> getFilterFlow(FilterData filterData, List<WaterDeviceConsumable> consumableList) {
        Map<String, Integer> returnMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(consumableList)) {
            for (WaterDeviceConsumable consumable : consumableList) {
                String name = consumable.getName();
                Integer flow = consumable.getFlow();
                if (filterData != null) {
                    if ("PP".equalsIgnoreCase(name)) {
                        returnMap.put("PP", filterData.getPpFlow());
                    }
                    if ("UDF".equalsIgnoreCase(name)) {
                        returnMap.put("UDF", filterData.getUdfFlow());
                    }
                    if ("T33".equalsIgnoreCase(name)) {
                        returnMap.put("T33", filterData.getThreeFlow());
                    }
                    if ("CTO".equalsIgnoreCase(name)) {
                        returnMap.put("CTO", filterData.getCtoFlow());
                    }
                    if ("RO".equalsIgnoreCase(name)) {
                        returnMap.put("RO", filterData.getRoFlow());
                    }
                } else {
                    returnMap.put(consumable.getName(), flow);
                }
            }
        }
        return returnMap;
    }

    @Override
    public WaterDeviceReplaceRecord getReplaceRecordBySnCode(String sn) {
        return waterDeviceReplaceRecordMapper.selectOneByNewSn(sn);
    }

    @Override
    public Date getFilterChangeDate(String sncode, Date simActivatingTime, String filterName, WaterDeviceReplaceRecord replaceRecord) {
        List<WaterDeviceFilterChangeRecord> filterChangeRecordList = this.listFilterChangeRecordBySnAndFilterName(sncode, filterName);
        if (replaceRecord != null) {
            if (CollectionUtil.isNotEmpty(filterChangeRecordList)) {
                return filterChangeRecordList.get(0).getCreateTime();
            } else {
                return replaceRecord.getCreateTime();
            }
        } else {
            if (CollectionUtil.isNotEmpty(filterChangeRecordList)) {
                return filterChangeRecordList.get(0).getCreateTime();
            } else {
                return simActivatingTime;
            }
        }
    }

    /**
     * 查询水机滤芯更换记录
     *
     * @param sn             SN码
     * @param consumableName 滤芯名称
     */
    private List<WaterDeviceFilterChangeRecord> listFilterChangeRecordBySnAndFilterName(String sn, String consumableName) {
        Example example = new Example(WaterDeviceFilterChangeRecord.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtil.isNotEmpty(sn)) {
            criteria.andEqualTo("sn", sn);
        }
        if (StringUtil.isNotEmpty(consumableName)) {
            criteria.andEqualTo("filterName", consumableName);
        }
        example.orderBy("createTime").desc();
        return waterDeviceFilterChangeRecordMapper.selectByExample(example);
    }

    /**
     * 水机同步数据-计算水机余额
     */
    @Override
    public void caculateMoney(WaterDevice device, WorkOrderDTO workOrder, Integer flow, Integer time, Map<String, Object> ru, WaterDevice update) {
        ProductCostDTO cost = productFeignHandler.getProductCostById(device.getCostId());
        if (cost == null) {
            log.error("2.22设备提交数据失败-计费方式不存在。【001】");
            ResultUtil.error(ru, "19", "计费方式不存在。");
            return;
        }
        update.setCostId(device.getCostId());
        //套餐租赁费
        // BigDecimal rentalFee = cost.getRentalFee();
        //开户费
        // BigDecimal installationFee = cost.getInstallationFee();
        //单价
        BigDecimal unitPrice = cost.getUnitPrice();
        Integer threshold1 = cost.getThreshold1();
        Integer threshold2 = cost.getThreshold2();
        Integer threshold3 = cost.getThreshold3();

        int restDays = 0;
	    //水机扣费计划
        List<DeductionPlan> plans = deductionPlanService.listByDeviceId(device.getId());
        if (CollectionUtil.isNotEmpty(plans)) {
            //如果水机设备有扣费计划，则按照扣费计划计算水机余额
            caculatePlanMoney(plans, device, workOrder, cost, flow, time, ru, update);
        } else {
            //水机剩余金额
            BigDecimal money = device.getMoney();
            BigDecimal initMoney = device.getInitMoney();
            Integer costType = cost.getType();
            int count;
            if (ProductCostTypeEnum.isFlow(costType)) {
                if (flow != -1 && flow > 0) {
                    //本次同步所使用的流量=已使用的流量-上次记录的已使用的流量
                    count = flow - device.getCurrentTotalFlow();
                    //水机剩余金额=水机剩余金额-流量计费单价*本次同步所使用的流量
                    money = money.subtract(unitPrice.multiply(new BigDecimal(count)));
                }
            } else if (ProductCostTypeEnum.isTime(costType)) {
                //设备使用的天数
                int day;
                Date start = device.getCreateTime();
                Date end = new Date();
                if (workOrder.getPayTerminal() != null && workOrder.getPayTerminal() == PayTerminal.USER.value
                        && workOrder.getPayTime() != null && workOrder.getPayTime().after(start)) {
                    //如果是货到付款（即用户支付）的话，包年计费的开始时间取工单实际支付时间，因为有可能水机安装后用户过了一段时间才支付
                    start = workOrder.getPayTime();
                }
                //180元完款的开始时间计算
                if ((cost.getId() == 5 || cost.getId() == 6) && workOrder.getCompletePayTime() != null) {
                    start = workOrder.getCompletePayTime();
                }
                if (device.getSpecialCostStartTime() != null) {
                    start = device.getSpecialCostStartTime();
                }
                //设备更换过套餐,开始时间从变更套餐那天计算
                if (device.getLastCostChangeTime() != null && !device.getCostChanged()) {
                    start = device.getLastCostChangeTime();
                }
                day = DateUtil.betweenDays(start, end);
                //第一年可以使用天数
                // int firstYearUseDay = 365 + 61;
                //第一年时间计费单价 应该是1180/426=2.77
                //2019-12-05修改，折机水机的扣费及算方式有问题
                // BigDecimal firstYearDayMoney = rentalFee.add(installationFee).divide(new BigDecimal(firstYearUseDay), 2, BigDecimal.ROUND_HALF_UP);
                // BigDecimal firstYearDayMoney = unitPrice;
                if (device.getRenewStatus() != null && device.getRenewStatus() == WaterDeviceRenewStatus.ALREADY.value) {
                    //续费时间
                    start = device.getLastRenewTime();
                    // if (day > firstYearUseDay) {
                    //     firstYearDayMoney = unitPrice;
                    // }
                    //从续费时间到当前时间的天数
                    day = DateUtil.betweenDays(start, end);
                }
                BigDecimal usedMoney = unitPrice.multiply(new BigDecimal(day));
                if (initMoney.compareTo(usedMoney) >= 0) {
                    money = initMoney.subtract(usedMoney);
                } else {
                    money = new BigDecimal(0);
                }
                restDays = money.divide(unitPrice, 0, BigDecimal.ROUND_HALF_UP).intValue();
                //本次同步所使用的天数=已使用的天数-上次记录的已使用的天数
                // count = time - device.getCurrentTotalTime();
                //水机剩余金额=水机剩余金额-时间计费单价*本次同步所使用的天数
                // money = money.subtract(unitPrice.multiply(new BigDecimal(count)));
                update.setCurrentTotalTime(day);
            }

            if (device.getNewCostId() != null && !Objects.equals(device.getCostId(), device.getNewCostId()) && device.getCostChanged()) {
                ProductCostDTO newCost = productFeignHandler.getProductCostById(device.getNewCostId());
                if (newCost == null) {
                    log.error("2.22设备提交数据失败-计费方式不存在。【002】");
                    ResultUtil.error(ru, "19", "计费方式不存在。");
                    return;
                }
                unitPrice = newCost.getUnitPrice();
                threshold1 = newCost.getThreshold1();
                threshold2 = newCost.getThreshold2();
                threshold3 = newCost.getThreshold3();
                costType = newCost.getType();
                if (ProductCostTypeEnum.isTime(costType)) {
                    update.setInitMoney(money);
                    //剩余使用时间
                    restDays = money.divide(unitPrice, 0, BigDecimal.ROUND_HALF_UP).intValue();
                }
                update.setCostId(newCost.getId());
                update.setCostName(newCost.getName());
                update.setCostType(newCost.getType());
                update.setNewCostId(null);
                update.setCostChanged(false);
            }

            if (money.compareTo(new BigDecimal(0)) < 0) {
                money = new BigDecimal(0);
            }
            if (workOrder == null || !workOrder.getPay()) {
                money = new BigDecimal(0);
                ru.put("ispaid", false);
            }
            update.setMoney(money);
            ru.put("money", money);
            ru.put("threshold", threshold1);
            ru.put("threshold2", threshold2);
            ru.put("threshold3", threshold3);
            boolean needNotice;
            if (ProductCostTypeEnum.isFlow(costType)) {
                ru.put("price", unitPrice);
                needNotice = money.divide(unitPrice, 0, BigDecimal.ROUND_HALF_UP).intValue() <= threshold1;
            } else {
                costType = ProductCostTypeEnum.TIME.value;
                ru.put("restDays", restDays);
                //设备剩余使用天数
                update.setDays(restDays);
                needNotice = restDays <= threshold1;
            }
            ru.put("type", costType);
            ru.put("costId", update.getCostId());
            update.setCurrentCostType(costType);
            if (needNotice) {
                try {
                    this.maxValueNotice(device, workOrder);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 封装消息发送（站务系统）
     *
     * @param pushType 推送类型：'10-系统推送 1-阈值提醒,2-续费推送,3-报警推送,4-创建账号,5-新工单分配,6-工单完成,7-审核通过,8-审核不通过,9-经销商续配额',
     * @param areaId 服务站服务区域id
     * @param title    消息标题
     * @param pushMode 2-翼猫APP,3-翼猫服务APP
     */
	@Override
	public void pushMsgToStation(WaterDevice device, int pushType, int mechanism, String title, Integer areaId,
			Map<String, String> stationMessage, Integer filterType) {

		StationMessageDTO message=new StationMessageDTO();
		message.setDeviceId(String.valueOf(device.getId()));
		message.setSncode(device.getSn());
		message.setWorkorderId(device.getWorkOrderId());
		message.setPushType(pushType);
		message.setCreateTime(new Date());
		message.setReceiverId(areaId);
		message.setTitle(title);
		message.setMechanism(mechanism);
		message.setContentMap(stationMessage);
		message.setPushObject(MessagePushObjectEnum.SYSTEM.value);
		message.setMessageType(0);
		message.setFilterType(filterType);
		rabbitTemplate.convertAndSend(RabbitConstant.STATION_MESSAGE_PUSH, message);
	}

    /**
     * 封装消息发送（发送给经销商APP、安装工APP）
     *
     * @param pushType 推送类型：1-余额不足 2-制水故障 3-TDS异常故障 4-PP滤芯故障 5-CTO滤芯故障 6-UDF滤芯故障 7-T33滤芯故障
     * @param receiver 需要发送的对象的姓名 安装工 用名称 经销商用用户id
     * @param devices  手机类型：1-安卓；2-苹果
     * @param title    消息标题
     * @param pushMode 2-翼猫APP,3-翼猫服务APP
     */
    @Override
    public void pushMsgToApp(WaterDevice device, Integer pushType, Integer mechanism, Integer pushMode, String title, Integer receiverId, String receiverName, Integer devices, Map<String, String> distributorMessage, Integer filterType) {
        //构建APP消息推送实体
        AppMessageDTO appMessage = new AppMessageDTO();
        appMessage.setMsgType(Constant.MSG_TYPE_NOTICE);
        appMessage.setDeviceId(String.valueOf(device.getId()));
        appMessage.setSncode(device.getSn());
        appMessage.setWorkorderId(device.getWorkOrderId());
        //appMessage.setContent(content);
        appMessage.setPushType(pushType);
        appMessage.setCreateTime(new Date());
        appMessage.setReceiverId(receiverId);
        appMessage.setReceiver(receiverName);
        appMessage.setDevices(devices);
        appMessage.setFilterType(filterType);
       /* if (devices != PhoneSystemType.ANDROID.value && devices != PhoneSystemType.IOS.value) {
            log.error("手机类型不确定!");
        }*/
        appMessage.setTitle(title);
        appMessage.setApp(pushMode);//1-推送给安装工；2-推送消息给经销商
        //appMessage.setType(type);
        appMessage.setMechanism(mechanism);
        //appMessage.setPushMode(MessagePushModeEnum.YIMAO_ENGINEER_APP_NOTICE.value);

        appMessage.setContentMap(distributorMessage);
        if (MessagePushModeEnum.YIMAO_ENGINEER_APP_NOTICE.value == pushMode) {
            //1-推送给安装工；
            appMessage.setPushObject(MessagePushObjectEnum.ENGINEER.value);
            rabbitTemplate.convertAndSend(RabbitConstant.ENGINEER_APP_MESSAGE_PUSH, appMessage);
        } else {
            //2-推送消息给经销商
            appMessage.setPushObject(MessagePushObjectEnum.DISTRIBUTOR.value);
            rabbitTemplate.convertAndSend(RabbitConstant.YIMAO_APP_MESSAGE_PUSH, appMessage);
        }
    }

    /**
     * 封装消息发送（发送给安装工APP）
     *
     * @param device     水机设备
     * @param faultType  故障类型：1-余额不足；2-制水故障；3-TDS异常；4-滤芯报警；5-阀值提醒；6-续费超期；
     * @param filterType 频次过滤类型：1-余额不足类型推送 2-制水故障类型推送 3-TDS异常故障类型推送 4-PP棉滤芯过期故障类型推送
     *                   5-CTO棉滤芯过期故障类型推送 6- UDF棉滤芯过期故障类型推送 7-T33棉滤芯过期故障类型推送
     */
    @Override
    public Boolean pushMsgToAppByMessageFilter(WaterDevice device, Integer faultType, Integer filterType, String filterName) {
        Integer deviceId = device.getId();
        String sn = device.getSn();
        boolean haveFault = waterDeviceFaultService.existsWith(deviceId, sn, faultType, filterName);
        if (haveFault) {
            //如果有报警信息，计算推送频次
            int hours = 200;
            //hours是可配置的，判断在hours小时之前到当前时间是否有过同类报警推送，如果有推送过消息就不在推送了
            Date compareDate = DateUtil.hourAfter(new Date(), -hours);
            MessagePushDTO mespush = systemFeign.findLastMessagePush(String.valueOf(deviceId), filterType, compareDate);
            return mespush == null;
        } else {
            return true;
        }
    }

    // /**
    //  * 水机同步数据-根据扣费计划计算水机余额
    //  */
    // private void caculatePlanMoney(DeductionPlan currentPlan, WaterDevice device, WorkOrderDTO workOrder, ProductCostDTO cost, Integer flow, Integer time, Map<String, Object> ru, WaterDevice update) {
    //     BigDecimal money = new BigDecimal(0);
    //     int restDays = 0;
    //     if (flow == -1) {
    //         flow = device.getCurrentTotalFlow();
    //     }
    //
    //     boolean flowFlag = ProductCostTypeEnum.isFlow(currentPlan.getDeductionsType());
    //     if (flowFlag) {
    //         //流量计费时设备扣费算法
    //         //本次提交数据距上次提交数据所使用的流量值=本次提交的流量值-上次提交的流量值
    //         int usedFlow = flow - device.getCurrentTotalFlow();
    //         //本次提交数据距上次提交数据所使用的金额=单价*使用的流量
    //         BigDecimal usedMoney = currentPlan.getUnitMoney().multiply(new BigDecimal(usedFlow));
    //     }
    // }

    /**
     * 水机同步数据-根据扣费计划计算水机余额
     */
    private void caculatePlanMoney(List<DeductionPlan> plans, WaterDevice device, WorkOrderDTO workOrder, ProductCostDTO cost, Integer flow, Integer time, Map<String, Object> ru, WaterDevice update) {
        BigDecimal money = new BigDecimal(0);
        int restDays = 0;
        if (flow == -1) {
            flow = device.getCurrentTotalFlow();
        }
        //当前扣费计划
        DeductionPlan currentPlan = null;
        //前一个扣费计划
        DeductionPlan lastPlan = null;
        boolean flowNextCurrent = false;
        BigDecimal lastChangeRestMoney = null;
        for (DeductionPlan plan : plans) {
	        //1 - 有待使用扣费计划状态进入
            if (currentPlan != null) {
                //累计扣费计划上的金额（水机总的剩余金额）
                money = money.add(plan.getFirstDayMoney());
                plan.setStatus(1);
            }
            BigDecimal unitMoney = plan.getUnitMoney();
            BigDecimal firstDayMoney = plan.getFirstDayMoney();
            //如果设备当前扣费计划不存在且计划开始时间为空时
	        //2 - 扣费计划状态未执行的数据走这里
            if ((flowNextCurrent || plan.getStartTime() == null) && currentPlan == null) {
                flowNextCurrent = false;
                if (plan.getStartTime() == null) {
                    if (lastPlan != null && lastPlan.getEndTime() != null) {
                        plan.setStartTime(lastPlan.getEndTime());
                    } else if (lastPlan == null) {
                        plan.setStartTime(new Date());
                    }
                    //当计费方式修改后
                    if (plan.getCostChanged() && lastChangeRestMoney != null) {
                        //int restDay = (int) Math.ceil(lastChangeRestMoney / plan.getUnitMoney());
                        int restDay = lastChangeRestMoney.divide(plan.getUnitMoney(), 0, BigDecimal.ROUND_HALF_UP).intValue();
                        plan.setFirstDayMoney(lastChangeRestMoney);
                        firstDayMoney = lastChangeRestMoney;
                        plan.setDeductionsNum(restDay);
                        plan.setCostChanged(false);
                    }
                    if (ProductCostTypeEnum.isTime(plan.getDeductionsType())) {
                        plan.setEndTime(DateUtil.dayAfter(new Date(), plan.getDeductionsNum()));
                    }
                }
                plan.setFirstDayFlow(flow);
                try {
                    int getbetweendays = DateUtil.betweenDays(plan.getStartTime(), new Date());
                    //money = money + (firstDayMoney - getbetweendays * unitMoney);
                    money = money.add(firstDayMoney.subtract(unitMoney.multiply(new BigDecimal(getbetweendays))));
                } catch (Exception e) {
                    log.error("时间转换异常", e);
                }
                currentPlan = plan;
                plan.setStatus(2);
            }
	        //3-扣费计划状态为使用中的数据走这里
            if (plan.getStartTime() != null && currentPlan == null) {//不管开始时间是否比当前时间更晚都作为当前套餐判断
	            //3.1 包流量计费扣费
                if (plan.getEndTime() == null && ProductCostTypeEnum.isFlow(plan.getDeductionsType())) {//包流量计费
                    int firstDayFlow = plan.getFirstDayFlow();
                    if (firstDayFlow == -1) {
                        firstDayFlow = flow;
                        plan.setFirstDayFlow(firstDayFlow);
                    }

                    int usedFlow = flow - firstDayFlow;
	                //3.1.1 当流量扣费修改为时间扣费
	                if (plan.getCostChanged()) {
                        // firstDayMoney = usedFlow * unitMoney;
                        firstDayMoney = unitMoney.multiply(new BigDecimal(usedFlow));
                        // lastChangeRestMoney = plan.getFirstDayMoney() - firstDayMoney;
                        //流量计费改为时间计费后剩余的时间计费金额
                        lastChangeRestMoney = plan.getFirstDayMoney().subtract(firstDayMoney);
                        plan.setFirstDayMoney(firstDayMoney);
                        plan.setCostChanged(false);
                        plan.setDeductionsNum(usedFlow);
                        //变更套餐把变更前的计费方式置为已使用
                        plan.setStatus(3);
                    }
                    if (usedFlow >= 0) {
                        // money = money + (firstDayMoney - usedFlow * unitMoney);
                        money = money.add(firstDayMoney.subtract(unitMoney.multiply(new BigDecimal(usedFlow))));
                    }
                    if (money.compareTo(new BigDecimal(0)) > 0) {
                        currentPlan = plan;
                        plan.setStatus(2);
                    } else {
                        flowNextCurrent = true;
                        plan.setEndTime(new Date());
                        money = new BigDecimal(0);
                    }
                } else if (plan.getEndTime() != null && plan.getEndTime().after(new Date())) {//3.2 包时长计费扣费（包时长且扣费计划结束时间未超过当前日期即有效期内的扣费计划）
                    try {
                        //扣费计划从时间修改为流量的处理
	                    if (plan.getCostChanged()) {//3.2.1 时间扣费修改为流量计费时
                            //套餐开始时间到当前时间的天数
                            int getBetweendays = DateUtil.betweenDays(plan.getStartTime(), new Date());
                            //按时间计费的费用
                            // firstDayMoney = getBetweendays * unitMoney;
                            firstDayMoney = unitMoney.multiply(new BigDecimal(getBetweendays));
                            //修改为流量计费后的初始计费费用
                            // lastChangeRestMoney = plan.getFirstDayMoney() - firstDayMoney;
                            lastChangeRestMoney = plan.getFirstDayMoney().subtract(firstDayMoney);
                            plan.setFirstDayMoney(firstDayMoney);
                            plan.setCostChanged(false);
                            plan.setDeductionsNum(getBetweendays);
                            flowNextCurrent = true;
                            plan.setEndTime(new Date());
                            //变更套餐把变更前的计费方式置为已使用
                            plan.setStatus(3);
	                    } else {//3.2.2
                            currentPlan = plan;
                            plan.setStatus(2);
                        }
                        int getbetweendays = DateUtil.betweenDays(plan.getStartTime(), new Date());
                        // money = money + (firstDayMoney - getbetweendays * unitMoney);
                        BigDecimal restMoney = firstDayMoney.subtract(unitMoney.multiply(new BigDecimal(getbetweendays)));
                        money = money.add(restMoney);
                    } catch (Exception e) {
                        log.error("时间转换异常", e);
                    }
                } else {//4 - 扣费计划状态已使用过的
                    plan.setStatus(3);
                }
            }
            //上一个扣费计划
            lastPlan = plan;
            deductionPlanService.update(plan);
        }

        //如果是流量扣费需要告知水机流量为多少时需要同步一次后台数据
        if (currentPlan != null && ProductCostTypeEnum.isFlow(currentPlan.getDeductionsType())) {
            int firstDayFlow = currentPlan.getFirstDayFlow();
            ru.put("noticeFlow", firstDayFlow + currentPlan.getDeductionsNum());
        }
        if (currentPlan == null) {
            currentPlan = lastPlan;
        }
        Integer threshold1 = currentPlan.getThreshold();
        Integer threshold2 = cost.getThreshold2();
        Integer threshold3 = cost.getThreshold3();

	    //累计扣费计划上的金额（水机总的剩余金额）
        update.setMoney(money);
        ru.put("money", money);

        boolean needNotice;
        if (lastPlan.getStartTime() == null || !lastPlan.getStartTime().before(new Date())) {
            threshold1 = 0;
            threshold2 = 0;
            threshold3 = 0;
        }
        ru.put("threshold", threshold1);
        ru.put("threshold2", threshold2);
        ru.put("threshold3", threshold3);

        int getbetweendays;
        int type = currentPlan.getDeductionsType();
        if (ProductCostTypeEnum.isTime(type)) {
            // type = ProductCostTypeEnum.TIME.value;
            try {
                getbetweendays = DateUtil.betweenDays(currentPlan.getStartTime(), new Date());
                restDays = currentPlan.getDeductionsNum() - getbetweendays;
            } catch (Exception e) {
                log.error("时间转换异常", e);
            }
            if (restDays < 0) {
                restDays = 0;
            }
            //时间计费剩余天数为0时的处理方法（新系统添加的逻辑）
            if (restDays == 0) {
                //获取下一个扣费计划
                DeductionPlan nextPlan = deductionPlanService.getNext(currentPlan);
                if (nextPlan != null) {
                    if (ProductCostTypeEnum.isTime(nextPlan.getDeductionsType())) {
                        restDays = nextPlan.getDeductionsNum();
                    } else {
                        type = ProductCostTypeEnum.FLOW.value;
                        ru.put("price", nextPlan.getUnitMoney());
                    }
                }
            }
            ru.put("restDays", restDays);
            update.setDays(restDays);
            needNotice = restDays <= threshold1;
        } else {
            needNotice = money.divide(currentPlan.getUnitMoney(), 0, BigDecimal.ROUND_HALF_UP).intValue() <= threshold1;
            ru.put("price", currentPlan.getUnitMoney());
        }

        ru.put("type", type);
        ru.put("currentType", type);
        update.setCurrentCostType(type);
        if (needNotice) {
            try {
                this.maxValueNotice(device, workOrder);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private void maxValueNotice(WaterDevice device, WorkOrderDTO workOrder) {
        boolean exists = waterDeviceFaultService.existsWith(device.getId(), device.getSn(), DeviceFaultType.NOTICE.value, null);
        if (!exists) {
            String deviceUserName = device.getDeviceUserName();
            String deviceUserPhone = device.getDeviceUserPhone();
            String sncode = device.getSn();
            Integer deviceId = device.getId();
            //获取消息模板
            Integer mechanism = MessageMechanismEnum.RENEW_THRESHOLD.value;
            //TODO 以下部分未找到合适模版
           /* if (workOrder.getNeedCompletePay() && (workOrder.getCompletePay() == null || !workOrder.getCompletePay())) {
                mechanism = MessageMechanismEnum.COMPLETION_THRESHOLD.getValue();
            }*/

            //经销商通知推送
            DistributorDTO distributor = userFeign.getDistributorBasicInfoByIdForMsgPushInfo(device.getDistributorId());
            if (!Objects.isNull(distributor)) {
                Map<String, String> distributorMessage = new HashMap<>();
                distributorMessage.put("#code#", sncode);
                distributorMessage.put("#code1#", deviceUserName);
                distributorMessage.put("#code2#", deviceUserPhone);
                pushMsgToApp(device, MessageModelTypeEnum.THRESHOLD_NOTICE.value, mechanism, MessagePushModeEnum.YIMAO_APP_NOTICE.value, MessageModelTypeEnum.THRESHOLD_NOTICE.name, distributor.getUserId(), distributor.getUserName(), distributor.getAppType(), distributorMessage, null);
            }


            //推送消息给站务系统
            //根据工单的省市区查询区域id
   		 	Integer areaId = redisCache.get(Constant.AREA_CACHE + workOrder.getProvince() + "_" + workOrder.getCity() + "_" + workOrder.getRegion(), Integer.class);
            if (areaId == null) {
                areaId = systemFeign.getRegionIdByPCR(workOrder.getProvince(), workOrder.getCity(), workOrder.getRegion());
            }
            EngineerDTO engineer = userFeign.getEngineerBasicInfoByIdForMsgPushInfo(device.getEngineerId());
            if(Objects.nonNull(engineer) && Objects.nonNull(areaId)) {
            	Map<String, String> stationMessage = new HashMap<>();
                stationMessage.put("#code#", sncode);
                stationMessage.put("#code1#", deviceUserName);
                stationMessage.put("#code2#", deviceUserPhone);
                stationMessage.put("#code3#", engineer.getRealName());
                stationMessage.put("#code4#", engineer.getPhone());
                if(Objects.isNull(distributor)) {
                	 stationMessage.put("#code5#", "");
                     stationMessage.put("#code6#", "");
                }else {
                	 stationMessage.put("#code5#", distributor.getRealName());
                     stationMessage.put("#code6#", distributor.getPhone());
                }
                pushMsgToStation(device, MessageModelTypeEnum.THRESHOLD_NOTICE.value, mechanism, "到达续费阈值", areaId, stationMessage,null);
                
            }


            //发送短信给用户
            SmsMessageDTO smsMessage = new SmsMessageDTO();
            smsMessage.setType(MessageModelTypeEnum.THRESHOLD_NOTICE.value);
            smsMessage.setPushObject(MessagePushObjectEnum.WATER_USER.value);
            smsMessage.setPhone(device.getDeviceUserPhone());
            smsMessage.setMechanism(MessageMechanismEnum.RENEW_THRESHOLD.value);
            smsMessage.setPushMode(MessagePushModeEnum.YIMAO_SMS.value);
            Map<String, String> messageMap = new HashMap<>();
            messageMap.put("#code#", sncode);
            smsMessage.setContentMap(messageMap);
            rabbitTemplate.convertAndSend(RabbitConstant.SMS_MESSAGE_PUSH, smsMessage);

            //设备故障信息
            WaterDeviceFaultDTO deviceFault = new WaterDeviceFaultDTO();
            deviceFault.setDeviceId(deviceId);
            deviceFault.setSn(sncode);
            deviceFault.setType(DeviceFaultType.NOTICE.value);
            deviceFault.setFault(DeviceFaultType.NOTICE.name);
            deviceFault.setState(DeviceFaultState.FAULT.value);
            rabbitTemplate.convertAndSend(RabbitConstant.DEVICE_FAULT, deviceFault);
        }
    }



}
