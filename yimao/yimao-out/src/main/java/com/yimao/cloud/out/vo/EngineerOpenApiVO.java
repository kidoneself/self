package com.yimao.cloud.out.vo;

import lombok.Data;

/**
 * 安装工程师人员VO
 */
@Data
public class EngineerOpenApiVO {

	private String id; //老安装工id
	private String loginName;//登录名
	private String realName;//真实姓名
	private String idCard;//身份证号
	private String sex;//性别
	private String phone;//联系方式
	private String province;//省份
	private String city;//城市
	private String region;//地区
	private String siteName;//服务站名称
	private String siteId;//服务站Id
	private String mail;//邮箱
	private boolean forbidden;//是否禁用

}
