package com.yimao.cloud.base.enums;

/**
 * 描述：水机设备分类（家用型、商用型）
 *
 * @author Zhang Bo
 * @date 2019/3/9.
 */
public enum WaterDeviceCategoryEnum {

    FAMILY("家用", 1),
    BUSINESS("商用", 2);

    public final String name;
    public final int value;

    WaterDeviceCategoryEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

}
