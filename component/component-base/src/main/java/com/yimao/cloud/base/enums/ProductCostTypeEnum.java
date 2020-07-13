package com.yimao.cloud.base.enums;

/**
 * 产品价格模版收费类型枚举
 *
 * @author Liu Yi
 * @date 2018/12/6.
 */
public enum ProductCostTypeEnum {

    /**
     * 计费类型：
     * 1-按流量
     * 2-包年
     */
    FLOW("流量计费", 1),
    TIME("时间计费", 2);

    public final String name;
    public final int value;

    ProductCostTypeEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static boolean isFlow(Integer value) {
        return value != null && value == FLOW.value;
    }

    public static boolean isTime(Integer value) {
        return value != null && value == TIME.value;
    }

    public static String getName(int value) {
        for (ProductCostTypeEnum e : values()) {
            if (e.value == value) {
                return e.name;
            }
        }
        return "";
    }
}
