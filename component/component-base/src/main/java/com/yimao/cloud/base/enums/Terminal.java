package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * 描述：终端：1-用户终端APP；2-微信公众号；3-翼猫APP；4-小猫店小程序；5-站务系统
 *
 * @Author Zhang Bo
 * @Date 2019/3/23
 */
public enum Terminal {

    WATER_DEVICE("净水设备", 1),
    WECHAT("健康e家公众号", 2),
    YIMAO_APP("翼猫APP", 3),
    MINI_HEALTHY("健康自测小程序", 4),
    STATION("服务站站务系统", 5),
    ADMIN("管理后台", 10),
    ;

    public final String name;
    public final int value;

    Terminal(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static Terminal find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }

    public static String getName(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().map(Terminal::getName).orElse("");
    }

    public String getName() {
        return this.name;
    }

}
