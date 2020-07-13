package com.yimao.cloud.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.auth.AuthConstants;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.IdentityTypeEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.CookieUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.user.UserAliasDTO;
import com.yimao.cloud.pojo.dto.user.UserBindDTO;
import com.yimao.cloud.pojo.dto.user.UserChangeRecordListDTO;
import com.yimao.cloud.pojo.dto.user.UserContidionDTO;
import com.yimao.cloud.pojo.dto.user.UserCountDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.dto.user.UserDistributorDTO;
import com.yimao.cloud.pojo.dto.wechat.WxUserInfo;
import com.yimao.cloud.pojo.query.station.StatisticsQuery;
import com.yimao.cloud.pojo.query.station.UserQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.station.UserVO;
import com.yimao.cloud.user.mapper.UserAuthsMapper;
import com.yimao.cloud.user.mapper.UserMapper;
import com.yimao.cloud.user.po.User;
import com.yimao.cloud.user.po.UserAuths;
import com.yimao.cloud.user.service.DistributorService;
import com.yimao.cloud.user.service.UserChangeService;
import com.yimao.cloud.user.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Zhang Bo
 * @date 2018/7/22.
 */
@RestController
@Slf4j
public class UserController {

    @Resource
    private UserCache userCache;
    @Resource
    private UserService userService;
    @Resource
    private UserAuthsMapper userAuthsMapper;
    @Resource
    private DistributorService distributorService;
    @Resource
    private UserChangeService userChangeService;

    /**
     * 下单时如果是普通用户，需要设置健康大使，如果不是直接返回用户信息
     */
    @GetMapping(value = "/user/{id}/setambassador")
    public UserDTO changeUserTypeIfMeetTheConditions(@PathVariable(value = "id") Integer id) {
        UserDTO dto = userService.changeUserTypeIfMeetTheConditions(id);
        if (Objects.nonNull(dto)) {
            dto.setPassword(null);
        }
        return dto;
    }

    /**
     * 根据用户id获取用户信息
     *
     * @param id 用户ID
     */
    @GetMapping(value = "/user/{id}")
    public UserDTO get(@PathVariable(value = "id") Integer id) {
        UserDTO dto = userService.getFullUserDTOById(id);
        if (Objects.nonNull(dto)) {
            dto.setPassword(null);
        }
        return dto;
    }

    /**
     * 根据用户id获取用户部分信息
     *
     * @param id 用户ID
     */
    @GetMapping(value = "/user/{id}/single")
    public UserDTO getBasicUserById(@PathVariable(value = "id") Integer id) {
        return userService.getBasicUserById(id);
    }

    /**
     * 根据用户id获取用户openid(公众号的openid)
     *
     * @param id 用户ID
     */
    @GetMapping(value = "/user/{id}/openid")
    public String getOpenidByUserId(@PathVariable(value = "id") Integer id) {
        UserAuths query = new UserAuths();
        query.setUserId(id);
        //微信
        query.setIdentityType(IdentityTypeEnum.WECHAT_GZH.value);
        List<UserAuths> list = userAuthsMapper.select(query);
        return CollectionUtil.isEmpty(list) ? null : list.get(0).getIdentifierUnique();
    }

    /**
     * 根据用户id更新用户信息
     *
     * @param dto 用户信息
     */
    @PutMapping(value = "/user")
    public void update(@RequestBody UserDTO dto) {
        userService.updateUser(dto);
    }

    /**
     * 更新用户信息（局部）
     *
     * @param dto 用户信息
     */
    @PatchMapping(value = "/user")
    public void patchUpdate(@RequestBody UserDTO dto) {
        dto.setUpdateTime(new Date());
        User user = new User(dto);
        userService.updateUserPart(user);
    }

