package com.yimao.cloud.out.vo;

/**
 * 
 * 产品名称VO类
 * @author zhouq [zhouq@hadlinks.com]
 *
 * created on 2017年1月6日下午2:32:49
 */
public class ProductsVO {

	private String id;//产品id
	private String name;//产品名称
	private String type;//产品使用范围

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
