package com.yimao.cloud.base.enums;

/**
 * @description  发票申请状态
 * @author zhilin.he
 * @date 2019/5/7 17:15
 */
public enum InvoiceApplyStatus {
    //原售后状态是：0-未开 1-开票中 2-已开
    NOT_APPLIED("未申请", 0),
    APPLIED("已申请", 2);

    public final String name;
    public final int value;

    InvoiceApplyStatus(String name, int value) {
        this.name = name;
        this.value = value;
    }
}
