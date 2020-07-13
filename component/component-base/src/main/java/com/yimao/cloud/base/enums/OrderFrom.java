package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * @author zhilin.he
 * @description 订单来源枚举
 * @date 2018/12/25 10:49
 */
public enum OrderFrom {

    /**
     * 订单来源：1-终端app；2-微信公众号；3-翼猫APP；4-小程序；5-翼猫总部业务系统；6-H5页面下单
     */
    APP("终端app", 1),
    WECHAT("微信公众号", 2),
    DIS_APP("翼猫APP", 3),
    MINI_CAT("小程序", 4),
    SYSTEM("翼猫总部业务系统", 5),
    H5("H5", 6);

    public final String name;
    public final int value;

    OrderFrom(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public static OrderFrom find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }
    
    /***
     * 根据ocde找name
     * @param value
     * @return
     */
    public static String getOrderFromName(Integer value) {  
    	OrderFrom[] businessModeEnums = values();  
        for (OrderFrom businessModeEnum : businessModeEnums) {  
            if (businessModeEnum.value==value.intValue()) {  
                return businessModeEnum.name;  
            }  
        }  
        return "";  
    }

}
