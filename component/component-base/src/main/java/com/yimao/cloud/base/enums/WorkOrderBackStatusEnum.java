package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * @description   退机工单状态
 * @author Liu Yi
 * @date 2020/7/2 11:17
 */
public enum WorkOrderBackStatusEnum {
    ASSIGNED("未受理", 1),
    INSTALLING("处理中", 2),
    SUSPEND("挂单", 3),
    COMPLETED("已完成", 4);
    public final String name;
    public final int value;

    WorkOrderBackStatusEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    /**
     * 根据工单状态判断是否接单
     */
    public static boolean accepted(int status) {
        return status == SUSPEND.value || status == INSTALLING.value || status == COMPLETED.value;
    }

    /**
     * 根据工单状态判断是否完成
     */
    public static boolean completed(int status) {
        return status == COMPLETED.value;
    }

    public static WorkOrderBackStatusEnum find(Integer value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }
}
