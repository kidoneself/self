package com.yimao.cloud.base.enums;

import java.util.Arrays;

public enum DispatchType {

    OWN_SELECT("手动派单", 1),
    SYS_SELECT("自动派单", 2);

    public final String name;
    public final int value;

    DispatchType(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static DispatchType find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }

}
