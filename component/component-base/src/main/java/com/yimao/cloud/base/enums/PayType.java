package com.yimao.cloud.base.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhilin.he
 * @description 支付类型枚举
 * @date 2019/1/30 15:27
 **/
public enum PayType {

    /**
     * 支付类型：1-微信；2-支付宝；3-POS机；4-转账；
     */

    WECHAT("微信", 1),
    ALIPAY("支付宝", 2),
    POS("POS机", 3),
    TRANSFER("转账", 4),
    OTHER("其他支付", 3);//原工单用

    public final String name;
    public final int value;

    PayType(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static PayType find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }

    public static List<Map<String, Object>> OtherPayList() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map;
        PayType[] otherPayTypes = new PayType[]{PayType.POS, PayType.TRANSFER};
        for (PayType otherPayType : otherPayTypes) {
            map = new HashMap<>();
            map.put("index", otherPayType.value);
            map.put("name", otherPayType.name);
            list.add(map);
        }
        return list;
    }
    
    public static String getPayTypeName(int value) {  
    	PayType[] businessModeEnums = values();  
        for (PayType businessModeEnum : businessModeEnums) {  
            if (businessModeEnum.value==value) {  
                return businessModeEnum.name;  
            }  
        }  
        return "";  
    }

}
