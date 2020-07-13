package com.yimao.cloud.system.processor;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.utils.SmsUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.system.MessageTemplateDTO;
import com.yimao.cloud.pojo.dto.system.SmsMessageDTO;
import com.yimao.cloud.system.mapper.MessageOverdueRemindRecordMapper;
import com.yimao.cloud.system.po.MessageOverdueRemindRecord;
import com.yimao.cloud.system.service.MessageTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/***
 * 功能描述:短信推送推送监听处理
 *
 * @auther: liu yi
 * @date: 2019/5/6 14:06
 */
@Component
@Slf4j
public class SMSMessageProcessor {

    @Resource
    private MessageTemplateService messageTemplateService;

    @Resource
    private MessageOverdueRemindRecordMapper messageOverdueRemindRecordMapper;

    /**
     * 功能描述:短信推送
     */
    @RabbitListener(queues = RabbitConstant.SMS_MESSAGE_PUSH)
    @RabbitHandler
    public void processor(SmsMessageDTO smsMessage) {
        try {
            if (smsMessage == null) {
                log.error("短信推送失败，参数非法");
                return;
            }
            //非空校验
            if (Objects.isNull(smsMessage.getType()) || Objects.isNull(smsMessage.getMechanism()) || Objects.isNull(smsMessage.getPushObject()) || Objects.isNull(smsMessage.getPushMode())) {
                log.info("查询消息模板参数为空");
                return;
            }
            if (StringUtil.isBlank(smsMessage.getPhone())) {
                log.info("推送号码不能为空！");
                return;
            }
            if (null == smsMessage.getContentMap() || smsMessage.getContentMap().size() <= 0) {
                log.error("短信推送失败，参数非法");
                return;
            }
            MessageTemplateDTO messageTemplate = messageTemplateService.getMessageTemplateByParam(smsMessage.getType(), smsMessage.getMechanism(), smsMessage.getPushObject(), smsMessage.getPushMode());
            if (Objects.isNull(messageTemplate)) {
                log.error("短信推送失败，未找到模版！");
                return;
            }
            String messageContent = getMessageContent(smsMessage.getContentMap(), messageTemplate.getTemplate());
            //短信推送
            String msgRes = SmsUtil.sendSms(messageContent, smsMessage.getPhone());
            log.info("发送消息返回结果=" + msgRes);

            // if (smsMessage.getMechanism() == MessageMechanismEnum.MONEY_NULL.value) {
            //     //余额为0时，保存一条超期提醒用户拆机事宜的记录
            //     this.saveOverdueRemindRecord(smsMessage.getType(), smsMessage.getMechanism());
            // }
        } catch (Exception e) {
            log.error("短信推送失败" + e.getMessage(), e);
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

    private void saveOverdueRemindRecord(Integer type, Integer mechanism) {
        String code = type + "_" + mechanism;
        MessageOverdueRemindRecord query = new MessageOverdueRemindRecord();
        query.setCode(code);
        int count = messageOverdueRemindRecordMapper.selectCount(query);
    }
}
