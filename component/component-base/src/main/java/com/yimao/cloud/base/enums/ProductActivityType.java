package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * @Description 产品活动类型：1-普通产品 2-折机产品 3-180产品；
 * @auther: Liu Yi
 * @date: 2019/8/6
 */
public enum ProductActivityType {
    PRODUCT_COMMON("普通产品", 1),
    PRODUCT_CONVERT("折机产品", 2),
    PRODUCT_180("180产品", 3),
    PANIC_BUYING("抢购产品", 5);

    public final String name;
    public final int value;

    ProductActivityType(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static ProductActivityType find(Integer value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }

}
