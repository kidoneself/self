package com.yimao.cloud.base.enums;


import java.util.Arrays;

/**
 * @description: 登录类别枚举
 * @author: yu chunlei
 * @create: 2019-04-26 16:19:44
 **/
public enum IdentityTypeEnum {

    WECHAT_GZH("健康e家公众号", 1),
    HEALTH_MINI("健康自测小程序", 2),
    MOBILE("手机号", 3),
    H5("H5", 4),
    WECHAT_JXSAPP("翼猫APP", 5);

    public final String name;
    public final int value;

    IdentityTypeEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public static IdentityTypeEnum find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }
}
