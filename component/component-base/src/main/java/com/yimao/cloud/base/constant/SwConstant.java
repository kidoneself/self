package com.yimao.cloud.base.constant;

import com.yimao.cloud.base.enums.UserType;

/**
 * @author liuhao@yimaokeji.com
 */
public class SwConstant {


    /**
     * 是否是经销商
     *
     * @param userType 用户类型
     * @return
     */
    public static Boolean swHasDistributor(Integer userType) {
        if (userType == UserType.USER_3.value || userType == UserType.USER_4.value || userType == UserType.USER_7.value) {
            return false;
        } else if (userType == UserType.DISTRIBUTOR_50.value || userType == UserType.DISTRIBUTOR_350.value || userType == UserType.DISTRIBUTOR_650.value || userType == UserType.DISTRIBUTOR_950.value || userType == UserType.DISTRIBUTOR_1000.value) {
            return true;
        }
        return false;
    }


    /**
     * 用户类型
     */
    public static String swUserType(Integer userType) {
        switch (userType) {
            case 0:
                return "经销商(体验版)";
            case 1:
                return "经销商(微创版)";
            case 2:
                return "经销商(个人版)";
            case 3:
                return "分享用户";
            case 4:
                return "普通用户";
            case 5:
                return "经销商(企业版)";
            case 6:
                return "经销商子账号(企业版)";
            case 7:
                return "分销用户";
            default:
                return "未知用户";
        }
    }

    /**
     * 支付状态
     */
    public static String swPayStatus(Integer status) {
        switch (status) {
            case 0:
                return "未付款";
            case 1:
                return "失败";
            case 2:
                return "待发货";
            case 3:
                return "待收货";
            case 4:
                return "已完成";
            default:
                return "其他";
        }
    }

    /**
     * 支付方式
     *
     * @param payType 支付方式
     */
    public static String swPayType(Integer payType) {
        switch (payType) {
            case 1:
                return "微信支付";
            case 2:
                return "支付宝支付";
            case 3:
                return "银联支付";
            default:
                return "其他支付";
        }
    }

    /**
     * 来源
     *
     * @param swTerminal 1-终端app；2-微信公众号；3-经销商APP；4-小程序；
     * @return
     */
    public static String swTerminal(Integer swTerminal) {
        switch (swTerminal) {
            case 1:
                return "终端app";
            case 2:
                return "微信公众号";
            case 3:
                return "经销商APP";
            case 4:
                return "小程序";
            default:
                return "其他";
        }
    }

    /**
     * 转换
     *
     * @param ticketNoStatus 体检卡状态
     * @return 状态
     */
    public static String swTicketStatus(Integer ticketNoStatus) {
        switch (ticketNoStatus) {
            case 1:
                return "待使用";
            case 2:
                return "已使用";
            case 3:
                return "待支付";
            case 4:
                return "已过期";
            default:
                return "未兑换";
        }
    }
}
