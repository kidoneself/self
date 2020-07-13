package com.yimao.cloud.system.processor;


import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.MessagePushModeEnum;
import com.yimao.cloud.base.properties.SystemProperties;
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
 * 功能描述:安装工app消息推送监听处理
 *
 * @auther: liu yi
 * @date: 2019/5/6 14:06
 */
@Component
@Slf4j
public class EngineerAppMessageProcessor {
    @Resource
    private MessagePushService messagePushService;
    @Resource
    private MessageTemplateService messageTemplateService;
    @Resource
    private SystemProperties systemProperties;
    @Resource
    private JpushClientUtil jpushClientUtil;

    /**
     * 功能描述:安装工app消息推送监听处理
     */
    @RabbitListener(queues = RabbitConstant.ENGINEER_APP_MESSAGE_PUSH)
    @RabbitHandler
    public void processor(AppMessageDTO appMessage) {
        log.info("========安装工app消息推送=======");
        try {
            if (appMessage == null) {
                log.error("消息推送失败");
                return;
            }
            //非空校验
            if (Objects.isNull(appMessage.getPushType()) || Objects.isNull(appMessage.getMechanism()) || Objects.isNull(appMessage.getPushObject())) {
                log.info("查询消息模板参数为空");
                return;
            }
            if (null == appMessage.getContentMap() || appMessage.getContentMap().size() <= 0) {
                log.error("短信推送失败，参数非法");
                return;
            }
            if (appMessage.getReceiverId() == null) {
                log.info("消息推送失败，推送人id不能为空！");
                return;
            }
            if (StringUtil.isBlank(appMessage.getReceiver())) {
                log.info("消息推送失败，推送人不能为空！");
                return;
            }
            if (appMessage.getDevices() == null || (appMessage.getDevices() != 0 && appMessage.getDevices() != 1 && appMessage.getDevices() != 2)) {
                log.info("消息推送失败，设备范围有误！");
                return;
            }

            //获取模版信息
            MessageTemplateDTO messageTemplate = messageTemplateService.getMessageTemplateByParam(appMessage.getPushType(), appMessage.getMechanism(), appMessage.getPushObject(), MessagePushModeEnum.YIMAO_ENGINEER_APP_NOTICE.value);
            if (Objects.isNull(messageTemplate)) {
                log.error("短信推送失败，短信模版不存在！");
                return;
            }
            String messageContent = getMessageContent(appMessage.getContentMap(), messageTemplate.getTemplate());
            MessagePush messagePush = new MessagePush(appMessage);
            messagePush.setApp(1);
            messagePush.setContent(messageContent);
            messagePush.setIsDelete(0);
            messagePush.setReadStatus(0);

            //保存通知发送记录
            this.messagePushService.insert(messagePush);
            jpushClientUtil.sendMessage(messagePush);
            
            /*String receiverId = String.valueOf(appMessage.getReceiverId());
            Map<String, Object> custom = new HashMap<>();
            custom.put("createTime", DateUtil.getDateToString(appMessage.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            custom.put("msgType", appMessage.getMsgType());
            custom.put("msgId", messagePush.getId());
            if (appMessage.getDevices() == PhoneSystemType.ANDROID.value) {
                androidPushAccount(custom, appMessage.getReceiver(), messageContent);
            } else if (appMessage.getDevices() == PhoneSystemType.IOS.value) {
                iosPushAccount(custom, receiverId, messageContent);
            } else {
                androidPushAccount(custom, appMessage.getReceiver(), messageContent);
                iosPushAccount(custom, appMessage.getReceiver(), messageContent);
            }*/
        } catch (Exception e) {
            log.error("APP消息推送发送异常" + e.getMessage(), e);
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

    /**
     * 功能描述:客服安卓推送
     *//*
    public int androidPushAccount(Map<String, Object> custom, String userName, String messageContent) {
        Message message = new Message();
        message.setType(Message.TYPE_MESSAGE);
        Style style = new Style(1);
        message.setContent(messageContent);
        message.setStyle(style);
        message.setCustom(custom);
        // int retCode = true;

        XingeApp xinge = new XingeApp(systemProperties.getEngineerXingeApp().androidAccessId, systemProperties.getEngineerXingeApp().androidSecretKey);
        JSONObject ret = xinge.pushSingleAccount(0, userName, message);
        log.info("安卓客服推送：" + userName + " 消息：" + ret.toString());
        int retCode = (Integer) ret.get("ret_code");
        return retCode;
    }

    *//**
     * 功能描述:客服ios推送
     *//*
    public int iosPushAccount(Map<String, Object> custom, String userName, String messageContent) {
        MessageIOS messageIOS = buildMessageIOS(custom, messageContent);
        //XingeApp xinge = new XingeApp(MessageConstant.IOS_ACCESS_ID_KF, MessageConstant.IOS_SECRET_KEY_KF);
        XingeApp xinge = new XingeApp(systemProperties.getEngineerXingeApp().iosAccessId, systemProperties.getEngineerXingeApp().iosSecretKey);
        JSONObject ret = xinge.pushSingleAccount(0, userName, messageIOS, Constant.PRO_ENVIRONMENT ? 1 : 2);
        log.info("苹果客服推送：" + userName + " 消息：" + ret.toString());
        return (Integer) ret.get("ret_code");
    }

    private MessageIOS buildMessageIOS(Map<String, Object> custom, String messageContent) {
        MessageIOS messageIOS = new MessageIOS();
        messageIOS.setAlert(messageContent);
        messageIOS.setCustom(custom);
        JSONObject obj = new JSONObject();
        JSONObject aps = new JSONObject();
        aps.put("sound", "beep.wav");
        aps.put("alert", messageContent);
        aps.put("custom", custom);
        aps.put("time", new Date());
        aps.put("content-available", 1);
        obj.put("aps", aps);
        messageIOS.setRaw(obj.toString());
        return messageIOS;
    }*/
}
