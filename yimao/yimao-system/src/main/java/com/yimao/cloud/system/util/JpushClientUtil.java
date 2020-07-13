package com.yimao.cloud.system.util;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosAlert;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import com.yimao.cloud.base.context.SpringContextHolder;
import com.yimao.cloud.base.enums.EnvironmentEnum;
import com.yimao.cloud.base.enums.MessagePushModeEnum;
import com.yimao.cloud.base.enums.PhoneSystemType;
import com.yimao.cloud.base.properties.SystemProperties;
import com.yimao.cloud.system.po.MessagePush;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @param
 * @author Liu Yi
 * @description 极光推送（目前只有经销商app）
 * @date 2019/10/23 11:19
 * @return
 */
@Slf4j
@Component
public  class JpushClientUtil {

    @Resource
    private SystemProperties systemProperties;

    private PushResult pushResult = null;
    private JPushClient yimaoClient = null;
    private JPushClient engineerClient = null;

    /**
     * @param
     * @return void
     * @description 发送消息通知封装
     * @author Liu Yi
     * @date 2019/10/23 11:45
     */
    public  Integer sendMessage(MessagePush mp) {
        PushPayload payload;
        try {
            //设备范围： 0-所有设备 1-Android设备 2-IOS设备
            if (mp.getDevices() == null) {
                log.error("消息通知推送失败，设备范围不存在！");
                return 0;
            }
            boolean apnsProduction;
            if(EnvironmentEnum.PRO.code.equalsIgnoreCase(SpringContextHolder.getEnvironment())) {
                apnsProduction=true;
            } else  {
                apnsProduction=false;
            }
            if (mp.getDevices() == PhoneSystemType.ALL.value) {
                payload = JpushClientUtil.buildPushObject_android_and_ios(mp, "extra",apnsProduction);
            } else if (mp.getDevices() == PhoneSystemType.ANDROID.value) {
                payload = buildPushObject_android_alias(mp);
            } else if (mp.getDevices() == PhoneSystemType.IOS.value) {
                payload = buildPushObject_ios_alias(mp,apnsProduction);
            } else {
                log.error("消息通知推送失败，设备范围不存在！");
                return 0;
            }

            if(mp.getApp() == MessagePushModeEnum.YIMAO_ENGINEER_APP_NOTICE.value){
                if(Objects.isNull(engineerClient)){
                    engineerClient = new JPushClient(systemProperties.getEngineerJpushApp().masterSecret,systemProperties.getEngineerJpushApp().getAppKey());
                }
                pushResult = engineerClient.sendPush(payload);
            } else if(mp.getApp() == MessagePushModeEnum.YIMAO_APP_NOTICE.value){
                if(Objects.isNull(yimaoClient)){
                    yimaoClient = new JPushClient(systemProperties.getYimaoJpushApp().masterSecret,systemProperties.getYimaoJpushApp().getAppKey());
                }
                pushResult = yimaoClient.sendPush(payload);
            }

            if (pushResult.getResponseCode() == 200) {
                return 1;
            } else {
                log.error("消息通知推送失败:" + pushResult.getResponseCode());
                return 0;
            }
        } catch (Exception e) {
            log.error("消息通知推送失败:" + e.getMessage());
            return 0;
        }
    }

