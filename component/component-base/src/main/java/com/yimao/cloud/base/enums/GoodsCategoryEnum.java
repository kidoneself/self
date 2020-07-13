package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * 库存物料
 * @author yaoweijun
 *
 */
public enum GoodsCategoryEnum {

	/**
	 *  库存物料分类类型
	 */
	DEVICE("设备",1),
	MATERIAL("耗材",2),
	DISPLAY_DEVICE("展示机",3);
	
	public final String name;
    public final int value;
    
    GoodsCategoryEnum(String name,int value){
    	this.name=name;
    	this.value=value;
    }
    
    public static GoodsCategoryEnum find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }
}
