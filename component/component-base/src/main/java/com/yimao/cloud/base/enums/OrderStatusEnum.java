package com.yimao.cloud.base.enums;

/**
 * 订单状态枚举
 *
 * @author Zhang Bo
 * @date 2018/10/23.
 */
public enum OrderStatusEnum {

    /**
     *  订单主状态：0-待付款 1-待审核 2-待发货 3-待出库  4-待收货 5-交易成功 6-售后中 7-交易关闭 8-已取消
     */

    PENDING_PAYMENT("待付款", 0),
    WAITING_AUDIT("待审核", 1),
    TO_BE_DELIVERED("待发货", 2),
    TO_BE_OUT_OF_STOCK("待出库", 3),
    PENDING_RECEIPT("待收货", 4),
    SUCCESSFUL_TRADE("交易成功", 5),
    AFTER_SALE("售后中", 6),
    CLOSED("交易关闭", 7),
    CANCELLED("已取消", 8),
	CHARGEBACK("CHARGEBACK", 10);

    /**
     * 状态：
     * 0-待付款，
     * 1-未受理，
     * 2-待发货，
     * 3-待收货，
     * 4-已完成，
     * 10-退单，
     * -1-审核中，
     * -2-未通过，
     * -3-已取消，
     * -4-已关闭
     */
//    PENDING_PAYMENT("PENDING_PAYMENT", 0),
//    NOT_ACCEPTED("NOT_ACCEPTED", 1),
//    TO_BE_DELIVERED("TO_BE_DELIVERED", 2),
//    PENDING_RECEIPT("PENDING_RECEIPT", 3),
//    COMPLETED("COMPLETED", 4),
//    CHARGEBACK("CHARGEBACK", 10),
//    UNDER_REVIEW("UNDER_REVIEW", -1),
//    DID_NOT_PASS("DID_NOT_PASS", -2),
//    CANCELLED("CANCELLED", -3),
//    CLOSED("CLOSED", -4);

    public final String name;
    public final int value;

    OrderStatusEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

}
