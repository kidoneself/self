package com.yimao.cloud.system.processor;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.framework.aop.dto.OperationLogDTO;
import com.yimao.cloud.framework.aop.service.OperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Zhang Bo
 * @date 2018/8/15
 */
@Component
@Slf4j
public class OperationLogProcessor {

    @Resource
    private OperationLogService operationLogService;

    @RabbitListener(queues = RabbitConstant.SYSTEM_OPERATION_LOG)
    @RabbitHandler
    public void processor(OperationLogDTO dto) {
        try {
            operationLogService.save(dto);
        } catch (Exception e) {
            log.error("操作日志添加失败，异常:{}", e.getMessage());
        }
    }

}
