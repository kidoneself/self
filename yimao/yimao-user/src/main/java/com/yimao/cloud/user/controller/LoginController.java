package com.yimao.cloud.user.controller;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.SystemType;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.DialogException;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.user.AdminDTO;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.user.service.AdminService;
import com.yimao.cloud.user.service.EngineerService;
import com.yimao.cloud.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 描述：各端登录逻辑控制器
 *
 * @Author Zhang Bo
 * @Date 2019/3/26
 */
@RestController
@Slf4j
public class LoginController {

    @Resource
    private AdminService adminService;
    @Resource
    private UserService userService;
    @Resource
    private EngineerService engineerService;
    @Resource
    private UserCache userCache;

    /**
     * 业务系统管理员登录
     */
    @GetMapping(value = "/admin/login")
    public AdminDTO login(@RequestParam("userName") String userName,
                          @RequestParam("password") String password,
                          @RequestParam("sysType") Integer sysType,
                          HttpServletRequest request) {
        return adminService.login(userName, password, sysType, request);
    }

    /**
     * 微信端登录（公众号、小程序）
     */
    @GetMapping(value = "/user/login")
    public UserDTO login(@RequestParam String openid) {
        if (StringUtil.isNotBlank(openid)) {
            return userService.loginByOpenid(openid, SystemType.WECHAT.value);
        } else {
            throw new BadRequestException("缺少登录参数。");
        }
    }

    /**
     * 经销商APP登录（经销商账号+密码登录）
     */
    @PostMapping("/jxsapp/login")
    public UserDTO distLogin(@RequestParam String username, @RequestParam String password, @RequestParam Integer appType) {
        return userService.appLoginByUsername(username, password, SystemType.JXSAPP.value, appType);
    }

    /**
     * 经销商APP登录（手机号+验证码登录）
     *
     * @param mobile     手机号
     * @param systemType 客户端类型
     */
    @PostMapping("/jxsapp/mobilelogin")
    public CommResult<Map<String, Object>> appLoginByMobile(@RequestParam String mobile, @RequestParam(required = false) Integer sharerId, @RequestParam Integer systemType, @RequestParam Integer appType) {
        try {
            return userService.appLoginByMobile(null, mobile, sharerId, systemType, appType);
        } catch (Exception e) {
            if (e instanceof DialogException) {
                //对话框消息提示
                return CommResult.dialogError(e.getMessage());
            } else {
                throw e;
            }
        }
    }

    /**
     * 经销商APP登录（手机号+验证码，选择账号登录）
     */
    @PostMapping("/jxsapp/mobilelogin/selectaccount")
    public CommResult<UserDTO> appLoginByMobileSelectAccount(@RequestParam String mobile, @RequestParam String key,
                                                             @RequestParam Integer userId, @RequestParam Integer systemType, @RequestParam Integer appType) {
        try {
            return userService.appLoginByMobileSelectAccount(mobile, key, userId, systemType, appType);
        } catch (Exception e) {
            if (e instanceof DialogException) {
                //对话框消息提示
                return CommResult.dialogError(e.getMessage());
            } else {
                throw e;
            }
        }
    }

    /**
     * 经销商APP登录（微信授权登录）
     */
    @PostMapping("/jxsapp/wxlogin")
    public CommResult<Map<String, Object>> wxLogin(@RequestParam String code, @RequestParam Integer appType, @RequestParam Integer systemType) {
        try {
            return userService.appLoginByWechat(code, systemType, appType);
        } catch (Exception e) {
            if (e instanceof DialogException) {
                //对话框消息提示
                return CommResult.dialogError(e.getMessage());
            } else {
                throw e;
            }
        }
    }

    /**
     * 经销商APP登录（微信授权登录，绑定手机号）
     */
    @PostMapping("/jxsapp/bindingmobile")
    public CommResult<Map<String, Object>> wxLoginBindingMobile(@RequestParam String mobile, @RequestParam(required = false) Integer sharerId, @RequestParam Integer systemType, @RequestParam Integer appType) {
        try {
            Integer userId = userCache.getUserId();
            return userService.appLoginByMobile(userId, mobile, sharerId, systemType, appType);
        } catch (Exception e) {
            if (e instanceof DialogException) {
                //对话框消息提示
                return CommResult.dialogError(e.getMessage());
            } else {
                throw e;
            }
        }
    }

    /**
     * 安装工登录
     */
    @GetMapping(value = "/engineer/login")
    public EngineerDTO login(@RequestParam(required = false) String userName,
                             @RequestParam(required = false) String password,
                             @RequestParam(required = false) Integer appType) {
        if (StringUtil.isNotBlank(userName) && StringUtil.isNotBlank(password)) {
            return engineerService.login(userName, password, appType);
        } else {
            throw new BadRequestException("用户名或密码不能为空。");
        }
    }

    /**
     * 根据用户id和终端
     *
     * @param userId     用户id
     * @param systemType 终端类型
     * @return
     */
    @GetMapping(value = "/h5/login")
    public UserDTO loginBySystemType(@RequestParam(value = "userId") Integer userId, @RequestParam(value = "systemType") Integer systemType) {
        return userService.loginBySystemType(userId, systemType);
    }
}
