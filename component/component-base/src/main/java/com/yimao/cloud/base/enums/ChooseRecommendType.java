package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * 选择推荐人方式：1-系统自动分配；2-手动选择；3-设置自己为推荐人；
 *
 * @author Zhang Bo
 * @date 2019/4/3
 */
public enum ChooseRecommendType {

    SYS("系统自动分配", 1),
    HAND("手动选择", 2),
    SELF("设置自己为推荐人", 3);

    public final String name;
    public final int value;

    ChooseRecommendType(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static ChooseRecommendType find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }

}
