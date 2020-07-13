package com.yimao.cloud.order.processor;

import javax.annotation.Resource;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.order.service.SyncWorkOrderService;

import lombok.extern.slf4j.Slf4j;

/**
 * 业务系统工单数据同步到售后系统
 */
@Component
@Slf4j
public class SyncWorkOrderProcessor {

   @Resource
   private SyncWorkOrderService syncWorkOrderService;

    @RabbitListener(queues = RabbitConstant.SYNC_WORK_ORDER)
    @RabbitHandler
    public void process(String workOrderId) {
    	try {
    		syncWorkOrderService.syncWorkOrder(workOrderId);
		} catch (Exception e) {
			log.error("==================业务系统工单数据同步到售后系统失败======="+e.getMessage());
		}
    }

    

}
