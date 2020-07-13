package com.yimao.cloud.wechat.controller;

import com.yimao.cloud.base.constant.WechatConstant;
import com.yimao.cloud.pojo.dto.wechat.WxOauth2Result;
import com.yimao.cloud.pojo.dto.wechat.WxUserInfo;
import com.yimao.cloud.wechat.service.WxService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
@Api(tags = "WxAppController")
public class WxAppController {

    @Resource
    private WxService wxService;

    @GetMapping(value = "/getwxuserinfobycode")
    public WxUserInfo getwxuserinfobycode(@RequestParam String code) {
        WxOauth2Result result = wxService.getAccessTokenByCode(code, WechatConstant.APPID, WechatConstant.SECRET);
        if (result == null) {
            return null;
        }
        return wxService.getWxUserInfo(result.getAccessToken(), result.getOpenid());
    }


}
