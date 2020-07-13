package com.yimao.cloud.wechat.processor;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.wechat.service.WxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 微信模板消息通知
 */
@Component
public class WxTemplateMessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(WxTemplateMessageProcessor.class);

    @Resource
    private WxService wxService;

    @RabbitListener(queues = RabbitConstant.WX_TEMPLATE_MESSAGE_QUEUE)
    @RabbitHandler
    public void process(Map<String, Object> msgMap) {
        try {
            logger.info("===模板消息发送队列执行了=WX_TEMPLATE_MESSAGE_QUEUE==");
            wxService.sendTemplateMessage(msgMap);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}

