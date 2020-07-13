package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * @description   推送方式
 * @author Liu Yi
 * @date 2019/10/11 11:19
 * @return
 */
public enum MessagePushModeEnum {
    YIMAO_SMS("短信",1),
    YIMAO_APP_NOTICE("翼猫APP",2),
    YIMAO_ENGINEER_APP_NOTICE("翼猫服务APP(安装工)",3),
    YIMAO_SYSTEM("翼猫业务系统",4),
    YIMAO_STATION("服务站站务系统",5);
	
    public final String name;
    public final int value;

    MessagePushModeEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public static MessagePushModeEnum find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }
}
