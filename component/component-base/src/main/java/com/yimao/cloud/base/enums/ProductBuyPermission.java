package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * 产品购买权限（人群）枚举类。
 *
 * @auther: liu.lin
 * @date: 2018/12/19
 */
public enum ProductBuyPermission {

    A_1("省代", "A_1", 1),
    A_2("市代", "A_2", 1),
    A_3("区代", "A_3", 1),

    D_0("折机版经销商", "D_0", 2),
    D_50("体验版经销商", "D_50", 2),
    D_350("微创版经销商", "D_350", 2),
    D_650("个人版经销商", "D_650", 2),
    D_950("企业版经销商（主）", "D_950", 2),
    D_1000("企业版经销商（子）", "D_1000", 2),

    M_1("站长（已承包服务站）", "M_1", 3),
    M_2("站长（未承包服务站）", "M_2", 3),

    U_7("分销用户", "U_7", 4),
    U_3("分享用户", "U_3", 4),
    U_4("普通用户", "U_4", 4);

    public final String name; //角色名字
    public final String code; //角色code
    public final int group;   //隶属组

    ProductBuyPermission(String name, String code, int group) {
        this.name = name;
        this.code = code;
        this.group = group;
    }

    public static ProductBuyPermission find(String code) {
        return Arrays.stream(values()).filter(item -> item.code.equalsIgnoreCase(code)).findFirst().orElse(null);
    }

}