package com.yimao.cloud.base.enums;

import java.util.Arrays;

/***
 * 功能描述:状态值 0-否 1-是
 *
 * @param:
 * @auther: liu yi
 * @date: 2019/6/14 11:24
 * @return:
 */
public enum StatusTypeEnum {

    NOT("否", 0),
    YES("是", 1);

    public final String name;
    public final int value;

    StatusTypeEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public static StatusTypeEnum find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }

}
