package com.yimao.cloud.order.service;

import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;

/**
 * 支付订单审核
 */
public interface OfflinePayService {

    void submitCredential(Long mainOrderId, Long subOrderId, String workOrderId, Integer payType, String payCredential);

	void otherPay(WorkOrderDTO wo);

}
