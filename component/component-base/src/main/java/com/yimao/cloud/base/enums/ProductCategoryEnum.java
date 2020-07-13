package com.yimao.cloud.base.enums;

/**
 * 产品类型枚举
 *
 * @author Zhang Bo
 * @date 2018/10/23.
 */
public enum ProductCategoryEnum {

    /**
     * 产品一级分类：
     * 1-智能净水
     * 2-健康食品
     * 3-生物科技
     * 4-智能睡眠
     * 5-健康评估
     */
    WATER("WATER", 1),
    FOOD("FOOD", 2),
    BIOLOGICAL("BIOLOGICAL", 3),
    SLEEP("SLEEP", 4),
    HRA("HRA", 5);

    public final String name;
    public final int value;

    ProductCategoryEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

}
