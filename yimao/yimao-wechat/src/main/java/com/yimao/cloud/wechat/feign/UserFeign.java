package com.yimao.cloud.wechat.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.pojo.dto.user.*;
import com.yimao.cloud.pojo.dto.wechat.WxUserInfo;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.feign.configuration.MultipartSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

@FeignClient(name = Constant.MICROSERVICE_USER, configuration = MultipartSupportConfig.class)
public interface UserFeign {

    /**
     * 用户登录
     */
    @RequestMapping(value = "/user/login", method = RequestMethod.GET)
    UserDTO login(@RequestParam(value = "openid") String openid);

    /**
     * 根据用户id获取用户信息
     */
    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    UserDTO getUserDTOById(@PathVariable(value = "id") Integer id);

    /**
     * 根据用户id获取用户信息
     */
    @GetMapping(value = "/user/{id}/single")
    UserDTO getBasicUserById(@PathVariable(value = "id") Integer id);

    /**
     * 根据用户id更新用户信息
     */
    @RequestMapping(value = "/user", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    void update(@RequestBody UserDTO userDTO);

    /**
     * 用户关系建立
     */
    @RequestMapping(value = "/user/process", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    UserDTO userProcess(@RequestParam("identityType") Integer identityType,
                        @RequestParam(value = "sharerId", required = false) Integer sharerId,
                        @RequestBody WxUserInfo wxUserInfo,
                        @RequestParam(value = "origin", required = false) Integer origin);

    /**
     * 用户地址
     */
    @RequestMapping(value = "/user/address/{pageNum}/{pageSize}", method = RequestMethod.GET)
    PageVO<UserAddressDTO> listUserAddress(@RequestParam("userId") Integer userId,
                                           @PathVariable(value = "pageNum") Integer pageNum,
                                           @PathVariable(value = "pageSize") Integer pageSize);

    /**
     * 新增用户地址
     */
    @RequestMapping(value = "/user/address", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    Integer saveUserAddress(@RequestBody UserAddressDTO dto);

    /**
     * 修改用户地址
     */
    @RequestMapping(value = "/user/address", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    Integer updateUserAddress(@RequestBody UserAddressDTO dto);

    /**
     * 删除用户地址
     */
    @RequestMapping(value = "/user/address/{id}", method = RequestMethod.DELETE)
    Integer deleteUserAddress(@PathVariable(value = "id") Integer id);

    /**
     * 设置默认地址
     */
    @RequestMapping(value = "/user/address/{id}", method = RequestMethod.PATCH)
    Integer setDefaultAddress(@PathVariable(value = "id") Integer id);

    /**
     * 获取默认地址
     */
    @RequestMapping(value = "/user/address/default", method = RequestMethod.GET)
    UserAddressDTO getDefaultAddress();

    /**
     * 获取用户地址总数
     */
    @RequestMapping(value = "/user/address/count", method = RequestMethod.GET)
    Integer countAddress();

    @RequestMapping(value = "/user/share/{id}/qrcode", method = RequestMethod.GET)
    String getQRCodeWithParam(@PathVariable("id") Integer userId);


    /**
     * 获取个人专属二维码
     */
    @RequestMapping(value = "/user/fixed/qrcode", method = RequestMethod.GET)
    File myFixedQRCodeImage(@RequestParam("openId") String openid);

    /**
     * 关注公众号/取关公众号- 身份变更记录
     *
     * @param userId 用户id
     * @param type   类型 1-关注公众号 -1 取关公众号
     * @param phone  手机号
     * @param time   时间
     * @param remark 备注
     */
    @RequestMapping(value = "/user/subscribe", method = RequestMethod.POST)
    void saveSubscribe(@RequestParam("userId") Integer userId,
                       @RequestParam("type") int type,
                       @RequestParam(value = "userType", required = false) Integer userType,
                       @RequestParam(value = "phone", required = false) String phone,
                       @RequestParam("time") Date time,
                       @RequestParam("remark") String remark);

    /**
     * 绑定经销商账户
     *
     * @param userBindDTO 绑定信息
     * @author hhf
     * @date 2019/4/16
     */
    @RequestMapping(value = "/user/bind/distributor", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    UserDTO bindDistributor(@RequestBody UserBindDTO userBindDTO);

    /**
     * 绑定经销商账户
     *
     * @param userBindDTO 绑定信息
     * @author hhf
     * @date 2019/4/17
     */
    @RequestMapping(value = "/user/bind/phone", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    UserDTO bindPhone(@RequestBody UserBindDTO userBindDTO);

    /**
     * 根据openId获取用户Id
     *
     * @param openid openid
     * @return 用户Id
     */
    @RequestMapping(value = "/user/unique/{openid}", method = RequestMethod.GET)
    Integer getUserIdByOpenid(@PathVariable("openid") String openid);

    /**
     * 获取我的推广客户列表（企业版主账号）
     *
     * @param userId 用户ID
     * @author hhf
     * @date 2019/4/22
     */
    @RequestMapping(value = "user/my/customers/company", method = RequestMethod.GET)
    List<UserDTO> findShareCustomers(@RequestParam("userId") Integer userId);

    /**
     * 获取我的推广客户列表
     *
     * @param userId 用户ID
     * @author hhf
     * @date 2019/4/22
     */
    @RequestMapping(value = "/user/my/customers", method = RequestMethod.GET)
    Map<String, Object> myCustomers(@RequestParam(value = "userId", required = false) Integer userId,
                                    @RequestParam(value = "distributorId", required = false) Integer distributorId);

    /**
     * 获取我的推广客户数量
     *
     * @param userId 用户ID
     * @return Integer
     * @author hhf
     * @date 2019/4/24
     */
    @RequestMapping(value = "/user/my/customers/count", method = RequestMethod.GET)
    Integer myCustomersCount(@RequestParam("userId") Integer userId);

    /**
     * 修改用户星级
     *
     * @param userAliasDTO 用户星级信息
     * @return void
     * @author hhf
     * @date 2019/4/24
     */
    @RequestMapping(value = "/user/asterisk", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void asterisk(@RequestBody UserAliasDTO userAliasDTO);

    /**
     * 我的健康大使
     *
     * @author hhf
     * @date 2019/4/28
     */
    @RequestMapping(value = "/user/ambassador", method = RequestMethod.GET)
    UserDTO getAmbassador(@RequestParam(value = "userId") Integer userId);

    /**
     * 根据省市区查询安装工列表（MySQL）
     *
     * @param province
     * @param city
     * @param region
     */
	/*
	 * @GetMapping(value = "/engineers") List<EngineerDTO>
	 * listEngineerByPCR(@RequestParam(value = "province") String province,
	 * 
	 * @RequestParam(value = "city") String city,
	 * 
	 * @RequestParam(value = "region") String region);
	 */
    
    /***
     * 根据区域id获取安装工信息
     * @param areaId
     * @return
     */
    @GetMapping(value = "/engineers/area")
    public List<EngineerDTO> listEngineerByRegion(@RequestParam(value = "areaId") Integer areaId);

    /**
     * 获取经销商信息
     *
     * @param id 经销商id
     * @return dto
     */
    @RequestMapping(value = "/distributor/{id}", method = RequestMethod.GET)
    DistributorDTO getDistributorById(@PathVariable("id") Integer id);


    //企业版经销商发展子账号
    @GetMapping(value = "/user/develop/sonAccount")
    Object developSonAccount(@RequestParam(value = "realName") String realName,
                             @RequestParam(value = "sex") Integer sex,
                             @RequestParam(value = "idCard") String idCard,
                             @RequestParam(value = "email", required = false) String email,
                             @RequestParam(value = "userId") Integer userId);


    @GetMapping(value = "/distributor/recommend/{province}/{city}/{region}")
    List<DistributorDTO> getRecommendByAddress(@RequestParam(value = "province") String province,
                                               @RequestParam(value = "city") String city,
                                               @RequestParam(value = "region") String region);


    /**
     * @Author ycl
     * @Description H5-经销商注册-验证经销商信息
     * @Date 14:32 2019/9/20
     * @Param
     **/
    @RequestMapping(value = "/h5/distributor/check", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    Object checkDistributor(@RequestBody OwnerDistributorDTO ownerDistributorDTO);

    /**
     * @Author ycl
     * @Description H5-经销商自注册-获取短信验证码
     * @Date 14:49 2019/9/20
     * @Param
     **/
    @RequestMapping(value = "/h5/distributor/sendsmscode", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    Object distributorSendSmsCode(@RequestBody OwnerDistributorDTO ownerDistributorDTO,
                                  @RequestParam(value = "key") String key);

    /**
     * @Author ycl
     * @Description H5-经销商自注册-校验短信验证码
     * @Date 14:51 2019/9/20
     * @Param
     **/
    @RequestMapping(value = "/h5/distributor/checksmscode", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    Object distributorCheckSmsCode(@RequestBody OwnerDistributorDTO ownerDistributorDTO,
                                   @RequestParam(value = "smsCode") String smsCode,
                                   @RequestParam(value = "key") String key);

    /**
     * @Author ycl
     * @Description H5-经销商自注册-确认经销商信息
     * @Date 16:17 2019/9/20
     * @Param
     **/
    @RequestMapping(value = "/h5/distributor/determine", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    Object determineDistributor(@RequestBody OwnerDistributorDTO ownerDistributorDTO,
                                @RequestParam(value = "key") String key,
                                @RequestParam(value = "smsCode") String smsCode);

    /**
     * @Author ycl
     * @Description H5-主账号发展子账号-子账号信息校验
     * @Date 19:13 2019/9/20
     * @Param
     **/
    @RequestMapping(value = "/h5/distributor/subaccount/check", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    Object checkSubaccount(@RequestBody SubDistributorAccountDTO subAccountDTO);


    /**
     * @Author ycl
     * @Description H5-主账号发展子账号-发送短信验证码
     * @Date 19:38 2019/9/20
     * @Param
     **/
    @RequestMapping(value = "/h5/distributor/subaccount/sendsmscode", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    Object subaccountSendsmscode(@RequestBody SubDistributorAccountDTO subAccountDTO, @RequestParam(value = "key") String key);


    /**
     * @Author ycl
     * @Description H5-主账号发展子账号-校验短信验证码
     * @Date 9:09 2019/9/23
     * @Param
     **/
    @RequestMapping(value = "/h5/distributor/subaccount/checksmscode", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    Object subaccountCheckSmsCode(@RequestBody SubDistributorAccountDTO subAccountDTO,
                                  @RequestParam(value = "key") String key,
                                  @RequestParam(value = "smsCode") String smsCode);


    /***
     * @Author ycl
     * @Description H5-主账号发展子账号-子账号信息确认
     * @Date 9:25 2019/9/23
     * @Param
     **/
    @RequestMapping(value = "/h5/distributor/subaccount/determine", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    Object determineSubaccount(@RequestBody SubDistributorAccountDTO subAccountDTO,
                               @RequestParam(value = "key") String key,
                               @RequestParam(value = "smsCode") String smsCode);


    //========================H5分享 2020-04-28 liuhao============================

    /**
     * H5分享（微信授权登录）
     */
    @PostMapping(value = "/jxsapp/wxlogin")
    CommResult<Map<String, Object>> wxLogin(@RequestParam("code") String code, @RequestParam(value = "appType") Integer appType, @RequestParam(value = "systemType") Integer systemType);

    /**
     * H5分享（绑定手机号）
     */
    @PostMapping(value = "/jxsapp/bindingmobile")
    CommResult<Map<String, Object>> wxLoginBindingMobile(@RequestParam(value = "mobile") String mobile, @RequestParam(value = "sharerId", required = false) Integer sharerId, @RequestParam(value = "systemType") Integer systemType, @RequestParam(value = "appType") Integer appType);

    /**
     * H5分享-选择账号登录（手机号+验证码）
     */
    @PostMapping(value = "/jxsapp/mobilelogin/selectaccount")
    CommResult<UserDTO> wxLoginByMobileSelectAccount(@RequestParam("mobile") String mobile,
                                                     @RequestParam("key") String key,
                                                     @RequestParam("userId") Integer userId,
                                                     @RequestParam("systemType") Integer systemType,
                                                     @RequestParam(value = "appType") Integer appType);

    /**
     * H5分享（根据手机号查询登录账号）
     */
    @PostMapping(value = "/jxsapp/mobilelogin")
    CommResult<Map<String, Object>> wxLoginByMobile(@RequestParam(value = "mobile") String mobile,
                                                    @RequestParam(value = "sharerId", required = false) Integer sharerId,
                                                    @RequestParam(value = "systemType") Integer systemType,
                                                    @RequestParam(value = "appType") Integer appType);


    /**
     * 用户授权后：根据授权用户的手机号，查询openid
     *
     * @param mobile
     * @return
     */
    @RequestMapping(value = "/h5/mobile/openid", method = RequestMethod.GET)
    String getOpenidByMobile(@RequestParam(value = "mobile") String mobile);

    @RequestMapping(value = "/h5/login", method = RequestMethod.GET)
    UserDTO loginBySystemType(@RequestParam(value = "userId") Integer userId, @RequestParam(value = "systemType") Integer systemType);
}
