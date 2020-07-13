package com.yimao.cloud.order.service;

import com.yimao.cloud.pojo.dto.order.AliPayRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Zhang Bo
 * @date 2017/12/4.
 */
public interface AliPayService {

    String tradewap(AliPayRequest payRequest);

    String tradeapp(AliPayRequest payRequest);

    String tradeprecreate(AliPayRequest payRequest);

    String callback(HttpServletRequest request, String queueName);

    Object tradeQuery(AliPayRequest payRequest);

}
