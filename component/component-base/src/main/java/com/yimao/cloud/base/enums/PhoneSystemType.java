package com.yimao.cloud.base.enums;

public enum PhoneSystemType {

    ALL(0, "ANDROID & IOS"),
    ANDROID(1, "ANDROID"),
    IOS(2, "IOS");

    public final int value;
    public final String name;

    PhoneSystemType(int value, String name) {
        this.value = value;
        this.name = name;
    }

}
