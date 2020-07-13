package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * 购物车类型：1-普通购物车；2-经销商购物车；3-站长购物车；4-特批水机购物车；5-特供产品购物车；
 **/
public enum ShopCartType {

    GENERAL("普通购物车", 1),
    PJXCP("经销商购物车", 2),
    PZZZG("站长购物车", 3),
    PTPSJ("特批水机购物车", 4),
    PTGCP("特供产品购物车", 5);

    public final String name;
    public final int value;

    ShopCartType(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static ShopCartType find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }
}
