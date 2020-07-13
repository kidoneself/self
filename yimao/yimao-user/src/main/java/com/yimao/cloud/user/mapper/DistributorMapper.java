package com.yimao.cloud.user.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.station.StationScheduleDTO;
import com.yimao.cloud.pojo.dto.station.UserStatisticsDTO;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import com.yimao.cloud.pojo.dto.user.DistributorExportDTO;
import com.yimao.cloud.pojo.dto.user.DistributorQueryDTO;
import com.yimao.cloud.pojo.dto.user.UserAccountDTO;
import com.yimao.cloud.pojo.query.station.DistributorQuery;
import com.yimao.cloud.pojo.query.station.StatisticsQuery;
import com.yimao.cloud.pojo.vo.station.DistributorVO;
import com.yimao.cloud.pojo.vo.station.UserGeneralSituationVO;
import com.yimao.cloud.user.po.Distributor;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Zhang Bo
 * @date 2018/11/13.
 */
public interface DistributorMapper extends Mapper<Distributor> {

    Page<DistributorDTO> pageQueryDistributor(DistributorQueryDTO query);

    DistributorDTO getDistributorById(Integer id);

    DistributorDTO getDistributorReferrerById(Integer recommendId);

    Distributor getDistByUserNameAndPass(@Param("username") String username, @Param("password") String pass);

    List<Map<String, Object>> countDistributor4ConfigName(Integer id);

    Integer countDistributor4Update(Integer id, Boolean sign);

    Integer countDistributor4total(Integer id, Integer level);

    Distributor getRecommendByDistributorId(Integer distributorId);

    Distributor getMainAccountByDistributorId(Integer distributorId);

    List<Integer> getDistributorIdByParam(@Param("userId") Integer userId, @Param("distributorType") Integer distributorType, @Param("distributorAccount") String distributorAccount, @Param("distributorName") String distributorName, @Param("province") String province,
                                          @Param("city") String city, @Param("region") String region, @Param("recommendName") String recommendName, @Param("recommendAccount") String recommendAccount);

    Integer countDistributorTotal(@Param("sign") Integer sign);

    Integer countAgentTotal(@Param("sign") Integer sign);

    boolean existsWithOldId(@Param("oldId") String oldId);

    Page<DistributorExportDTO> distributorExport(DistributorQueryDTO query);

    /**
     * 获取所有的子账号经销商
     *
     * @author hhf
     * @date 2019/5/30
     */
    List<DistributorDTO> selectSonDistributorById(Integer mid);

    /**
     * 根据省市区获取推荐人
     *
     * @author hhf
     * @date 2019/6/6
     */
    List<DistributorDTO> selectRecommendByAddress(String province, String city, String region);

    /**
     * 模糊查询经销商信息 （给服务站使用）
     *
     * @author hhf
     * @date 2019/6/13
     */
    List<DistributorDTO> getDistributorByParams(@Param("param") String param, @Param("provinces") List<String> provinces, @Param("citys") List<String> citys, @Param("regions") List<String> regions);

    /**
     * 经销商的代理级别设置为空
     */
    void updateAgentLevelToNull();

    Integer getSonAccountNum(@Param("id") Integer id);

    Page<DistributorDTO> getMyDistributors(Map map);

    Integer selectTotalDistributorNum(@Param("id") Integer id);

    Integer selectEnterpriseDistributorNum(@Param("id") Integer id);

    Integer selectPersonalDistributorNum(@Param("id") Integer id);

    Integer selectMinimalDistributorNum(@Param("id") Integer id);

    List<UserAccountDTO> queryDistributors(Map<String, Object> map);

    List<DistributorDTO> queryAgentByIdCard(@Param("idCard") String idCard);

    DistributorDTO getDistributorByUserId(@Param("userId") Integer userId);

    /**
     * 经销商代理商取消或新增推荐人，取消或新增发起人，取消设置null
     *
     * @param updatedistributor
     * @param value
     * @return
     */
    int updateAgentLevelAndRecommend(@Param("updatedistributor") Distributor updatedistributor, @Param("type") int value);


    Distributor selectByUserNameForAppLogin(@Param("userName") String userName);

    boolean checkForbiddenByUserName(@Param("userName") String userName);

    DistributorDTO selectDistributorBasicInfoByUserName(@Param("userName") String userName);

    void updatePasswordById(@Param("id") Integer id, @Param("password") String password);

    DistributorDTO getAgentByUserId(@Param("userId") Integer userId);

    Distributor getDistributorByIdForFillIntoUser(@Param("id") Integer id);

    DistributorDTO selectBasicInfoByIdForMsgPushInfo(@Param("id") Integer id);

    List<DistributorDTO> querySubAccountList(@Param("id") Integer id);

    int updateDistributorById(Distributor updatedistributor);

    void updateAppTypeByUserId(@Param("appType") Integer appType, @Param("userId") Integer userId);

    /**
     * 可设置字段为null（仅包括recommendId，recommendName）
     *
     * @param updatedistributor
     * @return
     */
    int updateNullFieldByDistributorId(Distributor updatedistributor);

    /**
     * 校验是不是折机版经销商
     */
    boolean checkDiscountDistributor(@Param("id") Integer id);

    DistributorDTO getDistInfoById(@Param("id") Integer id);

    Page<DistributorVO> pageQueryDistributorToStation(DistributorQuery query);

    List<Integer> getDistributorIdsByAreaIds(@Param("areaIds") Set<Integer> areaIds);

    /**
     * 站务系统根据区域查询经销商数区代理商数
     * @param areas
     * @return
     */
	StationScheduleDTO getStationDistributorNum(@Param("areaIds") Set<Integer> areas);

    List<UserStatisticsDTO> getDistributorSaleData(StatisticsQuery query);

    List<UserStatisticsDTO> getAgentSaleData(StatisticsQuery query);

    List<UserStatisticsDTO> getDistributorSalePicData(StatisticsQuery query);

    List<UserStatisticsDTO> getAgentSalePicData(StatisticsQuery query);

    UserGeneralSituationVO getAllDistributorNumByAreaIds(@Param("areas") Set<Integer> areas);

    UserGeneralSituationVO getDistrictAgentNumByAreaIds(@Param("areas") Set<Integer> areas);

    List<Integer> getDistributorIdsByAreaIdsForApp(@Param("areaIds") List<Integer> areaIds);
}
