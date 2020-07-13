package com.yimao.cloud.user.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.station.StationScheduleDTO;
import com.yimao.cloud.pojo.dto.station.UserStatisticsDTO;
import com.yimao.cloud.pojo.dto.user.UserContidionDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.dto.user.UserExportDTO;
import com.yimao.cloud.pojo.dto.user.UserIdentityDTO;
import com.yimao.cloud.pojo.query.station.StatisticsQuery;
import com.yimao.cloud.pojo.query.station.UserQuery;
import com.yimao.cloud.pojo.vo.station.UserGeneralSituationVO;
import com.yimao.cloud.pojo.vo.station.UserVO;
import com.yimao.cloud.user.po.User;
import com.yimao.cloud.user.po.UserAuths;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Zhang Bo
 * @date 2018/8/10.
 */
public interface UserMapper extends Mapper<User> {

    UserDTO selectBasicUserById(@Param(value = "userId") Integer userId);

    List<UserDTO> listUserIdByMid(@Param(value = "mid") Integer mid);

    List<Integer> selectUserIdByAmbassador(@Param(value = "userId") Integer userId);

    void changeCustomersToShareUser(Map<String, Object> queryMap);

    void changeCustomersDistributor(Map<String, Object> queryMap);

    Page<UserDTO> pageQueryUser(UserContidionDTO query);

    UserDTO getUserInfoById(@Param(value = "userId") Integer userId);

    UserDTO getAmbassInfo(@Param(value = "ambassadorId") Integer ambassadorId);

    Integer getTotalNum(@Param(value = "distributorId") Integer distributorId);

    Integer getMonthAddNum(@Param(value = "distributorId") Integer distributorId);

    Integer getYearAddNum(@Param(value = "distributorId") Integer distributorId);

    Integer updateMobile(@Param(value = "userId") Integer userId,
                         @Param(value = "mobile") String mobile,
                         @Param(value = "currentTime") Date currentTime);

    Integer updateHeadImage(@Param(value = "userId") Integer userId,
                            @Param(value = "headImg") String headImg);

    Integer updatePwd(@Param(value = "userId") Integer userId, @Param(value = "password") String password);

    Integer selectUserIdByPhone(@Param(value = "mobile") String mobile);

    /*List<UserDTO> selectCustomer(@Param(value = "distId") Integer distId,
                                 @Param(value = "province") String province,
                                 @Param(value = "city") String city,
                                 @Param(value = "region") String region,
                                 @Param(value = "type") String type);*/

    Integer countUserTotal(@Param("sign") Integer sign);

    List<UserDTO> findCustomers(@Param("id") Integer id,
                                @Param("mid") Integer mid,
                                @Param("distributorId") Integer distributorId,
                                @Param("key") String key);

    List<UserDTO> getUserByDistributor(@Param("mid") Integer mid,
                                       @Param("type") Integer type,
                                       @Param("key") String key);


    List<UserDTO> getUserByUserName(@Param("realName") String username);

    List<UserDTO> getDistributorByMid(@Param("mid") Integer mid);

    /**
     * 查询评估卡所有者的信息(包含id和openid(发送模板消息))
     *
     * @param ids 用户id集合
     * @return list
     */
    List<UserDTO> userByIds(@Param("ids") Collection ids);

    Page<UserExportDTO> userExportList(UserContidionDTO query);

    Page<UserExportDTO> userDistributorExportList(UserContidionDTO query);

    List<Integer> selectDistributorByMap(Map<String, Object> map);

    /**
     * 会员直升页面获取用户信息接口
     *
     * @param openid
     * @return
     */
    UserAuths getUserPhoneByOpenid(String openid);

    /**
     * @Author ycl
     * @Description 普通用户数量
     * @Date 9:25 2019/8/8
     * @Param
     **/
    Integer getGeneralUserCount(Map<String, Object> map);

    /**
     * @Author ycl
     * @Description 会员用户数量
     * @Date 9:32 2019/8/8
     * @Param
     **/
    Integer getVipUserCount(Map<String, Object> map);

    /**
     * @Author ycl
     * @Description 昨日新增数量
     * @Date 9:32 2019/8/8
     * @Param
     **/
    Integer getYesterdayNum(Map<String, Object> map);

    /**
     * @Author ycl
     * @Description 本月新增数量
     * @Date 9:41 2019/8/8
     * @Param
     **/
    Integer getCurrentMonthNum(Map<String, Object> map);

    Page<UserDTO> getCustomerList(@Param("userId") Integer userId,
                                  @Param("type") Integer type,
                                  @Param("mark") Integer mark,
                                  @Param("account") String account,
                                  @Param("key") String key,
                                  @Param("tag") Integer tag);

    Integer getTotalCount(Map<String, Object> map);

    Integer updateAmbassadorId(Map<String, Object> map);

    User selectForUpgradeVip(@Param("id") Integer id);

    List<UserDTO> selectByPhoneForAppLogin(@Param("mobile") String mobile);

    User selectByIdForFillIntoUser(@Param("id") Integer id);

    int updateUserByMid(User user);

    int updateUserByIdForTransfer(User user);

    int batchUpdateUserDistributor(Map<String, Object> map);

    UserIdentityDTO selectMultiIdentityByUserId(@Param("userId") Integer userId);

    Integer selectUserIdByMobile(@Param("mobile") String mobile, @Param("userId") Integer userId);

    User selectUserByUserName(@Param("userName") String userName, @Param("userId") Integer userId);

    boolean existsClient(@Param("userId") Integer userId);

    void disuse(@Param("userId") Integer userId, @Param("userAuthsId") Integer userAuthsId);

    int updateNullFieldByMidId(User user);

    int updateUserInfoById(User user);

    /**
     * 根据条件查询经销商id集合（站务系统）
     *
     * @param map
     * @return
     */
    List<Integer> selectDistributorIdByMapToStation(Map<String, Object> map);

    /**
     * 根据条件查询用户列表（站务系统）
     *
     * @param query
     * @return
     */
    Page<UserVO> pageQueryUserToStation(UserQuery query);

    UserStatisticsDTO getUserSaleData(StatisticsQuery query);

    List<UserStatisticsDTO> getUserSalePicData(StatisticsQuery query);

    StationScheduleDTO getStationGeneralUserNumAndVipUserNum(@Param("distributorIds") List<Integer> distributorIds);

    UserGeneralSituationVO getGeneralUserNumAndVipUserNumByAreaIds(@Param("distributorIds") List<Integer> distributorIds);

    /****
     * 获取当前经销商发展的所有经销商
     * @param distributorId
     * @return
     */
	List<Integer> getDistributorByDistributId(@Param("distributorId") Integer distributorId);

	 UserDTO getUserDistributorInfoById(@Param(value = "userId") Integer userId);

    String getOpenidByMobile(@Param("mobile") String mobile);
}
