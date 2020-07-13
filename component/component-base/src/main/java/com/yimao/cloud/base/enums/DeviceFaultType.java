package com.yimao.cloud.base.enums;

/**
 * 描述：水机设备故障类型
 *
 * @author Zhang Bo
 * @date 2019/7/13
 */
public enum DeviceFaultType {

    MONEY(1, "余额不足"),
    WATER(2, "制水故障"),
    TDS(3, "TDS异常"),
    FILTER(4, "滤芯报警"),
    NOTICE(5, "阈值提醒"),
    OVERDUE(6, "续费超期");

    public final int value;
    public final String name;

    DeviceFaultType(int value, String name) {
        this.value = value;
        this.name = name;
    }

}
