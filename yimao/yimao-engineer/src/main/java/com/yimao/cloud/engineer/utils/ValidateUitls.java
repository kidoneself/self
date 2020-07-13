package com.yimao.cloud.engineer.utils;

import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.StringUtil;

/***
 * 校验工具类
 * 
 * @author zhangbaobao
 *
 */
public class ValidateUitls {
	
	public static String HYDRAULICMATCHES = "^([0-9][0-9]*)$|(([1-9][0-9]*)\\.([0-9]))|[0]\\.([0-9])|(([1-9][0-9]*)\\.([0-9]{2}))|[0]\\.([0-9]{2})";
	public static String TDSMATCHES = "^([0-9][0-9]*)$";

	/****
	 * 校验型号、批次码、sn码
	 * 
	 * @param typeCode
	 * @param batchCode
	 * @param snCode
	 */
	public static void validateSnAndBatchCodeAndSimCard( String snCode,String logisticsCode,String simCard) {

		//非空校验
		if(StringUtil.isEmpty(snCode)) {
			throw new YimaoException("SN码不能为空");
		}
		
		if(StringUtil.isEmpty(logisticsCode)) {
			throw new YimaoException("批次码不能为空");
		}
		
		if(StringUtil.isEmpty(simCard)) {
			throw new YimaoException("simCard号不能为空");
		}
		
		
		// 校验批次码与工单中的机型是否一致
		if (!logisticsCode.substring(9, 11).equals(snCode.substring(9, 11))) {
			throw new YimaoException("批次码和SN码的对内型号不一致！");
		}
		
		//校验批次码
		if (!logisticsCode.startsWith("YM")) {
			throw new YimaoException("批次码编码格式不正确");
		} else if (!Character.isUpperCase(logisticsCode.substring(2, 3).toCharArray()[0])) {// 第三位为大写字母
			throw new YimaoException("第三位应为大写字母");
		} else if (logisticsCode.length() != 13 && logisticsCode.length() != 16) {
			throw new YimaoException("设备的批次码应为13或16位");
		} else if (!logisticsCode.substring(3, 7).matches("^[0-9]*$")) {
			throw new YimaoException("设备的批次码第4-8位,日期格式不正确！");
		} else if (Integer.parseInt(logisticsCode.substring(3, 5)) == 0 || Integer.parseInt(logisticsCode.substring(3, 5)) > 12
				|| Integer.parseInt(logisticsCode.substring(5, 7)) == 0
				|| Integer.parseInt(logisticsCode.substring(5, 7)) > 31) {// 第四位到第八位是月份和日
			throw new YimaoException("设备的批次码第4-8位,日期格式不正确！");
		} else if (logisticsCode.length() == 16) {// 新设备
			if (!Character.isUpperCase(logisticsCode.substring(11, 12).toCharArray()[0])) {
				throw new YimaoException("批次号中生产线应为大写字母！");
			} else if (!logisticsCode.substring(12).matches("^[0-9]*$")) {
				throw new YimaoException("设备的批次码后四位格式不正确！");
			}
		} else if (logisticsCode.length() == 13) {// 旧设备
			if (!Character.isUpperCase(logisticsCode.substring(8, 9).toCharArray()[0])) {
				throw new YimaoException("批次号中生产线应为大写字母！");
			} else if (!logisticsCode.substring(9).matches("^[0-9]*$")) {
				throw new YimaoException("设备的批次码后四位格式不正确！");
			}
		}
		
		//校验simcard
		

	}
	
	/****
	 * 校验水源水压
	 * @param tds
	 * @param hydraulic
	 */
	public static  void validateTdsAndHydraulic(String tds, String hydraulic) {
		if(StringUtil.isEmpty(tds)) {
			 throw new YimaoException("tds值不能为空");
		}
        if (!tds.matches(HYDRAULICMATCHES)) {
            throw new YimaoException("请输入正确的tds值");
        }
        
        if(StringUtil.isEmpty(hydraulic)) {
			 throw new YimaoException("原水水压值不能为空");
		}
        
        if (!hydraulic.matches(TDSMATCHES)) {
        	throw new YimaoException("\"请输入正确的原水水压");
        }
	}
}
