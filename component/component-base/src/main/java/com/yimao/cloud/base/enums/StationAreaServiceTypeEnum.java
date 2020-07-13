package com.yimao.cloud.base.enums;

import java.util.Arrays;
import java.util.Objects;

/**
 * 服务站、服务站公司的服务地区所拥有服务类型枚举
 *
 * @author Liu Long Jie
 * @date 2020-5-12
 */
public enum StationAreaServiceTypeEnum {

    ALL("售前+售后", 0),
    PRE_SALE("售前", 1),
    AFTER_SALE("售后", 2),
    ;

    public final String name;
    public final int value;

    StationAreaServiceTypeEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static String getServiceTypeName(int value){
        return Objects.requireNonNull(Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null)).name;
    }

    /**
     * 判断是否有售前权限
     *
     * @param value 用户类型
     */
    public static boolean havePreSale(Integer value) {
        if (value == null) {
            return false;
        }
        if (value == ALL.value || value == PRE_SALE.value) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否有售后权限
     *
     * @param value 用户类型
     */
    public static boolean haveAfterSale(Integer value) {
        if (value == null) {
            return false;
        }
        if (value == ALL.value || value == AFTER_SALE.value) {
            return true;
        }
        return false;
    }
}
