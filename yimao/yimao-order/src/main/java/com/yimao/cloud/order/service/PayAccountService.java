package com.yimao.cloud.order.service;

import com.yimao.cloud.pojo.dto.order.PayAccountDetail;

/**
 * @author zhangbaobao
 * @Desc 支付账号
 * @date 2019/9/19 13:27
 */
public interface PayAccountService {

    /**
     * 获取支付账号信息
     *
     * @param companyId   公司编号
     * @param platform    支付平台（和PayPlatform枚举类对应）：1-微信；2-支付宝；3-银行；
     * @param clientType  客户端（和SystemType枚举类对应）：1-健康e家公众号；4-经销商APP；5-安装工APP；6-水机PAD；10-H5页面；
     * @param receiveType 款项收取类型（默认1）：1-商品费用；2-经销代理费用
     */
    PayAccountDetail getPayAccountDetail(Integer companyId, Integer platform, Integer clientType, Integer receiveType);

    /**
     * 获取支付账号信息
     *
     * @param appid    appid
     * @param platform 支付平台（和PayPlatform枚举类对应）：1-微信；2-支付宝；3-银行；
     */
    PayAccountDetail getPayAccountDetail(String appid, Integer platform);

}
