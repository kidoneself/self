package com.yimao.cloud.base.enums;

/***
 * 功能描述:百得耗材id枚举
 *
 * @auther: liu yi
 * @date: 2019/5/17 9:33
 */
public enum BaideConsumableTypeIdEnum {
    FILTER_PP("PP","5"),
    FILTER_RO("RO","8"),
    FILTER_CTO("CTO","9"),
    FILTER_T33("T33","7"),
    FILTER_UDF("UDF","6");

    public final String name;
    public final String value;

    BaideConsumableTypeIdEnum(String name, String value){
        this.name = name;
        this.value = value;
    }
}
