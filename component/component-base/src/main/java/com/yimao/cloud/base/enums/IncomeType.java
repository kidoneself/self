package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * 描述：收益类型：1-产品收益；2-续费收益；3-服务收益；4-招商收益
 *
 * @author Liu Yi
 * @date 2019/1/18
 */
public enum IncomeType {

    PRODUCT_INCOME("产品收益", 1),
    RENEW_INCOME("续费收益", 2),
    SERVICE_INCOME("服务收益", 3),
    INVESTMENT_INCOME("招商收益", 4);

    public final String name;
    public final int value;

    IncomeType(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public static IncomeType find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }

}
