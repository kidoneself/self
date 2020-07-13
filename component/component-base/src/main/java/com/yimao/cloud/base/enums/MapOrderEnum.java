package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * @author Lizhqiang
 * @date 2020/7/3
 */
public enum MapOrderEnum {


    INSTALL("INSTALL", 1),
    REPAIR("REPAIR", 2),
    MOVE("MOVE", 3),
    MAINTAIN("MAINTAIN", 4),
    BACK("BACK", 5);

    public final String type;
    public final int value;

    MapOrderEnum(String type, Integer value) {
        this.type = type;
        this.value = value;
    }

    public static MapOrderEnum find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }
}
