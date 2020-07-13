package com.yimao.cloud.base.enums;

/**
 * 管理员类型
 *
 * @author Zhang Bo
 * @date 2018/11/1.
 */
public enum AdminTypeEnum {

    SYSTEM("翼猫业务系统管理员", 1),
    STATION("服务站站务系统管理员", 2);

    public final String name;
    public final int value;

    AdminTypeEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }
}
