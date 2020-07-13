package com.yimao.cloud.out.configuration;

import com.yimao.cloud.base.constant.RabbitConstant;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 队列创建
 *
 * @author Liu Yi
 * @date 2019/7/13
 */
@Configuration
public class RabbitConfiguration {

    @Bean
    public Queue syncMaintenanceWorkOrderQueue() {
        return new Queue(RabbitConstant.SYNC_MAINTENANCE_WORK_ORDER, true);
    }

    @Bean
    public Queue syncDeviceFaultQueue() {
        return new Queue(RabbitConstant.SYNC_DEVICE_FAULT, true);
    }

}
