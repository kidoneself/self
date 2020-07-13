package com.yimao.cloud.order.processor;

import javax.annotation.Resource;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.order.service.RepairOrderService;
import com.yimao.cloud.pojo.dto.order.WorkRepairOrderDTO;

import lombok.extern.slf4j.Slf4j;

/**
 * 水机推送创建故障工单
 * @author yaoweijun
 *
 */
@Component
@Slf4j
public class WorkRepairOrderPushCreateProcessor {
	 @Resource
	 private RepairOrderService repairOrderService;
	 
	 @RabbitListener(queues = RabbitConstant.WATERDEVICE_PUSH_REPAIRORDER_CREATE)
	 @RabbitHandler
	 public void process(WorkRepairOrderDTO dto) {
		 try {
			 repairOrderService.createDevicePushRepairOrder(dto);
		} catch (Exception e) {
			
			if(e instanceof YimaoException) {
				YimaoException yimaoException = (YimaoException)e;
				log.error(yimaoException.getMessage());
			}else if(e instanceof BadRequestException) {
				BadRequestException badRequestException = (BadRequestException)e;
				log.error(badRequestException.getMessage());
			}else {
				log.error(e.getMessage());				
			}		

		}
		 
	 }

}
