package com.yimao.cloud.base.constant;

/**
 * 描述：微信相关的常量配置信息。
 *
 * @Author Zhang Bo
 * @Date 2019/4/24
 */
public class WechatConstant {

    public static final String VERSION_NUMBER = "2.1.0";

    public static final String COOKIE_LOGIN_TOKEN = "YM_LOGIN_TOKEN";


    public static final String WX_AUTH_ACCESS_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";
    //微信支付接口：统一下单
    public static final String WX_UNIFIEDORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    //查询订单
    public static final String WX_ORDERQUERY_URL = "https://api.mch.weixin.qq.com/pay/orderquery";
    public static final String WX_GETTICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";
    public static final String MD5 = "MD5";
    public static final String SHA1 = "SHA1";
    public static final String JSAPI_NOTIFY_URL = "/api/wechat/wxpay/notify";//回调地址
    //普通商品微信支付回调
    public static final String WXPAY_NOTIFY_URL_ONE = "/api/order/wxpay/notify/one";
    //水机续费微信支付回调
    public static final String WXPAY_NOTIFY_URL_TWO = "/api/order/wxpay/notify/two";
    //经销商订单微信支付回调
    public static final String WXPAY_NOTIFY_URL_THREE = "/api/order/wxpay/notify/three";
    //安装工APP端工单微信支付回调
    public static final String WXPAY_NOTIFY_URL_FOUR = "/api/order/wxpay/notify/four";
    //申请退款回调
    public static final String WXREFUND_NOTIFY_URL_ONE = "/api/order/wxrefund/notify/one";

    //退款查询
    public static final String WX_REFUNDQUERY_URL = "https://api.mch.weixin.qq.com/pay/refundquery";


    public static final String SUCCESS = "SUCCESS";
    public static final String FAIL = "FAIL";
    public static final String JSAPI = "JSAPI";
    public static final String NATIVE = "NATIVE";
    public static final String APP = "APP";
    public static final String MWEB = "MWEB";
    public static final String KEFUMSG_TYPE_TEXT = "text";
    public static final String KEFUMSG_TYPE_IMAGE = "image";

    public static final String GRANT_TYPE = "authorization_code";
    public static final String WX_MALL_URL = "https://api.weixin.qq.com/sns/jscode2session";

    public static final String MINI_PAGE = "pages/tab-report/home/home";


    //APP 微信相关
    public static final String APPID = "wx1930af8f821efcf9";
    public static final String SECRET = "ed146a4e64dcac7fb5a5e6b864efed4e";

    //微信退款接口
    public static final String WX_REFUND_URL = "https://api.mch.weixin.qq.com/secapi/pay/refund";
    public static final String RETURN_CODE = "return_code";//微信退款响应状态码 SUCCESS/FAIL
    public static final String RESULT_CODE = "result_code";//微信退款返回业务处理状态码 SUCCESS/FAIL
    public static final String WX_REFUND_NOTIFY_URL = "/api/order/refund/notify";//微信退款回调地址
}
