package com.yimao.cloud.base.enums;

/**
 * 订单完成状态
 *
 * @author hhf
 * @date 2019/4/24
 */
public enum OrderCompleteStatusEnum {

    /**
     * 订单完成状态：0-暂不可结算；1-可结算；2-已结算；3-已失效；4-暂停结算
     */
    UNCOMPLETED("暂不可结算", 0),
    CAN_BE_SETTLED("可结算", 1),
    HAS_BEEN_SETTLED("已结算", 2),
    HAS_THE_FAILURE("已失效", 3),
    STOP_SETTLED("暂停结算", 4);

    public final String name;
    public final int value;

    OrderCompleteStatusEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }
}
