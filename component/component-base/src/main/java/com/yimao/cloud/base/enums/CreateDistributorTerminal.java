package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * 经销商创建端：3-翼猫业务系统；2-经销商app; 1-H5分享页面；
 *
 * @author Zhang Bo
 * @date 2019/4/2
 */
public enum CreateDistributorTerminal {

    SYSTEM("后台业务系统",3),
    APP("经销商APP", 2),
    H5("H5分享页面", 1);

    public final String name;
    public final int value;

    CreateDistributorTerminal(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static CreateDistributorTerminal find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }

}
