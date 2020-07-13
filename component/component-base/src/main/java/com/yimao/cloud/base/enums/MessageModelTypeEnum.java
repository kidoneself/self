package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * @author Liu Yi
 * @description 消息类别
 * @date 2019/10/11 10:28
 */
public enum MessageModelTypeEnum {
    THRESHOLD_NOTICE("阈值提醒", 1),
    RENEW_ORDER_PUSH("续费推送", 2),
    WARM_PUSH("报警推送", 3),
    CREATE_ACCOUNT("创建账号", 4),
    WATER_ORDER("新工单分配", 5),//净水工单
    ORDER_FINISH("工单完成", 6),
    PASS_AUDIT("审核通过", 7),
    UNPASS_AUDIT("审核不通过", 8),
    DISTRIBUTOR_RENEW_QUOTA("经销商续配额", 9),
    SYSTEM_NOTICE("系统推送", 10),
    VERIFY_NOTICE("提示审核", 11),
    CREATE_STATION_ENGINEER_ACCOUNT("创建安装工", 12),//站务系统售后创建账号消息类别
    CREATE_STATION_DISTRIBUTOR_ACCOUNT("创建经销商", 13);//站务系统售前创建账号消息类别

    public final String name;
    public final int value;

    MessageModelTypeEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public static MessageModelTypeEnum find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }
}
