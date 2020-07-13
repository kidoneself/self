package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * 产品价格模版类型枚举
 *
 * @author Liu Yi
 * @date 2018/12/6.
 */
public enum ProductCostModelTypeEnum {

    /**
     * 计费模版类型：
     * 1-首年
     * 2-续费
     */
    FIRST_YEAR("首年", 1),
    RENEW_FEE("续费", 2);

    public final String name;
    public final int value;

    ProductCostModelTypeEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static ProductCostModelTypeEnum find(Integer value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }

}
