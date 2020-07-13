package com.yimao.cloud.base.enums;

import java.util.Arrays;
import java.util.Objects;

/**
 * HRA体检卡赠送状态
 *
 * @author liuhao@yimaokeji.com
 */
public enum HraHandselStatusEnum {

    /**
     * Hra 赠送状态
     */
    HRA_SEND("赠送中", 1),
    HRA_RECEIVE("已领取", 2);

    public final String name;
    public final Integer value;

    HraHandselStatusEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public static HraHandselStatusEnum find(Integer value) {
        return Arrays.stream(values()).filter(item -> Objects.equals(item.value, value)).findFirst().orElse(null);
    }
}
