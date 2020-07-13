package com.yimao.cloud.order.service;
/**
 * 同步工单公共服务
 * @author zhangbaobao
 *
 */
public interface SyncWorkOrderService {
	
	/***
	 * 同步售后工单
	 * @param workOrderId
	 */
	public  Boolean syncWorkOrder(String workOrderId);

}
