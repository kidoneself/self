package com.yimao.cloud.base.enums;

import java.util.Arrays;

public enum MessagePushObjectEnum {
    ENGINEER("安装工程师",1),
    DISTRIBUTOR( "经销商",2),
    //USER("用户",3),
    ORDERING_USER("下单人（用户）",4),
    WATER_USER("水机用户",5),
    SYSTEM( "后台展示(系统保存)",6),
    CREATE_ACCOUNT("账号创建成功发送给代理商/经销商/创始人",7);

    public final String name;
    public final int value;

    MessagePushObjectEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public static MessagePushObjectEnum find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }
}
