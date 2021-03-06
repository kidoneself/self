package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * 性别：1-男；2-女；
 *
 * @author Zhang Bo
 * @date 2019/4/2
 */
public enum SexType {

    MAN("男", 1),
    WOMAN("女", 2);

    public final String name;
    public final int value;

    SexType(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static SexType find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }

}
