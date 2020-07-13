package com.yimao.cloud.base.enums;

public enum ChargeBackMoneyMethodEnum {
    UNDER_LINE_REFUND(1, "线下退款"),
    ONLINE_REFUND(2, "线上退款");

    private int value;
    private String text;

    private ChargeBackMoneyMethodEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return this.value;
    }

    public String getText() {
        return this.text;
    }
}
