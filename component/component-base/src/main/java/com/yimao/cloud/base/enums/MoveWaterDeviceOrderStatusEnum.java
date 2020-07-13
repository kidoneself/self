package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * 移机工单状态
 *
 * @author Liu Long Jie
 * @date 2020-6-29 11:34:30
 */
public enum MoveWaterDeviceOrderStatusEnum {

    //移机状态 1-待拆机；2-拆机中；3-待移入；4-移入中；5-已完成
    WAIT_DISMANTLE("待拆机", 1),
    DISMANTLE("拆机中", 2),
    WAIT_INSTALL("待移入", 3),
    INSTALL("移入中", 4),
    COMPLETED("已完成", 5);

    public final String name;
    public final int value;

    MoveWaterDeviceOrderStatusEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    /**
     * 根据工单状态判断是否完成
     */
    public static boolean completed(int status) {
        return status == COMPLETED.value;
    }

    public static MoveWaterDeviceOrderStatusEnum find(Integer value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }
}
