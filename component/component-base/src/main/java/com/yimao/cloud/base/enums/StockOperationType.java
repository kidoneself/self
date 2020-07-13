package com.yimao.cloud.base.enums;

/**
 * @author zhilin.he
 * @description 仓库操作类型
 * @date 2019/5/6 15:16
 */
public enum StockOperationType {

    DELIVERY("下发产品", 1),
    ALLOCATION("调拨产品", 2),
    RETURNING("产品返仓", 3);

    public final String name;
    public final int value;

    StockOperationType(String name, int value) {
        this.name = name;
        this.value = value;
    }
}
