package com.yimao.cloud.engineer.controller;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yimao.cloud.base.auth.AuthConstants;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.utils.CookieUtil;
import com.yimao.cloud.base.utils.StringUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.engineer.feign.UserFeign;
import com.yimao.cloud.engineer.utils.ApiResult;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/***
 * 安装工登录服务
 * 
 * @author zhangbaobao
 *
 */
@RestController
@Api(tags = "LonginController")
@Slf4j
public class LonginController {

	@Resource
	private UserFeign userFeign;

	/****
	 * 安装工app登录
	 * 
	 * @param userName
	 * @param password
	 * @param appType
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/login")
	@ApiOperation(value = "安装工登录")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "userName", value = "用户名", dataType = "String", required = true, paramType = "query"),
		@ApiImplicitParam(name = "password", value = "密码(加密后的)", dataType = "String", required = true, paramType = "query"),
		@ApiImplicitParam(name = "appType", value = "登录的手机系统：1-Android；2-ios", dataType = "String", required = true,defaultValue = "1",paramType = "query"),
	})
	public EngineerDTO login(@RequestParam("userName") String userName,
	                    @RequestParam("password") String password,
						@RequestParam(value = "appType",defaultValue = "1") Integer appType,
			            HttpServletRequest request, HttpServletResponse response) {
		log.info("=================engineerLogin====userName=" + userName + ",password=" + password + ",appType="+ appType);
		if (1 != appType && 2 != appType) {
			// 错误的appType参数，只允许：1 Android ，2 Ios
			throw new YimaoException("错误的appType参数，只允许：1-Android；2-Ios");
		}

		EngineerDTO engineer = userFeign.engineerLogin(userName, password, appType);

		if (engineer == null || StringUtil.isBlank(engineer.getToken())) {
			// 用户名或密码错误
			throw new YimaoException("用户名或密码错误");
		}
		engineer.setPassword(null);

		if (Constant.LOCAL_ENVIRONMENT || Constant.DEV_ENVIRONMENT) {
			CookieUtil.setCookie(request, response, AuthConstants.ENGINEER_TOKEN, engineer.getToken(), 2592000, true);
		}
		response.setHeader(AuthConstants.ENGINEER_TOKEN, engineer.getToken());
		engineer.setToken(null);
		return engineer;

	}

}
