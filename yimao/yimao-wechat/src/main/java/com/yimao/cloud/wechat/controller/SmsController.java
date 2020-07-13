package com.yimao.cloud.wechat.controller;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.service.SmsService;
import com.yimao.cloud.base.service.impl.SmsServiceImpl;
import com.yimao.cloud.base.utils.SmsUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 短信发送
 *
 * @author liuhao@yimaokeji.com
 * @version v2.0
 * @date 2019/5/7
 */
@RestController
@Slf4j
@Api(tags = "SmsController")
public class SmsController {

    @Resource
    private SmsService smsService;

    @Resource
    private RedisCache redisCache;

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     */
    @PostMapping(value = "/sms/send")
    @ApiOperation(value = "发送短信验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "countryCode", value = "区号", required = true, dataType = "String", paramType = "query")
    })
    public void sendCode(@RequestParam String phone, @RequestParam(defaultValue = "+86", required = false) String countryCode) {
        String code = smsService.getCode(phone, Constant.COUNTRY_CODE);
        String text = "【翼猫健康e家】您的验证码是" + code + "。如非本人操作，请忽略本短信";
        String s = SmsUtil.sendSms(text, phone);
        log.info("发送的验证码为：" + code + "，短信接口返回为：" + s);
        redisCache.set(SmsServiceImpl.SMS_CACHE + SmsServiceImpl.getKey(phone, Constant.COUNTRY_CODE), code);
    }


    /**
     * 验证短信验证码
     *
     * @param phone 手机号
     */
    @GetMapping(value = "/sms/verify")
    @ApiOperation(value = "验证短信验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "countryCode", value = "区号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "verifyCode", value = "验证码", required = true, dataType = "String", paramType = "query")
    })
    public Boolean verifyCode(@RequestParam String phone, @RequestParam(required = false) String countryCode, @RequestParam String verifyCode) {
        return smsService.verifyCode(phone, countryCode, verifyCode);
    }
}
