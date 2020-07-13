package com.yimao.cloud.out.vo;

import lombok.Data;

@Data
public class ServiceSiteOpenApiVO {

	/**
	 * 服务站id
	 */
	String servicesiteId;

	/**
	 * 省
	 */
	String province;

	/**
	 * 市
	 */
	String city;

	/**
	 * 区
	 */
	String region;

	/**
	 * 详细地址
	 */
	String address;

	/**
	 * 服务站名称
	 */
	String name;

	/**
	 * 联系人
	 */
	String person;

	/**
	 * 联系方式
	 */
	String phone;

	/**
	 * 邮箱
	 */
	String mail;

	/**
	 * 公司名称
	 */
	String company;

	/**
	 * 法人
	 */
	String legalPerson;

	/**
	 * 统一社会信用代码
	 */
	String creditCode;

	/**
	 * 站长姓名
	 */
	String masterName;

	/**
	 * 站长手机号
	 */
	String masterPhone;

	/**
	 * 服务站站长身份证号码
	 */
	String masterIdCard;

}
