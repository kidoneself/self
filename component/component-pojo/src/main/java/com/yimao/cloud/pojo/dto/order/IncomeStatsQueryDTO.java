package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/***
 * 收益统计请求参数
 * @author zhangbaobao
 *
 */
@Data
@ApiModel(description = "产品收益统计（经销商app）")
public class IncomeStatsQueryDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "类型:1-日,2-月,3-年",required=true)
	private Integer type;
	@ApiModelProperty(value = "订单完成日期/续费完成日期",required=true)
	private String completeTime;
	@ApiModelProperty(value = "收益类型:1-产品收益,2-续费收益",required=true)
	private Integer incomeType;
	@ApiModelProperty(value = "经销商id")
	private Integer distributorId;
	@ApiModelProperty(value = "用户id")
	private Integer userId;
	@ApiModelProperty(value = "用户类型")
	private Integer userType;
	@ApiModelProperty(value = "日收益和月收益的日期集合")
	private List<String> dates;

}
