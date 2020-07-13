package com.yimao.cloud.base.enums;

/**
 * 描述：水机设备滤芯故障
 *
 * @author Zhang Bo
 * @date 2019/7/13
 */
public enum DeviceFaultFilterType {

    PP("PP", "PP滤芯需要更换"),
    CTO("CTO", "CTO滤芯需要更换"),
    UDF("UDF", "UDF滤芯需要更换"),
    T33("T33", "T33滤芯需要更换");

    public final String name;
    public final String faultText;

    DeviceFaultFilterType(String name, String faultText) {
        this.name = name;
        this.faultText = faultText;
    }

}
