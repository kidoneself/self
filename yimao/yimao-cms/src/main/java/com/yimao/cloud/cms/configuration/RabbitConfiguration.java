package com.yimao.cloud.cms.configuration;

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
     * 视频点赞数，分享数，人数累加
     */
    @Bean
    public Queue videoCountAddQueue() {
        return new Queue(RabbitConstant.VIDEO_COUNT_ADD, true);
    }

    /**
     * 资讯查阅人数增加
     */
    @Bean
    public Queue contentCountAddQueue() {
        return new Queue(RabbitConstant.CONTENT_COUNT_ADD, true);
    }

}
