package com.yimao.cloud.order.service;

import com.yimao.cloud.pojo.dto.system.TransferAreaInfoDTO;

import java.util.List;

/***
 * 订单数据转让service
 * @author zhangbaobao
 *
 */
public interface OrderTransferService {
	
	/**
	 *安装工转让-- 更新订单、工单信息
	 * @param oldId
	 * @param newId
	 */
	void transferData(Integer oldId, Integer newId);
	
	/***
	 * 服务站公司承保转让-更新订单、工单信息
	 * @param transferAreaInfoDTOS
	 */
	void updateOrderDeviceForEngineerStation(List<TransferAreaInfoDTO> transferAreaInfoDTOS);
	
}
