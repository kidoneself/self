package com.yimao.cloud.system.processor;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.MessagePushModeEnum;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.system.AppMessageDTO;
import com.yimao.cloud.pojo.dto.system.MessageTemplateDTO;

import com.yimao.cloud.system.po.MessagePush;
import com.yimao.cloud.system.service.MessagePushService;
import com.yimao.cloud.system.service.MessageTemplateService;
import com.yimao.cloud.system.util.JpushClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;


/***
 * 功能描述:翼猫APP端消息推送
 *
 * @auther: liu yi
 * @date: 2019/5/6 14:06
 */
@Component
@Slf4j
public class YiMaoAppMessageProcessor {
    @Resource
    private MessagePushService messagePushService;
    @Resource
    private MessageTemplateService messageTemplateService;
    @Resource
    private JpushClientUtil jpushClientUtil;

    /**
     * 功能描述:翼猫APP端消息推送
     */
    @RabbitListener(queues = RabbitConstant.YIMAO_APP_MESSAGE_PUSH)
    @RabbitHandler
    public void processor(AppMessageDTO appMessage) {
        try {
            if (appMessage == null) {
                log.error("经销商消息推送失败");
                return;
            }
            //非空校验
            if (Objects.isNull(appMessage.getPushType()) || Objects.isNull(appMessage.getMechanism()) || Objects.isNull(appMessage.getPushObject())) {
                log.info("查询经销商消息模板参数有误!");
                return;
            }
            if (appMessage.getReceiverId() == null) {
                log.info("经销商消息推送失败，推送人不能为空！");
            }
            if (appMessage.getDevices() == null || (appMessage.getDevices() != 0 && appMessage.getDevices() != 1 && appMessage.getDevices() != 2)) {
                log.info("消息推送失败，设备范围有误！");
                return;
            }
            MessageTemplateDTO messageTemplate = messageTemplateService.getMessageTemplateByParam(appMessage.getPushType(), appMessage.getMechanism(), appMessage.getPushObject(), MessagePushModeEnum.YIMAO_APP_NOTICE.value);
            if (Objects.isNull(messageTemplate)) {
                log.info("查询经销商消息模板为空!");
                return;
            }

            String messageContent = getMessageContent(appMessage.getContentMap(), messageTemplate.getTemplate());
            MessagePush messagePush = new MessagePush(appMessage);
            messagePush.setApp(2);
            messagePush.setContent(messageContent);
            messagePush.setIsDelete(0);
            messagePush.setReadStatus(0);

            //保存通知发送记录
            this.messagePushService.insert(messagePush);
            jpushClientUtil.sendMessage(messagePush);
        } catch (Exception e) {
            log.error("经销商APP消息推送发送异常" + e.getMessage(), e);
        }
    }

    /**
     * @description 动态拼装模版信息
     * @author Liu Yi
     * @date 2019/10/11 15:22
     */
    private String getMessageContent(Map<String, String> map, String template) {
        for (String key : map.keySet()) {
            String value = map.get(key);
            //过滤掉null值
            if (StringUtil.isBlank(value)) {
                value = "";
            } else {
                value = map.get(key);
            }
            template = template.replace(key, value);
        }
        return template;
    }
}
