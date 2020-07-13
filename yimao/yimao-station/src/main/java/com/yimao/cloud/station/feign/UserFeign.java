package com.yimao.cloud.station.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.station.FlowStatisticsDTO;
import com.yimao.cloud.pojo.dto.station.StationScheduleDTO;
import com.yimao.cloud.pojo.dto.station.UserStatisticsDTO;
import com.yimao.cloud.pojo.dto.user.DistributorOrderAllInfoDTO;
import com.yimao.cloud.pojo.dto.user.DistributorOrderDTO;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.dto.user.UserChangeRecordListDTO;
import com.yimao.cloud.pojo.query.station.DistributorQuery;
import com.yimao.cloud.pojo.query.station.StationEngineerQuery;
import com.yimao.cloud.pojo.query.station.StatisticsQuery;
import com.yimao.cloud.pojo.query.station.UserQuery;
import com.yimao.cloud.pojo.query.user.DistributorOrderQueryDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.station.DistributorVO;
import com.yimao.cloud.pojo.vo.station.EngineerVO;
import com.yimao.cloud.pojo.vo.station.UserGeneralSituationVO;
import com.yimao.cloud.pojo.vo.station.UserVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 描述：系统微服务调用用户微服务的接口列表
 *
 * @author yaoweijun
 * @date 2019/12/23.
 */
@FeignClient(name = Constant.MICROSERVICE_USER)
public interface UserFeign {

    /**
     * 用户--根据条件查询用户列表（站务系统调用）
     *
     * @return query
     */
    @PostMapping(value = "/user/station/list/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<UserVO> getUserListInfo(@PathVariable(value = "pageNum") Integer pageNum,
                                   @PathVariable(value = "pageSize") Integer pageSize,
                                   @RequestBody(required = false) UserQuery query);

    /**
     * 用户--用户列表--用户详细信息 (站务系统调用)
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/user/distributor/station/{id}")
    UserVO getUserInfo(@PathVariable(value = "id") Integer id);

    /**
     * 用户--用户列表--用户信息变更记录 （站务系统调用）
     *
     * @param userId
     * @return
     */
    @GetMapping("/user/record/station/{userId}")
    UserChangeRecordListDTO stationGetUserChangeRecord(@PathVariable("userId") Integer userId);

    /**
     * 用户--经销商代理商分页查询（站务系统调用）
     *
     * @param pageNum  分页页数
     * @param pageSize 分页大小
     * @param query    查询条件
     */
    @PostMapping(value = "/distributor/station/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<DistributorVO> stationPageQueryDistributor(@PathVariable("pageNum") Integer pageNum,
                                                      @PathVariable("pageSize") Integer pageSize,
                                                      @RequestBody DistributorQuery query);

    /**
     * 用户--经销商/代理商--根据经销商ID查询详情信息（站务系统调用）
     *
     * @param id 经销商ID
     */
    @GetMapping(value = "/distributor/station/{id}/expansion")
    DistributorVO getDistributorExpansionInfo(@PathVariable("id") Integer id);

    /**
     * 用户--经销商/代理商--变更记录（站务系统调用）
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/distributor/station/changeInfo")
    UserChangeRecordListDTO getChangeInfoByDistributorId(@RequestParam("id") Integer id);

    /**
     * 流水统计-经销商订单销量
     *
     * @param query
     * @return
     */
    @GetMapping(value = "/distributor/station/distributorOrderData", consumes = MediaType.APPLICATION_JSON_VALUE)
    FlowStatisticsDTO getDistributorOrderData(@RequestBody StatisticsQuery query);


    /**
     * 用户--根据条件查询经销商订单列表(站务系统调用)
     *
     * @return query
     */
    @PostMapping(value = "/distributor/order/station/list/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<DistributorOrderDTO> pageDistributorOrderInfo(@PathVariable(value = "pageNum") Integer pageNum,
                                                         @PathVariable(value = "pageSize") Integer pageSize,
                                                         @RequestBody DistributorOrderQueryDTO query);

