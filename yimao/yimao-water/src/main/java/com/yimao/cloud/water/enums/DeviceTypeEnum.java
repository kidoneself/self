package com.yimao.cloud.water.enums;

/**
 * 设备数据来源
 *
 * @author Zhang Bo
 * @date 2019/2/15.
 */
public enum DeviceTypeEnum {

    USER("用户组", 1),
    STATION("服务站组", 2);

    public final String name;
    public final int value;

    DeviceTypeEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

}
