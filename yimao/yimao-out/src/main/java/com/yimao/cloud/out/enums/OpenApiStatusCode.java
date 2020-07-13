package com.yimao.cloud.out.enums;

public enum OpenApiStatusCode {
    SUCCESS("000", "成功"),
    APPID_NOT_EXISTED("101", "appId不存在"),
    APPID_LIMITED("102", "appId受限"),
    APPID_LOGIN_ERROR("103", "appId或者appKey错误"),
    ACCESSKEY_ERROR("105", "accessKey错误或不存在"),
    TOKEN_ERROR("106", "TOKEN非法"),
    TOKEN_OVERTIME("107", "TOKEN过期"),
    SIGN_LOST("108", "缺少签名信息"),
    SIGN_ERROR("109", "签名错误"),
    TOKEN_UPDATE_TOO_CLOSE("110", "token距离上次更新过于接近，请稍后再试"),
    IP_LIMINTED("111", "IP地址受限"),
    ACCESS_LIMITED("112", "请求过于频繁"),
    DOUBLE_CONNECTED("113", "重复请求"),
    TIME_FORMATE_ERROR("114", "时间戳格式错误"),
    TIME_TOO_OLD("115", "时间戳跟当前时间差距过大"),
    PARAM_NOT_FOUND("201", "参数不存在"),
    PARAM_MISSING("202", "参数缺失"),
    PARAM_ERROR("203", "参数不合法"),
    CUSTOMER_IS_FORBIDDEN("301", "客户信息被锁定"),
    CUSTOMER_INVOICE_NOT_FOUND("302", "客户发票信息不存在"),
    DATA_NOT_FOUND("303", "没有数据"),
    IDENTITY_ILLEGAL("304", "身份信息不合法"),
    YUNSIGN_ERROR("305", "云签错误"),
    RATING_ERROR("306", "评价失败"),
    USER_NOT_EXISTS("321", "用户不存在"),
    DISTRIBUTOR_ACCOUNT_EXISTS("322", "经销商账户不存在"),
    SERVER_BUSY("991", "服务器忙"),
    SERVER_ERROR("999", "系统错误");

    String code;
    String msg;

    private OpenApiStatusCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
