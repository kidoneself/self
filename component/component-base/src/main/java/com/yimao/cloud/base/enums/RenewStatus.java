package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * 续费工单支付状态：1-待审核，2-支付成功，3-支付失败
 */
public enum RenewStatus {

    WAITING_PAY("待支付", 0),
    AUDITED("待审核", 1),
    SUCCESS("支付成功", 2),
    FAIL("支付失败", 3);

    public final String name;
    public final int value;

    RenewStatus(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static RenewStatus find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }

}
