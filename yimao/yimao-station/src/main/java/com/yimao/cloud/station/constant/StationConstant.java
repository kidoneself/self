package com.yimao.cloud.station.constant;

public class StationConstant {
	//超级管理员角色id
	public static final int SUPERROLE=1;
	//超级管理员服务站公司id
	public static final int SUPERCOMPANYID=0;
	
	public static final String SUPERROLENAME="超级管理员";
	//短信登录校验token
    public static final String STATION_PHONETOKEN = "STATION:PHONELOGINTOKEN_";
    //短信登录校验token有效时间
    public static final Integer STATION_PHONETOKEN_EXPIRE = 300;
    //登录短信模板
    public static final String LOGIN_PHONE_MESSAGE="【翼猫服务站站务系统】您的验证码为:#code#，您正进行账号登录验证，请勿泄露给他人！";
    
}
