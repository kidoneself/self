package com.yimao.cloud.base.enums;

public enum MessageAppTypeEnum {
    CUSTOMER(1, "客服"),
    DISTRIBUTOR(2, "经销商");

    private int type;
    private String name;

    private MessageAppTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public static int customer() {
        return CUSTOMER.type;
    }

    public static int distributor() {
        return DISTRIBUTOR.type;
    }
}
