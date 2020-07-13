package com.yimao.cloud.order.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.order.PayRecordDTO;
import com.yimao.cloud.pojo.dto.user.*;
import com.yimao.cloud.pojo.vo.PageVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author Zhang Bo
 * @description 用户服务调用
 * @date 2018/11/12.
 */
@FeignClient(name = Constant.MICROSERVICE_USER)
public interface UserFeign {

    /**
     * 下单时如果是普通用户，需要设置健康大使，如果不是直接返回用户信息
     */
    @GetMapping(value = "/user/{id}/setambassador")
    UserDTO changeUserTypeIfMeetTheConditions(@PathVariable(value = "id") Integer id);

    /**
     * 获取用户信息
     *
     * @param id e家号
     * @return 用户信息
     */
    @GetMapping(value = "/user/{id}")
    UserDTO getUserById(@PathVariable(value = "id") Integer id);

    /**
     * 获取用户信息（部分信息）
     *
     * @param id e家号
     * @return 用户信息
     */
    @GetMapping(value = "/user/{id}/single")
    UserDTO getBasicUserById(@PathVariable(value = "id") Integer id);

    /**
     * 根据管理员ID获取管理员信息
     *
     * @param id 管理员ID
     * @return
     */
    @RequestMapping(value = "/admin/{id}", method = RequestMethod.GET)
    AdminDTO getAdminDTOById(@PathVariable("id") Integer id);


    /**
     * 根据e家号获取他的分销商信息
     *
     * @param id e家号
     * @return 分销商信息
     */
    @GetMapping(value = "/user/{id}/sale")
    UserDTO getMySaleUserById(@PathVariable(value = "id") Integer id);


    /**
     * 根据e家号获取他的经销商信息
     *
     * @param id e家号
     * @return 经销商信息
     */
    @GetMapping(value = "/user/{id}/distributor")
    DistributorDTO getMyDistributor(@PathVariable(value = "id") Integer id);

    /**
     * 获取经销商的推荐人
     *
     * @param distributorId 经销商id
     * @return 经销商推荐人信息
     */
    @GetMapping(value = "/distributor/recommend")
    DistributorDTO getMyRecommendByDistributorId(@RequestParam("distributorId") Integer distributorId);

    /**
     * 获取经销商的角色
     *
     * @param id 经销商id
     * @return 50-体验版经销商    350-微创版经销商    650-个人版经销商    950-企业版(主账号) 1000-企业版(子账号)    -50-折机版经销商
     */
    @GetMapping(value = "/user/{id}/role")
    Integer getRoleByDistributorId(@PathVariable(value = "id") Integer id);

    /**
     * @param id
     * @return com.yimao.cloud.pojo.dto.user.UserAddressDTO
     * @description 获取用户地址信息根据id
     * @author zhilin.he
     * @date 2019/1/2 17:03
     */
    @GetMapping(value = "/user/address/{id}")
//    @GetMapping("/address/{id}")
    UserAddressDTO getUserAddressById(@PathVariable(value = "id") Integer id);

    /**
     * @description 根据手机号获取用户信息
     */
    @GetMapping(value = "/user")
    UserDTO getUserByMobie(@RequestParam(value = "mobile") String mobile);

    /**
     * @description 根据多个用户id获取用户信息
     */
    @GetMapping(value = "/user/list")
    List<UserDTO> getUserByUserIdList(@RequestParam(value = "userIdList") List<Integer> userIdList);

    /**
     * 根据经销商ID查询经销商（单表信息）
     *
     * @param id 经销商ID
     */
    @GetMapping(value = "/distributor/{id}")
    DistributorDTO getDistributorBasicInfoById(@PathVariable("id") Integer id);

    /**
     * @param id
     * @return com.yimao.cloud.pojo.dto.user.DistributorDTO
     * @description 根据经销商ID查询经销商详情
     * @author zhilin.he
     * @date 2019/1/24 11:14
     */
    @GetMapping(value = "/distributor/{id}/expansion")
    DistributorDTO getDistributorById(@PathVariable("id") Integer id);

