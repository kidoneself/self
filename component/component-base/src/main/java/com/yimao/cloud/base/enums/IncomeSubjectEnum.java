package com.yimao.cloud.base.enums;

import lombok.Getter;

/**
 * @author Liu Yi
 * @description 收益主体CODE
 * @date 2019/1/21
 */
@Getter
public enum IncomeSubjectEnum {
    /**
     * 收益主体CODE
     */
    MAIN_COMPANY("翼猫总部", "MAIN_COMPANY"),
    PROVINCE_COMPANY("省级公司", "PROVINCE_COMPANY"),
    CITY_COMPANY("市级公司", "CITY_COMPANY"),
    PRODUCT_COMPANY("产品公司", "PRODUCT_COMPANY"),

    CITY_PARTNER("市级合伙人", "CITY_PARTNER"),
    CITY_SPONSOR("市级发起人", "CITY_SPONSOR"),
    RECOMMEND_STATION_COMPANY("推荐人所在区县级公司", "RECOMMEND_STATION_COMPANY"),
    ASSISTANT("智慧助理", "ASSISTANT"),

    DISTRIBUTOR_STATION_COMPANY("经销商所在区县级公司", "DISTRIBUTOR_STATION_COMPANY"),
    REGION_SPONSOR("区县级发起人", "REGION_SPONSOR"),
    STATION_MASTER("服务站站长", "STATION_MASTER"),
    REGION_SHAREHOLDER("区县级股东（推荐人）", "REGION_SHAREHOLDER"),

    DISTRIBUTOR("经销商", "DISTRIBUTOR"),
    DISTRIBUTOR_USER("会员用户", "DISTRIBUTOR_USER"),
    STATION_CONTRACTOR_PRODUCT("服务站承包人（产品收益）", "STATION_CONTRACTOR_PRODUCT"),
    STATION_CONTRACTOR_SERVICE("服务站承包人（服务收益）", "STATION_CONTRACTOR_SERVICE"),

    ENGINEER_STATION_COMPANY("安装工所在区县级公司", "ENGINEER_STATION_COMPANY"),
    ENGINEER("安装服务人员", "ENGINEER");

    public final String name;
    public final String value;

    IncomeSubjectEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

}
