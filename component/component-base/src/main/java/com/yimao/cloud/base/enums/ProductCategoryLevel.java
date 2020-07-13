package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * 描述：产品分类等级
 *
 * @author Zhang Bo
 * @date 2019/3/9.
 */
public enum ProductCategoryLevel {

    FIRST("一级分类", 1),
    SECOND("二级分类", 2),
    THIRD("三级分类", 3);

    public final String name;
    public final int value;

    ProductCategoryLevel(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static ProductCategoryLevel find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }

}