    /**
     * 根据地址信息（省市区）获取发起人信息
     *
     * @param province 省
     * @param city     市
     * @param region   区
     */
    @GetMapping(value = "/distributor/originator")
    DistributorDTO getOriginatorByAddress(@RequestParam(value = "province") String province,
                                          @RequestParam(value = "city", required = false) String city,
                                          @RequestParam(value = "region", required = false) String region);

    /**
     * 根据经销商ID获取经销商主账号信息
     *
     * @param distributorId 经销商ID
     * @return com.yimao.cloud.pojo.dto.user.DistributorDTO
     * @author Liu Yi
     * @date 2019/1/24
     */
    @GetMapping(value = "/distributor/main")
    DistributorDTO getMainAccountByDistributorId(@RequestParam("distributorId") Integer distributorId);


    /**
     * 根据经销商订单ID获取经销商订单信息
     *
     * @param orderId
     * @return DistributorOrderDTO
     * @author Liu Yi
     * @date 2019/1/24
     */
    @GetMapping(value = "distributor/order/{orderId}")
    DistributorOrderDTO getDistributorOrderById(@PathVariable(value = "orderId") Long orderId);

    // /**
    //  * 根据参数获取相关经销商id集合信息
    //  *
    //  * @param userId             用户e家号
    //  * @param distributorType    经销商类型
    //  * @param distributorAccount 经销商账号
    //  * @param distributorName    经销商名称
    //  * @param province           经销商归属区域省
    //  * @param city               经销商归属区域市
    //  * @param region             经销商归属区域区
    //  * @param recommendName      推荐人姓名
    //  * @param recommendAccount   推荐人账号
    //  * @return
    //  * @author Liu Yi
    //  * @date 2019/1/28
    //  */
    // @RequestMapping(value = "/distributor/ids")
    // List<Integer> getDistributorIdByParam(@RequestParam(value = "userId") Integer userId,
    //                                       @RequestParam(value = "distributorType") Integer distributorType,
    //                                       @RequestParam(value = "distributorAccount") String distributorAccount,
    //                                       @RequestParam(value = "distributorName") String distributorName,
    //                                       @RequestParam(value = "province") String province,
    //                                       @RequestParam(value = "city") String city,
    //                                       @RequestParam(value = "region") String region,
    //                                       @RequestParam(value = "recommendName") String recommendName,
    //                                       @RequestParam(value = "recommendAccount") String recommendAccount);

    /**
     * 根据多个经销商id获取经销商集合信息
     *
     * @param distributorIdList id集合
     * @return List<DistributorDTO>
     * @author Liu Yi
     * @date 2019/1/28
     */
    @RequestMapping(value = "/distributor/list", method = RequestMethod.GET)
    List<DistributorDTO> getDistributorByDistributorIdList(@RequestParam(value = "distributorIds") List<Integer> distributorIdList);

    /**
     * 根据ID获取安装工信息
     *
     * @param id 安装工ID
     */
    @RequestMapping(value = "/engineer/{id}", method = RequestMethod.GET)
    EngineerDTO getEngineerById(@PathVariable("id") Integer id);

    /**
     * 根据经销商ID获取推荐人信息
     *
     * @param distributorId 经销商Id
     * @return com.yimao.cloud.pojo.dto.user.DistributorDTO
     * @author hhf
     * @date 2019/3/9
     */
    @RequestMapping(value = "/distributor/recommend", method = RequestMethod.GET)
    DistributorDTO getRecommendByDistributorId(@RequestParam("distributorId") Integer distributorId);


    /**
     * 根据经销商ids获取经销商头像
     *
     * @param ids
     * @return
     * @auth lizhiqiang
     */
    @RequestMapping(value = "/user/image", method = RequestMethod.GET)
    List<String> getDistributorImageById(@RequestParam("ids") List<Integer> ids);

    /**
     * 普通用户下单的经销商信息
     *
     * @return UserIncomeAccountDTO
     * @author liuhao@yimaokeji.com
     */
    @RequestMapping(value = "/income/account", method = RequestMethod.GET)
    UserIncomeAccountDTO getIncomeAccount();


