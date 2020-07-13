package com.yimao.cloud.app.controller;

import com.yimao.cloud.app.feign.UserFeign;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.RemoteCallException;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.base.service.SmsService;
import com.yimao.cloud.base.utils.MD5Util;
import com.yimao.cloud.base.utils.SmsUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.UUIDUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import com.yimao.cloud.pojo.dto.user.RedAccountDTO;
import com.yimao.cloud.pojo.dto.user.UserCountDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;


@RestController
@Slf4j
@Api(tags = "UserController")
public class UserController {

    @Resource
    private UserFeign userFeign;
    @Resource
    private UserCache userCache;

    @Resource
    private SmsService smsService;
    @Resource
    private RedisCache redisCache;

    /**
     * @author ycl
     * @Description:经销商APP-用户数量
     * @Param: [roleLevel, distributorId]
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/1/11 10:37
     */
    @GetMapping("/user/dist")
    @ApiOperation(value = "经销商APP-用户数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleLevel", value = "经销商角色", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "distributorId", value = "经销商ID", required = true, dataType = "Long", paramType = "path")
    })
    public ResponseEntity getUserNum(
            @RequestParam("roleLevel") Integer roleLevel,
            @RequestParam("distributorId") Integer distributorId
    ) {
        UserCountDTO num = userFeign.getUserNum(roleLevel, distributorId);
        if (Objects.isNull(num)) {
            throw new RemoteCallException();
        }
        return ResponseEntity.ok(num);
    }

    /**
     * 经销商APP-忘记密码-获取短信验证码
     */
    @PostMapping("/user/fotgetpwd/sendsmscode")
    @ApiOperation(value = "经销商APP-忘记密码-获取短信验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userName", value = "账号", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "phone", value = "手机号", dataType = "String", paramType = "query", required = true)
    })
    public CommResult fotgetpwdSendSmsCode(@RequestParam String userName, @RequestParam String phone) {
        if (!phone.trim().matches(Constant.MOBILE_REGEX)) {
            throw new BadRequestException("手机号格式错误，请重新输入");
        }
        DistributorDTO distributor = userFeign.getDistributorByUserName(userName);
        if (distributor == null) {
            throw new BadRequestException("账号不存在，请重新输入");
        }
        if (distributor.getForbidden()) {
            throw new BadRequestException("账号已被禁用，请重新输入");
        }
        if (!phone.equals(distributor.getPhone())) {
            throw new BadRequestException("手机号与账号不匹配，请重新输入");
        }
        String code = smsService.getCode(phone, Constant.COUNTRY_CODE);
        String text = "【翼猫健康e家】您的验证码是" + code + "。如非本人操作，请忽略本短信";
        String s = SmsUtil.sendSms(text, phone);
        log.info("发送的验证码为：" + code + "，短信接口返回为：" + s);

        //设置一个随机字符串到缓存中，重新设置密码时用此key进行验证，确保下一步接口的安全性
        String key = UUIDUtil.longuuid();
        redisCache.set(Constant.D_FORGET_PWD + userName + "_" + phone, key, 3600L);
        return CommResult.ok(key);
    }

    /**
     * 经销商APP-忘记密码-校验短信验证码
     */
    @PostMapping("/user/fotgetpwd/checksmscode")
    @ApiOperation(value = "经销商APP-忘记密码-校验短信验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userName", value = "账号", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "phone", value = "手机号", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "key", value = "前一步接口返回的key", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "smsCode", value = "短信验证码", dataType = "String", paramType = "query", required = true),
    })
    public CommResult fotgetpwdCheckSmsCode(@RequestParam String userName, @RequestParam String phone, @RequestParam String key,
                                            @RequestParam String smsCode) {
        //关联接口生成的随机字符串，校验此key进行验证，确保接口的安全性
        String cachedKey = redisCache.get(Constant.D_FORGET_PWD + userName + "_" + phone);
        if (StringUtil.isEmpty(cachedKey) || !key.equals(cachedKey)) {
            throw new BadRequestException("非法操作");
        }
        //校验手机验证码
        Boolean bool = smsService.verifyCode(phone, Constant.COUNTRY_CODE, smsCode);
        if (!bool) {
            throw new BadRequestException("验证码错误，请重新输入");
        }
        return CommResult.ok();
    }

    /**
     * 经销商APP-忘记密码-设置新的密码
     */
    @PostMapping("/user/fotgetpwd/resetpwd")
    @ApiOperation(value = "经销商APP-忘记密码-设置新的密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userName", value = "账号", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "phone", value = "手机号", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "key", value = "前一步接口返回的key", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "password", value = "新密码（明文，不要进行加密）", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "confirmPassword", value = "确认密码（明文，不要进行加密）", dataType = "String", paramType = "query", required = true)
    })
    public CommResult fotgetpwdCheckSmsCode(@RequestParam String userName, @RequestParam String phone, @RequestParam String key,
                                            @RequestParam String password, @RequestParam String confirmPassword) {
        //关联接口生成的随机字符串，校验此key进行验证，确保接口的安全性
        String cachedKey = redisCache.get(Constant.D_FORGET_PWD + userName + "_" + phone);
        if (StringUtil.isEmpty(cachedKey) || !key.equals(cachedKey)) {
            throw new BadRequestException("非法操作");
        }
        if (!password.trim().matches(Constant.PASSWORD_REGEX)) {
            throw new BadRequestException("新密码要求6-16个字符，包含字母数字两种，不能使用标点和空格");
        }
        if (!password.equalsIgnoreCase(confirmPassword)) {
            throw new BadRequestException("新密码前后输入不一致，请重新输入");
        }
        userFeign.resetDistributorPassword(userName, password);
        return CommResult.ok();
    }

    /**
     * 经销商APP-修改密码
     */
    @PostMapping(value = "/user/modifypwd")
    @ApiOperation(value = "修改密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oldPassword", value = "原密码（明文，不要进行加密）", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "password", value = "新密码（明文，不要进行加密）", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "confirmPassword", value = "确认密码（明文，不要进行加密）", dataType = "String", paramType = "query", required = true)
    })
    public CommResult updatePassword(@RequestParam String oldPassword, @RequestParam String password, @RequestParam String confirmPassword) {
        Integer userId = userCache.getUserId();
        UserDTO user = userFeign.getBasicUserById(userId);
        if (user == null) {
            throw new BadRequestException("未查询到该用户");
        }
        log.info("老密码{}，新密码{}，确认密码{}，用户ID{}，用户密码{}", oldPassword, password, confirmPassword, userId, user.getPassword());
        //校验原密码是否正确
        String md5OldPassword = MD5Util.encodeMD5(oldPassword).toUpperCase();
        log.info("加密后的老密码{}，", md5OldPassword);
        if (!md5OldPassword.equalsIgnoreCase(user.getPassword())) {
            throw new BadRequestException("原密码输入错误，请重新输入");
        }
        //新密码不能和原密码相同
        if (oldPassword.equalsIgnoreCase(password)) {
            throw new BadRequestException("新密码不能和原密码相同");
        }
        //校验新密码格式
        if (!password.trim().matches(Constant.PASSWORD_REGEX)) {
            throw new BadRequestException("新密码要求6-16个字符，包含字母数字两种，不能使用标点和空格");
        }
        if (!password.equalsIgnoreCase(confirmPassword)) {
            throw new BadRequestException("新密码前后输入不一致，请重新输入");
        }
        //更新密码
        user.setPassword(MD5Util.encodeMD5(password).toUpperCase());
        userFeign.updatePassword(user);
        return CommResult.ok();
    }

    /**
     * 经销商APP-修改头像
     */
    @PostMapping(value = "/user/modifyheadimg")
    @ApiOperation(value = "修改头像")
    @ApiImplicitParam(name = "headImg", value = "头像图片地址路径", dataType = "String", paramType = "query", required = true)
    public UserDTO updateHead(@RequestParam String headImg) {
        if (!Constant.IMAGE_PATTERN.matcher(headImg).matches()) {
            throw new BadRequestException("头像格式不正确");
        }
        return userFeign.updateHeadImg(headImg);
    }


    /**
     * app我的健康大使
     *
     * @param userId 用户ID
     * @return com.yimao.cloud.pojo.dto.user.UserDTO
     * @author hhf
     * @date 2019/7/10
     */
    @GetMapping(value = "/user/ambassador")
    @ApiOperation(value = "我的健康大使")
    @ApiImplicitParam(name = "userId", value = "健康大使", dataType = "Integer", paramType = "query")
    public UserDTO ambassador(@RequestParam Integer userId) {
        return userFeign.getAmbassador(userId);
    }

    /**
     * @Author ycl
     * @Description app:用户信息
     * @Date 14:02 2019/7/16
     * @Param
     **/
    @GetMapping(value = "/user/{id}")
    @ApiOperation(value = "用户信息")
    public UserDTO get(@PathVariable(value = "id") Integer id) {
        return userFeign.get(id);
    }

    /**
     * @Author ycl
     * @Description
     * @Date 8:55 2019/7/18
     * @Param
     **/
    @PatchMapping(value = "/user")
    @ApiOperation(value = "更新用户信息")
    public ResponseEntity patchUpdate(@RequestBody UserDTO dto) {
        userFeign.patchUpdate(dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * @Author ycl
     * @Description 我的客户
     * @Date 15:04 2019/7/24
     * @Param
     **/
    @GetMapping(value = "user/my/customers/company")
    @ApiOperation(value = "我的客户")
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "String", paramType = "query")
    public ResponseEntity<List<UserDTO>> myCustomersCompany(@RequestParam(value = "userId") Integer userId) {
        List<UserDTO> userDTOList = userFeign.myCustomersCompany(userId);
        return ResponseEntity.ok(userDTOList);
    }


    /**
     * @Author ycl
     * @Description 用户中心--发展子账号
     * @Date 18:06 2019/8/5
     * @Param
     **/
    @GetMapping(value = "/user/sonAccount/info/{id}")
    @ApiOperation(value = "用户中心-发展子账号")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "经销商id", dataType = "Long", paramType = "path")
    })
    public Object getSonAccountInfo(@PathVariable(value = "id") Integer id) {
        Object obj = userFeign.getSonAccountInfo(id);
        return ResponseEntity.ok(obj);
    }


    /**
     * @Author ycl
     * @Description APP：我的销售--我的客户
     * @Date 11:18 2019/8/6
     * @Param
     **/
    @GetMapping(value = "/user/sale/my/customer")
    @ApiOperation(value = "我的销售--我的客户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "type:1-昨日 2-本月", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "queryType", value = "查询类型 0-全部（默认） 1-主账户 2-子账户", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "subDistributorId", value = "经销商子账号ID，当queryType=2时，此为必传", dataType = "Long", paramType = "query")
    })
    public Object queryMyCustomer(@RequestParam(value = "userId") Integer userId,
                                  @RequestParam(value = "type", required = false) Integer type,
                                  @RequestParam(value = "queryType", required = false) Integer queryType,
                                  @RequestParam(value = "subDistributorId", required = false) Integer subDistributorId) {
        return ResponseEntity.ok(userFeign.queryMyCustomer(userId, type, queryType, subDistributorId));
    }


    /**
     * @Author ycl
     * @Description APP：我的销售--我的客户--客户列表
     * @Date 17:24 2019/8/6
     * @Param
     **/
    @GetMapping(value = "/user/sale/list/customer")
    @ApiOperation(value = "我的销售--我的客户--客户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "key", value = "搜索关键字", dataType = "String", paramType = "query")
    })
    public ResponseEntity getCustomerList(@RequestParam(value = "userId") Integer userId,
                                          @RequestParam(value = "key", required = false) String key) {
        return ResponseEntity.ok(userFeign.getCustomerList(userId, key));

    }


    /**
     * @Author ycl
     * @Description 客户信息
     * @Date 15:51 2019/11/19
     * @Param
     **/
    @GetMapping(value = "/user/sale/customer/{id}")
    @ApiOperation(value = "我的销售--我的客户--客户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long", paramType = "path")
    })
    public UserDTO getCustomerDetailInfo(@PathVariable(value = "id") Integer id) {
        return userFeign.getCustomerDetailInfo(id);
    }


    /**
     * @Author ycl
     * @Description 我的代理--经营报表
     * @Date 14:06 2019/8/29
     * @Param
     **/
    @GetMapping(value = "/user/business/newspaper/{id}")
    @ApiOperation(value = "我的代理--经营报表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "区代Id", dataType = "Long", paramType = "path")
    })
    public Object getBusinessNewspaper(@PathVariable(value = "id") Integer id) {
        if (null == id) {
            throw new BadRequestException("参数为空");
        }
        return userFeign.getBusinessNewspaper(id);
    }


    /**
     * @param
     * @return com.yimao.cloud.pojo.dto.user.RedAccountDTO
     * @description 根据账户id和账户类型获取账户红包账户信息
     * @author Liu Yi
     * @date 2019/9/9 16:19
     */
    @GetMapping(value = "/user/redAccount")
    @ApiOperation(value = "根据账户id和账户类型获取账户红包账户信息")
    public RedAccountDTO getRedAccount() {
        Integer userId = userCache.getUserId();
        if (null == userId) {
            throw new BadRequestException("用户未登录！");
        }
        RedAccountDTO redAccount = userFeign.getRedAccountByAccountId(userId, 2);

        if (Objects.isNull(redAccount)) {
            return null;
        }

        redAccount.setCurrentMoney(redAccount.getCurrentMoney() / 100);
        redAccount.setTotalMoney(redAccount.getTotalMoney() / 100);
        redAccount.setLockedMoney(redAccount.getLockedMoney() / 100);
        return redAccount;
    }

    /**
     * @Author ycl
     * @Description 企业版经销商查看企业信息
     * @Date 10:03 2019/11/8
     * @Param
     **/
    @GetMapping(value = "/user/company/info/{id}")
    @ApiOperation(value = "企业版经销商查看企业信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "Long", paramType = "path")
    })
    public Object queryCompanyInfoById(@PathVariable(value = "id") Integer id) {
        return userFeign.queryCompanyInfoById(id);
    }


}
