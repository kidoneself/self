package com.yimao.cloud.base.utils;

import java.math.BigDecimal;

import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;

/***
 * @desc 金额计算
 * @author zhangbaobao
 *
 */
public class AmountUtils {
    
	//100
	public static BigDecimal RATIO=new BigDecimal(100);
	public static String reg = "^[0-9]+(.[0-9]+)?$";
	/****
	 * 将元转换为分，并且不保留小数,向上取整
	 * @param amount
	 */
	public static String  amountYuanToFen(String amount){
		if(StringUtils.isEmpty(amount) ||!amount.matches(reg)){
			return "";
		}
		BigDecimal amt=new BigDecimal(amount);
		return String.valueOf(amt.multiply(RATIO).setScale( 0, BigDecimal.ROUND_UP ).longValue());
	}
	
	/***
	 * 判断金额是否大于0
	 * @param amount
	 * @return
	 */
	public static boolean compareToZero(BigDecimal amount){
		if(null!=amount && amount.compareTo(BigDecimal.ZERO)==1){
			return true;
		}
		return false;
		
	}
	
	/****
	 * 比较两个金额(大于0)的大小,如果amount1 大于等于amount2 返回true,否则返回false
	 * @param amount1
	 * @param amount2
	 * @return
	 */
	public static boolean compareTo(BigDecimal amount1, BigDecimal amount2) {
		if (!AmountUtils.compareToZero(amount1) || !AmountUtils.compareToZero(amount2)) {
			return false;
		}
		int result = amount1.compareTo(amount2);
		if (result == 1) {
			return false;
		}
		return true;

	}
	
	/****
	 * 将金额格式化为两位小数
	 * @param num
	 * @return
	 */
	public static BigDecimal convertTo2NumPoint(BigDecimal num){
		if(null==num ||!compareToZero(num)){
			return new BigDecimal("0.00");
		}
		
		return num.setScale(2);
	}
	
}
