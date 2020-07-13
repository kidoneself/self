package com.yimao.cloud.order.service;

import com.yimao.cloud.pojo.dto.order.PayAccountDetail;
import com.yimao.cloud.pojo.dto.order.WechatPayRequest;

import java.util.SortedMap;

/**
 * @author Zhang Bo
 * @date 2017/12/4.
 */
public interface WxPayService {

    SortedMap<String, String> unifiedOrder(WechatPayRequest payRequest, PayAccountDetail payAccount);

    SortedMap<String, String> orderQuery(SortedMap<String, Object> reqMap);

}
