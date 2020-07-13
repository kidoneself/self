package com.yimao.cloud.base.enums;

import java.util.Arrays;

public enum WaterOrderType {

    FIRST("首年", 1),
    RENEW("续费", 2);

    public final String name;
    public final int value;

    WaterOrderType(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public static WaterOrderType find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }

}
