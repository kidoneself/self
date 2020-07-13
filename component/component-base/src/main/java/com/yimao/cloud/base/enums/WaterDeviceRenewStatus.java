package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * 水机续费状态：-1-无需续费；1-未续费；2-待续费；3-已续费；
 */
public enum WaterDeviceRenewStatus {

    NONEED("无需续费", -1),
    NOTYET("未续费", 1),
    WAIT("待续费", 2),
    ALREADY("已续费", 3);

    public final String name;
    public final int value;

    WaterDeviceRenewStatus(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static WaterDeviceRenewStatus find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }

}
