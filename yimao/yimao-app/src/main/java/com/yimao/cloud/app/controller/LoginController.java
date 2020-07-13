package com.yimao.cloud.app.controller;

import com.yimao.cloud.app.feign.UserFeign;
import com.yimao.cloud.base.auth.AuthConstants;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.enums.SystemType;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.base.service.SmsService;
import com.yimao.cloud.base.utils.CookieUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@Slf4j
@Api(tags = "LoginController")
public class LoginController {

    @Resource
    private UserFeign userFeign;
    @Resource
    private SmsService smsService;

    /**
     * 经销商APP登录（经销商账号+密码登录）
     */
    @PostMapping("/login")
    @ApiOperation(value = "经销商APP登录（经销商账号+密码登录）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "经销商账号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "经销商密码", required = true, dataType = "String", paramType = "query")
    })
    public UserDTO accountLogin(@RequestParam String username, @RequestParam String password, @RequestParam Integer appType,
                                HttpServletRequest request, HttpServletResponse response) {
        UserDTO user = userFeign.distLogin(username, password, appType);
        if (user == null || StringUtil.isBlank(user.getToken())) {
            throw new YimaoException("登录失败");
        }
        if (Constant.LOCAL_ENVIRONMENT || Constant.DEV_ENVIRONMENT) {
            CookieUtil.setCookie(request, response, AuthConstants.USER_TOKEN, user.getToken(), 2592000, true);
        }
        response.setHeader(AuthConstants.USER_TOKEN, user.getToken());
        user.setToken(null);
        return user;
    }

    /**
     * 经销商APP登录（手机号+验证码登录）
     */
    @PostMapping("/mobilelogin")
    @ApiOperation(value = "经销商APP登录（手机号+验证码登录）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "smsCode", value = "验证码", required = true, dataType = "String", paramType = "query")
    })
    public CommResult<Map<String, Object>> loginByMobile(@RequestParam String mobile, @RequestParam String smsCode, @RequestParam Integer appType,
                                                         HttpServletResponse response) {
        //校验手机格式
        if (!mobile.trim().matches(Constant.MOBILE_REGEX)) {
            throw new BadRequestException("手机号错误，请重新输入");
        }
        //校验手机验证码
        Boolean bool = smsService.verifyCode(mobile, Constant.COUNTRY_CODE, smsCode);
        log.info(">>>>>>>>>>验证码：" + smsCode + ",bool=" + bool + ">>>>>>>>>>");
        if (!bool) {
            throw new BadRequestException("验证码错误，请重新输入");
        }
        CommResult<Map<String, Object>> result = userFeign.appLoginByMobile(mobile, null, SystemType.JXSAPP.value, appType);
        if (result.getStatus() == CommResult.SUCCESS) {
            Map<String, Object> map = result.getData();
            if ((Integer) map.get("type") == 1) {
                response.setHeader(AuthConstants.USER_TOKEN, (String) map.get("token"));
                map.remove("token");
            }
        }
        return result;
    }

    /**
     * 经销商APP登录（手机号+验证码，选择账号登录）
     */
    @PostMapping("/mobilelogin/selectaccount")
    @ApiOperation(value = "经销商APP登录（手机号+验证码，选择账号登录）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "key", value = "前一步手机号验证后返回的key", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "userId", value = "e家号", dataType = "Long", paramType = "query", required = true)
    })
    public CommResult<UserDTO> appLoginByMobileSelectAccount(@RequestParam String mobile, @RequestParam String key,
                                                             @RequestParam Integer userId, @RequestParam Integer appType,
                                                             HttpServletResponse response) {
        CommResult<UserDTO> result = userFeign.appLoginByMobileSelectAccount(mobile, key, userId, SystemType.JXSAPP.value, appType);
        if (result.getStatus() == CommResult.SUCCESS) {
            UserDTO user = result.getData();
            if (user != null) {
                response.setHeader(AuthConstants.USER_TOKEN, user.getToken());
                user.setToken(null);
            }
        }
        return result;
    }

    /**
     * 经销商APP登录（微信授权登录）
     */
    @PostMapping("/wxlogin")
    @ApiOperation(value = "经销商APP登录（微信授权登录）")
    @ApiImplicitParam(name = "code", value = "微信授权code", required = true, dataType = "String", paramType = "query")
    public CommResult<Map<String, Object>> wxLogin(@RequestParam String code, @RequestParam Integer appType, HttpServletResponse response) {
        CommResult<Map<String, Object>> result = userFeign.wxLogin(code, appType, SystemType.JXSAPP.value);
        if (result.getStatus() == CommResult.SUCCESS) {
            Map<String, Object> map = result.getData();
            if ((Integer) map.get("type") == 1) {
                response.setHeader(AuthConstants.USER_TOKEN, (String) map.get("token"));
                map.remove("token");
            }
        }
        return result;
    }

    /**
     * 经销商APP登录（绑定手机号）
     */
    @PostMapping("/bindingmobile")
    @ApiOperation(value = "经销商APP登录（绑定手机号）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "smsCode", value = "验证码", required = true, dataType = "String", paramType = "query")
    })
    public CommResult<Map<String, Object>> bindingMobile(@RequestParam String mobile, @RequestParam String smsCode, @RequestParam(required = false) Integer sharerId, @RequestParam Integer appType,
                                                         HttpServletResponse response) {
        //校验手机格式
        if (!mobile.trim().matches(Constant.MOBILE_REGEX)) {
            throw new BadRequestException("手机号错误，请重新输入");
        }
        //校验手机验证码
        Boolean bool = smsService.verifyCode(mobile, Constant.COUNTRY_CODE, smsCode);
        if (!bool) {
            throw new BadRequestException("验证码错误，请重新输入");
        }
        CommResult<Map<String, Object>> result = userFeign.wxLoginBindingMobile(mobile, sharerId, SystemType.JXSAPP.value, appType);
        if (result.getStatus() == CommResult.SUCCESS) {
            Map<String, Object> map = result.getData();
            if ((Integer) map.get("type") == 1) {
                response.setHeader(AuthConstants.USER_TOKEN, (String) map.get("token"));
                map.remove("token");
            }
        }
        return result;
    }

}
