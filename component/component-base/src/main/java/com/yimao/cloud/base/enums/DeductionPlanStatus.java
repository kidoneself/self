package com.yimao.cloud.base.enums;

/**
 * 水机扣费计划状态：1-待使用；2-使用中；3-已使用；4-已失效（变更套餐时将当前设置为已失效，新建一条）
 */
public enum DeductionPlanStatus {

    TO_BE_USED("待使用", 1),
    USING("使用中", 2),
    USED("已使用", 3),
    EXPIRED("已失效", 4);

    public final String name;
    public final int value;

    DeductionPlanStatus(String name, int value) {
        this.name = name;
        this.value = value;
    }

}
