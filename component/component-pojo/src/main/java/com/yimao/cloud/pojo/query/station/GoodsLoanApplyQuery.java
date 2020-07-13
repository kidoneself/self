package com.yimao.cloud.pojo.query.station;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GoodsLoanApplyQuery extends BaseQuery implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 4867134201589993988L;
	
	@ApiModelProperty(value = "借调状态 0-待审核 1-已通过 2-拒绝")
	private Integer status;
	
	@ApiModelProperty(value = "借调类型 0-借入 1-借出")
	private Integer applyType;

}
