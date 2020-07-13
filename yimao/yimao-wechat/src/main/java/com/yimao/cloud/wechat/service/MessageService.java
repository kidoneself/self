package com.yimao.cloud.wechat.service;

/**
 * @author liuhao@yimaokeji.com
 * @version v2.0
 * @date 2019/5/27
 */
public interface MessageService {

    /**
     * 订单支付完成，消息发送
     * @param openId
     * @param orderId
     */
    void orderPaySuccessMessage(String openId, Long orderId) ;

}
