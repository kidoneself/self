package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * @description   消息机制
 * @author Liu Yi
 * @date 2019/10/11 10:58
 * @param
 * @return
 */
public enum MessageMechanismEnum {
    RENEW_THRESHOLD("到达续费阀值", 1),
    RENEW_SUCCESS("续费成功", 2),
    NO_RENEWAL_OF_THE_PERIOD("超过期限未续费", 3),
    MONEY_NULL("余额为0", 4),
    FILTER_CHANGE_AFTER("滤芯更换成功(滤芯更换后)", 5),
    FILTER_CHANGE_BEFORE("滤芯更换前（PP 、 UDF 、 CTO 、 T33 ）注：RO无消息推送滤芯更换后", 6),
    DEVICE_FAULT("设备故障（即制水故障)", 7),
    TDS_FAULT("TDS异常（100以上为异常，50以下为优，50-100为良）", 8),
    USER_CREATE_ORDER("用户下单", 9),
    REGISTER_SUCCESS("账号创建成功", 10),
    QUICK_ONLINE_FINANCIAL_AUDIT_PASS("快速上线财务审核(通过)", 11),
    QUICK_ONLINE_AUDIT_PASS("快速上线企业资质审核(通过)", 12),
    QUICK_ONLINE_FINANCIAL_AUDIT_UNPASS("快速上线财务审核(不通过)", 13),
    QUICK_ONLINE_AUDIT_UNPASS("快速上线企业资质审核(不通过)", 14),
    OFFLINE_FINANCIAL_AUDIT_PASS("选择线下支付(商城购物时审核通过)", 15),
    OFFLINE_FINANCIAL_AUDIT_UNPASS("选择线下支付(商城购物时审核不通过)", 16),
    DISTRIBUTOR_RENEW_QUOTA("经销商续配额通知", 17),
    WATER_ORDER_DISTRIBUTION("新工单分配", 18),
    WATER_ORDER_COMPLETION("工单完成", 19),
    DISTRIBUTION_SELF_ORDER_AUDIT("安装工发起退单，经销商进行审核（经销商自己下的订单）", 20),
    DISTRIBUTION_LATTER_ORDER_AUDIT("安装工发起退单，经销商进行审核（经销商下级用户下的订单）", 21);


    public final String name;
    public final int value;

    MessageMechanismEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public static MessageMechanismEnum find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }
    }