    /**
     * @param
     * @return cn.jpush.api.push.model.PushPayload
     * @description 安卓单个推送
     * @author Liu Yi
     * @date 2019/10/23 11:17
     */
    private static PushPayload buildPushObject_android_alias(MessagePush mp) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.alias(mp.getReceiverId().toString()))
                .setNotification(Notification.alert(mp.getTitle()))
                //.setCid("d4ee2375846bc30fa51334f5-21f305e0-4a52-42f3-a3dd-d2e49bdf0499")
                .setMessage(cn.jpush.api.push.model.Message.newBuilder()
                        .setMsgContent(mp.getContent())
                        //.addExtra("from", "JPush")
                        .build())
                .build();
    }

    /**
     * @param
     * @return cn.jpush.api.push.model.PushPayload
     * @description ios单个推送
     * @author Liu Yi
     * @date 2019/10/23 11:17
     */
    private static PushPayload buildPushObject_ios_alias(MessagePush mp,boolean apnsProduction) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.ios())
                .setAudience(Audience.alias(mp.getReceiverId().toString()))
                //.setNotification(Notification.alert(mp.getTitle()))
                .setNotification(Notification.newBuilder()
                        .addPlatformNotification(IosNotification.newBuilder()
                                .setAlert(mp.getTitle())
                                .setSound("sound.caf")
                                .build())
                        .build())
                //.setCid("d4ee2375846bc30fa51334f5-21f305e0-4a52-42f3-a3dd-d2e49bdf0499")
                .setMessage(cn.jpush.api.push.model.Message.newBuilder()
                        .setMsgContent(mp.getContent())
                        //.addExtra("from", "JPush")
                        .build())
                .setOptions(Options.newBuilder()
                        //此字段的值是用来指定本推送要推送的apns环境，false表示开发，true表示生产；对android和自定义消息无意义
                        .setApnsProduction(apnsProduction)
                        //此字段是给开发者自己给推送编号，方便推送者分辨推送记录
                        //.setSendno(1)
                        //此字段的值是用来指定本推送的离线保存时长，如果不传此字段则默认保存一天，最多指定保留十天，单位为秒
                        .setTimeToLive(86400)
                        .build()
                )
                .build();
    }

    /**
     * 推送给设备标识参数的用户
     *
     * @param registrationId     设备标识
     * @param notification_title 通知内容标题
     * @param msg_title          消息内容标题
     * @param msg_content        消息内容
     * @param extrasparam        扩展字段
     * @return 0推送失败，1推送成功
     */
    private  int sendToRegistrationId(List<String> registrationList, String notification_title, String msg_title, String msg_content, String extrasparam, String type) {
        int result = 0;
        try {
            PushPayload pushPayload = null;
            if (type.equals("0")) {//自定义推送
                pushPayload = JpushClientUtil.buildPushObject_all_registrationId_alertWithTitle(registrationList, notification_title, msg_title, msg_content, extrasparam);
                System.out.println(pushPayload);
            } else if (type.equals("1")) {
                pushPayload = JpushClientUtil.buildPushObject(registrationList, notification_title, msg_title, msg_content, extrasparam);
                System.out.println(pushPayload);
            }
            JPushClient jPushClient = new JPushClient(systemProperties.getYimaoJpushApp().masterSecret,systemProperties.getYimaoJpushApp().getAppKey());
            PushResult pushResult = jPushClient.sendPush(pushPayload);
            System.out.println(pushResult);
            if (pushResult.getResponseCode() == 200) {
                result = 1;
            }
        } catch (APIConnectionException e) {
            e.printStackTrace();

        } catch (APIRequestException e) {
            e.printStackTrace();
        }

        return result;
    }


    /**
     * 发送给所有用户
     *
     * @param mp          通知内容
     * @param msg_content 消息内容
     * @param extrasparam 扩展字段
     * @return 0推送失败，1推送成功
     */
    private static PushPayload buildPushObject_android_and_ios(MessagePush mp, String extrasparam,boolean apnsProduction) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.all())
                .setMessage(Message.content(mp.getContent()))//自定义信息
                .setNotification(Notification.newBuilder()
                        .setAlert(mp.getTitle())
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setAlert(mp.getTitle())
                                .setTitle(mp.getTitle())
                                //此字段为透传字段，不会显示在通知栏。用户可以通过此字段来做一些定制需求，如特定的key传要指定跳转的页面（value）
                                .addExtra("androidNotification extras key", extrasparam)
                                .build()
                        )
                        .addPlatformNotification(IosNotification.newBuilder()
                                //传一个IosAlert对象，指定apns title、title、subtitle等
                                .setAlert(mp.getTitle())
                                //直接传alert
                                //此项是指定此推送的badge自动加1
                                .incrBadge(1)
                                //此字段的值default表示系统默认声音；传sound.caf表示此推送以项目里面打包的sound.caf声音来提醒，
                                // 如果系统没有此音频则以系统默认声音提醒；此字段如果传空字符串，iOS9及以上的系统是无声音提醒，以下的系统是默认声音
                                .setSound("sound.caf")
                                //此字段为透传字段，不会显示在通知栏。用户可以通过此字段来做一些定制需求，如特定的key传要指定跳转的页面（value）
                                .addExtra("iosNotification extras key", extrasparam)
                                //此项说明此推送是一个background推送，想了解background看：http://docs.jpush.io/client/ios_tutorials/#ios-7-background-remote-notification
                                // .setContentAvailable(true)

                                .build()
                        )

                        .build()
                )
                //Platform指定了哪些平台就会像指定平台中符合推送条件的设备进行推送。 jpush的自定义消息，
                // sdk默认不做任何处理，不会有通知提示。建议看文档http://docs.jpush.io/guideline/faq/的
                // [通知与自定义消息有什么区别？]了解通知和自定义消息的区别
                .setMessage(Message.newBuilder()
                        .setMsgContent(mp.getContent())
                        .setTitle(mp.getTitle())
                        .addExtra("message extras key", extrasparam)
                        .build())

                .setOptions(Options.newBuilder()
                        //此字段的值是用来指定本推送要推送的apns环境，false表示开发，true表示生产；对android和自定义消息无意义
                        .setApnsProduction(apnsProduction)
                        //此字段是给开发者自己给推送编号，方便推送者分辨推送记录
                        .setSendno(1)
                        //此字段的值是用来指定本推送的离线保存时长，如果不传此字段则默认保存一天，最多指定保留十天，单位为秒
                        .setTimeToLive(86400)
                        .build()
                )
                .build();
    }

    private static PushPayload buildPushObject_all_registrationId_alertWithTitle(List<String> registrationList, String notification_title, String msg_title, String msg_content, String extrasparam) {
        System.out.println("----------buildPushObject_all_all_alert");
        //创建一个IosAlert对象，可指定APNs的alert、title等字段
        IosAlert alert = IosAlert.newBuilder()
                .setTitleAndBody(msg_title, notification_title, "")
                .setActionLocKey("PLAY")
                .build();

        return PushPayload.newBuilder()

                //指定要推送的平台，all代表当前应用配置了的所有平台，也可以传android等具体平台
                .setPlatform(Platform.all())
                //指定推送的接收对象，all代表所有人，也可以指定已经设置成功的tag或alias或该应应用客户端调用接口获取到的registration id
                .setAudience(Audience.registrationId(registrationList))
                //jpush的通知，android的由jpush直接下发，iOS的由apns服务器下发，Winphone的由mpns下发
                .setNotification(Notification.newBuilder()
//                		  //指定当前推送的android通知
//                        .addPlatformNotification(AndroidNotification.newBuilder()
//                                .setAlert(notification_title)
//                                .setTitle(msg_title)
//                                //此字段为透传字段，不会显示在通知栏。用户可以通过此字段来做一些定制需求，如特定的key传要指定跳转的页面（value）
//                                .addExtra("extrasparam",extrasparam)
//                                .build())
                        //指定当前推送的iOS通知
                        .addPlatformNotification(IosNotification.newBuilder()
                                //传一个IosAlert对象，指定apns title、title、subtitle等
                                .setAlert(alert)
                                //直接传alert
                                //此项是指定此推送的badge自动加1
                                .incrBadge(0)
                                //此字段的值default表示系统默认声音；传sound.caf表示此推送以项目里面打包的sound.caf声音来提醒，
                                // 如果系统没有此音频则以系统默认声音提醒；此字段如果传空字符串，iOS9及以上的系统是无声音提醒，以下的系统是默认声音
                                .setSound("default")
                                //此字段为透传字段，不会显示在通知栏。用户可以通过此字段来做一些定制需求，如特定的key传要指定跳转的页面（value）
                                .addExtra("extrasparam", extrasparam)
                                //此项说明此推送是一个background推送，想了解background看：http://docs.jpush.io/client/ios_tutorials/#ios-7-background-remote-notification
                                //取消此注释，消息推送时ios将无法在锁屏情况接收
                                // .setContentAvailable(true)

                                .build()).build())

                //Platform指定了哪些平台就会像指定平台中符合推送条件的设备进行推送。 jpush的自定义消息，
                // sdk默认不做任何处理，不会有通知提示。建议看文档http://docs.jpush.io/guideline/faq/的
                // [通知与自定义消息有什么区别？]了解通知和自定义消息的区别
                .setMessage(Message.newBuilder()
                        .setMsgContent(notification_title)
                        .setTitle(msg_title)
                        .addExtra("extrasparam", extrasparam)
                        .addExtra("userId", msg_content.replaceAll("安全提示", ""))
                        .build())

                .setOptions(Options.newBuilder()
                        //此字段的值是用来指定本推送要推送的apns环境，false表示开发，true表示生产；对android和自定义消息无意义
                        .setApnsProduction(false)
                        //此字段是给开发者自己给推送编号，方便推送者分辨推送记录
                        .setSendno(1)
                        //此字段的值是用来指定本推送的离线保存时长，如果不传此字段则默认保存一天，最多指定保留十天；
                        .setTimeToLive(86400)

                        .build())

                .build();

    }

    private static PushPayload buildPushObject(List<String> registrationList, String notification_title, String msg_title, String msg_content, String extrasparam) {
        System.out.println("----------buildPushObject_all_all_alert");
        //创建一个IosAlert对象，可指定APNs的alert、title等字段
        IosAlert alert = IosAlert.newBuilder()
                .setTitleAndBody("test alert", "subtitle", "test ios alert json")
                .setActionLocKey("PLAY")
                .build();
        return PushPayload.newBuilder()
                //指定要推送的平台，all代表当前应用配置了的所有平台，也可以传android等具体平台
                .setPlatform(Platform.all())
                //指定推送的接收对象，all代表所有人，也可以指定已经设置成功的tag或alias或该应应用客户端调用接口获取到的registration id
                .setAudience(Audience.registrationId(registrationList))
                //jpush的通知，android的由jpush直接下发，iOS的由apns服务器下发，Winphone的由mpns下发
                .setNotification(Notification.newBuilder()
                        //指定当前推送的android通知
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setAlert(notification_title)
                                .setTitle(msg_title)
                                //此字段为透传字段，不会显示在通知栏。用户可以通过此字段来做一些定制需求，如特定的key传要指定跳转的页面（value）
                                .addExtra("extrasparam", extrasparam)
                                .build())
                        //指定当前推送的iOS通知
                        .addPlatformNotification(IosNotification.newBuilder()
                                //传一个IosAlert对象，指定apns title、title、subtitle等
                                .setAlert(alert)
                                //直接传alert
                                //此项是指定此推送的badge自动加1
                                .incrBadge(1)
                                //此字段的值default表示系统默认声音；传sound.caf表示此推送以项目里面打包的sound.caf声音来提醒，
                                // 如果系统没有此音频则以系统默认声音提醒；此字段如果传空字符串，iOS9及以上的系统是无声音提醒，以下的系统是默认声音
                                .setSound("default")
                                //此字段为透传字段，不会显示在通知栏。用户可以通过此字段来做一些定制需求，如特定的key传要指定跳转的页面（value）
                                .addExtra("extrasparam", extrasparam)
                                //此项说明此推送是一个background推送，想了解background看：http://docs.jpush.io/client/ios_tutorials/#ios-7-background-remote-notification
                                //取消此注释，消息推送时ios将无法在锁屏情况接收
                                // .setContentAvailable(true)
                                .build())
                        .build())
                //Platform指定了哪些平台就会像指定平台中符合推送条件的设备进行推送。 jpush的自定义消息，
                // sdk默认不做任何处理，不会有通知提示。建议看文档http://docs.jpush.io/guideline/faq/的
                // [通知与自定义消息有什么区别？]了解通知和自定义消息的区别
                .setMessage(Message.newBuilder()
                        .setMsgContent(msg_content)
                        .setTitle(msg_title)
                        .addExtra("message extras key", extrasparam)
                        .build())

                .setOptions(Options.newBuilder()
                        //此字段的值是用来指定本推送要推送的apns环境，false表示开发，true表示生产；对android和自定义消息无意义
                        .setApnsProduction(false)
                        //此字段是给开发者自己给推送编号，方便推送者分辨推送记录
                        .setSendno(1)
                        //此字段的值是用来指定本推送的离线保存时长，如果不传此字段则默认保存一天，最多指定保留十天；
                        .setTimeToLive(86400)
                        .build())
