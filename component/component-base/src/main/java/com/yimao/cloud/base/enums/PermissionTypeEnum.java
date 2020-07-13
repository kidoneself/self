package com.yimao.cloud.base.enums;

/**
 * 权限类型枚举
 *
 * @author Liu Long Jie
 * @date 2020-5-11
 */
public enum PermissionTypeEnum {

    ALL("售前+售后", 0),
    PRE_SALE("售前", 1),
    AFTER_SALE("售后", 2),
    ;

    public final String name;
    public final int value;

    PermissionTypeEnum(String name, int value) {
        this.name = name;
        this.value = value;
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
