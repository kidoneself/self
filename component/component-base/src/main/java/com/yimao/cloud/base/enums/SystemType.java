package com.yimao.cloud.base.enums;

/**
 * @author Zhang Bo
 * @date 2018/11/1.
 */
public enum SystemType {

    WECHAT("翼猫健康e家", 1),
    SYSTEM("翼猫平台业务管理系统", 2),
    WATER("净水设备互动广告系统", 3),
    JXSAPP("经销商管理", 4),
    ENGINEER("翼猫服务", 5),
    PAD("水机PAD", 6),
    STATION("服务站站务系统",7),
    H5("H5", 10);
	
    public final String name;
    public final int value;

    SystemType(String name, int value) {
        this.name = name;
        this.value = value;
    }
}
