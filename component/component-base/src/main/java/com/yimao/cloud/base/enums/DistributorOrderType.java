package com.yimao.cloud.base.enums;

/**
 * @author Lizhqiang
 * @date 2018/12/27
 */
public enum DistributorOrderType {

    /**
     * 订单类型
     * <p>
     * 0-注册
     * 1-升级
     * 2-续费
     */
    REGISTER("注册", 0),
    UPGRADE("升级", 1),
    RENEW("续费", 2);

    public final String name;
    public final int value;

    DistributorOrderType(String name, int value) {
        this.name = name;
        this.value = value;
    }
}