    /**
     * 经销商订单详情
     *
     * @param orderId
     */
    @GetMapping(value = "distributor/order/{orderId}")
    DistributorOrderAllInfoDTO findDistributorOrderById(@PathVariable(value = "orderId") Long orderId);

    /**
     * 用户--安装工--根据id获取安装工详细信息（站务系统调用）
     *
     * @param id 安装工ID
     */
    @GetMapping(value = "/engineer/station/{id}/detail")
    EngineerVO getEngineerDetailByIdToStation(@PathVariable(value = "id") Integer id);

    /**
     * 用户--安装工分页查询（站务系统调用）
     *
     * @param pageNum  当前页
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    @PostMapping(value = "/engineers/station/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<EngineerVO> pageEngineerInfoToStation(@PathVariable(value = "pageNum") Integer pageNum,
                                                 @PathVariable(value = "pageSize") Integer pageSize,
                                                 @RequestBody StationEngineerQuery query);

    /**
     * 根据区域id集合获取经销商id集合
     *
     * @param areaIds
     * @return
     */
    @PostMapping(value = "/distributor/ids/area", consumes = MediaType.APPLICATION_JSON_VALUE)
    List<Integer> getDistributorIdsByAreaIds(@RequestBody Set<Integer> areaIds);

    /**
     * 控制台-待办事项(区县级代理商，经销商，会员用户，普通用户）
     *
     * @param areas
     * @return
     */
    @PostMapping(value = "/distributor/station/stationDistributorNum", consumes = MediaType.APPLICATION_JSON_VALUE)
    StationScheduleDTO getStationDistributorNum(@RequestBody Set<Integer> areas);

    /**
     * 站务系统-统计-用户统计（站务系统调用）
     *
     * @param query
     * @return
     */
    @GetMapping(value = "/user/station/userStatistics", consumes = MediaType.APPLICATION_JSON_VALUE)
    UserStatisticsDTO getUserStatisticsInfoToStation(@RequestBody StatisticsQuery query);

    /**
     * 站务系统-用户-用户概况（站务系统调用）
     *
     * @param areas
     * @return
     */
    @GetMapping("/user/station/generalSituation")
    UserGeneralSituationVO getUserGeneralSituation(@RequestParam("areas") Set<Integer> areas);

    /**
     * @Author Liu Long Jie
     * @Description 根据服务地区id 获取安装工id集合
     * @Date 2020-2-13 15:13:25
     * @Param
     **/
    @PostMapping(value = "/engineer/ids/areas",consumes = MediaType.APPLICATION_JSON_VALUE)
    List<Integer> getEngineerIdsByAreaIds(@RequestBody Set<Integer> areaIds);

    /**
     * 站务系统-统计-用户统计--获取普通用户，会员用户数量（站务系统调用）
     *
     * @param query
     * @return
     */
    @GetMapping(value = "/user/station/getUserRes", consumes = MediaType.APPLICATION_JSON_VALUE)
    UserStatisticsDTO getUserRes(@RequestBody StatisticsQuery query);

    /**
     * 查看合同
     *
     * @param distributorOrderId
     * @return
     */
    @GetMapping(value = "distributor/protocol/view/{distributorOrderId}")
    String previewDistributorProtocol(@PathVariable(value = "distributorOrderId") Long distributorOrderId);

    @GetMapping(value = "/user/engineer/station/list", consumes = MediaType.APPLICATION_JSON_VALUE)
    List<EngineerVO> getEngineerList(@RequestBody StationEngineerQuery query);
    
    /**
     * 根据安装工ID获取安装工信息
     *
     * @param id 安装工ID
     */
    @GetMapping(value = "/engineer/{id}")
    EngineerDTO getEngineerById(@PathVariable(value = "id") Integer id);

    @GetMapping(value = "/engineers/area")
    List<EngineerDTO> listEngineerByRegion(@RequestParam(value = "areaId") Integer areaId);
    
}
