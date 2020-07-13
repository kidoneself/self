package com.yimao.cloud.water.processor;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.framework.aop.dto.OperationLogDTO;
import com.yimao.cloud.framework.aop.service.OperationLogService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 系统操作日志监听队列
 *
 * @author Zhang Bo
 * @date 2019/1/11
 */
@Component
public class OperationLogProcessor {

    @Resource
    private OperationLogService operationLogService;

    @RabbitListener(queues = RabbitConstant.WATER_OPERATION_LOG)
    @RabbitHandler
    public void processor(OperationLogDTO dto) {
        operationLogService.save(dto);
    }

}
