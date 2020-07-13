package com.yimao.cloud.base.enums;

import java.util.Arrays;

public enum DistributorCreateOrderType {

    OWN("为自己下单", 1),
    OTHERS("为客户下单", 2);

    public final String name;
    public final int value;

    DistributorCreateOrderType(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static DistributorCreateOrderType find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }

}
