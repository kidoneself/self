package com.yimao.cloud.base.enums;

/**
 * @description  支付端
 * @author zhilin.he
 * @date 2019/5/6 16:08
 */
public enum PayTerminal {

    DEALER("经销商支付", 1),//翼猫APP支付
    USER("用户支付", 2),//安装工APP支付
    PAD("广告屏", 8),
    SYS("总部业务系统", 9);

    public final String name;
    public final int value;

    PayTerminal(String name, int value) {
        this.name = name;
        this.value = value;
    }

}
