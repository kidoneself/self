package com.yimao.cloud.pojo.dto.order;

import lombok.Data;

/***
 *  服务区域转让服务区域,如果有未完成和退单中的工单、维修单、维护单，需要返回给操作人确认。
 * @author zhangbaobao
 *
 */
@Data
public class WorkOrderUnfinishedRsDTO {
	
	private Integer orderNum;//订单数
	private Integer orderType;//订单类型:1.工单，2.维修单,3.维护
	private Integer status;//状态,目前只有未完成

}
