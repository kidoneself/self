package com.yimao.cloud.base.enums;

/**
 * 款项收取类型（默认1）：1-商品费用；2-经销代理费用
 *
 * @author Zhang Bo
 * @date 2019/9/25
 */
public enum PayReceiveType {

    ONE(1, "商品费用"),
    TWO(2, "经销代理费用");

    public final int value;
    public final String name;

    PayReceiveType(int value, String name) {
        this.value = value;
        this.name = name;
    }

}
