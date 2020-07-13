package com.yimao.cloud.system.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.base.Objects;
import com.yimao.cloud.pojo.dto.user.DistributorOrderExportDTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * 经销商订单导出类型转化类
 * @author yaoweijun
 *
 */
@Data
@ApiModel(description = "经销商订单导出转化类")
public class DistributorOrderExportConvertDTO implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 5407780696445478844L;
	  	@ApiModelProperty(value = "订单Id")
	    private Long id;
	  	@ApiModelProperty(value = "省")
	    private String province;
	  	@ApiModelProperty(value = "市")
	    private String city;
	  	 @ApiModelProperty(value = "区")
	    private String region;
	  	@ApiModelProperty(value = "订单类型/0-注册、1-升级、2-续费")
	    private String orderType;
	  	@ApiModelProperty(value = "经销商账号")
	    private String distributorAccount;
	  	@ApiModelProperty(value = "经销商姓名")
	    private String name;
	  	@ApiModelProperty(value = "经销商类型/50体验版、350微创版、650个人版、950企业版")
	    private String roleLevel;
	  	@ApiModelProperty(value = "升级经销商类型/50体验版、350微创版、650个人版、950企业版")
	    private String destRoleLevel;
	    @ApiModelProperty(value = "性别：1-男，2-女")
	    private String sex;
	    @ApiModelProperty(value = "身份证号")
	    private String idCard;
	    @ApiModelProperty(value = "手机号")
	    private String phone;
	    @ApiModelProperty(value = "推荐人姓名")
	    private String recommendName;
	    @ApiModelProperty(value = "支付方式/0-支付宝、1-微信、2-pos机、4-转账")
	    private String payType;
	    @ApiModelProperty(value = "支付状态/0-未支付、1-已支付、3-待审核")
	    private String payState;
	    @ApiModelProperty(value = "支付时间")
	    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	    private String payTime;
	    @ApiModelProperty(value = "价格")
	    private BigDecimal price;
	    @ApiModelProperty(value = "订单状态/0-待审核、1-已完成、2-待付款")
	    private String orderState;
	    @ApiModelProperty(value = "用户签署状态/0-未签署 、1-已经签署")
	    private String userSignState;
	    @ApiModelProperty(value = "服务站合同签署状态/0-未签署 、1-已经签署")
	    private String stationSignState;
	    @ApiModelProperty(value = "翼猫合同签署状态/0-未签署、1-已经签署")
	    private String ymSignState;
	    @ApiModelProperty(value = "财务审核状态/0-未审核、1-审核通过、2-审核不通过、3-无需审核")
	    private String financialState;
	    @ApiModelProperty(value = "财务审核人")
	    private String financialName;
	    @ApiModelProperty(value = "财务审核时间")
	    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	    private String financialTime;
	    @ApiModelProperty(value = "创建时间")
	    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	    private String createTime;
	    @ApiModelProperty(value = "支付流水号/线上付款的流水号")
	    private String tradeNo;
	    @ApiModelProperty(value = "订单来源/0-H5页面、1-经销商app、2-翼猫业务系统")
	    private String orderSouce;
	    @ApiModelProperty(value = "企业审核状态/0-未审核、1-审核通过、2-审核不通过、3-无需审核")
	    private String enterpriseState;
	    @ApiModelProperty(value = "企业审核人")
	    private String enterpriseUser;
	    @ApiModelProperty(value = "企业审核时间")
	    @JsonFormat(pattern="yyyy-MM-dd hh:mm:ss")
	    private String enterpriseTime;
	    @ApiModelProperty(value = "企业名称")
	    private String enterpriseName;
	    @ApiModelProperty(value = "完成时间")
	    @JsonFormat(pattern="yyyy-MM-dd hh:mm:ss")
	    private String completionTime;
	    @ApiModelProperty(value = "地区")
	    private String area;
	    @ApiModelProperty(value = "服务站公司名称")
	    private String stationCompanyName;
	    @ApiModelProperty(value = "财务审核次数")
	    private Integer financialCount;
	    @ApiModelProperty(value = "是否创建合同")
	    private String isCreateProtocol;

}
