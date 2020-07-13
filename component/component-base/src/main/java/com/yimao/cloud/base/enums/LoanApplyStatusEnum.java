package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * 站务系统-物资借调状态枚举
 * @author yaoweijun
 *
 */
public enum LoanApplyStatusEnum {
	//0-待审核 1-已通过 2-拒绝'
    WAIT("待审核", 0),
    AGREE("已通过", 1),
	REFUSE("拒绝", 2);

    public final String name;
    public final int value;

    LoanApplyStatusEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static LoanApplyStatusEnum find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }
}
