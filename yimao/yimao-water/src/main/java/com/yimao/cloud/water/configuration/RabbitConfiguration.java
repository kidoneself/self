package com.yimao.cloud.water.configuration;

import com.yimao.cloud.base.constant.RabbitConstant;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 队列创建
 *
 * @author Zhang Bo
 * @date 2019/4/11
 */
@Configuration
public class RabbitConfiguration {

    @Bean
    public Queue operationLogQueue() {
        return new Queue(RabbitConstant.WATER_OPERATION_LOG, true);
    }

    @Bean
    public Queue syncDeviceQueue() {
        return new Queue(RabbitConstant.SYNC_DEVICE, true);
    }

    @Bean
    public Queue deviceFaultQueue() {
        return new Queue(RabbitConstant.DEVICE_FAULT, true);
    }

    @Bean
    public Queue changeDeviceDistributorQueue() {
        return new Queue(RabbitConstant.CHANGE_DEVICE_DISTRIBUTOR, true);
    }

    @Bean
    public Queue changeDeviceUserQueue() {
        return new Queue(RabbitConstant.CHANGE_DEVICE_USER, true);
    }

    /**
     * 导出
     */
    @Bean
    public Queue exportActionWaterQueue() {
        return new Queue(RabbitConstant.EXPORT_ACTION_WATER, true);
    }
    

}
