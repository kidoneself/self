package com.yimao.cloud.base.enums;

/**
 * 水机更变位置记录来源方式
 */
public enum WaterDevicePlaceChangeRecordTypeEnum {

    WATER_DEVICE_AUTO_DETECTION("水机设备自动检测", 1),
    MOVE_WATER_DEVICE("移机", 2),
    ;

    public final String name;
    public final int value;

    WaterDevicePlaceChangeRecordTypeEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

}
