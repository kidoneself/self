package com.yimao.cloud.base.enums;

public enum MessageFilterTypeEnum {

    MONEY(1, "余额不足类型推送"),
    WATER(2, "制水故障类型推送"),
    TDS(3, "TDS异常故障类型推送"),
    PP(4, "PP滤芯过期故障类型推送"),
    CTO(5, "CTO滤芯过期故障类型推送"),
    UDF(6, "UDF滤芯过期故障类型推送"),
    T33(7, "T33滤芯过期故障类型推送");

    public final int value;
    public final String name;

    MessageFilterTypeEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

}
