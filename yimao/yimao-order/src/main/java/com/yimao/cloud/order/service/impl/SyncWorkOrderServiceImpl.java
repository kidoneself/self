package com.yimao.cloud.order.service.impl;

import com.yimao.cloud.base.baideApi.utils.BaideApiUtil;
import com.yimao.cloud.base.enums.PayStatus;
import com.yimao.cloud.base.enums.PayTerminal;
import com.yimao.cloud.base.enums.PayType;
import com.yimao.cloud.base.enums.StatusEnum;
import com.yimao.cloud.base.enums.WorkOrderCompleteEnum;
import com.yimao.cloud.base.enums.WorkOrderInstallStep;
import com.yimao.cloud.base.enums.WorkOrderStatusEnum;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.yun.YunOldIdUtil;
import com.yimao.cloud.order.feign.SystemFeign;
import com.yimao.cloud.order.feign.WaterFeign;
import com.yimao.cloud.order.mapper.WorkOrderMapper;
import com.yimao.cloud.order.po.WorkOrder;
import com.yimao.cloud.order.service.SyncWorkOrderService;
import com.yimao.cloud.pojo.dto.system.OnlineAreaDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDTO;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/****
 * 同步售后工单实现
 *
 * @author zhangbaobao
 *
 */
@Service
@Slf4j
public class SyncWorkOrderServiceImpl implements SyncWorkOrderService {

    @Resource
    private WorkOrderMapper workOrderMapper;
    @Resource
    private SystemFeign systemFeign;

    @Resource
    private WaterFeign waterFeign;

    @Override
    public Boolean syncWorkOrder(String workOrderId) {
        Boolean flag = false;
        try {
            WorkOrder workOrder = workOrderMapper.selectByPrimaryKey(workOrderId);
            if (workOrder == null) {
                log.error("业务系统工单数据同步到售后系统失败---未获取到工单信息---工单号={}", workOrderId);
                return flag;
            }
            // 校验是否为上线地区，不是上线地区不同步工单信息到售后
            String province = workOrder.getProvince();
            String city = workOrder.getCity();
            String region = workOrder.getRegion();
            OnlineAreaDTO online = systemFeign.getOnlineAreaByPCR(province, city, region);
            if (online == null || Objects.equals(online.getSyncState(), StatusEnum.NO.value())) {
                log.info("业务系统工单数据同步到售后系统---不是上线地区无需同步---工单号={}", workOrderId);
                return true;
            }

            //根据设备id查询sn码时间
            if (null != workOrder.getDeviceId()) {
                WaterDeviceDTO device = waterFeign.getWaterDeviceById(workOrder.getDeviceId());
                if (null != device && device.getSnEntryTime() != null) {
                    workOrder.setSnCodeTime(device.getSnEntryTime());
                }
            }

            // 组装售后系统的工单数据格式
            Map<String, Object> workOrderInfo = this.buildWorkOrderInfo(workOrder);
            JSONObject obj = JSONObject.fromObject(workOrderInfo);
            Map<String, Object> result = BaideApiUtil.sync(obj.toString());
            JSONObject json = JSONObject.fromObject(result);
            log.info("业务系统工单数据同步到售后系统---工单号={}---同步结果为=", workOrderId, json.toString());
            Date now = new Date();
            WorkOrder update = new WorkOrder();
            update.setId(workOrderId);
            if (null != json.get("code") && "00000000".equals(json.get("code").toString())) {
                JSONObject jsonData = JSONObject.fromObject(json.get("data"));
                boolean syncStatus = jsonData.getBoolean("syncStatus");
                String step = jsonData.getString("step");
                flag = syncStatus;
                if (syncStatus) {
                    // 工单同步售后成功
                    update.setSyncBaideStatus(StatusEnum.YES.value());
                    update.setSyncBaideTime(now);
                } else {
                    update.setSyncBaideStatus(StatusEnum.NO.value());
                }
                if (StringUtil.isNotEmpty(step)) {
                    update.setNextStep(Integer.parseInt(step));
                } else {
                    // 返回的step为空，表示工单已完了
                    if (workOrder.getNextStep() == null) {
                        update.setNextStep(WorkOrderInstallStep.ELEVEN.value);
                    }
                    update.setCompleteType(Objects.nonNull(workOrder.getCompleteType()) ? workOrder.getCompleteType() : WorkOrderCompleteEnum.NORMAL_COMPLETE.getState());// 1-正常完成
                    update.setStatus(WorkOrderStatusEnum.COMPLETED.value);
                    update.setStatusText("已完成");
                    if (workOrder.getCompleteTime() == null) {
                        update.setCompleteTime(now);
                    }
                }
            } else {
                String msg = json.getString("msg");
                log.info("============(workorderId=" + workOrderId + ")同步售后失败信息============:" + msg);
                update.setSyncBaideStatus(StatusEnum.FAILURE.value());
                if (msg != null && msg.length() < 60) {
                    update.setSyncBaideFailureText(msg);
                    update.setSyncBaideTime(now);
                }
            }
            workOrderMapper.updateByPrimaryKeySelective(update);
        } catch (Exception e) {
            flag = false;
            log.error("业务系统工单数据同步到售后系统异常---工单号={}", workOrderId);
            log.error(e.getMessage(), e);
        }
        return flag;

    }

