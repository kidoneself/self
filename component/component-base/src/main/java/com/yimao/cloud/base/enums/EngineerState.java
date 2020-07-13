package com.yimao.cloud.base.enums;

/**
 * 安装工状态枚举类
 */
public enum EngineerState {

    FREE(0, "空闲"),
    BUSY(1, "忙碌");

    public int value;
    public String name;

    EngineerState(int value, String name) {
        this.value = value;
        this.name = name;
    }

}
