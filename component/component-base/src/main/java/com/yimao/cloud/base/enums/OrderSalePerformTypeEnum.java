package com.yimao.cloud.base.enums;

/****
 * 销售业绩排行类型
 * 
 * @author zhangbaobao
 *
 */
public enum OrderSalePerformTypeEnum {
	INVEST_SALEPERFORM("招商销售", 1), PRODUCT_SALEPERFORM("产品销售", 2), RENEW_SALEPERFORM("续费销售", 3);

	public final String name;
	public final int value;

	OrderSalePerformTypeEnum(String name, int value) {
		this.name = name;
		this.value = value;
	}
}
