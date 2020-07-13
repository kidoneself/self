package com.yimao.cloud.app.controller;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.service.SmsService;
import com.yimao.cloud.base.service.impl.SmsServiceImpl;
import com.yimao.cloud.base.utils.SmsUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 短信验证码相关
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
    @ApiImplicitParam(name = "phone", value = "手机号", dataType = "String", paramType = "query", required = true)
    public void sendCode(@RequestParam String phone) {
        if (!phone.trim().matches(Constant.MOBILE_REGEX)) {
            throw new BadRequestException("手机号格式错误，请重新输入");
        }
        String code = smsService.getCode(phone, Constant.COUNTRY_CODE);
        String text = "【翼猫健康e家】您的验证码是" + code + "。如非本人操作，请忽略本短信";
        String s = SmsUtil.sendSms(text, phone);
        log.info("发送的验证码为：" + code + "，短信接口返回为：" + s);
        redisCache.set(SmsServiceImpl.SMS_CACHE + SmsServiceImpl.getKey(phone, Constant.COUNTRY_CODE), code);
    }

}
