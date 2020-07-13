package com.yimao.cloud.base.enums;

/**
 * 支付平台枚举类
 *
 * @author Zhang Bo
 * @date 2019/9/25
 */
public enum PayPlatform {

    WECHAT(1, "微信"),
    ALI(2, "支付宝"),
    BANK(3, "银行");

    public final int value;
    public final String name;

    PayPlatform(int value, String name) {
        this.value = value;
        this.name = name;
    }
}
