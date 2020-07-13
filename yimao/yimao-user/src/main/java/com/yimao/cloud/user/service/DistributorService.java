package com.yimao.cloud.user.service;

import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.pojo.dto.user.DistributorCountDTO;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import com.yimao.cloud.pojo.dto.user.DistributorExportDTO;
import com.yimao.cloud.pojo.dto.user.DistributorQueryDTO;
import com.yimao.cloud.pojo.dto.user.OwnerDistributorDTO;
import com.yimao.cloud.pojo.dto.user.SubDistributorAccountDTO;
import com.yimao.cloud.pojo.dto.user.TransferDistributorDTO;
import com.yimao.cloud.pojo.dto.user.UserAccountDTO;
import com.yimao.cloud.pojo.dto.user.UserChangeRecordListDTO;
import com.yimao.cloud.pojo.dto.user.UserCompanyApplyDTO;
import com.yimao.cloud.pojo.dto.user.UserCountDTO;
import com.yimao.cloud.pojo.query.station.DistributorQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.station.DistributorVO;
import com.yimao.cloud.user.po.Distributor;
import com.yimao.cloud.user.po.DistributorOrder;
import com.yimao.cloud.user.po.UserDistributorApply;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DistributorService {

    /**
     * 保存经销商信息
     *
     * @param distributor         经销商信息
     * @param createAccountType   经销商账号创建方式：1-系统自动生成；2-自定义创建；
     * @param chooseRecommendType 选择推荐人方式：1-系统自动分配；2-手动选择；3-设置自己为推荐人；
     * @param hasRecommend        是否有推荐人
     */
    void save(DistributorDTO distributor);

    /**
     * 获取经销商信息，带相关属性
     *
     * @param distributorId 经销商ID
     */
    DistributorDTO getFullDistributorDTOById(Integer distributorId);

    /**
     * 获取经销商信息，带相关属性
     *
     * @param distributor 经销商信息
     */
    DistributorDTO getFullDistributorDTOById(Distributor distributor);

    /**
     * 分页查询经销商信息
     *
     * @author hhf
     * @date 2018/12/18
     */
    PageVO<DistributorDTO> pageQueryDistributor(DistributorQueryDTO query, Integer pageNum, Integer pageSize);

    /**
     * 删除经销商
     *
     * @param id
     */
    void delete(Integer id);

    /**
     * 禁用/启用经销商
     *
     * @param id 经销商ID
     */
    void forbidden(Integer id);

    /**
     * 禁用/启用经销商下单
     *
     * @param id 经销商ID
     */
    void forbiddenOrder(Integer id);

    /**
     * 修改经销商信息
     *
     * @param distributor         经销商信息
     * @param chooseRecommendType 选择推荐人方式：1-系统自动分配；2-手动选择；3-设置自己为推荐人；
     * @param hasRecommend
     */
    void update(DistributorDTO dto);

    /**
     * 根据经销商ID查询经销商（单表信息）
     *
     * @param id 经销商ID
     */
    DistributorDTO getBasicInfoById(Integer id);

    /**
     * 根据经销商ID查询经销商（消息推送时只需获取很少的几个字段）
     *
     * @param id 经销商ID
     */
    DistributorDTO getBasicInfoByIdForMsgPushInfo(Integer id);

    /**
     * 根据经销商ID查询经销商信息（返回基本信息+扩展信息）
     *
     * @param id 经销商ID
     */
    DistributorDTO getExpansionInfoById(Integer id);

    /**
     * 用户统计
     *
     * @param roleLevel
     * @param distributorId
     * @return
     */
    UserCountDTO getUserNum(Integer roleLevel, Integer distributorId);

    /**
     * 经销商转让
     *
     * @param id          经销商ID
     * @param transferDTO 变更受理人信息
     */
    void transferDistributor(Integer id, TransferDistributorDTO transferDTO);

    /**
     * 增加配额
     *
     * @return
     * @author lizhiqiang
     * @date 2019/1/2
     */
    void updateDistributorQuota(Integer distributorId, Integer quota, Long orderId);

    /**
     * 验证经销商账号是否存在
     *
     * @param account
     * @return
     * @author hhf
     * @date 2019/1/7
     */
    Boolean existDistributorAccount(String account);

    /**
     * 根据经销商账号统计经销商信息
     *
     * @param id 经销商ID
     * @return DistributorCountDTO
     * @author hhf
     * @date 2019/1/21
     */
    DistributorCountDTO countDistributorById(Integer id);

    /**
     * 根据地址信息（省市区）获取发起人信息
     *
     * @param province 省
     * @param city     市
     * @param region   区
     * @return com.yimao.cloud.pojo.dto.user.DistributorDTO
     * @author hhf
     * @date 2019/1/24
     */
    DistributorDTO getOriginatorByAddress(String province, String city, String region);

    /**
     * 根据经销商ID获取推荐人信息
     *
     * @param distributorId 经销商ID
     * @return com.yimao.cloud.pojo.dto.user.DistributorDTO
     * @author hhf
     * @date 2019/1/24
     */
    DistributorDTO getRecommendByDistributorId(Integer distributorId);

    /**
     * 根据经销商ID获取经销商主账号信息
     *
     * @param distributorId 经销商ID
     * @return com.yimao.cloud.pojo.dto.user.DistributorDTO
     * @author hhf
     * @date 2019/1/24
     */
    DistributorDTO getMainAccountByDistributorId(Integer distributorId);

    /**
     * 根据身份证验证经销商是否已经注册
     *
     * @param idCard
     * @return java.lang.Boolean
     * @author hhf
     * @date 2019/1/26
     */
    Boolean existDistributorByIdCard(String idCard);

    /**
     * 根据参数获取经销商id
     *
     * @param userId
     * @param distributorType    经销商类型
     * @param distributorAccount 经销商账号
     * @param distributorName    经销商名称
     * @param province           省
     * @param city               市
     * @param region             区
     * @param recommendName      推荐人姓名
     * @param recommendAccount   推荐人账号
     * @return List
     * @author hhf
     * @date 2019/1/30
     */
    List<Integer> getDistributorIdByParam(Integer userId, Integer distributorType, String distributorAccount, String distributorName, String province, String city, String region, String recommendName, String recommendAccount);

    /**
     * 根据经销商ID集合获取经销商信息
     *
     * @param distributorIds 经销商Ids
     * @return java.util.List<com.yimao.cloud.pojo.dto.user.DistributorDTO>
     * @author hhf
     * @date 2019/2/21
     */
    List<DistributorDTO> getDistributorByDistributorIds(List<Integer> distributorIds);

    /**
     * 验证代理排名的值是否存在
     *
     * @param agentLevel
     * @param ranking
     * @return java.lang.Boolean
     * @author hhf
     * @date 2019/4/3
     */
    Boolean checkAgentRanking(Integer agentLevel, Integer ranking);

    /**
     * 根据省市区获取推荐人信息
     *
     * @param province 省
     * @param city     市
     * @param region   区
     * @author hhf
     * @date 2019/4/3
     */
    List<DistributorDTO> getRecommendByAddress(String province, String city, String region);


    /**
     * 根据用户ID获取上级经销商信息
     *
     * @author hhf
     * @date 2019/5/7
     */
    DistributorDTO getExpansionInfoByUserId(Integer userId);

    /**
     * 经销商导出
     *
     * @author hhf
     * @date 2019/5/16
     */
    List<DistributorExportDTO> distributorExport(DistributorQueryDTO query);

    /**
     * 更新经销商信息
     *
     * @param dto 经销商信息
     */
    void updateDistributor(DistributorDTO dto);

    /**
     * 根据主账号ID集合获取企业版子账号的经销商Id
     *
     * @author hhf
     * @date 2019/5/29
     */
    List<DistributorDTO> getSonDistributorByMid(Integer mid);

    /**
     * 模糊查询经销商信息 （给服务站使用）
     *
     * @author hhf
     * @date 2019/6/13
     */
    List<DistributorDTO> getDistributorByParams(String username, List<String> provinces, List<String> citys, List<String> regions);

    DistributorDTO getDistributorByUserName(String userName);

    Object getSonAccountInfo(Integer id);

    /**
     * @param
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @description 获取经销商用户信息和经销商订单信息
     * @author Liu Yi
     * @date 15:04 2019/8/20
     **/
    Map<String, Object> getDistributorAccountInfoForApp(Integer distributorId);

    /**
     * @param
     * @return com.yimao.cloud.pojo.dto.user.UserDTO
     * @description 注册经销商
     * @author Liu Yi
     * @date 2019/8/21 11:26
     */
    Map<String, Object> registDistributorForApp(String mobile, String countryCode, String smsCode, Integer registLevel, UserCompanyApplyDTO userCompanyApplyDTO);

    PageVO<DistributorDTO> getMyDistributors(Integer pageNum, Integer pageSize, Integer distributorId, Integer distributorType, String province, String city, String region);

    Object developSonAccount(String realName, Integer sex, String idCard, String email, Integer userId);

    DistributorDTO getDistirbutorInfoById(Integer id);

    UserChangeRecordListDTO getChangeInfoByUserId(Integer userId);

    List<DistributorDTO> getDistributorAndSonByMid(Integer mid);


    /**
     * 新增经销商
     *
     * @param userDistributorApply 注册经销商申请信息
     */
    void insertDistributor(UserDistributorApply userDistributorApply, DistributorOrder distributorOrder);

    List<DistributorDTO> queryDistributorByMobile(String mobile);

    List<UserAccountDTO> queryDistributors(List<Integer> distributorIds);

    /**
     * 生成经销商账号 YM + year + month + （7 位自随机数字）
     *
     * @author hhf
     * @date 2018/12/24
     */
    String generateDistributorAccount();


    CommResult checkDistributor(OwnerDistributorDTO ownerDistributorDTO);

    CommResult distributorSendSmsCode(OwnerDistributorDTO ownerDistributorDTO, String key);

    CommResult distributorCheckSmsCode(OwnerDistributorDTO ownerDistributorDTO, String smsCode, String key);


    CommResult determineDistributor(OwnerDistributorDTO ownerDistributorDTO, String key, String smsCode);

    CommResult checkSubaccount(SubDistributorAccountDTO subAccountDTO);

    CommResult subaccountSendsmscode(SubDistributorAccountDTO subAccountDTO, String key);

    CommResult subaccountCheckSmsCode(SubDistributorAccountDTO subAccountDTO, String key, String smsCode);

    CommResult determineSubaccount(SubDistributorAccountDTO subAccountDTO, String key, String smsCode);

    /**
     * 根据经销商id查询经销商，创建合同接口调用
     * @param id
     * @return
     */
    DistributorDTO getDistributorById(Integer id);

    /**
     * 根据条件分页查询经销商/代理商（站务系统）
     *
     * @param query
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageVO<DistributorVO> pageQueryDistributorToStation(DistributorQuery query, Integer pageNum, Integer pageSize);

    /**
     * 根据经销商id获取经销商的详情信息
     *
     * @param id
     * @return
     */
    DistributorVO getDistributorInfoByIdToStation(Integer id);

    /**
     * 根据经销商id查询变更记录
     *
     * @param id
     * @return
     */
    UserChangeRecordListDTO getChangeInfoByUserIdToStation(Integer id);

    /**
     * 根据areaId 获取经销商id
     *
     * @param areaIds
     * @return
     */
    List<Integer> getDistributorIdsByAreaIds(Set<Integer> areaIds);
}
