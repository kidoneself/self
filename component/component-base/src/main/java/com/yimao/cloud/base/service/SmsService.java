package com.yimao.cloud.base.service;


/**
 * @author liuhao
 * @date 2018-12-27
 */
public interface SmsService {

    /**
     * 生成验证码
     * @param phone 手机号
     * @param countryCode 国家代码
     * @return
     */
    String getCode(String phone, String countryCode);

    /**
     * 验证手机验证码
     * @param phone 手机号
     * @param countryCode 国家代码
     * @param verifyCode 验证码
     * @return Boolean
     */
    Boolean verifyCode(String phone, String countryCode, String verifyCode);

}
