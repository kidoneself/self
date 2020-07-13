package com.yimao.cloud.base.enums;

/**
 * 服务人员变更记录 来源端
 *
 * @author Liu Long Jie
 */
public enum ServiceEngineerChangeSourceEnum {

    STATION("服务站站务系统", 1),
    ;

    public final String name;
    public final Integer value;

    ServiceEngineerChangeSourceEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }


}
