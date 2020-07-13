package com.yimao.cloud.base.enums;

/**
 * 描述：提现成功状态：1-成功；2-失败；
 *
 * @Author Zhang Bo
 * @Date 2019/5/31
 */
public enum WithdrawFlag {

    SUCCESS("成功", 1),
    FAIL("失败", 2);

    public final String name;
    public final int value;

    WithdrawFlag(String name, int value) {
        this.name = name;
        this.value = value;
    }

}
