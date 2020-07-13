package com.yimao.cloud.order.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yimao.cloud.order.service.OrderTransferService;

/****
 * 订单、收益等数据转让。目前有安装工转让操作
 * @author zhangbaobao
 *
 */
@RestController
public class OrderTransferDataController {
	
	
	@Resource
	private OrderTransferService orderTransferService;
	/****
     * 安装工转让数据(工单、订单、续费单、收益、水机)
     * @param oldId
     * @param newId
     */
    @PostMapping(value = "/order/transfer/data")
	public void transferData(@RequestParam(value = "oldId") Integer oldId,@RequestParam(value = "newId") Integer newId) {
    	orderTransferService.transferData(oldId,newId);
    }
}
