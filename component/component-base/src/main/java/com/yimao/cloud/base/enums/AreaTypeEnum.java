package com.yimao.cloud.base.enums;

public enum AreaTypeEnum {

    PROVINCE("省或直辖市", 1),
    CITY("市", 2),
    REGION("区县", 3);

    public final String name;
    public final int value;

    AreaTypeEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

}
