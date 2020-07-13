package com.yimao.cloud.cms.processor;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.cms.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author liuhao@yimaokeji.com
 * @date 2018/12/05
 */
@Component
@Slf4j
public class VideoCountAddProcessor {

    @Resource
    private VideoService videoService;

    @RabbitListener(queues = RabbitConstant.VIDEO_COUNT_ADD)
    @RabbitHandler
    public void process(Map<String, Object> map) {
        try {
            log.info("===VIDEO_COUNT_ADD模板消息发送队列执行了===");
            videoService.updateVideoCount(map);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
