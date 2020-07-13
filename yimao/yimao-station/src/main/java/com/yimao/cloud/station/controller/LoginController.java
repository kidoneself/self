package com.yimao.cloud.station.controller;

import com.yimao.cloud.base.auth.AuthConstants;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.handler.JWTHandler;
import com.yimao.cloud.base.utils.CookieUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.vo.station.StationAdminLoginVO;
import com.yimao.cloud.station.constant.StationConstant;
import com.yimao.cloud.station.service.StationAdminService;
import com.yimao.cloud.station.util.CheckUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@Api(tags = "LoginController")
public class LoginController {

    @Resource
    private StationAdminService stationAdminService;
    @Resource
    private UserCache userCache;
    @Resource
    private RedisCache redisCache;
    @Resource
    private JWTHandler jwtHandler;
    @Resource
    private AmqpTemplate amqpTemplate;


    @GetMapping(value = "login")
    @ApiOperation(value = "站务系统登录")
    public Object login(@RequestParam("userName") String userName,
                        @RequestParam("password") String password,
                        HttpServletRequest request, HttpServletResponse response) {

        if (StringUtils.isBlank(userName)) {
            throw new BadRequestException("用户名不能为空。");
        }
        if (StringUtils.isBlank(password)) {
            throw new BadRequestException("密码不能为空。");
        }

        StationAdminLoginVO vo = stationAdminService.login(userName, password, request);

        if (Constant.LOCAL_ENVIRONMENT || Constant.DEV_ENVIRONMENT) {
            CookieUtil.setCookie(request, response, AuthConstants.STATION_TOKEN, vo.getToken(), 2592000, true);
        }
        response.setHeader(AuthConstants.STATION_TOKEN, vo.getToken());

        vo.setToken(null);
        return vo;
    }


    @GetMapping(value = "phoneMessageLogin")
    @ApiOperation(value = "站务系统登录-短信登录")
    public Object phoneMessageLogin(@RequestParam("phone") String phone,
                        @RequestParam("code") String code,
                        HttpServletRequest request, HttpServletResponse response) {

        if (StringUtils.isBlank(phone)) {
            throw new BadRequestException("手机号码不能为空。");
        }

        if(! CheckUtils.isMobile(phone)) {
        	throw new BadRequestException("手机号格式错误。");
        }

        if (StringUtils.isBlank(code)) {
            throw new BadRequestException("验证码不能为空。");
        }

        if(code.length() != 6) {
        	throw new BadRequestException("非六位验证码。");
        }

        Integer massageCode= Integer.parseInt(code);

        StationAdminLoginVO vo = stationAdminService.phoneMessageLogin(phone, massageCode, request);

        if (Constant.LOCAL_ENVIRONMENT || Constant.DEV_ENVIRONMENT) {
            CookieUtil.setCookie(request, response, AuthConstants.STATION_TOKEN, vo.getToken(), 2592000, true);
        }
        response.setHeader(AuthConstants.STATION_TOKEN, vo.getToken());

        vo.setToken(null);

        //将缓存删除
		redisCache.delete(StationConstant.STATION_PHONETOKEN+phone);

        return vo;
    }
}
