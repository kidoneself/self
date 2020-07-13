package com.yimao.cloud.base.enums;

import java.util.Arrays;
import java.util.Objects;

/**
 * HRA体检卡 什么周期状态
 *
 * @author liuhao@yimaokeji.com
 */
public enum HraLifeCycleEnum {

    /**
     * 状态 1-领取 2-支付  3-使用
     */
    HRA_LIFE_RECEiVER("已领取", 1),
    HRA_LIFE_PAY("已支付", 2),
    HRA_LIFE_USER("已使用", 3);

    public final String name;
    public final Integer value;

    HraLifeCycleEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public static HraLifeCycleEnum find(Integer value) {
        return Arrays.stream(values()).filter(item -> Objects.equals(item.value, value)).findFirst().orElse(null);
    }
}
