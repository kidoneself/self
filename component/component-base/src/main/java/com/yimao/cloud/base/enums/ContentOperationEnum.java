package com.yimao.cloud.base.enums;

/**
 * 资讯操作相关操作
 *
 * @author Liu Long Jie
 */
public enum ContentOperationEnum {

    WATCH("资讯查阅", "watch");

    public final String name;
    public final String value;

    ContentOperationEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }


}
