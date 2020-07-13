package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * 描述：维护工单来源：1-自动生成 2-总部添加，默认自动生成
 *
 * @author Liu Yi
 * @date 2019/1/18
 */
public enum MaintenanceWorkOrderSourceEnum {

    AUTO_CREATE("自动生成", 1),
    SYSTEM_INCOME("总部添加", 2);

    public final String name;
    public final int value;

    MaintenanceWorkOrderSourceEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public static MaintenanceWorkOrderSourceEnum find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }

}
