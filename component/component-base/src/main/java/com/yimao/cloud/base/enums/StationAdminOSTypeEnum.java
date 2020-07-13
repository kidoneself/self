package com.yimao.cloud.base.enums;

/**
 * 服务站站务系统管理员操作类型
 *
 * @author Liu long jie
 * @date 2019-12-31
 */
public enum StationAdminOSTypeEnum {

    LOGIN("登陆", 1),
    ;

    public final String name;
    public final int value;

    StationAdminOSTypeEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }
}
