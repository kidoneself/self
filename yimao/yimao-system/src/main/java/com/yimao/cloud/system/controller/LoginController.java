package com.yimao.cloud.system.controller;

import com.yimao.cloud.base.auth.AuthConstants;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.enums.SystemType;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CookieUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.user.AdminDTO;
import com.yimao.cloud.system.feign.UserFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Zhang Bo
 * @date 2018/11/7.
 */
@RestController
@Api(tags = "LoginController")
public class LoginController {

    @Resource
    private UserFeign userFeign;

    /**
     * 翼猫业务管理系统登录
     *
     * @param userName 用户名
     * @param password 密码
     * @param request  HTTP请求
     * @param response HTTP应答
     */
    @GetMapping(value = "/login")
    @ApiOperation(value = "翼猫业务管理系统登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userName", value = "用户名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", dataType = "String", paramType = "query")
    })
    public AdminDTO login(@RequestParam("userName") String userName,
                          @RequestParam("password") String password,
                          HttpServletRequest request, HttpServletResponse response) {
        AdminDTO admin = userFeign.login(userName, password, SystemType.SYSTEM.value);
        if (admin == null || StringUtil.isBlank(admin.getToken())) {
            throw new YimaoException("操作失败。");
        }
        if (Constant.LOCAL_ENVIRONMENT || Constant.DEV_ENVIRONMENT) {
            CookieUtil.setCookie(request, response, AuthConstants.ADMIN_TOKEN, admin.getToken(), 2592000, true);
        }
        response.setHeader(AuthConstants.ADMIN_TOKEN, admin.getToken());
        admin.setToken(null);
        return admin;
    }

    // /**
    //  * 测试日期时间在Feign中传递
    //  *
    //  * @param date 时间
    //  * @return
    //  */
    // @GetMapping(value = "/test/date")
    // @ApiOperation(value = "测试日期时间在Feign中传递", notes = "测试日期时间在Feign中传递")
    // @ApiImplicitParams({
    //         @ApiImplicitParam(name = "date", value = "时间", dataType = "Date", paramType = "query")
    // })
    // public Object login(@RequestParam("date") Date date) {
    //     System.out.println("输入的时间：" + DateUtil.dateToString(date));
    //     Date date1 = userFeign.testDate(date);
    //     System.out.println("输出的时间：" + DateUtil.dateToString(date1));
    //     return ResponseEntity.ok(date1);
    // }

}
