package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * 描述：分配类型：1-按比例分配；2-按金额分配；
 *
 * @author Zhang Bo
 * @date 2019/3/21
 */
public enum IncomeAllotType {

    RATIO("按比例分配", 1),
    MONEY("按金额分配", 2);

    public final String name;
    public final int value;

    IncomeAllotType(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public static IncomeAllotType find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }

}
