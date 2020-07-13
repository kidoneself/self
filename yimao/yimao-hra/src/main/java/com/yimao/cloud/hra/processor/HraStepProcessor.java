package com.yimao.cloud.hra.processor;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.hra.service.HraFlowRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 服务收益分配
 */
@Component
@Slf4j
public class HraStepProcessor {

    @Resource
    private HraFlowRecordService hraFlowRecordService;

    @RabbitListener(queues = RabbitConstant.HRA_STEP)
    @RabbitHandler
    public void process(Map<String, Object> map) {
        try {
            hraFlowRecordService.record(map);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
