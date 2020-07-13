package com.yimao.cloud.order.constant;

/**
 * @author Zhang Bo
 * @date 2019/3/21
 */
public class OrderConstant {

    // 将发货超过15天的订单标记为已完成状态，收益标记为可结算
//    public static final String ORDER_COMPLETE_HEALTHY_QUEUE = "direct.order.complete.healthy";
    // 水机工单完成、健康产品工单发货
    public static final String ORDER_COMPLETE_WATER_QUEUE = "direct.order.complete.water";

    public static final String ORDER_CONFIG_CACHE = "ORDER_CONFIG_CACHE";

}