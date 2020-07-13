package com.yimao.cloud.base.enums;

/**
 * 发起人枚举
 *
 * @author hhf
 * @date 2019/1/24
 */
public enum OriginatorEnum {

    PROVINCE_ORIGINATOR("省级发起人", 1),
    CITY_ORIGINATOR("市级发起人", 2),
    REGION_ORIGINATOR("区级发起人", 4),
    PROVINCE_CITY_ORIGINATOR("省+市级发起人", 3),
    PROVINCE_REGION_ORIGINATOR("省+区级发起人", 5),
    CITY_REGION_ORIGINATOR("市+区级发起人", 6),
    PROVINCE_CITY_REGION_ORIGINATOR("省+市+区级发起人", 7);

    public final String name;
    public final Integer value;

    OriginatorEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

}
