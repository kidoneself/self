package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * 产品模式：1-租赁；2-实物；3-虚拟；
 *
 * @author Zhang Bo
 * @date 2018/10/23.
 */
public enum ProductModeEnum {

    REALTHING("实物", 1),
    VIRTUAL("虚拟（电子卡券）", 2),
    LEASE("租赁", 3);

    public final String name;
    public final int value;

    ProductModeEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public static ProductModeEnum find(Integer value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }
}
