package com.yimao.cloud.base.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: yu chunlei
 * @create: 2018-12-26 16:21:48
 **/
@Slf4j
public class SmsUtil {

    //查账户信息的http地址
    private final static String URI_GET_USER_INFO = "https://sms.yunpian.com/v2/user/get.json";

    //智能匹配模板发送接口的http地址
    private final static String URI_SEND_SMS = "https://sms.yunpian.com/v2/sms/single_send.json";

    //模板发送接口的http地址
    private final static String URI_TPL_SEND_SMS = "https://sms.yunpian.com/v2/sms/tpl_single_send.json";

    //发送语音验证码接口的http地址
    private final static String URI_SEND_VOICE = "https://voice.yunpian.com/v2/voice/send.json";

    //绑定主叫、被叫关系的接口http地址
    private final static String URI_SEND_BIND = "https://call.yunpian.com/v2/call/bind.json";

    //解绑主叫、被叫关系的接口http地址
    private final static String URI_SEND_UNBIND = "https://call.yunpian.com/v2/call/unbind.json";

    //编码格式。发送编码格式统一用UTF-8
    private final static String ENCODING = "UTF-8";

    private final static String SMS_API_KEY = "0e6cb46f24a208c6ca296e44e11f5522";

    private final static String SMS_YX_API_KEY = "f0ba2bbc5ed10071d83f7e2b3b936682";

    /**
     * 取账户信息
     *
     * @return json格式字符串
     */

    public static String getUserInfo() {
        Map<String, String> params = new HashMap<>();
        params.put("apikey", SMS_API_KEY);
        return post(URI_GET_USER_INFO, params);
    }

    /**
     * 智能匹配模板接口发短信
     *
     * @param text   　短信内容
     * @param mobile 　接受的手机号
     * @return json格式字符串
     */

    public static String sendSmsYx(String text, String mobile) {
        Map<String, String> params = new HashMap<>();
        params.put("apikey", SMS_YX_API_KEY);
        params.put("text", text);
        params.put("mobile", mobile);
        return post(URI_SEND_SMS, params);
    }

    /**
     * 智能匹配模板接口发短信
     *
     * @param text   　短信内容
     * @param mobile 　接受的手机号
     * @return json格式字符串
     */

    public static String sendSms(String text, String mobile) {
        Map<String, String> params = new HashMap<>();
        params.put("apikey", SMS_API_KEY);
        params.put("text", text);
        params.put("mobile", mobile);
        return post(URI_SEND_SMS, params);
    }

    /**
     * 通过模板发送短信(不推荐)
     *
     * @param tpl_id    　模板id
     * @param tpl_value 　模板变量值
     * @param mobile    　接受的手机号
     * @return json格式字符串
     */

    public static String tplSendSms(long tpl_id, String tpl_value, String mobile) {
        Map<String, String> params = new HashMap<>();
        params.put("apikey", SMS_API_KEY);
        params.put("tpl_id", String.valueOf(tpl_id));
        params.put("tpl_value", tpl_value);
        params.put("mobile", mobile);
        return post(URI_TPL_SEND_SMS, params);
    }

    /**
     * 通过接口发送语音验证码
     *
     * @param mobile 接收的手机号
     * @param code   验证码
     * @return
     */

    public static String sendVoice(String mobile, String code) {
        Map<String, String> params = new HashMap<>();
        params.put("apikey", SMS_API_KEY);
        params.put("mobile", mobile);
        params.put("code", code);
        return post(URI_SEND_VOICE, params);
    }

    /**
     * 通过接口绑定主被叫号码
     *
     * @param from     主叫
     * @param to       被叫
     * @param duration 有效时长，单位：秒
     * @return
     */

    public static String bindCall(String from, String to, Integer duration) {
        Map<String, String> params = new HashMap<>();
        params.put("apikey", SMS_API_KEY);
        params.put("from", from);
        params.put("to", to);
        params.put("duration", String.valueOf(duration));
        return post(URI_SEND_BIND, params);
    }

    /**
     * 通过接口解绑绑定主被叫号码
     *
     * @param from   主叫
     * @param to     被叫
     * @return
     */
    public static String unbindCall(String from, String to) {
        Map<String, String> params = new HashMap<>();
        params.put("apikey", SMS_API_KEY);
        params.put("from", from);
        params.put("to", to);
        return post(URI_SEND_UNBIND, params);
    }

    /**
     * 基于HttpClient 4.5的通用POST方法
     *
     * @param url       提交的URL
     * @param paramsMap 提交<参数，值>Map
     * @return 提交响应
     */

    public static String post(String url, Map<String, String> paramsMap) {
        CloseableHttpClient client = HttpClients.createDefault();
        //设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(6000).setConnectTimeout(6000).build();
        String responseText = "";
        CloseableHttpResponse response = null;
        try {
            HttpPost method = new HttpPost(url);
            method.setConfig(requestConfig);
            if (paramsMap != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                for (Map.Entry<String, String> param : paramsMap.entrySet()) {
                    NameValuePair pair = new BasicNameValuePair(param.getKey(), param.getValue());
                    paramList.add(pair);
                }
                method.setEntity(new UrlEncodedFormEntity(paramList, ENCODING));
            }
            response = client.execute(method);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            try {
                client.close();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return responseText;
    }

    /**
     * 匹配模板接口发短信
     *
     * @param text   　短信内容
     * @param mobile 　接受的手机号
     * @return json格式字符串
     */
    public static String sendSms(String apikey, String text, String mobile) throws IOException {
        Map<String, String> params = new HashMap();
        params.put("apikey", apikey);
        params.put("text", text);
        params.put("mobile", mobile);
        return post(URI_SEND_SMS, params);
    }
}
