package com.yimao.cloud.wechat.controller;

import com.yimao.cloud.base.auth.AuthConstants;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.enums.AppTypeEnum;
import com.yimao.cloud.base.enums.SystemType;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.base.service.SmsService;
import com.yimao.cloud.base.utils.CookieUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.wechat.feign.UserFeign;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;

/**
 * @author Zhang Bo
 * @date 2018/7/22.
 */
@RestController
@Slf4j
@Api(tags = "LoginController")
public class LoginController {

    @Resource
    private UserFeign userFeign;
    @Resource
    private SmsService smsService;
    @Resource
    private RedisCache redisCache;

    /**
     * 用户登录
     */
    @GetMapping(value = "/login")
    @ApiOperation(value = "用户登录")
    @ApiImplicitParam(name = "openid", value = "openid", dataType = "String", paramType = "query", required = true)
    public UserDTO login(@RequestParam(value = "openid") String openid, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtil.isBlank(openid)) {
            throw new BadRequestException("缺少登录参数。");
        }
        UserDTO user = userFeign.login(openid);
        if (user == null || StringUtil.isBlank(user.getToken())) {
            throw new YimaoException("操作失败,用户不存在。");
        }
        if (Constant.LOCAL_ENVIRONMENT || Constant.DEV_ENVIRONMENT) {
            CookieUtil.setCookie(request, response, AuthConstants.USER_TOKEN, user.getToken(), 2592000, true);
        }
        response.setHeader(AuthConstants.USER_TOKEN, user.getToken());
        user.setToken(null);
        return user;
    }


    /**
     * H5分享（绑定手机号）
     */
    @PostMapping("/bindingmobile")
    @ApiOperation(value = "H5分享（绑定手机号）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "smsCode", value = "验证码", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "sharerId", value = "分享者id", required = true, dataType = "Integer", paramType = "query")
    })
    public CommResult<Map<String, Object>> bindingMobile(@RequestParam String mobile, @RequestParam String smsCode, @RequestParam(required = false) Integer sharerId, HttpServletRequest request, HttpServletResponse response) {
        //校验手机格式
        if (!mobile.trim().matches(Constant.MOBILE_REGEX)) {
            throw new BadRequestException("手机号校验失败，请重新输入");
        }
        //校验手机验证码
        Boolean bool = smsService.verifyCode(mobile, Constant.COUNTRY_CODE, smsCode);
        if (!bool) {
            throw new BadRequestException("验证码错误，请重新输入");
        }
        CommResult<Map<String, Object>> result = userFeign.wxLoginBindingMobile(mobile, sharerId, SystemType.H5.value, Integer.parseInt(AppTypeEnum.H5.getId()));
        if (result.getStatus() == CommResult.SUCCESS) {
            Map<String, Object> map = result.getData();
            if ((Integer) map.get("type") == 1) {
                if (Constant.LOCAL_ENVIRONMENT || Constant.DEV_ENVIRONMENT) {
                    CookieUtil.setCookie(request, response, AuthConstants.USER_TOKEN, (String) map.get("token"), 2592000, true);
                }
                response.setHeader(AuthConstants.USER_TOKEN, (String) map.get("token"));
                map.put("openid", this.getOpenidByMobile(mobile));
                map.remove("token");
            }
        }
        return result;
    }


    /**
     * H5分享-根据手机号获取账号（手机号登录）
     */
    @PostMapping("/mobilelogin")
    @ApiOperation(value = "H5分享（根据手机号获取账号）")
    @ApiImplicitParam(name = "mobile", value = "手机号", required = true, dataType = "String", paramType = "query")
    public CommResult<Map<String, Object>> loginByMobile(@RequestParam String mobile, @RequestParam(required = false) String key, @RequestParam(required = false) Integer sharerId, HttpServletRequest request, HttpServletResponse response) {
        //校验手机格式
        if (!mobile.trim().matches(Constant.MOBILE_REGEX)) {
            throw new BadRequestException("手机号错误，请重新输入");
        }
        String okey = redisCache.get(Constant.H5_SHARE_LOGIN_SETP + key);
        if (StringUtil.isEmpty(okey) || !Objects.equals(key, okey)) {
            log.error("H5-非法登录");
            throw new BadRequestException("非法登录");
        }
        CommResult<Map<String, Object>> result = userFeign.wxLoginByMobile(mobile, sharerId, SystemType.H5.value, Integer.parseInt(AppTypeEnum.H5.getId()));
        if (result.getStatus() == CommResult.SUCCESS) {
            Map<String, Object> map = result.getData();
            if ((Integer) map.get("type") == 1) {
                if (Constant.LOCAL_ENVIRONMENT || Constant.DEV_ENVIRONMENT) {
                    CookieUtil.setCookie(request, response, AuthConstants.USER_TOKEN, (String) map.get("token"), 2592000, true);
                }
                response.setHeader(AuthConstants.USER_TOKEN, (String) map.get("token"));
                map.put("openid", this.getOpenidByMobile(mobile));
                map.remove("token");
            }
        }
        return result;
    }

    /**
     * H5分享-选择账号登录（手机号 + 验证码）
     */
    @PostMapping("/mobilelogin/selectaccount")
    @ApiOperation(value = "H5分享-选择账号登录（手机号+验证码，选择账号登录）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "key", value = "前一步手机号验证后返回的key", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "userId", value = "e家号", dataType = "Long", paramType = "query", required = true)
    })
    public CommResult<UserDTO> appLoginByMobileSelectAccount(@RequestParam String mobile, @RequestParam String key, @RequestParam Integer userId, HttpServletRequest request, HttpServletResponse response) {
        CommResult<UserDTO> result = userFeign.wxLoginByMobileSelectAccount(mobile, key, userId, SystemType.H5.value, Integer.parseInt(AppTypeEnum.H5.getId()));
        if (result.getStatus() == CommResult.SUCCESS) {
            UserDTO user = result.getData();
            if (user != null) {
                if (Constant.LOCAL_ENVIRONMENT || Constant.DEV_ENVIRONMENT) {
                    CookieUtil.setCookie(request, response, AuthConstants.USER_TOKEN, user.getToken(), 2592000, true);
                }
                response.setHeader(AuthConstants.USER_TOKEN, user.getToken());
                user.setOpenid(this.getOpenidByMobile(mobile));
                user.setToken(null);
            }
        }
        return result;
    }


    /**
     * 根据用户id和终端
     *
     * @param userId 用户id
     */
    @GetMapping(value = "/h5/login")
    public UserDTO loginBySystemType(@RequestParam(value = "userId") Integer userId, @RequestParam(required = false) String key, HttpServletRequest request, HttpServletResponse response) {
        String okey = redisCache.get(Constant.H5_SHARE_LOGIN_SETP + key);
        if (StringUtil.isEmpty(okey) || !Objects.equals(key, okey)) {
            log.error("H5-非法登录2");
            throw new BadRequestException("非法登录");
        }
        UserDTO user = userFeign.loginBySystemType(userId, SystemType.H5.value);
        if (user != null) {
            if (Constant.LOCAL_ENVIRONMENT || Constant.DEV_ENVIRONMENT) {
                CookieUtil.setCookie(request, response, AuthConstants.USER_TOKEN, user.getToken(), 2592000, true);
            }
            response.setHeader(AuthConstants.USER_TOKEN, user.getToken());
            user.setToken(null);
        }
        return user;
    }


    /**
     * 用户授权后：根据授权用户的手机号，查询openid
     * 注：选择账号登录-其他账号没有openid
     *
     * @param mobile 手机号
     * @return
     */
    private String getOpenidByMobile(String mobile) {
        return userFeign.getOpenidByMobile(mobile);
    }
}
