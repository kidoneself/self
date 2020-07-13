package com.yimao.cloud.base.enums;

public enum TransferOperateTypeEnum {
    AFTER_SALE_TRANSFER("售后承包转让", 1),
    ENGINEER_TRANSFER("安装工转让", 2);

    public final String name;
    public final int value;

    TransferOperateTypeEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }
}
