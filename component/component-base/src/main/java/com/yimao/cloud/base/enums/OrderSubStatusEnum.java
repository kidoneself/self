package com.yimao.cloud.base.enums;

/**
 * @author zhilin.he
 * @description 订单子状态或售后状态枚举
 * @date 2019/1/30 15:27
 **/
public enum OrderSubStatusEnum {

    /**
     *  子状态(售后中状态):0-待审核(经销商)；1-待审核(总部)；,2-待退货入库,3-待退款(财务),4-售后失败,5-售后成功
     */
    PENDING_AUDIT_DISTRIBUTOR("待经销商审核", 0),
    PENDING_AUDIT_HEAD("待总部审核", 1),
    RETURNING_TO_WAREHOUSE("待退货", 2),
    PENDING_REFUND("待退款", 3),
    AFTER_SALE_FAILURE("售后失败", 4),
    AFTER_SALE_SUCCESS("售后成功", 5),
	AFTER_SALE_REFUNDING("退款中",6);
    public final String name;
    public final int value;

    OrderSubStatusEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

}
