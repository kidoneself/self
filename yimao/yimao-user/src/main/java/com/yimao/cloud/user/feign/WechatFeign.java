package com.yimao.cloud.user.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.wechat.WxUserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = Constant.MICROSERVICE_WECHAT)
public interface WechatFeign {

    @GetMapping(value = "/getwxuserinfobycode")
    WxUserInfo getWxUserInfoByCode(@RequestParam("code") String code);

}
