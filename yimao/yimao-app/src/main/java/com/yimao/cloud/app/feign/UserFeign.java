package com.yimao.cloud.app.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.pojo.dto.order.AgentSalesOverviewDTO;
import com.yimao.cloud.pojo.dto.order.SalesStatsDTO;
import com.yimao.cloud.pojo.dto.order.SalesStatsQueryDTO;
import com.yimao.cloud.pojo.dto.order.SalesStatsResultDTO;
import com.yimao.cloud.pojo.dto.user.CompanyCustomerDTO;
import com.yimao.cloud.pojo.dto.user.CustomerAddressDTO;
import com.yimao.cloud.pojo.dto.user.DistributorCountDTO;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import com.yimao.cloud.pojo.dto.user.DistributorOrderAllInfoDTO;
import com.yimao.cloud.pojo.dto.user.DistributorOrderDTO;
import com.yimao.cloud.pojo.dto.user.DistributorProtocolDTO;
import com.yimao.cloud.pojo.dto.user.DistributorQueryDTO;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.dto.user.PersonCustomerDTO;
import com.yimao.cloud.pojo.dto.user.RedAccountDTO;
import com.yimao.cloud.pojo.dto.user.UserAddressDTO;
import com.yimao.cloud.pojo.dto.user.UserCompanyApplyDTO;
import com.yimao.cloud.pojo.dto.user.UserCountDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.dto.user.UserDistributorRegisterDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.feign.configuration.MultipartSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@FeignClient(name = Constant.MICROSERVICE_USER, configuration = MultipartSupportConfig.class)
public interface UserFeign {

    /**
     * 经销商APP登录（经销商账号+密码登录）
     */
    @PostMapping(value = "/jxsapp/login")
    UserDTO distLogin(@RequestParam(value = "username") String username,
                      @RequestParam(value = "password") String password,
                      @RequestParam(value = "appType") Integer appType);

    /**
     * 经销商APP登录（手机号+验证码登录）
     */
    @PostMapping(value = "/jxsapp/mobilelogin")
    CommResult<Map<String, Object>> appLoginByMobile(@RequestParam(value = "mobile") String mobile,
                                                     @RequestParam(value = "sharerId", required = false) Integer sharerId,
                                                     @RequestParam(value = "systemType") Integer systemType,
                                                     @RequestParam(value = "appType") Integer appType);

    /**
     * 经销商APP登录（手机号+验证码，选择账号登录）
     */
    @PostMapping(value = "/jxsapp/mobilelogin/selectaccount")
    CommResult<UserDTO> appLoginByMobileSelectAccount(@RequestParam("mobile") String mobile, @RequestParam("key") String key,
                                                      @RequestParam("userId") Integer userId, @RequestParam("systemType") Integer systemType, @RequestParam(value = "appType") Integer appType);

    /**
     * 经销商APP登录（微信授权登录）
     */
    @PostMapping(value = "/jxsapp/wxlogin")
    CommResult<Map<String, Object>> wxLogin(@RequestParam("code") String code,
                                            @RequestParam(value = "appType") Integer appType,
                                            @RequestParam(value = "systemType") Integer systemType);

    /**
     * 经销商APP登录（微信授权登录，绑定手机号）
     */
    @PostMapping(value = "/jxsapp/bindingmobile")
    CommResult<Map<String, Object>> wxLoginBindingMobile(@RequestParam(value = "mobile") String mobile, @RequestParam(value = "sharerId") Integer sharerId, @RequestParam(value = "systemType") Integer systemType, @RequestParam(value = "appType") Integer appType);

    //根据用户id获取用户部分信息
    @GetMapping(value = "/user/{id}/single")
    UserDTO getBasicUserById(@PathVariable(value = "id") Integer id);

    /**
     * @author ycl
     * @Description:经销商APP-用户数量
     * @Param: [roleLevel, distributorId]
     * @Return: com.yimao.cloud.pojo.dto.user.UserCountDTO
     * @Create: 2019/1/11 14:10
     */
    @RequestMapping(value = "/user/dist", method = RequestMethod.GET)
    UserCountDTO getUserNum(@RequestParam("roleLevel") Integer roleLevel,
                            @RequestParam("distributorId") Integer distributorId);

