package com.yimao.cloud.base.constant;

import com.yimao.cloud.base.context.SpringContextHolder;
import com.yimao.cloud.base.enums.EnvironmentEnum;

import java.io.File;
import java.util.regex.Pattern;

/**
 * 常量类
 *
 * @author Zhang Bo
 * @date 2018/7/24.
 */
public class Constant {

    public static final String MICROSERVICE_CAT = "yimao-cat";
    public static final String MICROSERVICE_CMS = "yimao-cms";
    public static final String MICROSERVICE_HRA = "yimao-hra";
    public static final String MICROSERVICE_OPENAPI = "yimao-openapi";
    public static final String MICROSERVICE_ORDER = "yimao-order";
    public static final String MICROSERVICE_OUT = "yimao-out";
    public static final String MICROSERVICE_PRODUCT = "yimao-product";
    public static final String MICROSERVICE_SYSTEM = "yimao-system";
    public static final String MICROSERVICE_USER = "yimao-user";
    public static final String MICROSERVICE_WECHAT = "yimao-wechat";
    public static final String MICROSERVICE_WATER = "yimao-water";
    public static final String MICROSERVICE_STATION = "yimao-station";

    public static final String GATE_WAY_PREFIX = "/api";


    public static final boolean LINUX_SERVER = true;

    public static final String COUNTRY_CODE = "+86";

    //第三方登录类别
    public static final String WECHAT = "WeChatOfficialAccount";
    //public static final String HEALTHY = "WeChatMiniProgram";
    public static final String CAT = "WeChatMiniProgram";

    //小程序商城登录类别
    public static final String WECHAT_MINI_MALL = "WeChatMiniMall";

    public static final String DEFAULT_HEADIMAGE = "/static/headImages/default_headImage.png";

    public static final String IMAGE_TEMP_FOLDER = File.separator + "tmp" + File.separator;

    public static final String WX_JSAPI_ACCESS_TOKEN = "A:WX_JSAPI_ACCESS_TOKEN";
    public static final String WX_HEALTH_MINI_ACCESS_TOKEN = "A:WX_HEALTH_MINI_ACCESS_TOKEN";
    public static final String WX_MINISTORE_ACCESS_TOKEN = "A:WX_MINISTORE_ACCESS_TOKEN";
    // public static final String WX_JXSAPP_ACCESS_TOKEN = "A:WX_JSXAPP_ACCESS_TOKEN";
    public static final String WX_TICKET = "A:WX_TICKET";

    public static final String QRCODE_TICKET = "_qrcode_ticket";

    //用户相关
    public static final String USER_CACHE = "USER::";
    public static final String DISTRIBUTOR_CACHE = "DISTRIBUTOR::";
    public static final String ADMIN_CACHE = "ADMIN::";

    public static final String PERMISSIONS = "PERMISSIONS::";
    public static final String STATION_PERMISSIONS = "STATION_PERMISSIONS";

    public static final String ROLE_PERMISSIONS = "ROLE_PERMISSIONS::";
    public static final String STATION_ROLE_PERMISSIONS = "STATION_ROLE_PERMISSIONS::";

    public static final String D_LOGIN_SETP_TWO = "D_LOGIN_SETP_TWO_";
    public static final String D_FORGET_PWD = "D_FORGET_PWD_";

    //H5经销商注册
    public static final String H5_DISTRIBUTOR_CHECK = "H5_DISTRIBUTOR_CHECK";
    public static final String H5_DISTRIBUTOR_MOBILE_CODE = "H5_DISTRIBUTOR_MOBILE_CODE";
    public static final String H5_DISTRIBUTOR_SMS_CODE = "H5_DISTRIBUTOR_SMS_CODE";
    //H5经销商企业版主账号发展子账号
    public static final String H5_SUBACCOUNT_CHECK = "H5_SUBACCOUNT_CHECK";
    public static final String H5_SUBACCOUNT_MOBILE_CODE = "H5_SUBACCOUNT_MOBILE_CODE";
    public static final String H5_SUBACCOUNT_SMS_CODE = "H5_SUBACCOUNT_SMS_CODE";

    //H5商品分享-登录标识
    public static final String H5_SHARE_LOGIN_SETP = "H5_SHARE_LOGIN_SETP::";
    //专属二维码
    public static final String SPECIAL_CHARACTER_SHARE = "_fff_";

    // HRA相关
    //体检次数限制
    public static final String HRA_TICKET_LIMIT = "LIST:HRA_TICKET_LIMIT";
    //赠送卡过期时间 24小时
    public static final long SEND_TICKET_EXPIRED_TIME = 60 * 60 * 24L;
    //批量赠送卡key值 后加uuid
    public static final String TICKETNO = "LIST:TICKETNO:";

