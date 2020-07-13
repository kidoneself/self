package com.yimao.cloud.base.enums;

/**
 * @author Lizhqiang
 * @date 2019/3/19
 */

public enum HraStateEnum {


    /**
     * 服务站查询hra条件
     * 0 不限，1 没有 2 有
     */
    ALL("ALL", 0),
    HAS("HAS", 1),
    NONE("NONE", 2);

    public final String name;
    public final Integer value;

    HraStateEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }


}
