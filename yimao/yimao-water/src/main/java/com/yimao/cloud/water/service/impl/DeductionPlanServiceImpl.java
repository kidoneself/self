package com.yimao.cloud.water.service.impl;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.PayTerminal;
import com.yimao.cloud.base.enums.ProductCostTypeEnum;
import com.yimao.cloud.base.enums.WaterDeviceRenewStatus;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import com.yimao.cloud.pojo.dto.product.ProductCostDTO;
import com.yimao.cloud.water.feign.OrderFeign;
import com.yimao.cloud.water.feign.ProductFeign;
import com.yimao.cloud.water.handler.ProductFeignHandler;
import com.yimao.cloud.water.mapper.DeductionPlanChangeRecordMapper;
import com.yimao.cloud.water.mapper.DeductionPlanMapper;
import com.yimao.cloud.water.mapper.WaterDeviceMapper;
import com.yimao.cloud.water.po.DeductionPlan;
import com.yimao.cloud.water.po.DeductionPlanChangeRecord;
import com.yimao.cloud.water.po.WaterDevice;
import com.yimao.cloud.water.service.DeductionPlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 描述：水机设备扣费计划
 *
 * @Author Zhang Bo
 * @Date 2019/9/3
 */
@Service
@Slf4j
public class DeductionPlanServiceImpl implements DeductionPlanService {

    @Resource
    private DeductionPlanMapper deductionPlanMapper;
    @Resource
    private WaterDeviceMapper waterDeviceMapper;
    @Resource
    private DeductionPlanChangeRecordMapper deductionPlanChangeRecordMapper;
    @Resource
    private OrderFeign orderFeign;
    @Resource
    private ProductFeignHandler productFeignHandler;
    @Resource
    private ProductFeign productFeign;
    @Resource
    private UserCache userCache;

    @Override
    public void save(DeductionPlan plan) {
        deductionPlanMapper.insert(plan);
    }

    @Override
    public void update(DeductionPlan plan) {
        deductionPlanMapper.updateByPrimaryKey(plan);
    }

    @Override
    public List<DeductionPlan> listByDeviceId(Integer deviceId) {
        return deductionPlanMapper.listAll(deviceId);
    }

    @Override
    public List<DeductionPlanChangeRecord> listChangeRecordByDeviceId(Integer deviceId) {
        return deductionPlanChangeRecordMapper.listAll(deviceId);
    }

