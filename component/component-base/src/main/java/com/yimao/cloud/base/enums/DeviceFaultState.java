package com.yimao.cloud.base.enums;

/**
 * 描述：水机设备故障状态
 *
 * @author Zhang Bo
 * @date 2019/7/13
 */
public enum DeviceFaultState {

    FAULT(1, "故障"),
    RESOLVE(2, "故障已解除");

    public final int value;
    public final String name;

    DeviceFaultState(int value, String name) {
        this.value = value;
        this.name = name;
    }

}