    /**
     * 用户关系建立
     *
     * @param identityType
     * @param sharerId
     * @param wxUserInfo
     * @param origin
     * @return
     * @throws Exception
     */
    @PutMapping(value = "/user/process")
    @ApiOperation(value = "用户关系处理（创建用户关系）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "identityType", value = "登录类型（微信公众号、小程序等）", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "sharerId", value = "分享者ID", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "wxUserInfo", value = "微信用户信息", required = true, dataType = "WxUserInfo", paramType = "body")
    })
    public UserDTO userProcess(@RequestParam Integer identityType,
                               @RequestParam(required = false) Integer sharerId,
                               @RequestBody WxUserInfo wxUserInfo,
                               @RequestParam(required = false) Integer origin) throws Exception {
        log.info("/user/process,identityType" + identityType + ",sharerId=" + sharerId + ",wxUserInfo=" + JSONObject.toJSONString(wxUserInfo) + ",origin=" + origin);
        UserDTO userDTO = userService.userProcess(identityType, sharerId, wxUserInfo, origin);
        userDTO.setPassword(null);
        return userDTO;
    }


    /**
     * 关注公众号/取关公众号- 身份变更记录
     *
     * @param userId 用户id
     * @param type   类型 1-关注公众号 -1 取关公众号
     * @param phone  手机号
     * @param time   时间
     * @param remark 备注
     */
    @PostMapping(value = "/user/subscribe")
    public void saveSubscribe(@RequestParam("userId") Integer userId,
                              @RequestParam("type") int type,
                              @RequestParam(value = "userType", required = false) Integer userType,
                              @RequestParam(value = "phone", required = false) String phone,
                              @RequestParam("time") Date time,
                              @RequestParam("remark") String remark) {

        userChangeService.save(userId, type, userType, phone, time, remark, null);
    }

    /**
     * 管理系统-分页查询用户列表
     *
     * @param pageNum
     * @param pageSize
     * @param query
     * @return
     */
    @PostMapping(value = "/user/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询用户信息", notes = "分页查询用户信息")
    public ResponseEntity<PageVO<UserDTO>> pageQueryUser(@PathVariable(value = "pageNum") Integer pageNum,
                                                         @PathVariable(value = "pageSize") Integer pageSize,
                                                         @RequestBody(required = false) UserContidionDTO query) {

        PageVO<UserDTO> page = userService.pageQueryUser(query, pageNum, pageSize);
        if (page == null) {
            throw new NotFoundException("未找到用户信息。");
        }
        return ResponseEntity.ok(page);
    }

    /**
     * 用户--根据条件查询用户列表（站务系统调用）
     *
     * @return query
     */
    @PostMapping("/user/station/list/{pageNum}/{pageSize}")
    public Object getUserInfo(@PathVariable(value = "pageNum") Integer pageNum,
                              @PathVariable(value = "pageSize") Integer pageSize,
                              @RequestBody(required = false) UserQuery query) {
        PageVO<UserVO> page = userService.pageQueryUserToStation(query, pageNum, pageSize);
        if (page == null) {
            throw new NotFoundException("未找到用户信息。");
        }
        return ResponseEntity.ok(page);
    }

    /**
     * 管理系统-用户详细信息
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/user/distributor/{id}")
    @ApiOperation(value = "根据用户ID查询用户信息", notes = "根据用户ID查询用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long", paramType = "path")
    })
    public UserDTO getUserInfo(@PathVariable(value = "id") Integer id) {
        return userService.getUserInfoById(id);
    }

    /**
     * 用户--用户列表--用户详细信息 (站务系统调用)
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/user/distributor/station/{id}")
    @ApiOperation(value = "根据用户ID查询用户信息", notes = "根据用户ID查询用户信息")
    @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long", paramType = "path")
    public Object stationGetUserInfo(@PathVariable(value = "id") Integer id) {
        UserVO userVO = userService.stationGetUserInfo(id);
        return ResponseEntity.ok(userVO);
    }


    /**
     * 管理系统-解绑手机号
     *
     * @param userId
     * @return
     */
    @PatchMapping(value = "/user/unBindPhone/{userId}")
    @ApiOperation(value = "解绑用户手机号", notes = "解绑用户手机号")
    public ResponseEntity unBindPhone(@PathVariable("userId") Integer userId) {
        userService.unBindPhone(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 管理系统-用户信息变更记录
     *
     * @param userId
     * @return
     */
    @GetMapping("/user/record/{userId}")
    @ApiOperation(value = "用户信息变更记录", notes = "用户信息变更记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "Long", paramType = "path")
    })
    public ResponseEntity<UserChangeRecordListDTO> getUserChangeRecord(@PathVariable("userId") Integer userId) {
        return ResponseEntity.ok(userService.getChangeInfoByUserId(userId));
    }

    /**
     * 用户--用户列表--用户信息变更记录 （站务系统调用）
     *
     * @param userId
     * @return
     */
    @GetMapping("/user/record/station/{userId}")
    public ResponseEntity<UserChangeRecordListDTO> stationGetUserChangeRecord(@PathVariable("userId") Integer userId) {
        return ResponseEntity.ok(userService.stationGetChangeInfoByUserId(userId));
    }

    /**
     * 获取经销商个人信息根据用户信息
     *
     * @param userId
     * @return
     */
    @GetMapping("/dist/{userId}")
    @ApiOperation(value = "经销商个人信息", notes = "经销商个人信息")
    @ApiImplicitParam(name = "userId", value = "用户e家号", required = true, dataType = "Long", paramType = "path")
    public ResponseEntity<UserDistributorDTO> getUserDistributor(@PathVariable(value = "userId") Integer userId) {
        return ResponseEntity.ok(userService.getUserDistributor(userId));
    }


    /**
     * 经销商APP-用户数量
     *
     * @param roleLevel     经销商角色
     * @param distributorId 经销商ID
     * @return
     */
    @GetMapping("/user/dist")
    @ApiOperation(value = "经销商APP-用户数量", notes = "经销商APP-用户数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleLevel", value = "经销商角色", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "distributorId", value = "经销商ID", required = true, dataType = "Long", paramType = "path")
    })
    public ResponseEntity<UserCountDTO> getUserNum(
            @RequestParam("roleLevel") Integer roleLevel,
            @RequestParam("distributorId") Integer distributorId) {
        return ResponseEntity.ok(distributorService.getUserNum(roleLevel, distributorId));
    }

    /**
     * 上传头像
     *
     * @return
     */
    @PostMapping(value = "/user/modifyheadimg")
    public UserDTO updateHeadImage(@RequestParam String headImg) {
        Integer userId = userCache.getUserId();
        return userService.updateHeadImage(userId, headImg);
    }


    /**
     * 忘记密码重置密码
     */
    @PatchMapping("/user/resetpwd")
    public void resetPwd(@RequestParam String userName, @RequestParam String password) {
        userService.updatePwd(userName, password);
    }


    /**
     * 用户退出登录
     *
     * @param userId
     * @param request
     * @return
     */
    @GetMapping("/user/logout/{userId}")
    @ApiOperation(value = "用户退出登录", notes = "用户退出登录")
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "Long", paramType = "path")
    public ResponseEntity<Integer> exit(@PathVariable("userId") Integer userId, HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(userService.exit(userId, request, response));
    }

    /**
     * @return java.util.List<com.yimao.cloud.pojo.dto.user.UserDTO>
     * @Author lizhiqiang
     * @Date 2019/3/22
     * @Param [ids]
     */
    @GetMapping(value = "/user/image")
    @ApiOperation(value = "获取经销商头像集合", notes = "获取经销商头像集合")
    @ApiImplicitParam(name = "ids", value = "经销商id", required = true, dataType = "Long", paramType = "query")
    public Object getDistributorImageById(@RequestParam("ids") List<Integer> ids) {
        List<String> distributorImageById = userService.getDistributorImageById(ids);
        return distributorImageById;
    }

    /**
     * 新：根据用户id：生成分享二维码
     *
     * @param ids
     * @return
     */
    @GetMapping(value = {"/user/share/{id}/qrcode"})
    @ApiOperation(value = "生成分享二维码", notes = "生成分享二维码")
    @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "Long", paramType = "path")
    public Object buildShareQRCode(@PathVariable("id") Integer id) throws UnsupportedEncodingException {
        String qrCode = userService.getQRCodeWithParam(id, 0, "", 0L);
        if (StringUtil.isEmpty(qrCode)) {
            throw new BadRequestException("生成二维码失败！");
        }
        String qrcode = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + URLEncoder.encode(qrCode, "UTF-8");
        return ResponseEntity.ok(qrcode);
    }


    /**
     * （公众号）绑定经销商账户
     *
     * @param userBindDTO 绑定信息
     * @return org.springframework.http.ResponseEntity<com.yimao.cloud.pojo.dto.user.UserDTO>
     * @author hhf
     * @date 2019/4/16
     */
    @PostMapping(value = "/user/bind/distributor")
    @ApiOperation(value = "绑定经销商账户", notes = "绑定经销商账户")
    @ApiImplicitParam(name = "userBindDTO", value = "用户信息", required = true, dataType = "UserBindDTO", paramType = "body")
    public UserDTO bindDistributor(@RequestBody UserBindDTO userBindDTO,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        UserDTO userDTO = userService.bindDistributor(userBindDTO);
        CookieUtil.setCookie(request, response, AuthConstants.USER_TOKEN, userDTO.getToken(), 2592000, true);
        return userDTO;
    }

    /**
     * （公众号）绑定手机号
     *
     * @param userBindDTO 绑定信息
     * @return org.springframework.http.ResponseEntity<com.yimao.cloud.pojo.dto.user.UserDTO>
     * @author hhf
     * @date 2019/4/17
     */
    @PostMapping(value = "/user/bind/phone")
    @ApiOperation(value = "绑定手机号", notes = "绑定手机号")
    @ApiImplicitParam(name = "userBindDTO", value = "用户信息", required = true, dataType = "UserBindDTO", paramType = "body")
    public UserDTO bindPhone(@RequestBody UserBindDTO userBindDTO) {
        return userService.bindPhone(userBindDTO);
    }


    /**
     * 根据openId,获取用户信息
     *
     * @param openid openid
     * @return int
     */
    @RequestMapping(value = "/user/unique/{openid}", method = RequestMethod.GET)
    @ApiOperation(value = "根据openId,获取用户信息", notes = "根据openId,获取用户信息")
    @ApiImplicitParam(name = "openid", value = "openid", required = true, dataType = "String", paramType = "path")
    public ResponseEntity getUserIdByOpenid(@PathVariable("openid") String openid) {
        return ResponseEntity.ok(userService.getUserIdByOpenid(openid));
    }

    /**
     * （公众号）获取我的推广客户列表（企业版主账号）
     *
     * @param userId 用户ID
     * @return ResponseEntity
     * @author hhf
     * @date 2019/4/22
     */
    @GetMapping(value = "user/my/customers/company")
    @ApiOperation(value = "获取我的推广客户列表（企业版主账号）", notes = "获取我的推广客户列表（企业版主账号）")
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "String", paramType = "query")
    public List<UserDTO> myCustomersCompany(@RequestParam Integer userId) {
        UserDTO user = userService.getBasicUserById(userId);
        if (user == null) {
            throw new NotFoundException("查询失败，用户不存在。");
        }
        return userService.findShareCustomers(user, true);
    }

    /**
     * （公众号）获取我的推广客户列表
     *
     * @param userId 用户ID
     * @return Map
     * @author hhf
     * @date 2019/4/22
     */
    @GetMapping(value = "/user/my/customers")
    @ApiOperation(value = "获取我的推广客户列表", notes = "获取我的推广客户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "distributorId", value = "经销商ID", dataType = "Long", paramType = "query")
    })
    public Map<String, Object> myCustomers(@RequestParam(required = false) Integer userId, @RequestParam(required = false) Integer distributorId) {
        return userService.myCustomers(userId, distributorId);
    }

    /**
     * （公众号）获取我的推广客户数量
     *
     * @param userId 用户ID
     * @return Map
     * @author hhf
     * @date 2019/4/24
     */
    @GetMapping(value = "/user/my/customers/count")
    @ApiOperation(value = "获取我的推广客户数量", notes = "获取我的推广客户数量")
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "String", paramType = "query")
    public Integer myCustomersCount(@RequestParam Integer userId) {
        return userService.myCustomersCount(userId);
    }

    /**
     * 修改用户星级
     *
     * @param userAliasDTO 用户星级信息
     * @return void
     * @author hhf
     * @date 2019/4/24
     */
    @PostMapping(value = "/user/asterisk")
    @ApiOperation(value = "修改用户星级", notes = "修改用户星级")
    @ApiImplicitParam(name = "userAliasDTO", value = "用户ID", required = true, dataType = "UserAliasDTO", paramType = "body")
    public void asterisk(@RequestBody UserAliasDTO userAliasDTO) {
        userService.asterisk(userAliasDTO);
    }

    /**
     * @param username
     * @Description: 根据用户名模糊查询用户
     * @author ycl
     * @Return: java.lang.String
     * @Create: 2019/4/23 9:55
     */
    @GetMapping(value = "/user")
    public List<UserDTO> getUserByUserName(@RequestParam String username) {
        return userService.getUserByUserName(username);
    }


    /**
     * @param id
     * @param mid
     * @param distributorId
     * @Description: 获取我的客户
     * @author ycl
     * @Return: java.util.List<com.yimao.cloud.pojo.dto.user.UserDTO>
     * @Create: 2019/4/23 17:58
     */
    @GetMapping(value = "/user/customers")
    @ApiOperation(value = "获取我的客户列表")
    public List<UserDTO> findCustomers(@RequestParam Integer id,
                                       @RequestParam(required = false) Integer mid,
                                       @RequestParam(required = false) Integer distributorId) {
        return userService.findCustomers(id, mid, distributorId);
    }


    /**
     * 根据主账号ID集合获取企业版子账号的用户信息
     *
     * @param mid 主账号ID
     * @return List
     * @author hhf
     * @date 2019/4/26
     */
    @GetMapping(value = "/user/child")
    @ApiOperation(value = "根据经销商ID集合获取经销商信息", notes = "根据经销商ID集合获取经销商信息")
    @ApiImplicitParam(name = "mid", value = "主账号ID", dataType = "Integer", paramType = "query")
    public List<UserDTO> getDistributorByMid(@RequestParam Integer mid) {
        return userService.getDistributorByMid(mid);
    }

    /**
     * 我的健康大使
     *
     * @param userId 用户ID
     * @return com.yimao.cloud.pojo.dto.user.UserDTO
     * @author hhf
     * @date 2019/4/28
     */
    @GetMapping(value = "/user/ambassador")
    @ApiOperation(value = "我的健康大使", notes = "我的健康大使")
    @ApiImplicitParam(name = "userId", value = "健康大使", dataType = "Integer", paramType = "query")
    public UserDTO ambassador(@RequestParam Integer userId) {
        return userService.getAmbassador(userId);
    }

    // /**
    //  * 订单支付完成后执行的升级用户身份逻辑
    //  *
    //  * @param userDTO
    //  */
    // @PostMapping(value = "/user/upgrade/sale")
    // @ApiOperation(value = "升级为会员用户")
    // @ApiImplicitParam(name = "userDTO", value = "用户信息", dataType = "UserDTO", paramType = "body")
    // public void upgradeToSaleUser(@RequestBody UserDTO userDTO) {
    //     User user = new User(userDTO);
    //     user.setBeSalesTime(new Date());
    //     user.setUpdateTime(new Date());
    //     userService.upgradeToSaleUser(user);
    // }

    /**
     * 查询评估卡所有者的信息(包含id和openid(发送模板消息))
     *
     * @param userIds
     * @return
     */
    @GetMapping(value = "/user/ids")
    public List<UserDTO> userByIds(@RequestParam("userIds") Set<Integer> userIds) {
        return userService.userByIds(userIds);
    }


    // @GetMapping(value = "/user/upgrade/sale")
    // @ApiOperation(value = "升级为会员用户")
    // public void updateUserIncomePermission(@RequestParam("ids") List<Integer> ids) {
    //     userService.updateUserIncomePermission(ids);
    // }

    @GetMapping(value = "/user/usertype")
    @ApiOperation(value = "通过用户身份查询用户")
    public List<Integer> getUserByUserType(@RequestParam(value = "userType", required = false) Integer userType) {
        return userService.getUserByUserType(userType);
    }

    /**
     * 根据用户ID获取分销用户（收益分配调用）
     *
     * @param id 用户e家号
     * @return com.yimao.cloud.pojo.dto.user.UserDTO
     * @author hhf
     * @date 2019/5/18
     */
    @GetMapping(value = "/user/{id}/sale")
    public UserDTO getMySaleUserById(@PathVariable Integer id) {
        return userService.getMySaleUserById(id);
    }

    //根据分销商ID查询用户
    @GetMapping(value = "/user/ambassador/ids")
    public List<Integer> findUserByAmbassadorId(@RequestParam(value = "ambassadorId") Integer ambassadorId) {
        return userService.findUserByAmbassadorId(ambassadorId);
    }

    //根据经销商e家号查询
    @GetMapping(value = "/user/distributor/ids")
    public List<Integer> getDistributorByUserId(@RequestParam(value = "distributorId") Integer distributorId) {
        return userService.getDistributorByUserId(distributorId);
    }

    /**
     * 经销商APP-修改密码
     */
    @PatchMapping(value = "/user/modifypwd")
    public void updatePassword(@RequestBody UserDTO user) {
        userService.modifyPassword(user);
    }

    @GetMapping(value = "/userAuths/{unionid}")
    public List<Integer> queryUserAuthsByUnionid(@PathVariable(value = "unionid") String unionid) {
        return userService.queryUserAuthsByUnionid(unionid);
    }


    @GetMapping(value = "/user/openid")
    public UserDTO getUserDtoByOpenid(@RequestParam(value = "identityType") Integer identityType, @RequestParam(value = "identifierUnique") String identifierUnique) {
        return userService.getUserDtoByOpenid(identityType, identifierUnique);
    }

    /**
     * @Author ycl
     * @Description 我的销售--我的客户
     * @Date 9:24 2019/8/8
     * @Param
     **/
    @GetMapping(value = "/user/sale/my/customer")
    public Object queryMyCustomer(@RequestParam(value = "userId") Integer userId,
                                  @RequestParam(value = "type", required = false) Integer type,
                                  @RequestParam(value = "queryType", required = false) Integer queryType,
                                  @RequestParam(value = "subDistributorId", required = false) Integer subDistributorId) {
        return userService.queryMyCustomer(userId, type, queryType, subDistributorId);
    }


    /**
     * @Author ycl
     * @Description 我的销售--我的客户--客户列表
     * @Date 9:45 2019/8/8
     * @Param
     **/
    @GetMapping(value = "/user/sale/list/customer")
    public Object getCustomerList(@RequestParam(value = "userId") Integer userId,
                                  @RequestParam(value = "key", required = false) String key) {
        return userService.getCustomerList(userId, key);
    }

    /**
     * @Author ycl
     * @Description 客户信息
     * @Date 13:27 2019/11/19
     * @Param
     **/
    @GetMapping(value = "/user/sale/customer/{id}")
    public UserDTO getCustomerDetailInfo(@PathVariable(value = "id") Integer id) {
        return userService.getCustomerDetailInfo(id);
    }


    /**
     * @Author ycl
     * @Description 管理系统--解绑微信
     * @Date 13:23 2019/8/12
     * @Parfam
     **/
    @GetMapping(value = "/user/bind/wechat")
    public Object unBindWechat(@RequestParam(value = "userId") Integer userId) {
        return userService.unBindWechat(userId);
    }


    /**
     * @Author ycl
     * @Description 管理系统--更换健康大使
     * @Date 15:58 2019/8/12
     * @Param
     **/
    @GetMapping(value = "/user/change/ambassador")
    public void changeAmbassador(@RequestParam(value = "userId") Integer userId,
                                 @RequestParam(value = "ambassadorId") Integer ambassadorId) {
        userService.changeAmbassador(userId, ambassadorId);
    }


    /**
     * @Author ycl
     * @Description 我的代理-经营报表
     * @Date 16:44 2019/8/29
     * @Param
     **/
    @GetMapping(value = "/user/business/newspaper/{id}")
    public Object getBusinessNewspaper(@PathVariable(value = "id") Integer id) {
        return userService.getBusinessNewspaper(id);
    }


    /**
     * @Author ycl
     * @Description 企业版经销商查看企业信息
     * @Date 9:53 2019/11/8
     * @Param
     **/
    @GetMapping(value = "/user/company/info/{id}")
    public Object queryCompanyInfoById(@PathVariable(value = "id") Integer id) {
        return userService.queryCompanyInfoById(id);
    }


    @Resource
    private UserMapper userMapper;

    /**
     * 数据迁移用（业务不准调用）
     */
    @GetMapping(value = "/user/getByPhone")
    public UserDTO getUserByPhone(@RequestParam(value = "phone") String phone) {
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("mobile", phone);
        example.orderBy("createTime").asc();
        List<User> userList = userMapper.selectByExample(example);
        if (CollectionUtil.isNotEmpty(userList)) {
            User user = userList.get(0);
            UserDTO dto = new UserDTO();
            user.convert(dto);
            return dto;
        }
        return null;
    }

    /**
     * 站务系统-统计-用户统计（站务系统调用）
     *
     * @param query
     * @return
     */
    @GetMapping(value = "/user/station/userStatistics")
    public Object getUserStatisticsInfoToStation(@RequestBody StatisticsQuery query) {

        return ResponseEntity.ok(userService.getUserStatisticsInfoToStation(query));
    }

    /**
     * 站务系统-用户-用户统计（站务系统调用）
     *
     * @param areas
     * @return
     */
    @GetMapping("/user/station/generalSituation")
    public Object getUserGeneralSituation(@RequestParam("areas") Set<Integer> areas) {

        return ResponseEntity.ok(userService.getUserGeneralSituation(areas));
    }

    /**
     * 站务系统-统计-用户统计--获取普通用户，会员用户数量（站务系统调用）
     *
     * @param query
     * @return
     */
    @GetMapping(value = "/user/station/getUserRes")
    public Object getUserRes(@RequestBody StatisticsQuery query) {
        return ResponseEntity.ok(userService.getUserRes(query));
    }

    /****
     * 获取经销商发展的所有经销商信息
     * @return
     */
    @GetMapping(value = "/user/{id}/getDistributors")
    public List<Integer> getDistributorsBySuperDistributId(@PathVariable(value ="id")Integer id){
    	return userMapper.getDistributorByDistributId(id);
    }

    /**
     * 根据用户id获取用户信息包含(经销商区代信息)
     *
     * @param id 用户ID
     */
    @GetMapping(value = "/user/{id}/info")
    public UserDTO getUserDistributorInfoById(@PathVariable(value = "id") Integer id) {
        return userMapper.getUserDistributorInfoById(id);
    }

    /**
     * 用户授权后：根据授权用户的手机号，查询openid
     *
     * @param mobile
     * @return
     */
    @GetMapping(value = "/h5/mobile/openid")
    public String getOpenidByMobile(@RequestParam(value = "mobile") String mobile) {
        return userService.getOpenidByMobile(mobile);
    }
}