    /**
     * 水机续费时如果没有扣费计划记录，创建设备当前扣费计划
     */
    @Override
    public DeductionPlan caculate(WaterDevice device) {
        BigDecimal money = device.getMoney();
        BigDecimal initMoney = device.getInitMoney();
        Integer currentTotalFlow = device.getCurrentTotalFlow();
        //设备当前计费方式
        ProductCostDTO cost = productFeignHandler.getProductCostById(device.getCostId());
        //设备有修改计费方式，在这里进行计费方式切换
        if (device.getNewCostId() != null && !Objects.equals(device.getNewCostId(), device.getCostId()) && device.getCostChanged()) {
            cost = productFeignHandler.getProductCostById(device.getNewCostId());
            //包流量改成包年套餐
            if (ProductCostTypeEnum.isTime(cost.getType())) {
                //计费方式切换，需要把当前的余额设置为初始化金额（计费方式不一样了，按新方式扣费需要把当前的余额作为扣费初始值）
                initMoney = money;
            }
            waterDeviceMapper.updateChangedCostInfo(device.getId(), cost.getId(), cost.getName(), cost.getType());
        }
        //套餐租赁费
        BigDecimal rentalFee = cost.getRentalFee();
        //开户费
        BigDecimal installationFee = cost.getInstallationFee();
        //单价
        BigDecimal unitPrice = cost.getUnitPrice();
        Date currentDate = new Date();
        //流量计费
        if (ProductCostTypeEnum.isFlow(cost.getType())) {
            //大于0
            if (money.compareTo(new BigDecimal(0)) > 0) {
                DeductionPlan plan = new DeductionPlan();
                plan.setDeviceId(device.getId());
                plan.setSnCode(device.getSn());
                plan.setCostId(cost.getId());
                plan.setDeductionsType(ProductCostTypeEnum.FLOW.value);
                //阀值
                plan.setThreshold(cost.getThreshold1());
                //扣费计划开始第一天剩余流量
                plan.setFirstDayFlow(currentTotalFlow);
                //扣费计划开始第一天剩余金额
                plan.setFirstDayMoney(money);
                //单价
                plan.setUnitMoney(unitPrice);
                //剩余金额除以单价等于剩余可扣流量（升）
                BigDecimal remain = money.divide(unitPrice, 0, BigDecimal.ROUND_HALF_UP);
                //剩余多少升流量
                plan.setDeductionsNum(remain.intValue());
                plan.setStartTime(currentDate);
                plan.setCreateTime(currentDate);
                plan.setSorts(1);
                plan.setCostChanged(false);
                plan.setStatus(1);//当前扣费计划使用状态：1-待使用；2-使用中；3-已使用；4-已失效（变更套餐时将当前设置为已失效，新建一条）
                deductionPlanMapper.insert(plan);
                return plan;
            }
        } else if (ProductCostTypeEnum.isTime(cost.getType())) {
            //设备使用的天数
            int day;
            Date startTime = device.getCreateTime();
            WorkOrderDTO workOrder = orderFeign.getWorkOrderById(device.getWorkOrderId());
            if (workOrder.getPayTerminal() != null && workOrder.getPayTerminal() == PayTerminal.USER.value) {
                //如果是货到付款（即用户支付）的话，包年计费的开始时间取工单实际支付时间，因为有可能水机安装后用户过了一段时间才支付
                startTime = workOrder.getPayTime();
            }
            Date start = startTime;
            Date currentTime = new Date();
            boolean costChanged = false;
            //设备更换过套餐,开始时间从变更套餐那天计算
            if (device.getLastCostChangeTime() != null && !device.getCostChanged()) {
                start = device.getLastCostChangeTime();
                costChanged = true;
            }
            //第一年可以使用天数
            int firstYearUseDay = 365 + 61;
            //第一年时间计费单价 应该是1180/426=2.77
            BigDecimal firstYearDayMoney = rentalFee.add(installationFee).divide(new BigDecimal(firstYearUseDay), 2, BigDecimal.ROUND_HALF_UP);
            //首年按时间计费开始时间到当前时间的天数
            day = DateUtil.betweenDays(start, currentTime);
            if (costChanged) {
                int changeTime = DateUtil.betweenDays(startTime, device.getLastCostChangeTime());
                //首年流量计费修改为时间计费后，时间计费套餐剩余的使用天数
                firstYearUseDay = firstYearUseDay - changeTime;
            }
            //是否是首年
            boolean isFirestYear = day <= firstYearUseDay;
            //还有多少天到期
            int firstYearLeftoverDay = firstYearUseDay - day;
            //第一年结束时间
            Date firstYearEndDay = DateUtil.dayAfter(start, firstYearUseDay);

            //已续费
            //第二年续费后不计算赠送部分
            if (null != device.getRenewStatus() && device.getRenewStatus() == WaterDeviceRenewStatus.ALREADY.value) {
                //续费时间
                start = device.getLastRenewTime();
                //从续费时间到当前时间的天数
                day = DateUtil.betweenDays(start, currentTime);
                if (!isFirestYear) {
                    money = initMoney.subtract(unitPrice.multiply(new BigDecimal(day)));
                    int restDay = money.divide(unitPrice, 0, BigDecimal.ROUND_HALF_UP).intValue();
                    DeductionPlan plan = new DeductionPlan();
                    plan.setDeviceId(device.getId());
                    plan.setSnCode(device.getSn());
                    plan.setCostId(cost.getId());
                    plan.setDeductionsType(ProductCostTypeEnum.TIME.value);
                    //阀值
                    plan.setThreshold(cost.getThreshold1());
                    plan.setFirstDayMoney(money);
                    plan.setUnitMoney(unitPrice);
                    plan.setDeductionsNum(restDay);
                    plan.setStartTime(currentDate);
                    plan.setEndTime(DateUtil.dayAfter(currentDate, restDay));
                    plan.setCreateTime(currentDate);
                    plan.setSorts(1);
                    plan.setCostChanged(false);
                    plan.setStatus(1);//当前扣费计划使用状态：1-待使用；2-使用中；3-已使用；4-已失效（变更套餐时将当前设置为已失效，新建一条）
                    deductionPlanMapper.insert(plan);
                    return plan;
                } else {
                    //第一年扣费未扣完情况，并且又续费的情况，生成剩余金额的扣费计划
                    BigDecimal firstDayMoney = firstYearDayMoney.multiply(new BigDecimal(firstYearLeftoverDay));
                    DeductionPlan plan = new DeductionPlan();
                    plan.setDeviceId(device.getId());
                    plan.setSnCode(device.getSn());
                    plan.setCostId(cost.getId());
                    plan.setDeductionsType(ProductCostTypeEnum.TIME.value);
                    //阀值
                    plan.setThreshold(cost.getThreshold1());
                    plan.setFirstDayMoney(firstDayMoney);
                    plan.setUnitMoney(firstYearDayMoney);
                    plan.setDeductionsNum(firstYearLeftoverDay);
                    plan.setStartTime(currentDate);
                    plan.setEndTime(firstYearEndDay);
                    plan.setCreateTime(currentDate);
                    plan.setSorts(1);
                    plan.setCostChanged(false);
                    plan.setStatus(1);//当前扣费计划使用状态：1-待使用；2-使用中；3-已使用；4-已失效（变更套餐时将当前设置为已失效，新建一条）
                    deductionPlanMapper.insert(plan);

                    //第一年扣费未扣完情况，并且又续费的情况，生成续费的扣费计划 TODO
                    money = money.subtract(firstDayMoney);
                    int restDay = money.divide(unitPrice, 0, BigDecimal.ROUND_HALF_UP).intValue();
                    DeductionPlan renewPlan = new DeductionPlan();
                    plan.setDeviceId(device.getId());
                    plan.setSnCode(device.getSn());
                    plan.setCostId(cost.getId());
                    renewPlan.setDeductionsType(ProductCostTypeEnum.TIME.value);
                    //阀值
                    plan.setThreshold(cost.getThreshold1());
                    renewPlan.setFirstDayMoney(money);
                    renewPlan.setUnitMoney(unitPrice);
                    renewPlan.setDeductionsNum(restDay);
                    plan.setCreateTime(currentDate);
                    plan.setSorts(2);
                    plan.setCostChanged(false);
                    plan.setStatus(1);//当前扣费计划使用状态：1-待使用；2-使用中；3-已使用；4-已失效（变更套餐时将当前设置为已失效，新建一条）
                    deductionPlanMapper.insert(renewPlan);
                    return renewPlan;
                }
            } else {
                money = initMoney.subtract(firstYearDayMoney.multiply(new BigDecimal(day)));
                if (money.compareTo(new BigDecimal(0)) > 0) {
                    int restDay = money.divide(firstYearDayMoney, 0, BigDecimal.ROUND_HALF_UP).intValue();
                    DeductionPlan plan = new DeductionPlan();
                    plan.setDeviceId(device.getId());
                    plan.setSnCode(device.getSn());
                    plan.setCostId(cost.getId());
                    plan.setDeductionsType(ProductCostTypeEnum.TIME.value);
                    //阀值
                    plan.setThreshold(cost.getThreshold1());
                    plan.setFirstDayMoney(money);
                    plan.setUnitMoney(unitPrice);
                    plan.setDeductionsNum(restDay);
                    plan.setStartTime(currentDate);
                    plan.setEndTime(DateUtil.dayAfter(currentDate, restDay));
                    plan.setCreateTime(currentDate);
                    plan.setSorts(1);
                    plan.setCostChanged(false);
                    plan.setStatus(1);//当前扣费计划使用状态：1-待使用；2-使用中；3-已使用；4-已失效（变更套餐时将当前设置为已失效，新建一条）
                    deductionPlanMapper.insert(plan);
                    return plan;
                }
            }
        }
        return null;
    }

