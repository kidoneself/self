package com.yimao.cloud.order.configuration;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.order.constant.OrderConstant;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {

    /**
     * 翼猫系统健康产品15天到期队列
     */
//    @Bean
//    public Queue orderCompleteHealthyQueue() {
//        return new Queue(OrderConstant.ORDER_COMPLETE_HEALTHY_QUEUE, true);
//    }

    /**
     * 翼猫系统工单完成队列
     */
    @Bean
    public Queue orderCompleteWaterQueue() {
        return new Queue(OrderConstant.ORDER_COMPLETE_WATER_QUEUE, true);
    }

    /**
     * 提现审核日志
     */
    @Bean
    public Queue withdrawAuditQueue() {
        return new Queue(RabbitConstant.WITHDRAW_AUDIT, true);
    }

    /**
     * 升级会员用户
     */
    @Bean
    public Queue upgradevip() {
        return new Queue(RabbitConstant.USER_UPGRADEVIP, true);
    }

    /**
     * 订单支付成功回调
     */
    @Bean
    public Queue orderPayCallback() {
        return new Queue(RabbitConstant.ORDER_PAY_CALLBACK, true);
    }

    /**
     * 续费订单支付成功回调
     */
    @Bean
    public Queue renewOrderPayCallback() {
        return new Queue(RabbitConstant.RENEWORDER_PAY_CALLBACK, true);
    }

    /**
     * 经销商订单支付成功回调
     */
    @Bean
    public Queue distributorOrderPayCallback() {
        return new Queue(RabbitConstant.DISTRIBUTOR_ORDER_PAY_CALLBACK, true);
    }

    /**
     * 发送短信
     */
    @Bean
    public Queue sendPhoneMessageQueue() {
        return new Queue(RabbitConstant.PHONE_MESSAGE_SEND, true);
    }

    /**
     * 退款回调
     */
    @Bean
    public Queue refundCallback() {
        return new Queue(RabbitConstant.REFUND_CALLBACK, true);
    }

    /**
     * 配额变化记录
     */
//    @Bean
//    public Queue quotaChangeRecordQueue() {
//        return new Queue(RabbitConstant.QUOTA_CHANGE_RECORD, true);
//    }

    /**
     * 工单支付成功回调
     */
    @Bean
    public Queue workOrderOrderPayCallback() {
        return new Queue(RabbitConstant.WORK_ORDER_PAY_CALLBACK, true);
    }

    /**
     * 工单操作记录
     */
    @Bean
    public Queue workOrderOperation() {
        return new Queue(RabbitConstant.WORK_ORDER_OPERATION, true);
    }

    /**
     * 业务系统工单数据同步到售后系统
     */
    @Bean
    public Queue syncWorkOrder() {
        return new Queue(RabbitConstant.SYNC_WORK_ORDER, true);
    }

    /**
     * 老流程补库存后，设置工单step
     */
    @Bean
    public Queue updateWorkOrderStep() {
        return new Queue(RabbitConstant.RESET_WORK_ORDER_STEP, true);
    }

    @Bean
    public Queue insertOrderInvoice() {
        return new Queue(RabbitConstant.INSERT_WORK_ORDER_INVOICE, true);
    }

    /***
     * 地区上线同步工单
     * @return
     */
    @Bean
    public Queue areaOnlineSyncWorkOrder() {
        return new Queue(RabbitConstant.AREA_ONLINE_SYNC_WORK_ORDER, true);
    }

    /**
     * 体验版经销商升级之后收益结算数据的处理
     */
    @Bean
    public Queue distributorUpgradeIncomeStatusChange() {
        return new Queue(RabbitConstant.DISTRIBUTOR_UPGRADE_INCOME_STATUS, true);
    }

    /**
     * 体验版经销商升级之后给经销商下的工单“第一次升级时间”字段赋值
     */
    @Bean
    public Queue distributorUpgradeSetWorkOderFirstUpgradeTime() {
        return new Queue(RabbitConstant.WORK_ORDER_SET_FIRST_UPGRADE_TIME, true);
    }

    /**
     * 导出
     */
    @Bean
    public Queue exportActionOrderQueue() {
        return new Queue(RabbitConstant.EXPORT_ACTION_ORDER, true);
    }
    /**
     * 系统推送创建维修单队列
     * @return
     */
    @Bean
    public Queue devicePushRepairOrderCreateQueue() {
        return new Queue(RabbitConstant.WATERDEVICE_PUSH_REPAIRORDER_CREATE, true);
    }
}
