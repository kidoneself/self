package com.yimao.cloud.base.enums;

/**
 * 常用时间单位：1-日；2-月；3-年；
 *
 * @author Zhang Bo
 * @date 2019/05/28
 */
public enum BaseTimeUnit {

    DAY("日", 1),
    MONTH("月", 2),
    YEAR("年", 3);

    public final String name;
    public final int value;

    BaseTimeUnit(String name, int value) {
        this.name = name;
        this.value = value;
    }

}
