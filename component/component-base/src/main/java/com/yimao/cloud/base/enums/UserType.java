package com.yimao.cloud.base.enums;

import java.util.Arrays;
import java.util.Objects;

/**
 * 用户类型枚举
 *
 * @author Zhang Bo
 * @date 2018/10/23.
 */
public enum UserType {

    UN_BIND_USER("未绑定用户", -1),//云平台用户

    DISTRIBUTOR_50("体验版经销商", 0),
    DISTRIBUTOR_350("微创版经销商", 1),
    DISTRIBUTOR_650("个人版经销商", 2),
    DISTRIBUTOR_950("企业版经销商（主）", 5),
    DISTRIBUTOR_1000("企业版经销商（子）", 6),
    DISTRIBUTOR_DISCOUNT_50("折机版经销商", 8),

    //    USER_3("分享用户", 3),
//    USER_4("普通用户", 4),
//    USER_7("分销用户", 7),
    USER_3("普通用户（有健康大使）", 3),
    USER_4("普通用户（无健康大使）", 4),
    USER_7("会员用户", 7),
    AGENT("代理商", 10),
    AGENT_P("省代", 11),
    AGENT_C("市代", 12),
    AGENT_R("区代", 13),

    STATION_MASTER("服务站站长", 15),

    CREATOR("创始人", 20);

    public final String name;
    public final int value;

    UserType(String name, int value) {
        this.name = name;
        this.value = value;
    }

    /**
     * 判断用户是否为会员用户
     *
     * @param value 用户类型
     */
    public static boolean isVipUser(Integer value) {
        if (value == null) {
            return false;
        }
        if (value == USER_7.value) {
            return true;
        }
        return false;
    }

    /**
     * 判断用户是否为经销商
     *
     * @param value 用户类型
     */
    public static boolean isDistributor(Integer value) {
        if (value == null) {
            return false;
        }
        if (value == DISTRIBUTOR_50.value || value == DISTRIBUTOR_350.value || value == DISTRIBUTOR_650.value
                || value == DISTRIBUTOR_950.value || value == DISTRIBUTOR_1000.value || value == DISTRIBUTOR_DISCOUNT_50.value) {
            return true;
        }
        return false;
    }

    /**
     * 判断用户是否为企业版经销商
     *
     * @param value 用户类型
     */
    public static boolean isCompanyDistributor(Integer value) {
        if (value == null) {
            return false;
        }
        if (value == DISTRIBUTOR_950.value || value == DISTRIBUTOR_1000.value) {
            return true;
        }
        return false;
    }

    /**
     * 根据用户类型获取用户类型名称
     *
     * @param value 用户类型
     */
    public static String getNameByType(Integer value) {
        return Objects.requireNonNull(Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null)).name;
    }

}
