package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * 同步状态描述转换
 * @author zhangbaobao
 *
 */
public enum AreaSyncState {
	    YES("Y","已同步"),
	    NO("N","未同步"),
	    FAILURE("FAILURE","同步失败");
	    private String status;
	    private String desc;

	    AreaSyncState(String status,String desc) {
	        this.status = status;
	        this.desc=desc;
	    }

	    public String value() {
	        return this.status;
	    }

	    public static boolean isYes(String code) {
	        return code != null && code.equals(YES.value());
	    }

	    public static boolean isNo(String code) {
	        return code != null && code.equals(NO.value());
	    }
	    public static AreaSyncState find(String value) {
	        return Arrays.stream(values()).filter(item -> item.status.equals(value)).findFirst().orElse(null);
	    }
	    
	    public static String getAreaSyncStateName(String value) {  
	    	AreaSyncState[] syncstateModeEnums = values();  
	        for (AreaSyncState syncstateModeEnum : syncstateModeEnums) {  
	            if (syncstateModeEnum.status.equals(value)) {  
	                return syncstateModeEnum.desc;  
	            }  
	        }  
	        return "";  
	    }
	    
}
