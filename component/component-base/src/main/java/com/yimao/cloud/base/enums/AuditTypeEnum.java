package com.yimao.cloud.base.enums;

/**
 * @author zhilin.he
 * @description  审核类型枚举
 * @date 2019/1/30 15:27
 **/
public enum AuditTypeEnum {

    /**
     *  审核类型  1.取消订单退款（未收货），2.申请退货退款（已收货），3：提现
     */

    CANCELLATION_ORDER_REFUND("CANCELLATION_ORDER_REFUND", 1),
    APPLICATION_FOR_REFUND("APPLICATION_FOR_REFUND", 2),
    CASH_WITHDRAWAL("CASH_WITHDRAWAL", 3);

    public final String name;
    public final int value;

    AuditTypeEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }


}
