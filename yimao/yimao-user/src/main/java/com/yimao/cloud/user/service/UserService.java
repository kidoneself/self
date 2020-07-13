package com.yimao.cloud.user.service;

import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.pojo.dto.station.UserStatisticsDTO;
import com.yimao.cloud.pojo.dto.user.*;
import com.yimao.cloud.pojo.dto.wechat.WxUserInfo;
import com.yimao.cloud.pojo.query.station.StatisticsQuery;
import com.yimao.cloud.pojo.query.station.UserQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.station.UserGeneralSituationVO;
import com.yimao.cloud.pojo.vo.station.UserVO;
import com.yimao.cloud.user.po.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserService {

    /**
     * openid登录
     *
     * @param openid openid
     */
    UserDTO loginByOpenid(String openid, Integer system);

    /**
     * 为了提高效率，部分场景只需要获取用户基础信息
     *
     * @param id 用户ID
     * @return
     */
    UserDTO getBasicUserById(Integer id);

    /**
     * 获取用户信息，带健康大使和经销商信息
     *
     * @param userId 用户ID
     * @return
     */
    UserDTO getFullUserDTOById(Integer userId);

    /**
     * 获取用户信息，带健康大使和经销商信息
     *
     * @param user 用户信息
     * @return
     */
    UserDTO getFullUserDTOById(User user);

    /**
     * 更新用户信息
     *
     * @param userDTO
     */
    void updateUser(UserDTO userDTO);

    void updateUserPart(User user);

    /**
     * 用户核心处理逻辑（创建账号、设置健康大使、设置经销商等）
     *
     * @param identityType
     * @param sharerId
     * @param wxUserInfo
     * @param origin
     * @return
     * @throws Exception
     */
    UserDTO userProcess(Integer identityType, Integer sharerId, WxUserInfo wxUserInfo, Integer origin) throws Exception;


    PageVO<UserDTO> pageQueryUser(UserContidionDTO query, Integer pageNum, Integer pageSize);

    /**
     * 解绑手机号
     *
     * @param userId 用户id
     * @return
     */
    Integer unBindPhone(Integer userId);

    /**
     * 修改头像
     */
    UserDTO updateHeadImage(Integer userId, String headImg);

    /**
     * 忘记密码重置密码
     */
    void updatePwd(String userName, String password);

    /**
     * 用户退出登录
     *
     * @param userId
     * @param request
     * @return
     */
    Integer exit(Integer userId, HttpServletRequest request, HttpServletResponse response);

    /**
     * 获取经销商个人信息
     *
     * @param userId
     * @return
     */
    UserDistributorDTO getUserDistributor(Integer userId);

    /**
     * 绑定手机号
     *
     * @param bindDTO
     */
    UserDTO bindPhone(UserBindDTO bindDTO);

    List<String> getDistributorImageById(List<Integer> ids);

    /**
     * 微信二维码生成
     *
     * @param userId
     * @param shareType
     * @param shareNo
     * @param dateTime
     * @return
     */
    String getQRCodeWithParam(Integer userId, Integer shareType, String shareNo, Long dateTime);

    /**
     * 绑定经销商账户
     *
     * @param userBindDTO
     * @return java.lang.Object
     * @author hhf
     * @date 2019/4/16
     */
    UserDTO bindDistributor(UserBindDTO userBindDTO);


    /**
     * 根据openid获取用户Id
     *
     * @param openid openid
     * @return 用户id
     */
    Integer getUserIdByOpenid(String openid);

    /**
     * 获取我的推广客户列表（企业版主账号）
     *
     * @param user 用户ID
     * @return java.util.List<com.yimao.cloud.pojo.dto.user.UserDTO>
     * @author hhf
     * @date 2019/4/19
     */
    List<UserDTO> findShareCustomers(UserDTO user, boolean isMaster);

    /**
     * 获取我的推广客户列表
     *
     * @param userId 用户ID
     * @return java.util.Map<java.lang.String ,   java.lang.Object>
     * @author hhf
     * @date 2019/4/22
     */
    Map<String, Object> myCustomers(Integer userId, Integer distributorId);


    /**
     * 根据用户名模糊查询用户
     *
     * @param username 用户名
     * @return com.yimao.cloud.pojo.dto.user.UserDTO
     * @author ycl
     * @date 2019/4/23 10:11
     */
    List<UserDTO> getUserByUserName(String username);


    /**
     * 查询我的客户
     *
     * @param id
     * @param mid
     * @param distributorId
     * @return java.util.List<com.yimao.cloud.pojo.dto.user.UserDTO>
     * @author ycl
     * @date 2019/4/24 8:56
     */
    List<UserDTO> findCustomers(Integer id, Integer mid, Integer distributorId);

    /**
     * 获取我的推广客户数量
     *
     * @param userId 用户ID
     * @return java.lang.Integer
     * @author hhf
     * @date 2019/4/24
     */
    Integer myCustomersCount(Integer userId);

    /**
     * 修改用户星级
     *
     * @param userAliasDTO 用户星级信息
     * @return void
     * @author hhf
     * @date 2019/4/24
     */
    void asterisk(UserAliasDTO userAliasDTO);

    /**
     * 根据主账号ID集合获取企业版子账号
     *
     * @param mid 主账号ID
     * @return java.util.List<com.yimao.cloud.pojo.dto.user.DistributorDTO>
     * @author hhf
     * @date 2019/4/25
     */
    List<UserDTO> getDistributorByMid(Integer mid);

    /**
     * 我的健康大使
     *
     * @author hhf
     * @date 2019/4/28
     */
    UserDTO getAmbassador(Integer userId);

    /**
     * 升级为会员用户(分销)
     *
     * @param user
     */
    void upgradeToSaleUser(User user);

    /**
     * 查询评估卡所有者的信息(包含id和openid(发送模板消息))
     *
     * @param userIds id集合
     * @return list
     */
    List<UserDTO> userByIds(Set<Integer> userIds);

    List<Integer> getUserByUserType(Integer userType);

    /**
     * 根据用户ID获取分销用户
     *
     * @author hhf
     * @date 2019/5/18
     */
    UserDTO getMySaleUserById(Integer id);

    //根据分销商ID查询用户
    List<Integer> findUserByAmbassadorId(Integer ambassadorId);

    List<Integer> getDistributorByUserId(Integer distributorId);

    //用户详情信息
    UserDTO getUserInfoById(Integer id);

    void modifyPassword(UserDTO user);

    List<Integer> queryUserAuthsByUnionid(String unionid);

    UserDTO getUserDtoByOpenid(Integer identityType, String identifierUnique);

    Object queryMyCustomer(Integer userId, Integer type, Integer queryType, Integer subDistributorId);

    Object getCustomerList(Integer userId, String key);

    Object unBindWechat(Integer userId);

    void changeAmbassador(Integer userId, Integer ambassadorId);

    Object getBusinessNewspaper(Integer id);

    UserDTO appLoginByUsername(String username, String password, Integer system, Integer appType);

    UserChangeRecordListDTO getChangeInfoByUserId(Integer userId);

    CommResult<Map<String, Object>> appLoginByMobile(Integer userId, String mobile, Integer sharerId, Integer system, Integer appType);

    CommResult<UserDTO> appLoginByMobileSelectAccount(String mobile, String key, Integer userId, Integer system, Integer appType);

    CommResult<Map<String, Object>> appLoginByWechat(String code, Integer system, Integer appType);

    UserDTO changeUserTypeIfMeetTheConditions(Integer id);

    Object queryCompanyInfoById(Integer id);

    UserDTO getCustomerDetailInfo(Integer id);

    /**
     * 查询用户列表（站务系统）
     *
     * @param query
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageVO<UserVO> pageQueryUserToStation(UserQuery query, Integer pageNum, Integer pageSize);

    /**
     * 根据用户id查询用户详情信息（站务系统）
     *
     * @param id
     * @return
     */
    UserVO stationGetUserInfo(Integer id);

    UserChangeRecordListDTO stationGetChangeInfoByUserId(Integer userId);

    UserStatisticsDTO getUserStatisticsInfoToStation(StatisticsQuery query);

    UserGeneralSituationVO getUserGeneralSituation(Set<Integer> areas);

    UserStatisticsDTO getUserRes(StatisticsQuery query);

    String getOpenidByMobile(String mobile);

    /**
     * 根据系统终端+用户ID进行登录
     *
     * @param userId     用户id
     * @param systemType 终端
     * @return
     */
    UserDTO loginBySystemType(Integer userId, Integer systemType);
}
