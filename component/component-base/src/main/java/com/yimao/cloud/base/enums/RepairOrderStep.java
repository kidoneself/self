package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * 维修处理中步骤
 * @author yaoweijun
 *
 */
public enum RepairOrderStep {
	CONFIRM(1, "故障确认"),
    REPAIR(2, "故障维修"),
    SUBMIT(3, "确认提交");
	
	public final int value;
    public final String name;
    
    RepairOrderStep(int value,String name){
    	this.value=value;
    	this.name=name;
    }
    
    public static RepairOrderStep find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }
}
