package com.yimao.cloud.base.constant;

/**
 * @author Zhang Bo
 * @date 2017/12/7.
 */
public class AliConstant {

    // 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String ALIPAY_NOTIFY_URL_ONE = "/api/order/alipay/notify/one";
    public static String ALIPAY_NOTIFY_URL_TWO = "/api/order/alipay/notify/two";
    public static String ALIPAY_NOTIFY_URL_THREE = "/api/order/alipay/notify/three";
    public static String ALIPAY_NOTIFY_URL_FOUR = "/api/order/alipay/notify/four";
    public static String RETURN_URL = "/api/#/shop/paychose";
    // 请求网关地址
    public static String URL = "https://openapi.alipay.com/gateway.do";
    // 编码
    public static String CHARSET = "UTF-8";
    // 返回格式
    public static String FORMAT = "json";
    public static String RSA2 = "RSA2";
    public static String RSA = "RSA";

    // public static String QUICK_WAP_PAY = "QUICK_WAP_PAY";
    // public static String QUICK_MSECURITY_PAY = "QUICK_MSECURITY_PAY";

    public static String TRADE_SUCCESS = "TRADE_SUCCESS";

    public static String ALI_TRADE_WAP = "ALI_TRADE_WAP";
    public static String ALI_TRADE_APP = "ALI_TRADE_APP";
    // public static String ALIPAY_SUCCESS = "10000";


    public static final String APPID_YWL = "2019111169099234";
    public static final String APPID_SWKJ = "2019111169086271";
}