    /**
     * 业务系统工单字段转换成售后系统工单字段
     */
    private Map<String, Object> buildWorkOrderInfo(WorkOrder workOrder) {
        Map<String, Object> workOrderInfo = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        workOrderInfo.put("code", workOrder.getId());
        // 支付状态和时间
        workOrderInfo.put("payStatus", workOrder.getPay() == null ? false : workOrder.getPay());
        if (workOrder.getPayTime() != null) {
            workOrderInfo.put("payTime", DateUtil.dateToString(workOrder.getPayTime()));
        }
        // 接单状态和时间
        workOrderInfo.put("acceptStatus", WorkOrderStatusEnum.accepted(workOrder.getStatus()));
        if (workOrder.getAcceptTime() != null) {
            workOrderInfo.put("acceptTime", DateUtil.dateToString(workOrder.getAcceptTime()));
        }
        // 预约时间
        if (workOrder.getAppointTime() != null && WorkOrderStatusEnum.accepted(workOrder.getStatus())) {
            workOrderInfo.put("planServiceTime", DateUtil.dateToString(workOrder.getAppointTime()));
        }
        // 云签状态和时间
        workOrderInfo.put("signStatus", StatusEnum.isYes(workOrder.getSignStatus()));
        if (workOrder.getSignTime() != null) {
            workOrderInfo.put("signTime", DateUtil.dateToString(workOrder.getSignTime()));
        }
        // 开票状态和时间
        workOrderInfo.put("billStatus", workOrder.getInvoice() == null ? false : workOrder.getInvoice());
        if (workOrder.getInvoiceTime() != null) {
            workOrderInfo.put("billTime", DateUtil.dateToString(workOrder.getInvoiceTime()));
        }
        // 完成状态和时间
        workOrderInfo.put("completeStatus", WorkOrderStatusEnum.completed(workOrder.getStatus()));
        if (workOrder.getCompleteTime() != null) {
            workOrderInfo.put("completeTime", DateUtil.dateToString(workOrder.getCompleteTime()));
        }
        // 创建时间
        if (workOrder.getCreateTime() != null) {
            workOrderInfo.put("createTime", DateUtil.dateToString(workOrder.getCreateTime()));
        }
        workOrderInfo.put("orderId", workOrder.getSubOrderId().toString());
        // 退单信息
        boolean chargeback = workOrder.getChargeback() == null ? false : workOrder.getChargeback();
        workOrderInfo.put("isBackOrder", workOrder.getChargeback() == null ? false : workOrder.getChargeback());
        if (chargeback) {
            if (workOrder.getChargebackStatus() != null) {
                if (workOrder.getChargebackStatus() == 0) {
                    workOrderInfo.put("backOrderState", 3);
                } else if (workOrder.getChargebackStatus() == 1) {
                    workOrderInfo.put("backOrderState", 1);
                } else if (workOrder.getChargebackStatus() == 2) {
                    workOrderInfo.put("backOrderState", 2);
                }
            }
            data.put("backOrderRemark", workOrder.getChargebackRemark());
            data.put("backOrderReason", workOrder.getChargebackReason());
            if (workOrder.getChargebackTime() != null) {
                data.put("backTime", DateUtil.dateToString(workOrder.getChargebackTime()));
            }
            // if (workOrder.getDistributorVerifyBackOrderTime() != null) {
            //     data.put("distributorVerifyBackOrderTime",
            //             DateUtil.dateToString(workOrder.getDistributorVerifyBackOrderTime()));
            // }
            // if (workOrder.getHeadquartersVerifyBackOrderTime() != null) {
            //     data.put("headquartersVerifyBackOrderTime",
            //             DateUtil.dateToString(workOrder.getHeadquartersVerifyBackOrderTime()));
            // }
        }

        workOrderInfo.put("isOldOrder", false);
        workOrderInfo.put("confirmUserInfoStatus",
                workOrder.getUserConfirm() == null ? false : workOrder.getUserConfirm());

        workOrderInfo.put("isUserOrder", false);
        data.put("payStyle",
                workOrder.getPayTerminal() != null && workOrder.getPayTerminal() == PayTerminal.DEALER.value ? 1 : 0);
        data.put("contractionNum", workOrder.getConfirmation());
        data.put("charging", workOrder.getOldCostId());
        data.put("chargingName", workOrder.getCostName());
        data.put("accountFee", workOrder.getOpenAccountFee().doubleValue());
        //服务费
        double serviceMoney = workOrder.getFee().doubleValue() - workOrder.getOpenAccountFee().doubleValue();
        data.put("payFee", serviceMoney);
        data.put("province", workOrder.getProvince());
        data.put("city", workOrder.getCity());
        data.put("region", workOrder.getRegion());
        // data.put("userId", workOrder.getUserId());
        data.put("userId", workOrder.getOldUserId());
        data.put("userName", workOrder.getUserName());
        data.put("userPhone", workOrder.getUserPhone());
        // data.put("distributorId", workOrder.getDistributorId());
        data.put("distributorId", workOrder.getOldDistributorId());
        data.put("distributorName", workOrder.getDistributorName());
        data.put("distributorPhone", workOrder.getDistributorPhone());
        data.put("distributorIdCard", workOrder.getDistributorIdCard());
        data.put("distributorRoleName", workOrder.getDistributorRoleName());
        data.put("distributorAccount", workOrder.getDistributorAccount());
        if (workOrder.getSubDistributorId() != null) {
            // data.put("distributorChildId", workOrder.getSubDistributorId());
            data.put("distributorChildId", workOrder.getOldSubDistributorId());
            data.put("distributorChildName", workOrder.getSubDistributorName());
            data.put("distributorChildAccount", workOrder.getSubDistributorAccount());
        }
        data.put("scanCodeType", workOrder.getScanCodeType());

        data.put("engineerId", workOrder.getOldEngineerId());
        data.put("engineerName", workOrder.getEngineerName());
        data.put("engineerPhone", workOrder.getEngineerPhone());

        data.put("serviceStationId", workOrder.getOldStationId());
        data.put("chargeTypeId", workOrder.getOldCostId());
        data.put("chargeTypeName", workOrder.getCostName());

        // 设备相关信息
        data.put("productTypeId", YunOldIdUtil.getProductTypeId());
        data.put("productTypeName", YunOldIdUtil.getProductTypeName());
        data.put("productRangeId", YunOldIdUtil.getProductScopeId(workOrder.getDeviceModel()));
        data.put("productRangeName", YunOldIdUtil.getProductScope(workOrder.getDeviceModel()));
        data.put("productId", YunOldIdUtil.getProductId(workOrder.getDeviceModel()));
        data.put("productName", workOrder.getDeviceModel());
        data.put("sncode", workOrder.getSn());
        data.put("batchCode", workOrder.getLogisticsCode());
        data.put("simcard", workOrder.getSimCard());

        data.put("address", workOrder.getAddress());
        data.put("contractId", workOrder.getSignOrderId());
        if (null != workOrder.getPayType()) {
            if (PayType.WECHAT.value == workOrder.getPayType()) {
                data.put("payType", 1);
            } else if (PayType.ALIPAY.value == workOrder.getPayType()) {
                data.put("payType", 0);
            } else {
                data.put("payType", 2);
                Integer payStatus = workOrder.getPayStatus();
                if (payStatus != null) {
                    if (payStatus == PayStatus.PAY.value) {
                        data.put("accountingStatus", true);
                        data.put("accountingStatusText", "审核通过");
                        data.put("auditState", "1");
                    } else if (payStatus == PayStatus.WAITING_AUDIT.value) {
                        data.put("accountingStatus", false);
                        data.put("accountingStatusText", "审核中");
                        data.put("auditState", "0");
                    } else if (payStatus == PayStatus.FAIL.value) {
                        data.put("accountingStatus", false);
                        data.put("accountingStatusText", "审核未通过");
                        data.put("auditState", "2");
                    }
                }
            }
        }
        data.put("payTaxNo", workOrder.getTradeNo());
        // 经销商评价内容
        data.put("appraise", workOrder.getDistributorAppraiseContent());
        // 完款信息添加
        data.put("payCompleteStatus", workOrder.getCompletePay());
        data.put("payCompleteOrderNo", workOrder.getCompleteOutTradeNo());
        data.put("payCompleteTradeNo", workOrder.getCompleteTradeNo());
        if (null != workOrder.getCompletePayTime()) {
            data.put("payCompleteTime", DateUtil.dateToString(workOrder.getCompletePayTime()));
        }

        if (null != workOrder.getCompletePayType()) {
            if (PayType.WECHAT.value == workOrder.getCompletePayType()) {
                data.put("payCompletePayType", 1);
            } else if (PayType.ALIPAY.value == workOrder.getCompletePayType()) {
                data.put("payCompletePayType", 0);
            } else {
                data.put("payCompletePayType", 2);
            }
        }
        if (null != workOrder.getCompletePayFee()) {
            data.put("payCompleteMoney", workOrder.getCompletePayFee().doubleValue());
        }

        //sn设备激活时间
        if (null != workOrder.getSnCodeTime()) {
            data.put("sncodeTime", DateUtil.dateToString(workOrder.getSnCodeTime()));
        }
        workOrderInfo.put("data", data);
        return workOrderInfo;
    }

}
