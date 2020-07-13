package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * 经销商账号创建方式：1-系统自动生成；2-自定义创建；
 *
 * @author Zhang Bo
 * @date 2019/4/3
 */
public enum CreateDistributorAccountType {

    SYS("系统自动生成", 1),
    HAND("自定义创建", 2);

    public final String name;
    public final int value;

    CreateDistributorAccountType(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static CreateDistributorAccountType find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }

}
