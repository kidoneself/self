package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * 经销商角色等级
 *
 * @author hhf
 * @date 2018/12/19
 */
public enum DistributorRoleLevel {

    /**
     * 经销商角色等级 50体验版、350微创版、650个人版、950企业版
     */
    DISCOUNT("折机版经销商", -50),
    D_50("体验版经销商", 50),
    D_350("微创版经销商", 350),
    D_650("个人版经销商", 650),
    D_950("企业版主账号", 950),
    D_1000("企业版子账号", 1000);

    public final String name;
    public final int value;

    DistributorRoleLevel(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static DistributorRoleLevel find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }

    public static Integer getUserType(int value) {
        if (value == 50) {
            return 0;
        }
        if (value == 350) {
            return 1;
        }
        if (value == 650) {
            return 2;
        }
        if (value == 950) {
            return 5;
        }
        if (value == 1000) {
            return 6;
        }
        if(value == -50) {
        	return 8;
        }
        return null;
    }

}
