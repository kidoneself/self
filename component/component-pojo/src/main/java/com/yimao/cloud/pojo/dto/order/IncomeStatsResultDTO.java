package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/****
 * 收益统计响应数据
 * @author zhangbaobao
 *
 */
@Data
public class IncomeStatsResultDTO implements Serializable{
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "订单完成日期/续费完成日期")
	private String completeTime;
	@ApiModelProperty(value = "对应日期的收益金额")
	private BigDecimal amount;
}
