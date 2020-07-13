package com.yimao.cloud.pojo.dto.system;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
/**
 * 
 * @author yaoweijun
 *
 */
@Getter
@Setter
@ApiModel(description = "短信消息推送")
public class PhoneMessageDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7187058340400066510L;
	//发送手机号码
	private String phone;
	//发送消息模板类型
	private String type;
	//发送消息模板机制
	private String mechanism;
	//发送消息模板推送对象
	private String pushObject;
	//发送消息模板推送方式
	private String pushMode;
	//发送消息内容（多字段以","按模板替换顺序拼接）
	private String contents;
	
}
