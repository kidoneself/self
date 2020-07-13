package com.yimao.cloud.wechat.controller;

import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.properties.WechatProperties;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Zhang Bo
 * @date 2018/7/22.
 */
@RestController
@Api(tags = "TestController")
public class TestController {

    @Resource
    private DomainProperties domainProperties;
    @Resource
    private WechatProperties wechatProperties;

    @GetMapping(value = "/test1")
    @ApiOperation(value = "测试1")
    public Object test1() {
        return domainProperties.getAd() + "---" + domainProperties.getAdmin() + "---" + domainProperties.api + "---" + domainProperties.wechat;
    }

    @GetMapping(value = "/test2")
    @ApiOperation(value = "测试2")
    public Object test2() {
        String appid = wechatProperties.getJsapi().getAppid();
        System.out.println(appid);
        return wechatProperties.getJsapi().getAppid();
    }

}
