package com.yimao.cloud.wechat.configuration;

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
     * 关注公众号
     *
     * @return queue
     */
    @Bean
    public Queue userSubscribeQueue() {
        return new Queue(RabbitConstant.USER_SUBSCRIBE_QUEUE, true);
    }

    /**
     * 取关公众号
     *
     * @return queue
     */
    @Bean
    public Queue userUnSubscribeQueue() {
        return new Queue(RabbitConstant.USER_UNSUBSCRIBE_QUEUE, true);
    }

    /**
     * 微信模板消息推送队列
     *
     * @return queue
     */
    @Bean
    public Queue wxTemplateMessageQueue() {
        return new Queue(RabbitConstant.WX_TEMPLATE_MESSAGE_QUEUE, true);
    }
}