//
                .build();

    }

    private static PushPayload buildPushObject_android_all_alertWithTitle(String notification_title, String msg_title, String msg_content, String extrasparam) {
        System.out.println("----------buildPushObject_android_registrationId_alertWithTitle");
        return PushPayload.newBuilder()
                //指定要推送的平台，all代表当前应用配置了的所有平台，也可以传android等具体平台
                .setPlatform(Platform.android())
                //指定推送的接收对象，all代表所有人，也可以指定已经设置成功的tag或alias或该应应用客户端调用接口获取到的registration id
                .setAudience(Audience.all())
                //jpush的通知，android的由jpush直接下发，iOS的由apns服务器下发，Winphone的由mpns下发
                .setNotification(Notification.newBuilder()
                        //指定当前推送的android通知
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setAlert(notification_title)
                                .setTitle(notification_title)
                                //此字段为透传字段，不会显示在通知栏。用户可以通过此字段来做一些定制需求，如特定的key传要指定跳转的页面（value）
                                .addExtra("androidNotification extras key", extrasparam)
                                .build())
                        .build()
                )
                //Platform指定了哪些平台就会像指定平台中符合推送条件的设备进行推送。 jpush的自定义消息，
                // sdk默认不做任何处理，不会有通知提示。建议看文档http://docs.jpush.io/guideline/faq/的
                // [通知与自定义消息有什么区别？]了解通知和自定义消息的区别
                .setMessage(Message.newBuilder()
                        .setMsgContent(msg_content)
                        .setTitle(msg_title)
                        .addExtra("message extras key", extrasparam)
                        .build())

                .setOptions(Options.newBuilder()
                        //此字段的值是用来指定本推送要推送的apns环境，false表示开发，true表示生产；对android和自定义消息无意义
                        .setApnsProduction(false)
                        //此字段是给开发者自己给推送编号，方便推送者分辨推送记录
                        .setSendno(1)
                        //此字段的值是用来指定本推送的离线保存时长，如果不传此字段则默认保存一天，最多指定保留十天，单位为秒
                        .setTimeToLive(86400)
                        .build())
                .build();
    }

    private static PushPayload buildPushObject_ios_all_alertWithTitle(String notification_title, String msg_title, String msg_content, String extrasparam) {
        System.out.println("----------buildPushObject_ios_registrationId_alertWithTitle");
        IosAlert alert = IosAlert.newBuilder()
                .setTitleAndBody(notification_title, msg_title, msg_content)
                .setActionLocKey("PLAY")
                .build();

        return PushPayload.newBuilder()
                //指定要推送的平台，all代表当前应用配置了的所有平台，也可以传android等具体平台
                .setPlatform(Platform.ios())
                //指定推送的接收对象，all代表所有人，也可以指定已经设置成功的tag或alias或该应应用客户端调用接口获取到的registration id
                .setAudience(Audience.all())
                //jpush的通知，android的由jpush直接下发，iOS的由apns服务器下发，Winphone的由mpns下发
                .setNotification(Notification.newBuilder()
                        //指定当前推送的android通知
                        .addPlatformNotification(IosNotification.newBuilder()
                                //传一个IosAlert对象，指定apns title、title、subtitle等
                                .setAlert(alert)
                                //直接传alert
                                //此项是指定此推送的badge自动加1
                                .incrBadge(1)
                                //此字段的值default表示系统默认声音；传sound.caf表示此推送以项目里面打包的sound.caf声音来提醒，
                                // 如果系统没有此音频则以系统默认声音提醒；此字段如果传空字符串，iOS9及以上的系统是无声音提醒，以下的系统是默认声音
                                .setSound("sound.caf")
                                //此字段为透传字段，不会显示在通知栏。用户可以通过此字段来做一些定制需求，如特定的key传要指定跳转的页面（value）
                                .addExtra("iosNotification extras key", extrasparam)
                                //此项说明此推送是一个background推送，想了解background看：http://docs.jpush.io/client/ios_tutorials/#ios-7-background-remote-notification
                                // .setContentAvailable(true)

                                .build())
                        .build()
                )
                //Platform指定了哪些平台就会像指定平台中符合推送条件的设备进行推送。 jpush的自定义消息，
                // sdk默认不做任何处理，不会有通知提示。建议看文档http://docs.jpush.io/guideline/faq/的
                // [通知与自定义消息有什么区别？]了解通知和自定义消息的区别
                .setMessage(Message.newBuilder()
                        .setMsgContent(msg_content)
                        .setTitle(msg_title)
                        .addExtra("message extras key", extrasparam)
                        .build())

                .setOptions(Options.newBuilder()
                        //此字段的值是用来指定本推送要推送的apns环境，false表示开发，true表示生产；对android和自定义消息无意义
                        .setApnsProduction(false)
                        //此字段是给开发者自己给推送编号，方便推送者分辨推送记录
                        .setSendno(1)
                        //此字段的值是用来指定本推送的离线保存时长，如果不传此字段则默认保存一天，最多指定保留十天，单位为秒
                        .setTimeToLive(86400)
                        .build())
                .build();
    }
}
