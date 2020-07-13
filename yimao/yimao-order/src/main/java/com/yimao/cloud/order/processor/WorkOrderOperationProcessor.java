package com.yimao.cloud.order.processor;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.order.mapper.WorkOrderOperationMapper;
import com.yimao.cloud.order.po.WorkOrderOperation;
import com.yimao.cloud.pojo.dto.order.WorkOrderOperationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 工单操作记录
 */
@Component
@Slf4j
public class WorkOrderOperationProcessor {

    @Resource
    private WorkOrderOperationMapper workOrderOperationMapper;

    @RabbitListener(queues = RabbitConstant.WORK_ORDER_OPERATION)
    @RabbitHandler
    public void process(WorkOrderOperationDTO dto) {
        try {
            log.info("添加工单操作记录");
            WorkOrderOperation operation = new WorkOrderOperation(dto);
            operation.setId(null);
            workOrderOperationMapper.insert(operation);
        } catch (Exception e) {
            log.error("添加工单操作记录异常");
            log.error(e.getMessage(), e);
        }
    }

}
