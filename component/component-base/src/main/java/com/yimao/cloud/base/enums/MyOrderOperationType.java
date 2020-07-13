package com.yimao.cloud.base.enums;

/**
 * @author zhilin.he
 * @description 翼猫app--我的订单--操作类型枚举
 * @date 2019/7/29 11:34
 **/
public enum MyOrderOperationType {

    /**
     * 操作类型: -1-全部 0-待付款 1-待发货 2-待收货 3-已完成 4-退款/退货/待审核 5-待退货 6-待退款 7-取消记录
     */

    ALL("全部", -1),
    PENDING_PAYMENT("待付款", 0),
    TO_BE_DELIVERED("待发货", 1),
    PENDING_RECEIPT("待收货", 2),
    SUCCESSFUL_TRADE("已完成", 3),
    PENDING_AUDIT("待审核", 4),
    RETURNING("待退货", 5),
    PENDING_REFUND("待退款", 6),
    CANCELLED_RECODE("取消记录", 7);


    public final String name;
    public final int value;

    MyOrderOperationType(String name, int value) {
        this.name = name;
        this.value = value;
    }

}
