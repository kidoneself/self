package com.yimao.cloud.out.vo;

import lombok.Data;

@Data
public class ProductOpenApiVO {

	/**
	 * 产品Id
	 */
	private String id;
	
	/**
	 * 产品名称
	 */
	private String name;
	
	/**
	 * 产品类型
	 */
	private String productType;
	
	/**
	 * 产品类型Id
	 */
	private String productTypeId;
	
	/**
	 * 产品范围
	 */
	private String productScope;
	
	/**
	 * 产品范围Id
	 */
	private String productScopeId;
	
	/**
	 * 产品型号
	 */
	private String productModel;

}
