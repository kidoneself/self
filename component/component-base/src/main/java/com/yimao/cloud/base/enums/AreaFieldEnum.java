package com.yimao.cloud.base.enums;

public enum AreaFieldEnum {

    PROVINCE("省", "province"),
    CITY("市", "city"),
    REGION("区县", "region");

    public final String name;
    public final String value;

    AreaFieldEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

}
