package com.yimao.cloud.base.enums;

/**
 * @description  发票抬头
 * @author zhilin.he
 * @date 2019/5/7 17:15
 */
public enum InvoiceHeadEnum {

    COMPANY("公司", 1),
    ONE("个人", 2);

    public final String name;
    public final int value;

    InvoiceHeadEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

}