    /**
     * 获取下一个扣费计划
     */
    @Override
    public DeductionPlan getNext(DeductionPlan currentPlan) {
        return deductionPlanMapper.selectNextPlan(currentPlan.getDeviceId(), currentPlan.getSorts(), currentPlan.getCreateTime());
    }

    /**
     * 更换设备时更新扣费计划
     *
     * @param deviceId         设备ID
     * @param currentTotalFlow 设备当前使用的套餐总流量
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void updatePlansForChangeDevice(Integer deviceId, Integer currentTotalFlow, String oldSn, String newSn) {
        BigDecimal zero = new BigDecimal(0);
        int currentflow = currentTotalFlow;
        //更换设备，扣费套餐需要清0;
        int currentPlanIndex = this.getCurrentPlanIndex(deviceId, currentTotalFlow);
        if (currentPlanIndex >= 0) {
            //更换设备时更新扣费计划数据上的SN码
            deductionPlanMapper.updateSnForChangeDevice(deviceId, oldSn, newSn);
            List<DeductionPlan> plans = this.listByDeviceId(deviceId);
            if (plans.size() > currentPlanIndex) {
                DeductionPlan plan = plans.get(currentPlanIndex);
                if (plan.getDeductionsType() == ProductCostTypeEnum.FLOW.value) {
                    int firstDayFlow = plan.getFirstDayFlow();
                    if (firstDayFlow != -1) {
                        try {
                            Date now = new Date();
                            int useFlow = currentflow - firstDayFlow;
                            if (plan.getStartTime() == null) {
                                plan.setStartTime(now);
                            }
                            plan.setEndTime(now);
                            plan.setDeductionsNum(currentflow);
                            BigDecimal useMoney = plan.getUnitMoney().multiply(new BigDecimal(useFlow));
                            BigDecimal leftMoney = plan.getFirstDayMoney().subtract(useMoney);
                            plan.setFirstDayMoney(useMoney);
                            //当前扣费计划使用状态：1-待使用；2-使用中；3-已使用；4-已失效（变更套餐时将当前设置为已失效，新建一条）
                            plan.setStatus(3);
                            deductionPlanMapper.updateByPrimaryKeySelective(plan);

                            //结束当前扣费方式，生成新的扣费方式
                            DeductionPlan newPlan = new DeductionPlan();
                            newPlan.setDeviceId(plan.getDeviceId());
                            newPlan.setSnCode(plan.getSnCode());
                            newPlan.setCostId(plan.getCostId());
                            //阈值
                            newPlan.setThreshold(plan.getThreshold());
                            newPlan.setDeductionsType(ProductCostTypeEnum.FLOW.value);
                            newPlan.setFirstDayMoney(leftMoney.compareTo(zero) < 0 ? zero : leftMoney);
                            newPlan.setUnitMoney(plan.getUnitMoney());
                            //剩余金额除以单价等于剩余可扣流量（升）
                            BigDecimal remain = leftMoney.divide(plan.getUnitMoney(), 0, BigDecimal.ROUND_HALF_UP);
                            //剩余多少升流量
                            newPlan.setDeductionsNum(remain.intValue());
                            newPlan.setCreateTime(now);
                            newPlan.setSorts(plan.getSorts());
                            newPlan.setCostChanged(false);
                            newPlan.setStatus(1);
                            deductionPlanMapper.insert(newPlan);
                        } catch (Exception e) {
                            log.error("时间转换异常", e);
                        }
                    }
                }
                log.info("更换设备时，更新扣费计划成功");
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void changePlan(Integer planId) {
        DeductionPlan plan = deductionPlanMapper.selectByPrimaryKey(planId);
        if (plan == null) {
            throw new BadRequestException("请选择需要修改的扣费计费方式");
        }
        if (plan.getStatus() == null || plan.getStatus() != 1 && plan.getStatus() != 2) {
            throw new BadRequestException("该扣费计费方式不支持修改");
        }
        if (plan.getCostChanged() != null && plan.getCostChanged()) {
            throw new BadRequestException("该扣费计费方式正在变更中，待数据同步后方可再次修改");
        }
        Integer oldCostId = plan.getCostId();
        //设备当前计费方式
        List<ProductCostDTO> costList = productFeign.listProductRenewCostByOldCostId(oldCostId);
        ProductCostDTO cost = null;
        if (CollectionUtil.isNotEmpty(costList)) {
            for (ProductCostDTO dto : costList) {
                if (!Objects.equals(dto.getType(), plan.getDeductionsType())) {
                    cost = dto;
                }
            }
        }
        if (cost == null) {
            throw new BadRequestException("未能获取到修改后的扣费计费方式");
        }
        int newDeductionsType = cost.getType();
        int oldDeductionsType = plan.getDeductionsType();
        Integer deviceId = plan.getDeviceId();
        String snCode = plan.getSnCode();
        Date now = new Date();
        BigDecimal unitPrice = cost.getUnitPrice();
        DeductionPlan newPlan = null;
        if (plan.getStatus() == 1) {
            plan.setDeductionsType(newDeductionsType);
            plan.setUnitMoney(unitPrice);
            plan.setDeductionsNum(plan.getFirstDayMoney().divide(unitPrice, 0, BigDecimal.ROUND_HALF_UP).intValue());
            plan.setThreshold(cost.getThreshold1());
            plan.setCostId(cost.getId());
            plan.setUpdateTime(now);
        } else {
            plan.setCostChanged(true);
            //结束当前扣费方式，生成新的扣费方式
            newPlan = new DeductionPlan();
            newPlan.setDeviceId(deviceId);
            newPlan.setSnCode(snCode);
            newPlan.setCostId(cost.getId());
            newPlan.setDeductionsType(newDeductionsType);
            newPlan.setThreshold(cost.getThreshold1());
            newPlan.setUnitMoney(unitPrice);
            newPlan.setCreateTime(now);
            newPlan.setSorts(plan.getSorts());
            newPlan.setCostChanged(true);
            newPlan.setStatus(1);//当前扣费计划使用状态：1-待使用；2-使用中；3-已使用；4-已失效（变更套餐时将当前设置为已失效，新建一条）
        }
        deductionPlanMapper.updateByPrimaryKey(plan);
        if (newPlan != null) {
            deductionPlanMapper.insert(newPlan);
        }
        //保存手动修改扣费计划记录
        DeductionPlanChangeRecord planChangeRecord = new DeductionPlanChangeRecord();
        planChangeRecord.setCreator(userCache.getCurrentAdminRealName());
        planChangeRecord.setDeviceId(deviceId);
        planChangeRecord.setPlanId(plan.getId());
        String opereation = "";
        if (oldDeductionsType == ProductCostTypeEnum.FLOW.value) {
            opereation = ProductCostTypeEnum.FLOW.name + "修改为" + ProductCostTypeEnum.TIME.name;
        } else if (oldDeductionsType == ProductCostTypeEnum.TIME.value) {
            opereation = ProductCostTypeEnum.TIME.name + "修改为" + ProductCostTypeEnum.FLOW.name;
        }
        planChangeRecord.setOperation(opereation);
        planChangeRecord.setCreateTime(now);
        deductionPlanChangeRecordMapper.insert(planChangeRecord);
    }

    private int getCurrentPlanIndex(Integer deviceId, Integer currentTotalFlow) {
        BigDecimal zero = new BigDecimal(0);
        BigDecimal money = zero;
        int currentIndex = -1;
        int flow = currentTotalFlow;
        List<DeductionPlan> plans = this.listByDeviceId(deviceId);
        if (CollectionUtil.isEmpty(plans)) {
            return currentIndex;
        }
        boolean flowNextCurrent = false;
        for (DeductionPlan plan : plans) {
            currentIndex = currentIndex + 1;
            BigDecimal unitMoney = plan.getUnitMoney();
            BigDecimal firstDayMoney = plan.getFirstDayMoney();
            if (flowNextCurrent || plan.getStartTime() == null) {
                //如果设备当前扣费计划不存在且计划开始时间为空时
                return currentIndex;
            }
            if (plan.getStartTime() != null && plan.getStartTime().before(new Date())) {
                if (plan.getEndTime() == null && plan.getDeductionsType() == ProductCostTypeEnum.FLOW.value) {//包流量计费
                    int firstDayFlow = plan.getFirstDayFlow();
                    if (firstDayFlow == -1) {
                        firstDayFlow = flow;
                    }
                    int usedFlow = flow - firstDayFlow;
                    if (plan.getCostChanged()) {
                        //当流量扣费修改为时间扣费
                        firstDayMoney = unitMoney.multiply(new BigDecimal(usedFlow));
                    }
                    if (usedFlow >= 0) {
                        money = money.add(firstDayMoney.subtract(unitMoney.multiply(new BigDecimal(usedFlow))));
                    }
                    if (money.compareTo(zero) > 0) {
                        return currentIndex;
                    } else {
                        flowNextCurrent = true;
                        money = zero;
                    }
                } else if (plan.getEndTime() != null && plan.getEndTime().after(new Date())) {
                    if (plan.getCostChanged()) {
                        //时间扣费修改为流量计费时
                        flowNextCurrent = true;
                    } else {
                        return currentIndex;
                    }
                }
            }
        }
        return currentIndex;
    }

}
