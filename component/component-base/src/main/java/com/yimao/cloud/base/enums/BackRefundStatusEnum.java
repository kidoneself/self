package com.yimao.cloud.base.enums;

public enum BackRefundStatusEnum {
    NON_REFUND(0, "未退款"),
    REFUNDING(1, "退款中"),
    REFUND_SUCCESS(2, "退款成功"),
    REFUND_FAIL(3, "退款失败"),
    PRE_REFUND(4, "预退款");

    private int value;
    private String text;

    private BackRefundStatusEnum(int value, String text) {
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
