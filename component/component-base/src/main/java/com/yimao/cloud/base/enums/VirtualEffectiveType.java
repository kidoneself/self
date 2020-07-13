package com.yimao.cloud.base.enums;

/**
 * 虚拟产品生效时间类型：1-无限期；2-指定时间内可用；3-自定义时间段
 *
 * @author Zhang Bo
 * @date 2019/05/28
 */
public enum VirtualEffectiveType {

    UNLIMITED("无限期", 1),
    ASSIGN("指定时间内可用", 2),
    BETWEEN("自定义时间段", 3);

    public final String name;
    public final int value;

    VirtualEffectiveType(String name, int value) {
        this.name = name;
        this.value = value;
    }

}