    /**
     * 修改用户信息
     *
     * @param userDTO 用户信息
     */
    @RequestMapping(value = "/user", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
    Integer updateUser(@RequestBody UserDTO userDTO);

    /**
     * @param id
     * @Description: 获取用户地址数量
     * @author ycl
     * @Return: java.lang.Integer
     * @Create: 2019/4/15 10:20
     */
    @RequestMapping(value = "/address/count/{id}")
    Integer getAddressCountByUserId(@PathVariable(value = "id") Integer id);


    /**
     * @param addressDTO
     * @Description: 修改地址信息
     * @author ycl
     * @Return: void
     * @Create: 2019/4/15 10:22
     */
    @RequestMapping(value = "/user/address", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateAddress(@RequestBody UserAddressDTO addressDTO);


    /**
     * @param id
     * @param mid
     * @param distributorId
     * @Description: 查询推广客户
     * @author ycl
     * @Return: java.util.List<com.yimao.cloud.pojo.dto.user.UserDTO>
     * @Create: 2019/4/23 18:01
     */
    @GetMapping(value = "/user/customers")
    List<UserDTO> findCustomers(@RequestParam(value = "id") Integer id,
                                @RequestParam(value = "mid", required = false) Integer mid,
                                @RequestParam(value = "distributorId", required = false) Integer distributorId);

    /**
     * 根据省市区查询安装工
     *
     * @param province 省
     * @param city     市
     * @param region   区
     */
	/*
	 * @GetMapping(value = "/engineers") List<EngineerDTO>
	 * getEngineerByArea(@RequestParam(value = "province") String
	 * province, @RequestParam(value = "city") String city, @RequestParam(value =
	 * "region") String region);
	 */
    
    /***
     * 根据区域id获取安装工信息
     * @param areaId
     * @return
     */
    @GetMapping(value = "/engineers/area")
    public List<EngineerDTO> listEngineerByRegion(@RequestParam(value = "areaId") Integer areaId);

    /**
     * 根据省市区查询安装工数量（包含已经禁用的）
     */
    @GetMapping(value = "/engineers/count")
    int countAllEngineerByArea(@RequestParam(value = "areaId") Integer areaId);

    /**
     * 校验安装工是否存在
     *
     * @param province 省
     * @param city     市
     * @param region   区
     */
    @GetMapping(value = "/engineer/exists")
    boolean checkEngineerExistsByArea(@RequestParam(value = "areaId") Integer areaId);

    /**
     * 更新安装工信息
     */
    @RequestMapping(value = "/engineer", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateEngineer(@RequestBody EngineerDTO dto);

    @GetMapping(value = "/engineer")
    EngineerDTO getEngineerByOldId(@RequestParam(value = "oldId") String oldId);

    /**
     * 修改经销商信息
     *
     * @param dto 经销商信息
     */
    @RequestMapping(value = "/distributor/update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateDistributor(@RequestBody DistributorDTO dto);

    /**
     * 根据主账号ID获取企业版子账号的信息
     *
     * @param mid 主账号ID
     * @return List
     * @author hhf
     * @date 2019/5/30
     */
    @RequestMapping(value = "/distributor/child", method = RequestMethod.GET)
    List<DistributorDTO> getSonDistributorByMid(@RequestParam(value = "mid") Integer mid);

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
     * 根据用户ID获取上级经销商信息（返回基本信息+扩展信息）
     *
     * @param userId 用户e家号
     * @author hhf
     * @date 2019/5/7
     */
    @RequestMapping(value = "/distributor/expansion", method = RequestMethod.GET)
    DistributorDTO getExpansionInfoByUserId(@RequestParam(value = "userId") Integer userId);


    // @RequestMapping(value = "/user/upgrade/sale", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    // void upgradeToSaleUser(@RequestBody UserDTO userDTO);

    @RequestMapping(value = "/user/address/default", method = RequestMethod.GET)
    UserAddressDTO defaultList();

    // @RequestMapping(value = "/user/upgrade/sale", method = RequestMethod.GET)
    // void updateUserIncomePermission(@RequestParam("ids") List<Integer> ids);

    //根据分销商ID查询用户
    @RequestMapping(value = "/user/ambassador/ids", method = RequestMethod.GET)
    List<Integer> findUserByAmbassadorId(@RequestParam(value = "ambassadorId") Integer ambassadorId);

    //根据经销商e家号查询
    @RequestMapping(value = "/user/distributor/ids", method = RequestMethod.GET)
    List<Integer> getDistributorByUserId(@RequestParam(value = "distributorId", required = false) Integer distributorId);

    // 经销商订单详情
    @GetMapping(value = "distributor/order/{id}/basis")
    DistributorOrderDTO findBasisDistributorOrderById(@PathVariable(value = "id") Long id);

    /**
     * 我的客户--客户列表
     */
    @GetMapping(value = "/user/sale/list/customer/{pageNum}/{pageSize}")
    PageVO<UserDTO> getCustomerList(@PathVariable(value = "pageNum") Integer pageNum,
                                    @PathVariable(value = "pageSize") Integer pageSize,
                                    @RequestParam(value = "userId") Integer userId,
                                    @RequestParam(value = "type", required = false) Integer type,
                                    @RequestParam(value = "mark", required = false) Integer mark,
                                    @RequestParam(value = "account", required = false) String account,
                                    @RequestParam(value = "key", required = false) String key,
                                    @RequestParam(value = "tag", required = false) Integer tag);

    /**
     * @param record
     * @return void
     * @description 经销商订单支付回调
     * @author Liu Yi
     * @date 2019/9/16 17:49
     */
    @PostMapping(value = "user/distributor/order/payCallback", consumes = MediaType.APPLICATION_JSON_VALUE)
    void distributorOrderPayCallback(@RequestBody PayRecordDTO record);

    /**
     * 根据经销商ID查询经销商信息（消息推送时只需获取很少的几个字段）
     */
    @GetMapping(value = "/distributor/{id}/formsgpushinfo")
    DistributorDTO getDistributorBasicInfoByIdForMsgPushInfo(@PathVariable("id") Integer id);

    /**
     * 根据安装工ID获取安装工信息（消息推送时只需获取很少的几个字段）
     */
    @GetMapping(value = "/engineer/{id}/formsgpushinfo")
    EngineerDTO getEngineerBasicInfoByIdForMsgPushInfo(@PathVariable(value = "id") Integer id);

    /**
     * 根据用户id更新用户信息
     *
     * @param dto 用户信息
     */
    @RequestMapping(value = "/user", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    void update(@RequestBody UserDTO dto);

    /**
     * 根据等级查询配置信息
     *
     * @return
     */
    @GetMapping(value = "distributor/role")
    DistributorRoleDTO getConfigByLevel(@RequestParam(value = "level") Integer level);


    /**
     * 根据地址ID 和类型获取地址信息
     *
     * @param id   地址ID
     * @param type 1-我的地址 2-客户地址 3-企业地址
     * @return map
     */
    @GetMapping(value = "/user/address/type/{id}")
    Object getAddressByType(@PathVariable(value = "id") Integer id, @RequestParam(value = "type") Integer type);

    @PostMapping(value = "/waterdeviceuser", consumes = MediaType.APPLICATION_JSON_VALUE)
    WaterDeviceUserDTO saveOrGetWaterDeviceUserByPhone(@RequestBody WaterDeviceUserDTO dto);

    //----数据迁移用（业务不准调用）---start

    @GetMapping(value = "/distributor")
    DistributorDTO getDistributorByOldId(@RequestParam(value = "oldId") String oldId);

    @GetMapping("/waterdeviceuser/getByOldId")
    WaterDeviceUserDTO getWaterDeviceUserByOldId(@RequestParam(value = "oldId") String oldId);

    @GetMapping(value = "/user/getByPhone")
    UserDTO getUserByPhone(@RequestParam(value = "phone") String phone);

    @GetMapping("/waterdeviceuser/{id}")
    WaterDeviceUserDTO getDeviceUserDTOInfoById(@PathVariable(value = "id") Integer id);

    //----数据迁移用（业务不准调用）---end
    
    /***
     * 获取招商销售业绩
     * @param startTime
     * @param endTime
     * @return
     */
    @GetMapping(value = "/distributor/perform/data")
    List<SalePerformRankDTO> getDistributorPerformRank(@RequestParam(value = "startTime")String startTime, @RequestParam(value = "endTime")String endTime);
}
