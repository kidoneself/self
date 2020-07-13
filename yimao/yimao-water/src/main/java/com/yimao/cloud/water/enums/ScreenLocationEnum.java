package com.yimao.cloud.water.enums;

/**
 * 描述：大屏广告、小屏广告。
 *
 * @Author Zhang Bo
 * @Date 2019/2/21 11:26
 * @Version 1.0
 */
public enum ScreenLocationEnum {

    ONE("大屏广告", 1),
    TWO("小屏广告", 2);

    public final String name;
    public final int value;

    ScreenLocationEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

}
