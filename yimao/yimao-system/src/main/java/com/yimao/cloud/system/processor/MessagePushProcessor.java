package com.yimao.cloud.system.processor;

import com.tencent.xinge.Message;
import com.tencent.xinge.MessageIOS;
import com.tencent.xinge.Style;
import com.tencent.xinge.XingeApp;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.MessageModelTypeEnum;
import com.yimao.cloud.base.enums.PhoneSystemType;
import com.yimao.cloud.base.properties.SystemProperties;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.pojo.dto.system.MessagePushDTO;
import com.yimao.cloud.system.po.MessagePush;
import com.yimao.cloud.system.service.MessagePushService;
import com.yimao.cloud.system.util.JpushClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/***
 * 功能描述:消息推送监听处理(系统推送)
 *
 * @auther: liu yi
 * @date: 2019/5/6 14:06
 */
@Component
@Slf4j
public class MessagePushProcessor {
    @Resource
    private MessagePushService messagePushService;
    @Resource
    private JpushClientUtil jpushClientUtil;
    @Resource
    private SystemProperties systemProperties;
    /**
     * 功能描述:APP端消息推送
     */
    @RabbitListener(queues = RabbitConstant.SYSTEM_MESSAGE_PUSH)
    @RabbitHandler
    public void processor(MessagePushDTO mp) {
        try {
            if (mp == null) {
                log.error("消息推送失败");
                return;
            }
            Map<String, Object> custom = new HashMap<>();
            custom.put("createTime", DateUtil.getDateToString(mp.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            custom.put("msgType", mp.getMsgType());
            //插入数据库
            MessagePush messagePush = new MessagePush(mp);
            messagePush.setPushType(MessageModelTypeEnum.SYSTEM_NOTICE.value);
            this.messagePushService.insert(messagePush);
            custom.put("msgId", messagePush.getId());
            switch (mp.getApp()) {
                case 0:
                    if (mp.getClickNotice() == 2) {
                        custom.put("url", mp.getAddress());
                    }
                    systemPush(messagePush);
                    break;
                case 1://安装工
                    if (mp.getDevices() == PhoneSystemType.ANDROID.value) {
                        androidPushAccount_KF(custom, mp);
                    } else if (mp.getDevices() == PhoneSystemType.IOS.value) {
                        iosPushAccount_KF(custom, mp);
                    } else {
                        androidPushAccount_KF(custom, mp);
                        iosPushAccount_KF(custom, mp);
                    }
                    break;
                case 2://经销商
                    jpushClientUtil.sendMessage(messagePush);
                    break;
            }
        } catch (Exception e) {
            log.error("APP消息推送发送异常" + e.getMessage(), e);
        }
    }

    /**
     * 功能描述:系统推送
     */
    public void systemPush(MessagePush mp) {
        Message message = new Message();
        message.setType(Message.TYPE_MESSAGE);
        Style style = new Style(1);
        int retCode_jxs = 0;
        int retCode_kf = 0;
        int retIOSCode_kf = 0;
        Map<String, Object> custom = new HashMap();
        custom.put("msgType", 0);
        custom.put("createTime", DateUtil.getDateToString(mp.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
        if (mp.getClickNotice() == 2) {
            custom.put("url", mp.getAddress());
            //mp.setAddress(url);
        }
        mp.setDevices(0);
        custom.put("msgId", mp.getId());
        message.setContent(mp.getContent());
        message.setStyle(style);
        message.setCustom(custom);
        MessageIOS messageIOS = new MessageIOS();
        JSONObject obj = new JSONObject();
        JSONObject aps = new JSONObject();
        aps.put("title", "系统通知");
        aps.put("msgType", 1);
        aps.put("url", mp.getAddress());
        aps.put("sound", "beep.wav");
        aps.put("alert", mp.getContent());
        aps.put("time", DateUtil.getDateToString(mp.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
        aps.put("content-available", 1);
        aps.put("custom", custom);
        obj.put("aps", aps);
        messageIOS.setRaw(obj.toString());
        messageIOS.setCustom(custom);
        XingeApp xingeAndroid_kf;
        XingeApp xingeIOS_kf;
        switch (mp.getDevices()) {
            case 0:
                xingeAndroid_kf = new XingeApp(systemProperties.getEngineerXingeApp().androidAccessId, systemProperties.getEngineerXingeApp().androidSecretKey);
                JSONObject retAll_kf = xingeAndroid_kf.pushAllDevice(0, message);
                retCode_kf = (Integer) retAll_kf.get("ret_code");
                xingeIOS_kf = new XingeApp(systemProperties.getEngineerXingeApp().iosAccessId, systemProperties.getEngineerXingeApp().iosSecretKey);
                JSONObject retIOSAll_kf = xingeIOS_kf.pushAllDevice(4, messageIOS, Constant.PRO_ENVIRONMENT ? 1 : 0);
                retIOSCode_kf = (Integer) retIOSAll_kf.get("ret_code");

                retCode_jxs = jpushClientUtil.sendMessage(mp);
                if (retCode_kf == 0 || retIOSCode_kf == 0 || retCode_jxs == 0) {
                    log.info("推送全局消息失败。进行删除...");
                    this.messagePushService.deleteMessagePushById(mp.getId());
                }
                break;
            case 1:
                xingeAndroid_kf = new XingeApp(systemProperties.getEngineerXingeApp().androidAccessId, systemProperties.getEngineerXingeApp().androidSecretKey);
                JSONObject ret_kf = xingeAndroid_kf.pushAllDevice(3, message);
                retCode_kf = (Integer) ret_kf.get("ret_code");

                retCode_jxs = jpushClientUtil.sendMessage(mp);
                if (retCode_jxs == 0 || retCode_kf == 0) {
                    log.info("推送全局消息失败。进行删除...");
                    this.messagePushService.deleteMessagePushById(mp.getId());
                }
                break;
            case 2:
                xingeIOS_kf = new XingeApp(systemProperties.getEngineerXingeApp().iosAccessId, systemProperties.getEngineerXingeApp().iosSecretKey);
                JSONObject retI_kf = xingeIOS_kf.pushAllDevice(4, messageIOS, Constant.PRO_ENVIRONMENT ? 1 : 0);
                retCode_kf = (Integer) retI_kf.get("ret_code");

                retCode_jxs = jpushClientUtil.sendMessage(mp);
                if (retCode_jxs == 0 || retCode_kf == 0) {
                    log.info("推送全局消息失败。进行删除...");
                    this.messagePushService.deleteMessagePushById(mp.getId());
                }
        }

    }

    /**
     * 功能描述:安装工安卓推送
     */
    public int androidPushAccount_KF(Map<String, Object> custom, MessagePushDTO mp) {
        Message message = new Message();
        message.setType(Message.TYPE_MESSAGE);
        Style style = new Style(1);
        message.setContent(mp.getContent());
        message.setStyle(style);
        message.setCustom(custom);
        // int retCode = true;
        XingeApp xinge = new XingeApp(systemProperties.getEngineerXingeApp().androidAccessId, systemProperties.getEngineerXingeApp().androidSecretKey);
        JSONObject ret = xinge.pushSingleAccount(0, mp.getReceiver(), message);
        log.info("安卓客服推送：" + mp.getReceiver() + " 消息：" + ret.toString());
        int retCode = (Integer) ret.get("ret_code");
        return retCode;
    }

    /**
     * 功能描述:客服ios推送
     */
    public int iosPushAccount_KF(Map<String, Object> custom, MessagePushDTO mp) {
        MessageIOS messageIOS = buildMessageIOS(custom, mp);

        XingeApp xinge = new XingeApp(systemProperties.getEngineerXingeApp().iosAccessId, systemProperties.getEngineerXingeApp().iosSecretKey);
        JSONObject ret = xinge.pushSingleAccount(0, mp.getReceiver(), messageIOS, Constant.PRO_ENVIRONMENT ? 1 :2);
        log.info("苹果客服推送：" + mp.getReceiver() + " 消息：" + ret.toString());
        return (Integer) ret.get("ret_code");
    }

    private MessageIOS buildMessageIOS(Map<String, Object> custom, MessagePushDTO mp) {
        MessageIOS messageIOS = new MessageIOS();
        messageIOS.setAlert(mp.getContent());
        messageIOS.setCustom(custom);
        JSONObject obj = new JSONObject();
        JSONObject aps = new JSONObject();
        aps.put("sound", "beep.wav");
        aps.put("alert", mp.getContent());
        aps.put("custom", custom);
        aps.put("time", new Date());
        aps.put("content-available", 1);
        obj.put("aps", aps);
        messageIOS.setRaw(obj.toString());
        return messageIOS;
    }
}
