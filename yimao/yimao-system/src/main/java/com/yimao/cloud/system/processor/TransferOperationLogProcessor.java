package com.yimao.cloud.system.processor;

import javax.annotation.Resource;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.pojo.dto.system.TransferOperationLogDTO;
import com.yimao.cloud.system.service.TransferOperationLogService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Liu Long Jie
 * @date 2020-5-21 11:18:58
 */
@Component
@Slf4j
public class TransferOperationLogProcessor {

    @Resource
    private TransferOperationLogService transferOperationLogService;


    @RabbitListener(queues = RabbitConstant.TRANSFER_OPERATION_LOG)
    @RabbitHandler
    public void processor(TransferOperationLogDTO dto) {
        log.info("====================转让日志记录队列开始执行=======================");
        try {
        	transferOperationLogService.save(dto);
            log.info("====================转让日志记录队列执行成功=======================");
        } catch (Exception e) {
            log.error("转让日志记录失败，异常{}",e.getMessage());
        }
    }

}
