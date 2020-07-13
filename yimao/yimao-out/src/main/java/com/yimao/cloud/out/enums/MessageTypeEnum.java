package com.yimao.cloud.out.enums;


import com.yimao.cloud.base.enums.StatusEnum;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @description   适配于原安装工app菜单显示
 * @author Liu Yi
 * @date 2019/10/17 9:56
 * @return
 */
public enum MessageTypeEnum {
    ALL("全部", 0, StatusEnum.YES.value()),
    NEW_WORK_ORDER("新工单分配", 5, StatusEnum.NO.value()),
    WARM_PUSH("报警推送", 3, StatusEnum.NO.value()),
    SYSTEM_PUSH("系统推送", 10, StatusEnum.NO.value()),
    THRESHOLD_TOO_LOW_PUSH("阀值提醒", 1, StatusEnum.NO.value()),
    RENEW_ORDER_PUSH("续费推送", 2, StatusEnum.NO.value());

    private static List list = new ArrayList();
    private int type;
    private String typeZNText;
    private String isDefault;

    MessageTypeEnum(String typeZNText, int type, String isDefault) {
        this.type = type;
        this.typeZNText = typeZNText;
        this.isDefault = isDefault;
    }

    public static MessageTypeEnum getMessageType(int type) {
        if (type > 0) {
            MessageTypeEnum[] types = values();
            MessageTypeEnum[] var2 = types;
            int var3 = types.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                MessageTypeEnum messageTypeEnum = var2[var4];
                if (messageTypeEnum.type == type) {
                    return messageTypeEnum;
                }
            }
        }

        return null;
    }

    public static String getMessageTypeText(int type) {
        MessageTypeEnum messageTypeEnum = getMessageType(type);
        return messageTypeEnum != null ? messageTypeEnum.getTypeZNText() : ALL.getTypeZNText();
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTypeZNText() {
        return this.typeZNText;
    }

    public String toString() {
        return "{'type':" + this.type + ", 'typeZNText':'" + this.typeZNText + '\'' + ", 'isDefault':'" + this.isDefault + '\'' + '}';
    }

    public static List getMessageTypeJson() {
        if (list != null && list.size() > 0) {
            return list;
        } else {
            MessageTypeEnum[] types = values();
            MessageTypeEnum[] var1 = types;
            int var2 = types.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                MessageTypeEnum messageTypeEnum = var1[var3];
                list.add(JSONObject.fromObject(messageTypeEnum.toString()));
            }
            return list;
        }
    }
}
