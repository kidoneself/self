package com.yimao.cloud.pojo.query.order;

import java.io.Serializable;
import java.util.Date;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(description = "应体列表查询条件")
public class WithdrawQuery implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4658823841288792757L;
	private String orderId;
    private Integer refer;
    private Integer userId;
    private String phone;
    private Long partnerTradeNo;
	@ApiModelProperty(value = "提现审核结束时间")
	private String startTime;
	@ApiModelProperty(value = "提现审核结束时间")
	private String endTime;

	private Integer incomeType;
	private Integer status;

}
