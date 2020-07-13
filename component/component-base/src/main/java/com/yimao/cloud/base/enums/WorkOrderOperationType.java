package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * @author zhilin.he
 * @description 云平台工单操作类型
 * @date 2019/4/26 16:03
 **/
public enum WorkOrderOperationType {

    WORK_ORDER("工单列表", 1),
    CANCEL("退单列表", 2),
    DELETE("删除列表", 3);

    public final String name;
    public final int value;

    WorkOrderOperationType(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public static WorkOrderOperationType find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }

}
