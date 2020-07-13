package com.yimao.cloud.system.processor;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.pojo.dto.system.StoreHouseOperationLogDTO;
import com.yimao.cloud.pojo.dto.system.TransferOperationLogDTO;
import com.yimao.cloud.system.service.StoreHouseOperationLogService;
import com.yimao.cloud.system.service.TransferOperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Liu Long Jie
 * @date 2020-6-22 11:44:46
 */
@Component
@Slf4j
public class StoreHouseOperationLogProcessor {

    @Resource
    private StoreHouseOperationLogService storeHouseOperationLogService;


    @RabbitListener(queues = RabbitConstant.STORE_HOUSE_OPERATION_LOG)
    @RabbitHandler
    public void processor(StoreHouseOperationLogDTO dto) {
        log.info("====================库存管理操作日志记录队列开始执行=======================");
        try {
            storeHouseOperationLogService.save(dto);
            log.info("====================库存管理操作日志记录队列执行成功=======================");
        } catch (Exception e) {
            log.error("库存管理操作日志记录失败，异常{}",e.getMessage());
        }
    }

}
