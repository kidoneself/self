package com.yimao.cloud.base.enums;

/**
 * 经销商支付状态
 *
 * @author Zhang Bo
 * @date 2018/11/1.
 */
public enum PayStateEnum {

    UN_PAY("未支付", 1),
    PAY("已支付", 2),//(支付成功)
    //NOT_PAY("未支付", 2),//原工单、续费工单用
    PAY_FAIL("支付失败",3),
    WAITING_AUDIT("待审核", 4),
    //PAY_SUCCESS("支付成功",5),
    PAY_UNREQUIRED("无需支付",6);

    //支付状态 1-未支付，2-已完成(支付成功)， 3-支付失败 4-待审核 6-无需支付

    public final String name;
    public final int value;

    PayStateEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }
}
