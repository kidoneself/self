package com.yimao.cloud.engineer.constant;

public final class MsgConstant {
    public static final String SYSTEM_ERROR_CODE = "00";
    public static final String SYSTEM_ERROR_MSG = "系统异常";
    public static final String MSGCODE_FAIL_CODE = "11";
    public static final String MSGCODE_FAIL_MSG = "验证码发送失败";
    public static final String MSGCODE_ERROR_CODE = "12";
    public static final String MSGCODE_ERROR_MSG = "验证码错误";
    public static final String USER_NOT_EXIST_CODE = "13";
    public static final String USER_NOT_EXIST_MSG = "用户名不存在";
    public static final String PASSWORD_ERROR_CODE = "14";
    public static final String PASSWORD_ERROR_MSG = "密码输入错误";
    public static final String WATERDEVICE_ERROR_CODE = "15";
    public static final String WATERDEVICE_ERROR_MSG = "无此型号水机";
    public static final String AUTHORIZATION_ERROR_CODE = "16";
    public static final String AUTHORIZATION_ERROR_MSG = "授权码不正确";
    public static final String AVAILABLE_CODE = "17";
    public static final String AVAILABLE_CODE_MSG = "并发访问数量大";
    public static final String NO_STORE_CODE = "18";
    public static final String NO_STORE_MSG = "该区域无库存";
    public static final String SNCODE_ERROR_CODE = "19";
    public static final String SNCODE_ERROR_MSG = "sn码不存在";
    public static final String PHONE_NOT_EXIST_CODE = "20";
    public static final String PHONE_NOT_EXIST_MSG = "该号码不存在";
    public static final String DEVICE_NOT_SETTING_CODE = "21";
    public static final String DEVICE_NOT_SETTING_MSG = "设备未设置套餐信息";
    public static final String ALIPAY_GET_CODE_ERROR_CODE = "22";
    public static final String ALIPAY_GET_CODE_ERRPR_MSG = "生成二维码失败";
    public static final String NO_SERVICESITE_CODE = "23";
    public static final String NO_SERVICESITE_MSG = "该地区无服务站";
    public static final String SNCODE_EXIST_CODE = "24";
    public static final String SNCODE_EXIST_MSG = "sn码已被绑定";
    public static final String ICCID_BIND_CODE = "25";
    public static final String ICCID_BIND_MSG = "iccid与sn码不匹配，请确认信息后再重试！";
    public static final String USER_ERROR_CODE = "26";
    public static final String USER_ERROR_MSG = "用户已存在";
    public static final String DISCOUNT_ERROR_CODE = "27";
    public static final String DISCOUNT_ERROR_MSG = "配额不足";
    public static final String DISTRIBUTOR_ADD_ERROR_CODE = "28";
    public static final String DISTRIBUTOR_ADD_ERROR_MSG = "您不是经销商，不能添加工单";
    public static final String WORKORDER_NOT_PAID_CODE = "29";
    public static final String WORKORDER_NOT_PAID_MSG = "工单未支付";
    public static final String NO_STOCK_CODE = "31";
    public static final String NO_STOCK_MSG = "该地区没有仓库";
    public static final String UPLOAD_PIC_ERROR_CODE = "32";
    public static final String UPLOAD_PIC_ERROR_MSG = "图片上传失败";
    public static final String UPLOAD_ERROR_CODE = "33";
    public static final String UPLOAD_ERROR_MSG = "请上传正确格式的文件";
    public static final String SIMCARD_USED_CODE = "34";
    public static final String SIMCARD_USED_MSG = "该sim卡已被绑定";
    public static final String NO_CUSTOMER_CODE = "35";
    public static final String NO_CUSTOMER_MSG = "该地区没有客服";
    public static final String USER_LOGIN_VALID_CODE = "37";
    public static final String USER_LOGIN_VALID_MSG = "您已在其他设备上登录";
    public static final String USER_LOGIN_VALID_MSG2 = "无Sim卡，无法登录";
    public static final String USER_LOGIN_VALID_MSG3 = "账号禁用，无法登录";
    public static final String USER_LOGIN_VALID_MSG4 = "Sim已绑定，无法登录";
    public static final String ADD_WORKORDER_CODE = "38";
    public static final String ADD_WORKORDER_MSG = "该型号的产品库存不足";
    public static final String ILLAGEL_ARGUMENT_CODE = "39";
    public static final String ILLAGEL_ARGUMENT_MSG = "非法请求参数";
    public static final String WORKORDER_ACCEPTED_ERROR_CODE = "40";
    public static final String WORKORDER_ACCEPTED_ERROR_MSG = "客服已受理，不允许进行退单操作";
    public static final String WORKORDER_ERROR_CODE = "41";
    public static final String WORKORDER_ERROR_MSG = "已受理工单不能被删除";
    public static final String WORKORDER_ALLOTED_CODE = "42";
    public static final String WORKORDER_ALLOTED_MSG = "工单已分配至客服";
    public static final String WORKORDER_CHARGEBACK_CODE = "43";
    public static final String WORKORDER_CHARGEBACK_MSG = "工单已退单，不允许进行接单操作";
    public static final String NO_DEVICE_USER_CODE = "44";
    public static final String NO_DEVICE_USER_MSG = "无此水机用户信息";
    public static final String NO_DEVICE_WORKORDER_CODE = "45";
    public static final String NO_DEVICE_WORKORDER_MSG = "无此水机对应工单";
    public static final String RENEWORDER_NOT_PAID_CODE = "46";
    public static final String RENEWORDER_NOT_PAID_MSG = "续费未成功";
    public static final String LOGISTICSCODING_ERROR_CODE = "47";
    public static final String LOGISTICSCODING_ERROR_MSG = "物流编码已存在";
    public static final String SNCODE_EXIST_ERROR_CODE = "48";
    public static final String SNCODE_EXIST_ERRPR_MSG = "sn码已存在";
    public static final String PARAM_ERROR_CODE = "49";
    public static final String PARAM_ERROR_MSG = "参数错误";
    public static final String DISTRIBUTOR_NO_CONTRACT_ERROR_CODE = "50";
    public static final String DISTRIBUTOR_NO_CONTRACT_ERROR_MSG = "不支持查看线下签署的合同";
    public static final String DISTRIBUTOR_FORBIDDEN_ERROR_CODE = "51";
    public static final String DISTRIBUTOR_FORBIDDEN_ERROR_MSG = "您的账号已禁止使用,不允许登录";
    public static final String DISTRIBUTOR_FORBIDDENORDER_ERROR_CODE = "52";
    public static final String DISTRIBUTOR_FORBIDDENORDER_ERROR_MSG = "您的账号已禁止下单";
    public static final String ORDER_PAYED_ERROR_CODE = "53";
    public static final String ORDER_PAYED_ERROR_MSG = "工单无须支付";
    public static final String ORDER_COMPLETE_ERROR_CODE = "54";
    public static final String ORDER_COMPLETE_ERROR_MSG = "工单已完成";
    public static final String ORDER_PAID_ERROR_CODE = "55";
    public static final String ORDER_PAID_ERROR_MSG = "订单已支付，无法修改支付端";
    public static final String NO_CONTRACT_CODE = "56";
    public static final String NO_CONTRACT_MSG = "还未签署电子合同";
    public static final String JXS_IS_NOT_EXIST_CODE = "57";
    public static final String JXS_IS_NOT_EXIST_MSG = "经销商不存在";
    public static final String PHONE_IS__EXIST_CODE = "58";
    public static final String PHONE_IS_EXIST_MSG = "该电话已被使用";
    public static final String WORKORDER_IS_NOT_EXIST_CODE = "59";
    public static final String WORKORDER_IS_NOT_EXIST_MSG = "工单不存在";
    public static final String MSGCODE_ERROR_VALID_TIME_CODE = "60";
    public static final String MSGCODE_ERROR_VALID_TIME_MSG = "验证码已失效";
    public static final String SCAN_ERROR_PARAM_IS_EMPTY_CODE = "61";
    public static final String SCAN_ERROR_PARAM_IS_EMPTY_MSG = "参数不得为空";
    public static final String DEVICE_ERROR_REF_CUSTOMER_NOT_EXISTS_CODE = "62";
    public static final String DEVICE_ERROR_REF_CUSTOMER_NOT_EXISTS_MSG = "设备服务的工程师数据异常";
    public static final String SYSTEM_CONFIG_NOT_EXISTS_CODE = "63";
    public static final String SYSTEM_CONFIG_NOT_EXISTS_MSG = "配置项不存在!请联系管理员处理";
    public static final String SYSTEM_DIMISSON_NOT_PASS_CODE = "64";
    public static final String SYSTEM_DIMISSON_NOT_PASS_MSG = "您没有权限操作";
    public static final String DEVICE_ERROR_IS_OPERATION_CODE = "65";
    public static final String DEVICE_ERROR_IS_OPERATION_MSG = "该设备正在操作中,请稍后重试";
    public static final String OTHER_PAY_UNKNOW_CODE = "66";
    public static final String OTHER_PAY_UNKNOW_MSG = "未知的其他支付类型";
    public static final String VERSION_NOT_EXIST_CODE = "67";
    public static final String VERSION_NOT_EXIST_MSG = "版本号不存在";
    public static final String ORDER_ID_NOT_EXISTS_CODE = "68";
    public static final String ORDER_ID_NOT_EXISTS_MSG = "订单不存在";
    public static final String DEVICE_NOT_EXISTS_CODE = "69";
    public static final String DEVICE_NOT_EXISTS_MSG = "设备不存在";
    public static final String DEVICE_ALREADY_UNLOCK_CODE = "70";
    public static final String DEVICE_ALREADY_UNLOCK_MSG = "设备当前未锁定";
    public static final String QRCODE_ILLEGALITY_CODE = "71";
    public static final String QRCODE_ILLEGALITY_MSG = "非法二维码扫描!";
    public static final String OTHER_PAY_NOT_EXISTS_CODE = "72";
    public static final String OTHER_PAY_NOT_EXISTS_MSG = "系统中尚未配置该产品的支付账号信息!";
    public static final String ONLINE_AREA_CANNOT_USER_PAY_CODE = "73";
    public static final String ONLINE_AREA_CANNOT_USER_PAY_MSG = "您所在地区正在升级新流程，用户支付不允许支付!";
    public static final String ONLINE_AREA_CANNOT_OTHER_PAY_CODE = "74";
    public static final String ONLINE_AREA_CANNOT_OTHER_PAY_MSG = "您所在地区正在升级新流程，其他支付不允许支付!";
    public static final String NOT_EXIST_PAY_ACCOUNT_CODE = "75";
    public static final String NOT_EXIST_PAY_ACCOUNT_MSG = "当前产品未配置对应收款账号!";
    public static final String SMS_API_KEY = "9c24a788b63481b6ddbfb859b8b70908";
}
