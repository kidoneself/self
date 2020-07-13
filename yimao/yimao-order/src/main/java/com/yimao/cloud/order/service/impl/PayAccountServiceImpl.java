package com.yimao.cloud.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.order.mapper.PayAccountMapper;
import com.yimao.cloud.order.po.PayAccount;
import com.yimao.cloud.order.service.PayAccountService;
import com.yimao.cloud.pojo.dto.order.PayAccountDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zhangbaobao
 * @desc 支付账号
 * @date 2019/9/19
 */
@Service
@Slf4j
public class PayAccountServiceImpl implements PayAccountService {

    @Resource
    private PayAccountMapper payAccountMapper;

    /**
     * 获取支付账号信息
     *
     * @param companyId   公司编号
     * @param platform    支付平台（和PayPlatform枚举类对应）：1-微信；2-支付宝；3-银行；
     * @param clientType  客户端（和SystemType枚举类对应）：1-健康e家公众号；4-经销商APP；5-安装工APP；6-水机PAD；10-H5页面；
     * @param receiveType 款项收取类型（默认1）：1-商品费用；2-经销代理费用
     */
    @Override
    public PayAccountDetail getPayAccountDetail(Integer companyId, Integer platform, Integer clientType, Integer receiveType) {
        PayAccount payAccount = payAccountMapper.selectPayAccount(companyId, platform, clientType, receiveType);
        if (payAccount == null) {
            return null;
        } else {
            return JSONObject.parseObject(payAccount.getAccountDetail(), PayAccountDetail.class);
        }
    }

    /**
     * 获取支付账号信息
     *
     * @param appid    appid
     * @param platform 支付平台（和PayPlatform枚举类对应）：1-微信；2-支付宝；3-银行；
     */
    @Override
    public PayAccountDetail getPayAccountDetail(String appid, Integer platform) {
        PayAccount payAccount = payAccountMapper.selectPayAccountByAppid(appid, platform);
        if (payAccount == null) {
            return null;
        } else {
            return JSONObject.parseObject(payAccount.getAccountDetail(), PayAccountDetail.class);
        }
    }

}
