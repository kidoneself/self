package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * 功能描述: 类目类型 ：分前台后台
 *
 * @auther: liu.lin
 * @date: 2018/12/7
 */
public enum ProductCategoryType {

    BACKEND("后台类目", 1),
    FRONT("前台类目", 2);

    public final String name;
    public final int value;

    ProductCategoryType(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static ProductCategoryType find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }

}
