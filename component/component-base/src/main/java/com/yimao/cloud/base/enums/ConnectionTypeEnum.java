package com.yimao.cloud.base.enums;

import java.util.Objects;

/**
 * 网络类型。
 *
 * @author Zhang Bo
 * @date 2019/2/15.
 */
public enum ConnectionTypeEnum {

    UNLIMITED("不限", null),
    WIFI("WIFI", 1),
    GPRS2("2G", 2),
    GPRS3("3G", 3),
    GPRS4("4G", 4),
    GPRS5("5G", 5);

    public final String name;
    public final Integer value;

    ConnectionTypeEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public static String getNameByValue(Integer value) {
        for (ConnectionTypeEnum item : values()) {
            if (Objects.equals(item.value, value)) {
                return item.name;
            }
        }
        return null;
    }

}
