package com.yimao.cloud.out.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.dto.user.RedAccountDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.dto.user.WaterDeviceUserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 描述：用户微服务的接口列表
 *
 * @author Zhang Bo
 * @date 2019/3/8.
 */
@FeignClient(name = Constant.MICROSERVICE_USER)
public interface UserFeign {

    /**
     * 根据用户ID获取用户信息
     *
     * @param id 用户ID
     */
    @GetMapping(value = "/user/{id}")
    UserDTO getUserById(@PathVariable(value = "id") Integer id);

    /**
     * 根据安装工ID获取安装工信息
     *
     * @param id 安装工ID
     */
    @RequestMapping(value = "/engineer/{id}", method = RequestMethod.GET)
    EngineerDTO getEngineerById(@PathVariable(value = "id") Integer id);

    /**
     * 安装工登录
     *
     * @param userName 用户名
     * @param password 密码
     */
    @RequestMapping(value = "/engineer/login", method = RequestMethod.GET)
    EngineerDTO engineerLogin(@RequestParam(value = "userName") String userName,
                              @RequestParam(value = "password") String password,
                              @RequestParam(value = "appType") Integer appType);

    /**
     * 根据用户名查询安装工
     *
     * @param userName 用户名
     */
    @RequestMapping(value = "/engineer", method = RequestMethod.GET)
    EngineerDTO getEngineerByUserName(@RequestParam(value = "userName") String userName, @RequestParam(value = "oldId") String oldId);

    /**
     * 根据手机号查询安装工
     *
     * @param phone 安装工手机号
     */
    @GetMapping(value = "/engineer/phone")
    EngineerDTO getEngineerByPhone(@RequestParam(value = "phone") String phone);

    /**
     * 检查iccid是否已经存在
     *
     * @param iccid SIM卡卡号
     */
    @RequestMapping(value = "/engineer/check/iccid", method = RequestMethod.GET)
    Boolean checkEngineerIccid(@RequestParam(value = "iccid") String iccid);

    /**
     * 安装工登录时更新部分属性
     *
     * @param update 安装工需要更新的属性
     */
    @RequestMapping(value = "/engineer", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateEngineer(@RequestBody EngineerDTO update);

    /**
     * 安装工改密码
     */
    @PatchMapping(value = "/engineer/password", consumes = MediaType.APPLICATION_JSON_VALUE)
    void updatePassword(@RequestBody EngineerDTO dto);

    /**
     * 根据经销商ID查询经销商详情
     *
     * @param id 经销商Id
     */
    @RequestMapping(value = "/distributor/{id}", method = RequestMethod.GET)
    DistributorDTO getDistributorById(@PathVariable("id") Integer id);

    /**
     * 根据经销商ID查询经销商信息（消息推送时只需获取很少的几个字段）
     */
    @GetMapping(value = "/distributor/{id}/formsgpushinfo")
    DistributorDTO getDistributorBasicInfoByIdForMsgPushInfo(@PathVariable("id") Integer id);

    /**
     * 根据经销商账号查询经销商
     *
     * @param userName 经销商账号
     */
    @GetMapping(value = "/distributor/account")
    DistributorDTO getDistributorByUserName(@RequestParam("userName") String userName);

    /**
     * 根据用户id更新用户信息
     *
     * @param dto 用户信息
     */
    @RequestMapping(value = "/user", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    void update(@RequestBody UserDTO dto);

    // /**
    //  * 根据参数获取经销商id
    //  *
    //  * @param distributorType    经销商类型
    //  * @param distributorAccount 经销商账号
    //  * @param distributorName    经销商名称
    //  * @param province           省
    //  * @param city               市
    //  * @param region             区
    //  * @param recommendName      推荐人姓名
    //  * @param recommendAccount   推荐人账号
    //  * @return org.springframework.http.ResponseEntity
    //  * @author hhf
    //  * @date 2019/1/30
    //  */
    // @GetMapping(value = "/distributor/ids")
    // List<Integer> getDistributorIdByParam(@RequestParam(value = "userId", required = false) Integer userId,
    //                                       @RequestParam(value = "distributorType", required = false) Integer distributorType,
    //                                       @RequestParam(value = "distributorAccount", required = false) String distributorAccount,
    //                                       @RequestParam(value = "distributorName", required = false) String distributorName,
    //                                       @RequestParam(value = "province", required = false) String province,
    //                                       @RequestParam(value = "city", required = false) String city,
    //                                       @RequestParam(value = "region", required = false) String region,
    //                                       @RequestParam(value = "recommendName", required = false) String recommendName,
    //                                       @RequestParam(value = "recommendAccount", required = false) String recommendAccount);

    /**
     * 根据老的安装工ID获取安装工信息
     *
     * @param oldId 老的安装工ID
     */
    @RequestMapping(value = "/engineer/old/{oldId}", method = RequestMethod.GET)
    EngineerDTO getEngineerByOldId(@PathVariable(value = "oldId") String oldId);

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
     * 云签回调，修改合同签署状态
     *
     * @param info
     */
    @PostMapping("distributor/protocol/backCall")
    Void backCall(@RequestParam("info") String info);

    @RequestMapping(value = "/engineer/{id}/unbind", method = RequestMethod.PATCH)
    void unbindIccid(@PathVariable("id") String id,
                     @RequestParam("type") Integer type);

    /**
     * 根据省市区查询安装工列表
     *
     * @param province 省
     * @param city     市
     * @param region   区
     */
	/*
	 * @GetMapping(value = "/engineers") List<EngineerDTO>
	 * listEngineerByPCR(@RequestParam(value = "province", required = false) String
	 * province,
	 * 
	 * @RequestParam(value = "city", required = false) String city,
	 * 
	 * @RequestParam(value = "region", required = false) String region);
	 */
    
    @GetMapping(value = "/engineers/area")
    public List<EngineerDTO> listEngineerByRegion(@RequestParam(value = "areaId") Integer areaId);

    @GetMapping("/waterdeviceuser/{id}")
    WaterDeviceUserDTO getWaterDeviceUserById(@PathVariable(value = "id") Integer id);

    @RequestMapping(value = "/waterdeviceuser/phone", method = RequestMethod.GET)
    WaterDeviceUserDTO getWaterDeviceUserByPhone(@RequestParam(value = "phone") String phone);
    /**工单解决措施------------------end--------------------------*/

    /**
     * 更改水机用户信息
     */
    @PatchMapping(value = "waterdeviceuser", consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateDeviceUserInfo(@RequestBody WaterDeviceUserDTO deviceUserDTO);

}
