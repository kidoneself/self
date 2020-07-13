package com.yimao.cloud.base.enums;


/**
 * 经销商订单状态
 * @author Zhang Bo
 * @date 2019/1/2.
 */
public enum DistributorOrderStateEnum {

    /**
     * 状态 0-待审核、1-已完成、2-待付款 3-已关闭
     */
    WAITING_AUDIT("待审核", 0),
    COMPLETED("已完成", 1),
    PENDING_PAYMENT("待付款", 2),
    CLOSE("已关闭", 3);


    public final String name;
    public final int value;

    DistributorOrderStateEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

}
