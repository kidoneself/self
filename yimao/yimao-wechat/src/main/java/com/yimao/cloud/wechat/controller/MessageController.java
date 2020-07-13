package com.yimao.cloud.wechat.controller;

import com.yimao.cloud.wechat.service.MessageService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author liuhao@yimaokeji.com
 * @version v2.0
 * @date 2019/5/29
 */
@RestController
@Slf4j
@Api(tags = "MessageController")
public class MessageController {

    @Resource
    private MessageService messageService;

    /**
     * 订单完成后的消息推送
     *
     * @param openid  openId
     * @param orderId 订单号
     */
    @GetMapping(value = "/message/push")
    public void orderPaySuccessMessage(@RequestParam("openid") String openid, @RequestParam("orderId") Long orderId) {
        messageService.orderPaySuccessMessage(openid, orderId);
    }
}
