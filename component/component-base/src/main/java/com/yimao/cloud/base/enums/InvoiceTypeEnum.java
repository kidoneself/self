package com.yimao.cloud.base.enums;

/**
 * @description  发票类型
 * @author zhilin.he
 * @date 2019/5/7 17:15
 */
public enum InvoiceTypeEnum {

    ORDINARY("普通", 1),
    RENEW("增值", 2);

    public final String name;
    public final int value;

    InvoiceTypeEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }
}
