package com.yimao.cloud.base.enums;

/***
 * 功能描述:耗材类型枚举
 *
 * @auther: liu yi
 * @date: 2019/5/17 9:33
 */
public enum ConsumableTypeEnum {
    FILTER_ELEMENT("滤芯",1),
    FILTER_NET("滤网",2);

    public final String name;
    public final int value;

    ConsumableTypeEnum(String name, int value){
        this.name = name;
        this.value = value;
    }
}
