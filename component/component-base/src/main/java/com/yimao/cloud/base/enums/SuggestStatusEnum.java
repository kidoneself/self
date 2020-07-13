package com.yimao.cloud.base.enums;

/**
 * 描述：回复状态
 *
 * @author Liu Long Jie
 * @date 2020-3-24
 */
public enum SuggestStatusEnum {

    NO_REPLY("未回复", 0),
    ALREADY_REPLY("已回复", 1),
    ;

    public final String name;
    public final int value;

    SuggestStatusEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }
}
