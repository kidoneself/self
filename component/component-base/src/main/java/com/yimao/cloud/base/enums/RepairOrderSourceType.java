package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * 维修工单来源
 */
public enum RepairOrderSourceType {
		
    ENGINEER_APP(0, "安装工app"),
    SYSTEM(1, "后台业务系统创建"),
    WATER_DEVICE_PUSH(2, "水机故障推送创建");
	
	public final int value;
    public final String name;
    
    RepairOrderSourceType(int value,String name){
    	this.value=value;
    	this.name=name;
    }
    
    public static RepairOrderSourceType find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }

}
