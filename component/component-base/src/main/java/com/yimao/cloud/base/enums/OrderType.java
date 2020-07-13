package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * @author zhilin.he
 * @description 订单类型
 * @date 2019/5/7 17:15
 */
public enum OrderType {

    PRODUCT("普通产品订单", 1),
    RENEW("水机续费订单", 2),
    DISTRIBUTOR("经销商订单", 3),
    WORKORDER("工单订单", 4);

    public final String name;
    public final int value;

    OrderType(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static OrderType find(Integer value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }

}
