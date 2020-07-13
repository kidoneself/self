package com.yimao.cloud.base.enums;

/**
 * @description   其他支付审核状态：0-审核中、1-审核通过、2-审核未通过
 * @author zhilin.he  
 * @date 2019/7/13 14:06
 */
public enum OtherPayAudit {

    /**
     * 其他支付审核状态：0-审核中、1-审核通过、2-审核未通过
     */
    OTHERPAY_VERIFYING("0", "审核中"),
    OTHERPAY_PASS("1", "审核通过"),
    OTHERPAY_NOT_PASS("2", "审核未通过");

    public final String name;
    public final String value;

    OtherPayAudit(String name, String value) {
        this.name = name;
        this.value = value;
    }
    
    
}
