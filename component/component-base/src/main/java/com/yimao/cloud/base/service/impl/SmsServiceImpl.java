package com.yimao.cloud.base.service.impl;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.service.SmsService;
import com.yimao.cloud.base.utils.RandomUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author liuhao
 * @date 2018-12-27
 */
@Service
public class SmsServiceImpl implements SmsService {
    private static Logger logger = LoggerFactory.getLogger(SmsService.class);

    public static final String SMS_CACHE = "smsCache_";
    public static final String SMS_VERIFY_COUNT = "smsVerifyCount_";
    public static final String SMS_VERIFY_CACHE = "smsVerifyCache_";
    public static final String NX = "NX";
    public static final String EX = "EX";

    @Resource
    private RedisCache redisCache;


    @Override
    public String getCode(String phone, String countryCode) {
        if (!phone.trim().matches(Constant.MOBILE_REGEX)) {
            throw new BadRequestException("请输入合法的手机号");
        }

        String key = this.getKey(phone, countryCode);
        String code = redisCache.get(SMS_CACHE + key);
        if (StringUtils.isEmpty(code)) {
            code = this.generateCode();
            //仅在键不存在的时候设置，过期时间为61秒
            redisCache.set(SMS_CACHE + key, code, 61);
        }
        return code;
    }

    @Override
    public Boolean verifyCode(String phone, String countryCode, String verifyCode) {
        String key = this.getKey(phone, countryCode);
        String cachedCode = this.getCachedCode(phone, countryCode);
        if (StringUtils.isEmpty(cachedCode)) {
            return false;
        }
        int repeat = this.getVerifyCount(this.verifyCountKey(key, cachedCode));
        if (repeat >= 2) {
            this.removeCode(phone, countryCode);
        }

        if (!StringUtils.isEmpty(cachedCode) && Objects.equals(cachedCode, verifyCode)) {
            this.removeCode(phone, countryCode);
            return true;
        } else {
            logger.info("Verify fail: {}, invalidated the cached code. {} [cached], {} [provided]", key, cachedCode, verifyCode);
            return false;
        }
    }


    private String getCachedCode(String phone, String countryCode) {
        String key = this.getKey(phone, countryCode);
        return redisCache.get(SMS_CACHE + key);
    }

    private void removeCode(String phone, String countryCode) {
        if (redisCache.hasKey(SMS_CACHE + this.getKey(phone, countryCode))) {
            redisCache.delete(SMS_CACHE + this.getKey(phone, countryCode));
        }
    }

    private Integer getVerifyCount(String key) {
        String count;
        count = redisCache.get(SMS_VERIFY_COUNT + key);
        if (StringUtils.isEmpty(count)) {
            count = String.valueOf(0);
        }
        redisCache.set(SMS_VERIFY_COUNT + key, Integer.valueOf(count) + 1 + "", 61);
        return Integer.valueOf(count);
    }

    private String verifyCountKey(String key, String code) {
        StringBuilder sb = new StringBuilder();
        sb.append(key);
        sb.append("_");
        sb.append(code);
        return sb.toString();
    }


    public static String getKey(String phone, String countryCode) {
        StringBuilder sb = new StringBuilder();
        sb.append(countryCode);
        sb.append('_');
        sb.append(phone);
        return sb.toString();
    }

    private String generateCode() {
        return String.valueOf(RandomUtil.nextInt((int) Math.pow(10.0D, 5.0D), (int) Math.pow(10.0D, 6.0D) - 1));
    }

}
