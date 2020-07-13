package com.yimao.cloud.base.enums;

public enum OrderCreateType {

    SINGLE("单件商品下单", 1),
    SHOPCART("购物车下单", 2);

    public final String name;
    public final int value;

    OrderCreateType(String name, int value) {
        this.name = name;
        this.value = value;
    }

}
