package com.yimao.cloud.base.enums;

import java.util.Arrays;

/***
 * 产品活动状态枚举
 * @author zhangbaobao
 * @date 2020/3/12
 */
public enum ProductActivityStatusEnum {
	NOT_OPEN("未激活", -1),
	NOT_STARTED("未开始", 1),
	PROCESSING("进行中", 2),
	OVER("已结束", 3),
	TERMINATED("已终止", 4);
    public final String name;
    public final int value;

    ProductActivityStatusEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static ProductActivityStatusEnum find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }
    
    public static String getActivityStatusName(int value) {  
    	ProductActivityStatusEnum[] productActivityStatusEnums = values();  
        for (ProductActivityStatusEnum productActivityStatusEnum : productActivityStatusEnums) {  
            if (productActivityStatusEnum.value==value) {  
                return productActivityStatusEnum.name;  
            }  
        }  
        return "";  
    }
    
}
