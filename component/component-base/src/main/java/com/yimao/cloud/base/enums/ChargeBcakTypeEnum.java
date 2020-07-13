package com.yimao.cloud.base.enums;

/****
 * 退单类型
 * 
 * @author zhangbaobao
 *
 */
public enum ChargeBcakTypeEnum {
	USER_CHARGEBACK(1, "用户退单"), DEALER_CHARGEBACK(2, "经销商退单"), other_CHARGEBACK(3, "其他");

	private int type;
	private String desc;

	ChargeBcakTypeEnum(int type, String desc) {
		this.type = type;
		this.desc = desc;
	}

	public int value() {
		return this.type;
	}

	public static String getChargeBcakTypeDesc(int value) {
		ChargeBcakTypeEnum[] cbtEnums = values();
		for (ChargeBcakTypeEnum chargeEnum : cbtEnums) {
			if (chargeEnum.type == value) {
				return chargeEnum.desc;
			}
		}
		return "";
	}
}
