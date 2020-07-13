package com.yimao.cloud.base.enums;

import java.util.Arrays;

/***
 * 签约类型
 * @author zhangbaobao
 *
 */
public enum SignTypeEnum {
	
	PAPER_CONTRACT("纸质", 1),
	ELECTRONIC_SIGN("电子签约", 2);

    public final String name;
    public final int value;

    SignTypeEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static SignTypeEnum find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }
    
    /***
     * 校验签约类型是否合法
     * @param value
     * @return
     */
    public static Boolean exsistsSignType(int value) {
    	if (find(value)!=null) {
			return true;
		}
    	return false;
    }
}
