package com.yimao.cloud.pojo.vo.order;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/***
 * 批量退款审核对象
 * @author zhangbaobao
 *
 */
@ApiModel(description = "工单对象")
@Getter
@Setter
public class RefundReviewBatchVo implements Serializable{
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Long> ids;//售后单号集合
	private Boolean pass;//是否通过 
	private String reason;//审核原因
}
