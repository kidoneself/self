package com.yimao.cloud.base.enums;

/**
 * @description  支付状态
 * @date 2019/5/6 15:45
 */
public enum PayStatus {

    UN_PAY("未支付", 1),
    WAITING_AUDIT("待审核", 2),
    PAY("已支付", 3),
    FAIL("支付失败", 4);

    public final String name;
    public final int value;

    PayStatus(String name, int value) {
        this.name = name;
        this.value = value;
    }


}
