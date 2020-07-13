package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * @Description 积分状态
 * @auther: liu.lin
 * @date: 2019/1/30
 */
public enum IntegralRuleStatus {

    NOTONSHELF("未上架", 1),
    ONSHELF("已上架", 2),
    OFFSHELF("已下架", 3),
    DELETED("已删除", 4);

    public final String name;
    public final int value;

    IntegralRuleStatus(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static IntegralRuleStatus find(Integer value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }

}
