package com.yimao.cloud.hra.configuration;

import com.yimao.cloud.base.constant.RabbitConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 队列创建
 *
 * @author liuhao@yimaokeji.com
 * @date 2019/04/09
 */
@Configuration
@Slf4j
public class RabbitConfiguration {


    /**
     * 服务收益分配
     *
     * @return queue
     */
    @Bean
    public Queue userSubscribeQueue() {
        return new Queue(RabbitConstant.HRA_ALLOT, true);
    }

    /**
     * HRA步骤
     *
     * @return
     */
    @Bean
    public Queue hraStepQueue() {
        return new Queue(RabbitConstant.HRA_STEP, true);
    }

    /**
     * 导出
     */
    @Bean
    public Queue exportActionHraQueue() {
        return new Queue(RabbitConstant.EXPORT_ACTION_HRA, true);
    }

}
