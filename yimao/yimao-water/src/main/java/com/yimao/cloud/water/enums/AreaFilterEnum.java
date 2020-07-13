package com.yimao.cloud.water.enums;

/**
 * 区域选择过滤。
 *
 * @author Zhang Bo
 * @date 2019/2/15.
 */
public enum AreaFilterEnum {

    UNLIMITED("不限", null),
    SELECT("多选", 1);

    public final String name;
    public final Integer value;

    AreaFilterEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

}
