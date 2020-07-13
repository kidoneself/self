package com.yimao.cloud.base.enums;

import java.util.Arrays;
import java.util.Objects;

/**
 * 时间类型
 *
 * @author liuhao@yimaokeji.com
 */
public enum TimeUnitEnum {

    /**
     * 状态 1-日 2-周  3-月 4-季 5-年
     */
    DAY("日", 1),
    WEEK("周", 2),
    MONTH("月", 3),
    QUARTER("季", 4),
    YEAR("年", 5);

    public final String name;
    public final Integer value;

    TimeUnitEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public static TimeUnitEnum find(Integer value) {
        return Arrays.stream(values()).filter(item -> Objects.equals(item.value, value)).findFirst().orElse(null);
    }
}