    /**
     * 经销商APP重置密码
     */
    @RequestMapping(value = "/user/resetpwd", method = RequestMethod.PATCH)
    void resetDistributorPassword(@RequestParam(value = "userName") String userName, @RequestParam(value = "password") String password);

    /**
     * @param userId
     * @param pageNum
     * @param pageSize
     * @Description: 经销商APP-地址列表
     * @author ycl
     * @Create: 2019/1/16 11:58
     */
    @RequestMapping(value = "/user/address/{pageNum}/{pageSize}", method = RequestMethod.GET)
    PageVO<UserAddressDTO> listAddress(@RequestParam("userId") Integer userId,
                                       @PathVariable(value = "pageNum") Integer pageNum,
                                       @PathVariable(value = "pageSize") Integer pageSize);

    /**
     * @param dto
     * @Description:添加新地址
     * @author ycl
     * @Return: void
     * @Create: 2019/1/16 12:09
     */
    @RequestMapping(value = "/user/address", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void saveAddress(@RequestBody UserAddressDTO dto);

    /**
     * @param dto
     * @Description:修改地址
     * @author ycl
     * @Return: void
     * @Create: 2019/1/16 12:11
     */
    @RequestMapping(value = "/user/address", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateAddress(@RequestBody UserAddressDTO dto);

    /**
     * @Description:获取默认地址
     * @author ycl
     * @Return: com.yimao.cloud.pojo.dto.user.UserAddressDTO
     * @Create: 2019/1/16 13:47
     */
    @RequestMapping(value = "/user/address/default/{userId}", method = RequestMethod.GET)
    UserAddressDTO getDefaultAddress(@PathVariable("userId") Integer userId);

    /**
     * @param id
     * @Description:删除地址
     * @author ycl
     * @Return: void
     * @Create: 2019/1/17 16:00
     */
    @RequestMapping(value = "/user/address/{id}", method = RequestMethod.DELETE)
    void deleteAddress(@PathVariable("id") Integer id);


    /**
     * @param addressId
     * @Description:设置默认地址
     * @author ycl
     * @Return: void
     * @Create: 2019/1/16 12:13
     */
    @RequestMapping(value = "/user/address/{addressId}", method = RequestMethod.PATCH)
    void updateDefault(@PathVariable("addressId") Integer addressId);

    /**
     * @param pageNum
     * @param pageSize
     * @Description: 经销商APP- 我的客户
     * @author ycl
     * @Return: com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.user.UserDTO>
     * @Create: 2019/1/26 10:40
     */
    @RequestMapping(value = "/customers/{pageNum}/{pageSize}", method = RequestMethod.GET)
    PageVO<CustomerAddressDTO> pageQueryCustomer(@PathVariable(value = "pageNum") Integer pageNum,
                                                 @PathVariable(value = "pageSize") Integer pageSize,
                                                 @RequestParam(value = "userId") Integer userId);

    /**
     * @param personCustomerDTO
     * @Description: 客户地址-新增个人客户
     * @author ycl
     * @Return: void
     * @Create: 2019/1/22 11:31
     */
    @RequestMapping(value = "/customers/person", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void savePersonCustomer(@RequestBody PersonCustomerDTO personCustomerDTO);


    /**
     * @param companyCustomerDTO
     * @Description: 客户地址-新增企业客户
     * @author ycl
     * @Return: void
     * @Create: 2019/1/22 11:32
     */
    @RequestMapping(value = "/customers/company", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void saveCompanyCustomer(@RequestBody CompanyCustomerDTO companyCustomerDTO);

    /**
     * 经销商APP-修改头像
     */
    @RequestMapping(value = "/user/modifyheadimg", method = RequestMethod.POST)
    UserDTO updateHeadImg(@RequestParam("headImg") String headImg);

    /**
     * 根据经销商账号统计经销商信息
     *
     * @param id
     * @return com.yimao.cloud.pojo.dto.user.DistributorCountDTO
     * @author hhf
     * @date 2019/1/22
     */
    @RequestMapping(value = "/distributor/statistics/{id}", method = RequestMethod.GET)
    DistributorCountDTO countDistributorById(@PathVariable("id") Integer id);

    /**
     * 根据身份证验证经销商是否已经注册
     *
     * @param idCard 身份证账号
     * @return Boolean
     * @author hhf
     * @date 2018/1/26
     */
    @RequestMapping(value = "/distributor/verify", method = RequestMethod.GET)
    Boolean existDistributorByIdCard(@RequestParam("idCard") String idCard);

    /**
     * 分页查询经销商信息
     *
     * @param pageNum  分页页数
     * @param pageSize 分页大小
     * @param query    查询信息
     * @return com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.user.DistributorDTO>
     * @author hhf
     * @date 2019/1/26
     */
    @RequestMapping(value = "/distributor/{pageNum}/{pageSize}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<DistributorDTO> pageQueryDistributor(@PathVariable(value = "pageNum") Integer pageNum,
                                                @PathVariable(value = "pageSize") Integer pageSize,
                                                @RequestBody DistributorQueryDTO query);


    //查询区域下的安装工
	/*
	 * @RequestMapping(value = "/engineers", method = RequestMethod.GET)
	 * List<EngineerDTO> getEngineerByPRC(@RequestParam(value = "province") String
	 * province,
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
     * 我的健康大使
     *
     * @author hhf
     * @date 2019/7/10
     */
    @RequestMapping(value = "/user/ambassador", method = RequestMethod.GET)
    UserDTO getAmbassador(@RequestParam(value = "userId") Integer userId);

    @RequestMapping(value = "/distributor/{id}", method = RequestMethod.GET)
    DistributorDTO getBasicInfoById(@PathVariable(value = "id") Integer id);


    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    UserDTO get(@PathVariable(value = "id") Integer id);

    /**
     * 根据企业版主账号ID获取企业版子账号集合
     *
     * @param mid 主账号ID
     * @return List
     * @author Liu  Yi
     * @date 2019/8/15
     */
    @GetMapping(value = "/distributor/child")
    List<DistributorDTO> getSonDistributorByMid(@RequestParam(value = "mid") Integer mid);


    /**
     * @Author ycl
     * @Description 修改用户信息
     * @Date 8:54 2019/7/18
     * @Param
     **/
    @RequestMapping(value = "/user", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
    void patchUpdate(@RequestBody UserDTO dto);

    @RequestMapping(value = "/user/modifypwd", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
    void updatePassword(@RequestBody UserDTO user);

    //我的客户
    @GetMapping(value = "user/my/customers/company")
    List<UserDTO> myCustomersCompany(@RequestParam(value = "userId") Integer userId);

    @GetMapping(value = "/user/sonAccount/info/{id}")
    Object getSonAccountInfo(@PathVariable(value = "id") Integer id);

    /**
     * @Author ycl
     * @Description 查询经销商角色配置信息（所有）
     * @Date 10:58 2019/8/6
     * @Param
     **/
    @GetMapping(value = "/distributor/roles")
    Object list();

    @GetMapping(value = "/user/sale/my/customer")
    Object queryMyCustomer(@RequestParam(value = "userId") Integer userId,
                           @RequestParam(value = "type", required = false) Integer type,
                           @RequestParam(value = "queryType", required = false) Integer queryType,
                           @RequestParam(value = "subDistributorId", required = false) Integer subDistributorId);

    @GetMapping(value = "/user/sale/list/customer")
    Object getCustomerList(@RequestParam(value = "userId") Integer userId,
                           @RequestParam(value = "key", required = false) String key);


    /**
     * @Author liuyi
     * @Description app-经销商当前身份信息(获取经销商当前身份和剩余配额和总配额 我的合同)
     * @Date 9:17 2019/8/19
     * @Param
     **/
    @GetMapping(value = "/distributor/info/app/{distributorId}")
    Map<String, Object> getDistributorAccountInfoForApp(@PathVariable(value = "distributorId") Integer distributorId);

    /**
     * @param
     * @return com.yimao.cloud.pojo.dto.user.UserDTO
     * @description 经销商app注册经销商
     * @author Liu Yi
     * @date 2019/8/22 10:32
     */
    @PostMapping(value = "/distributor/app/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Object> registDistributorForApp(@RequestBody UserDistributorRegisterDTO userDistributorRegisterDTO);

    @GetMapping(value = "/distributor/app/contract/{orderId}/preview")
    DistributorProtocolDTO previewContract(@PathVariable(value = "orderId") Long orderId);


    /**
     * @param
     * @return com.yimao.cloud.pojo.dto.user.UserDTO
     * @description 经销商app注册经销商
     * @author Liu Yi
     * @date 2019/8/22 10:32
     */
    @PatchMapping(value = "/distributor/app/contract/{orderId}/sign")
    DistributorOrderDTO signContract(@PathVariable(value = "orderId") Long orderId);

    /**
     * @param orderType        订单类型1-升级 2-续费
     * @param distributorLevel 经销商升级等级
     * @return java.lang.String
     * @description 获取经销商升级或者续费提示信息
     * @author Liu Yi
     * @date 2019/8/23 13:31
     */
    @GetMapping(value = "/distributor/app/order/remindMessage")
    String getDistributorOrderRemindMessage(@RequestParam(value = "orderType") Integer orderType,
                                            @RequestParam(value = "distributorLevel", required = false) Integer distributorLevel);

    /**
     * 升级经销商订单
     *
     * @param dto
     * @author Liu Yi
     * @date 2019/1/9
     */
    @PostMapping(value = "user/distributor/order/upgrade", consumes = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Object> upgradeOrder(@RequestBody DistributorOrderDTO dto);

    /**
     * 查询订单所需价格
     *
     * @param origLevel
     * @param destLevel
     * @param distributorId
     * @author Liu Yi
     * @date 2019/1/9
     */
    @GetMapping(value = "user/distributor/order/price")
    BigDecimal getOrderPrice(@RequestParam(value = "distributorId") Integer distributorId,
                             @RequestParam(value = "origLevel") Integer origLevel,
                             @RequestParam(value = "destLevel", required = false) Integer destLevel,
                             @RequestParam(value = "orderType") Integer orderType);

    @GetMapping(value = "distributor/order/{orderId}")
    DistributorOrderAllInfoDTO findDistributorOrderById(@PathVariable(value = "orderId") Long orderId);

    /**
     * 经销商订单详情
     *
     * @param id
     */
    @GetMapping(value = "distributor/order/{id}/basis")
    DistributorOrderDTO findBasisDistributorOrderById(@PathVariable(value = "id") Long id);

    /**
     * 重新提交企业信息（未完成的订单）
     *
     * @param dto
     * @author Liu Yi
     * @date 2019/1/9
     */
    @PostMapping(value = "user/distributor/order/companyApply/renewCommit", consumes = MediaType.APPLICATION_JSON_VALUE)
    void renewCommitCompanyApply(@RequestBody UserCompanyApplyDTO dto);


    /**
     * 续费经销商订单
     *
     * @param dto
     * @author Liu Yi
     * @date 2019/1/9
     */
    @PostMapping(value = "user/distributor/order/renew", consumes = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Object> renewOrder(@RequestBody DistributorOrderDTO dto);

    /**
     * 经销商订单提交支付凭证
     *
     * @param dto
     * @author Liu Yi
     * @date 2019/1/9
     */
    @PutMapping(value = "user/distributor/order/{id}/credential")
    void submitCredential(@PathVariable(value = "id") Long id,
                          @RequestParam(value = "payType") Integer payType,
                          @RequestParam(value = "payCredential") String payCredential);

    /**
     * @param distributorId 经销商id
     * @return java.util.List<com.yimao.cloud.user.po.DistributorOrder>
     * @description 根据创建人（经销商）查询未完成订单
     * @author Liu Yi
     * @date 14:53 2019/8/20
     **/
    @GetMapping(value = "/distributor/app/order/unfinished")
    List<DistributorOrderDTO> unfinishedOrderListByCreator(@RequestParam(value = "distributorId") Integer distributorId);

    @GetMapping(value = "/agent/distributors/app/{pageNum}/{pageSize}")
    Object getMyDistributors(@PathVariable(value = "pageNum") Integer pageNum,
                             @PathVariable(value = "pageSize") Integer pageSize,
                             @RequestParam(value = "distributorId") Integer distributorId,
                             @RequestParam(value = "distributorType", required = false) Integer distributorType,
                             @RequestParam(value = "province", required = false) String province,
                             @RequestParam(value = "city", required = false) String city,
                             @RequestParam(value = "region", required = false) String region);


    //我的代理-经营报表
    @GetMapping(value = "/user/business/newspaper/{id}")
    Object getBusinessNewspaper(@PathVariable(value = "id") Integer id);

    /**
     * 上传营业执照
     *
     * @return
     */
    @RequestMapping(value = "/distributor/businessLicenseImage", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String uploadBusinessLicenseImage(@RequestPart("image") MultipartFile file);

    /**
     * 企业版主账号和子账号列表
     */
    @GetMapping(value = "/distributor/mid/child")
    List<DistributorDTO> getDistributorAndSonByMid(@RequestParam("mid") Integer mid);

    /**
     * @param
     * @return com.yimao.cloud.pojo.dto.user.RedAccountDTO
     * @description 根据账户id和账户类型获取账户红包账户信息
     * @author Liu Yi
     * @date 2019/9/9 16:19
     */
    @GetMapping(value = "/redAccount/accountId")
    RedAccountDTO getRedAccountByAccountId(@RequestParam("accountId") Integer accountId,
                                           @RequestParam("accountType") Integer accountType);

    /**
     * @Author ycl
     * @Description 地址管理-客户地址-修改个人客户
     * @Date 19:15 2019/9/17
     * @Param
     **/
    @RequestMapping(value = "/customers/person", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    void updatePersonCustomer(@RequestBody PersonCustomerDTO personCustomerDTO);


    /**
     * @Author ycl
     * @Description 地址管理-客户地址-修改企业客户
     * @Date 19:16 2019/9/17
     * @Param
     **/
    @RequestMapping(value = "/customers/company", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateCompanyCustomer(@RequestBody CompanyCustomerDTO companyCustomerDTO);


    /**
     * @Author ycl
     * @Description 地址管理-客户地址-删除个人客户
     * @Date 9:39 2019/9/18
     * @Param
     **/
    @DeleteMapping(value = "/customers/person/{id}")
    void deletePersonCustomer(@PathVariable(value = "id") Integer id);


    @DeleteMapping(value = "/customers/company/{id}")
    void deleteCompanyCustomer(@PathVariable(value = "id") Integer id);


    @GetMapping(value = "/customers/detail/info/{id}")
    Object queryCustomerInfo(@PathVariable(value = "id") Integer id,
                             @RequestParam(value = "type") Integer type);

    @GetMapping(value = "/distributor/account")
    DistributorDTO getDistributorByUserName(@RequestParam(value = "userName") String userName);

    /**
     * 合同创建
     *
     * @param dto
     * @return
     */
    @PostMapping(value = "distributor/protocol/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    Void createProtocol(@RequestBody DistributorProtocolDTO dto);

    /**
     * 查看合同页
     *
     * @param distributorOrderId
     * @return
     */
    @GetMapping(value = "distributor/protocol/view/{distributorOrderId}")
    String previewDistributorProtocol(@PathVariable(value = "distributorOrderId") Long distributorOrderId);

    /**
     * 云签回调，修改合同签署状态
     *
     * @param info
     */
    @PostMapping("distributor/protocol/backCall")
    Void backCall(@RequestParam("info") String info);

    /**
     * 合同签署页面
     *
     * @param distributorOrderId
     * @return
     */
    @GetMapping(value = "distributor/protocol/sign/{distributorOrderId}")
    String signDistributorProtocol(@PathVariable(value = "distributorOrderId") Long distributorOrderId);

    /**
     * 根据经销商的ID查询合同信息（用户，总部都已签署的合同）
     *
     * @param distributorId
     * @return
     */
    @GetMapping("distributor/protocol/listShow")
    List<DistributorProtocolDTO> queryDistributorProtocolByDistIdAndSignStatus(@RequestParam("distributorId") Integer distributorId);

    @GetMapping("distributor/protocol/checkUserSignStatus")
    Map<String, String> checkUserSignStatus(@RequestParam("orderId") Long orderId);

    @GetMapping(value = "/user/address/type/{id}")
    Object getAddressByType(@PathVariable(value = "id") Integer id,
                            @RequestParam(value = "type") Integer type);

    @GetMapping(value = "/user/company/info/{id}")
    Object queryCompanyInfoById(@PathVariable(value = "id") Integer id);


    @GetMapping(value = "/user/sale/customer/{id}")
    UserDTO getCustomerDetailInfo(@PathVariable(value = "id") Integer id);
    
    /***
     * 招商销售额统计(包含销售额数据、各类型增长趋势数据、各类型占比数据)
     * @param query
     * @return
     */
    @PostMapping(value = "/distributor/investment/statsInfo", consumes = MediaType.APPLICATION_JSON_VALUE)
	SalesStatsResultDTO getInvestmentStats(@RequestBody SalesStatsQueryDTO query);
    
    /***
     * 根据日期类型统计销售额数据(年、月、日)
     * @param query
     * @return
     */
    @PostMapping(value = "/distributor/investment/sale/statsInfo", consumes = MediaType.APPLICATION_JSON_VALUE)
	List<SalesStatsDTO> getInvestmentSalesStats(@RequestBody SalesStatsQueryDTO query);
    
    /***
     * (根据年、月)统计各类型销售增长趋势统计
     * @param query
     * @return
     */
    @PostMapping(value = "/distributor/increase/trend/statsInfo", consumes = MediaType.APPLICATION_JSON_VALUE)
	public List<SalesStatsDTO> getInvestmentIncreaseTrendStats(@RequestBody SalesStatsQueryDTO query);

    /****
     * 获取经销商发展的所有经销商信息
     * @return
     */
    @GetMapping(value = "/user/{id}/getDistributors")
    List<Integer> getDistributorList(@PathVariable(value = "id") Integer id);

	@GetMapping(value = "/user/{id}/info")
	UserDTO getUserDistributorInfoById(@PathVariable(value = "id") Integer id);

	/**
	 * @param
	 * @description 经销商app-经营报表-汇总统计
	 * @author Liu Yi
	 * @date 2020/4/27 15:21
	 */
	@PostMapping(value = "/distributor/investment/home/report", consumes = MediaType.APPLICATION_JSON_VALUE)
	AgentSalesOverviewDTO getOrderSalesHomeReport(@RequestBody SalesStatsQueryDTO query);

	/**
	 * @param
	 * @description 经销商app-经营报表-累计销售金额统计表
	 * @author Liu Yi
	 * @date 2020/4/27 15:21
	 */
	@PostMapping(value = "/distributor/investment/trend/report",consumes = MediaType.APPLICATION_JSON_VALUE)
	List<SalesStatsDTO> getOrderSalesTotalReport(@RequestBody SalesStatsQueryDTO query);
	
	/***
     * 根据areaid获取经销商信息
     * @param areaIds
     * @return
     */
    @GetMapping(value = "/distributor/ids/area/app")
    public List<Integer> getDistributorIdsByAreaIdsForApp(@RequestParam(value = "areaIds") List<Integer> areaIds);

    /**
     * 经销商订单类型名称
     * @return
     */
    @GetMapping(value = "/distributor/order/type/name")
    List<String> queryDistributorOrderTypeNames();
}