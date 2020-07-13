package com.yimao.cloud.base.enums;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author liuhao@yimaokeji.com
 * @date 2019/04/25
 */
public enum HraDeviceTypeEnum {

    /**
     * 体检设备型号 1-I型设备 2-II型设备
     */
    HRA_I("I型设备", 1),
    HRA_II("II型设备", 2);

    public final String name;
    public final Integer value;

    HraDeviceTypeEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public static HraDeviceTypeEnum find(Integer value) {
        return Arrays.stream(values()).filter(item -> Objects.equals(item.value, value)).findFirst().orElse(null);
    }
}
