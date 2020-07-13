package com.yimao.cloud.base.enums;

/**
 * 描述：水机设备型号
 *
 * @author Zhang Bo
 * @date 2019/3/9.
 */
public enum WaterDeviceModelEnum {

    M_1601T("家用I型", "1601T"),
    M_1602T("家用II型", "1602T"),
    M_1603T("家用III型", "1603T"),
    M_1601L("商用I型", "1601L");

    public final String name;
    public final String value;

    WaterDeviceModelEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

}
