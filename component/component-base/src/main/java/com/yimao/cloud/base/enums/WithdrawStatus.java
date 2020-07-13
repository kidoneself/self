package com.yimao.cloud.base.enums;

/**
 * 描述：提现状态：1-审核通过；2-审核不通过；3-待审核；4-申请提现但未短信校验通过；
 *
 * @Author Zhang Bo
 * @Date 2019/5/31
 */
public enum WithdrawStatus {

    PASS("审核通过", 1),
    NOT_PASS("审核不通过", 2),
    WAIT_REVIEW("待审核", 3),
    CHECK_FAIL("申请提现但未短信校验通过", 4);

    public final String name;
    public final int value;

    WithdrawStatus(String name, int value) {
        this.name = name;
        this.value = value;
    }

}
