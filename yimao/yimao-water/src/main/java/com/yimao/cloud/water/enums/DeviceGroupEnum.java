package com.yimao.cloud.water.enums;

/**
 * 设备组
 *
 * @author Zhang Bo
 * @date 2019/2/15.
 */
public enum DeviceGroupEnum {

    USER("用户组", 1),
    STATION("服务站组", 2);

    public final String name;
    public final int value;

    DeviceGroupEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

}
