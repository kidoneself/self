package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * 维修工单状态
 * @author yaoweijun
 *
 */
public enum RepairOrderStatus {
	  	PENDING(1, "待维修"),
	  	PROCESSIONG(2, "维修中"),
	    HANG_UP(3, "挂单"),	   
	    SOLVED(4, "已完成");
		
		public final int value;
	    public final String name;
	    
	    RepairOrderStatus(int value,String name){
	    	this.value=value;
	    	this.name=name;
	    }
	    
	    public static RepairOrderStatus find(int value) {
	        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
	    }
}