    /**
     * 省市区
     */
    public static final String AREA_CACHE = "AREA_CACHE::";
    /**
     * 手机号正则
     */
    public static final String MOBILE_REGEX = "^1[3|4|5|6|7|8|9][0-9]{9}$";
    /**
     * 密码正则（字母和数字）
     */
    public static final String PASSWORD_REGEX = "^[a-zA-Z0-9]{6,16}$";

    // 快递鸟 信息
    public static final String UC = "优速快递";
    public static final String YTO = "圆通速递";
    public static final String ANE = "安能物流";
    public static final String YMDD = "壹米滴答";
    public static final String SF = "顺丰速运";
    public static final String ZTO = "中通快递";
    public static final String STO = "申通快递";
    public static final String YD = "韵达速递";
    public static final String YZPY = "邮政快递包裹";
    public static final String EMS = "EMS";


    //物流公司编码
    public static final String UC_CODE = "UC";
    public static final String YTO_CODE = "YTO";
    public static final String ANE_CODE = "ANE";
    public static final String YMDD_CODE = "YMDD";
    public static final String SF_CODE = "SF";
    public static final String ZTO_CODE = "ZTO";
    public static final String STO_CODE = "STO";
    public static final String YD_CODE = "YD";
    public static final String YZPY_CODE = "YZPY";
    public static final String EMS_CODE = "EMS";

    //电商ID
    public static final String EBusinessID = "1421816";
    //电商加密私钥，快递鸟提供，注意保管，不要泄漏
    public static final String AppKey = "203ccbb4-c74e-44b4-9653-34e83c21a921";
    //请求url
    public static final String ReqURL = "http://api.kdniao.com/Ebusiness/EbusinessOrderHandle.aspx";

    // 微信提现
    public static final int WITHDRAW_CASH_COUNT = 3;           //每日可提现最大次数
    public static final String MAX_CASH_DAY = "20000";           //每日可提现最大金额
    public static final String MIN_CASH_DAY = "1";               //可提现最小金额


    //服务站
    public static final double EARTH_RADIUS = 6378.137;

    public static final String INCOME_PERMISSION = "1,2,3,5";

    //第三方登录类别
    public static final String WECHAT_OFFICIAL_ACCOUNT = "WeChatOfficialAccount";

    public static boolean LOCAL_ENVIRONMENT = false;
    public static boolean DEV_ENVIRONMENT = false;
    public static boolean TEST_ENVIRONMENT = false;
    public static boolean PRO_ENVIRONMENT = false;

    static {
        String environment = SpringContextHolder.getEnvironment();
        if (EnvironmentEnum.LOCAL.code.equalsIgnoreCase(environment)) {
            LOCAL_ENVIRONMENT = true;
        } else if (EnvironmentEnum.DEV.code.equalsIgnoreCase(environment)) {
            DEV_ENVIRONMENT = true;
        } else if (EnvironmentEnum.TEST.code.equalsIgnoreCase(environment)) {
            TEST_ENVIRONMENT = true;
        } else if (EnvironmentEnum.PRO.code.equalsIgnoreCase(environment)) {
            PRO_ENVIRONMENT = true;
        }
    }


    //内蒙古自治区呼和浩特市只允许手动选择安装工
    String PROVINCE = "内蒙古自治区";
    String CITY = "呼和浩特市";

    //水机180元分配
    String[] MODELTYPES = new String[]{"1601T60天体验", "1602T60天体验", "1603T60天体验", "1601L60天体验"};

    public static final Pattern IMAGE_PATTERN = Pattern.compile(".*(.jpg|.JPG|.png|.PNG|.gif|.GIF|.jpeg|.JPEG)$");
    public static final Pattern VIDEO_PATTERN = Pattern.compile(".*(.mp4|.webm|.ogv)$");
    public static final Pattern PDF_PATTERN = Pattern.compile(".*(.pdf)$");
    // public static final String STATIC_RESOURCE_STARTSWITH = "/static/";


    public static final int MSG_TYPE_RADIO = 0;
    public static final int MSG_TYPE_NOTICE = 1;


    public static final String EXPORT_PROGRESS = "EXPORT_PROGRESS::";
    public static final String SYSTEM_LOGIN_FAIL = "SYSTEM_LOGIN_FAIL::";
    public static final String SYSTEM_LOGIN_LOCK = "SYSTEM_LOGIN_LOCK::";
    public static final String SYSTEM_LOGIN_CODE = "SYSTEM_LOGIN_CODE::";

    public static final String PRODUCT_ACTIVITY_CACHE = "PRODUCT_ACTIVITY_CACHE::";

    public static final String PAY_BODY = "翼猫科技智能净水设备";
}
