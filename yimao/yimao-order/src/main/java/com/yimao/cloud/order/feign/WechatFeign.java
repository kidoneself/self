package com.yimao.cloud.order.feign;

import com.yimao.cloud.base.constant.Constant;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Chen Hui Yang
 * @date 2018/12/27
 */
@FeignClient(name = Constant.MICROSERVICE_WECHAT)
public interface WechatFeign {

    /**
     * 消息推送
     *
     * @param openid  用户的openId
     * @param orderId 订单号
     */
    @RequestMapping(value = "/message/push", method = RequestMethod.GET)
    void orderPaySuccessMessage(@RequestParam("openid") String openid, @RequestParam("orderId") Long orderId);
}
