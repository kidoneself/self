package com.yimao.cloud.base.enums;

/**
 * @author zhilin.he
 * @description 翼猫app--我的订单--订单详情状态枚举
 * @date 2019/7/27 17:31
 */
public enum OrderDetailStatus {

    /**
     * 订单详情状态:1、待付款，2、代付中，3、支付审核失败，4、支付审核中，5、待接单，6、待发货，
     * 7、订单取消中，8、出库中，9、已接单，10、待收货，11、已完成，12、待总部审核，13、待经销商审核，
     * 14、待退货，15、待退款，16、已退款，17、已取消，18、取消失败 19、待出库 20、交易关闭
     */

    PENDING_PAYMENT("待付款", 1),
    PAYING("代付中", 2),
    PAY_AUDIT_FAILED("支付审核失败", 3),
    PAY_AUDITING("支付审核中", 4),
    WAITING_ORDER("待接单", 5),
    TO_BE_DELIVERED("待发货", 6),
    ORDER_CANCEL("订单取消中", 7),
    OUT_OF_THE_LIBRARY("出库中", 8),
    ORDER_RECEIVED("已接单", 9),
    PENDING_RECEIPT("待收货", 10),
    SUCCESSFUL_TRADE("已完成", 11),
    PENDING_AUDIT_HEAD("待总部审核", 12),
    PENDING_AUDIT_DISTRIBUTOR("待经销商审核", 13),
    RETURNING("待退货", 14),
    PENDING_REFUND("待退款", 15),
    REFUNDED("已退款", 16),
    CANCELLED("已取消", 17),
    CANCEL_FAILED("取消失败", 18),
    WAIT_TO_LIBRARY("待出库", 19),
    TRADING_CLOSED("交易关闭", 20);//售后成功，但无需退款的订单。（目前只针对租赁商品，货到付款，申请退单，且全部审核通过的）


    public final String name;
    public final int value;

    OrderDetailStatus(String name, int value) {
        this.name = name;
        this.value = value;
    }

}
