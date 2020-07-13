package com.yimao.cloud.station.controller;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.PermissionTypeEnum;
import com.yimao.cloud.base.enums.StationQueryEnum;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.pojo.dto.user.UserChangeRecordListDTO;
import com.yimao.cloud.pojo.query.station.DistributorQuery;
import com.yimao.cloud.pojo.query.station.StationEngineerQuery;
import com.yimao.cloud.pojo.query.station.UserQuery;
import com.yimao.cloud.pojo.query.user.DistributorOrderQueryDTO;
import com.yimao.cloud.pojo.vo.order.EngineerWorkOrderVO;
import com.yimao.cloud.pojo.vo.station.DistributorVO;
import com.yimao.cloud.pojo.vo.station.UserGeneralSituationVO;
import com.yimao.cloud.pojo.vo.station.UserVO;
import com.yimao.cloud.station.aop.annotation.StationQuery;
import com.yimao.cloud.station.feign.OrderFeign;
import com.yimao.cloud.station.feign.UserFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户模块
 *
 * @author yaoweijun
 */
@RestController
@Api(tags = "SystemUserController")
public class SystemUserController {

    @Resource
    private UserCache userCache;
    @Resource
    private UserFeign userFeign;

    @Resource
    private OrderFeign orderFeign;

    /**
     * 用户--概况
     *
     * @return
     */
    @GetMapping("/user/station/generalSituation")
    @ApiOperation(value = "概况")
    public UserGeneralSituationVO generalSituation() {
        //获取售前区域
        Set<Integer> areas = userCache.getStationUserAreas(1, PermissionTypeEnum.PRE_SALE.value);
        if (CollectionUtil.isEmpty(areas)) {
            return new UserGeneralSituationVO(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        }
        return userFeign.getUserGeneralSituation(areas);
    }

    /**
     * 用户--根据条件查询用户列表
     *
     * @return query
     */
    @StationQuery(value = StationQueryEnum.ListQuery, serviceType = PermissionTypeEnum.PRE_SALE)
    @PostMapping("/user/station/list/{pageNum}/{pageSize}")
    @ApiOperation(value = "根据条件查询用户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "query", value = "查询条件", dataType = "UserQuery", paramType = "body")
    })
    public Object getUserInfo(@PathVariable(value = "pageNum") Integer pageNum,
                              @PathVariable(value = "pageSize") Integer pageSize,
                              @RequestBody(required = false) UserQuery query) {
        return userFeign.getUserListInfo(pageNum, pageSize, query);
    }

    /**
     * 用户--用户列表--用户信息变更记录 （站务系统调用）
     *
     * @param userId
     * @return
     */
    @GetMapping("/user/record/station/{userId}")
    @ApiOperation(value = "用户信息变更记录（站务系统调用）", notes = "用户信息变更记录（站务系统调用）")
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "Long", paramType = "path")
    public Object stationGetUserChangeRecord(@PathVariable("userId") Integer userId) {

        return userFeign.stationGetUserChangeRecord(userId);
    }

    /**
     * 用户--用户列表--用户详细信息
     *
     * @param id
     * @return
     */
    @StationQuery(StationQueryEnum.InfoQuery)
    @GetMapping(value = "/user/distributor/station/{id}")
    @ApiOperation(value = "根据用户ID查询用户信息", notes = "根据用户ID查询用户信息")
    @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long", paramType = "path")
    public UserVO getUserInfo(@PathVariable(value = "id") Integer id) {

        return userFeign.getUserInfo(id);
    }

    /**
     * 用户--根据条件查询经销商/代理商列表
     *
     * @return query
     */
    @StationQuery(value = StationQueryEnum.ListQuery, serviceType = PermissionTypeEnum.PRE_SALE)
    @PostMapping("/user/distributor/station/list/{pageNum}/{pageSize}")
    @ApiOperation(value = "经销商代理商分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "query", value = "查询信息", dataType = "DistributorQuery", paramType = "body")
    })
    public Object getDistributorAndAgentInfo(@PathVariable(value = "pageNum") Integer pageNum,
                                             @PathVariable(value = "pageSize") Integer pageSize,
                                             @RequestBody DistributorQuery query) {

        return userFeign.stationPageQueryDistributor(pageNum, pageSize, query);
    }

    /**
     * 用户--经销商/代理商--变更记录
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/distributor/station/changeInfo")
    @ApiOperation(value = "根据经销商ID获取用户变更记录")
    @ApiImplicitParam(name = "id", value = "经销商ID", required = true, dataType = "Long", paramType = "query")
    public UserChangeRecordListDTO getChangeInfoByDistributorId(@RequestParam Integer id) {
        return userFeign.getChangeInfoByDistributorId(id);
    }

    /**
     * 用户--经销商/代理商--根据经销商ID查询详情信息（站务系统调用）
     *
     * @param id 经销商ID
     */
    @StationQuery(StationQueryEnum.InfoQuery)
    @GetMapping(value = "/distributor/station/{id}/expansion")
    @ApiOperation(value = "根据经销商ID查询详情信息（返回基本信息+扩展信息）")
    @ApiImplicitParam(name = "id", value = "经销商ID", required = true, dataType = "Long", paramType = "path")
    public DistributorVO getDistributorExpansionInfo(@PathVariable("id") Integer id) {
        return userFeign.getDistributorExpansionInfo(id);
    }

    /**
     * 用户--根据条件查询经销商订单列表
     *
     * @return query
     */
    @StationQuery(value = StationQueryEnum.ListQuery, serviceType = PermissionTypeEnum.PRE_SALE)
    @PostMapping("/user/distributor/order/station/list/{pageNum}/{pageSize}")
    @ApiOperation(value = "根据条件查询经销商订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "query", value = "查询信息", dataType = "DistributorOrderQueryDTO", paramType = "body")
    })
    public Object getDistributorOrderInfo(@PathVariable(value = "pageNum") Integer pageNum,
                                          @PathVariable(value = "pageSize") Integer pageSize,
                                          @RequestBody DistributorOrderQueryDTO query) {

        return userFeign.pageDistributorOrderInfo(pageNum, pageSize, query);
    }

    /**
     * 用户--经销商订单--根据id查询经销商订单详情
     *
     * @param orderId
     */
    @StationQuery(StationQueryEnum.InfoQuery)
    @GetMapping(value = "/user/distributor/order/station/{orderId}")
    @ApiOperation(value = "根据id查询经销商订单详情")
    @ApiImplicitParam(name = "orderId", value = "订单号", required = true, dataType = "Long", paramType = "path")
    public Object findDistributorOrderById(@PathVariable(value = "orderId") Long orderId) {

        return userFeign.findDistributorOrderById(orderId);
    }

    /**
     * 用户--根据条件查询安装工列表
     *
     * @return query
     */
    @StationQuery(value = StationQueryEnum.ListQuery, serviceType = PermissionTypeEnum.AFTER_SALE)
    @PostMapping("/user/engineer/station/list/{pageNum}/{pageSize}")
    @ApiOperation(value = "根据条件查询安装工列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "query", value = "查询条件", dataType = "StationEngineerQuery", paramType = "body")
    })
    public Object getEngineerInfo(@PathVariable(value = "pageNum") Integer pageNum,
                                  @PathVariable(value = "pageSize") Integer pageSize,
                                  @RequestBody StationEngineerQuery query) {

        return userFeign.pageEngineerInfoToStation(pageNum, pageSize, query);
    }

    /**
     * 订单--安装工单--安装工程师筛选条件
     *
     * @return query
     */
    @StationQuery(value = StationQueryEnum.ListQuery, serviceType = PermissionTypeEnum.PRE_SALE)
    @GetMapping("/user/engineer/station/list")
    @ApiOperation(value = "安装工程师筛选条件")
    public Object getEngineerInfo(StationEngineerQuery query) {

        return userFeign.getEngineerList(query);
    }

    /**
     * 用户--安装工--根据id获取安装工详细信息
     *
     * @param id 安装工ID
     */
    @StationQuery(StationQueryEnum.InfoQuery)
    @GetMapping(value = "/user/engineer/station/{id}/detail")
    @ApiOperation(value = "根据id获取安装工详细信息")
    @ApiImplicitParam(name = "id", value = "安装工id", required = true, dataType = "Long", paramType = "path")
    public Object getDetailByIdToStation(@PathVariable(value = "id") Integer id) {

        return userFeign.getEngineerDetailByIdToStation(id);
    }

    /**
     * 用户--安装工--获取安装工的工单列表
     *
     * @param engineerId 安装工ID
     */
    @StationQuery(StationQueryEnum.InfoQuery)
    @GetMapping(value = "/workorder/engineer/station/{engineerId}")
    @ApiOperation(value = "获取安装工的工单列表")
    @ApiImplicitParam(name = "engineerId", value = "安装工ID", dataType = "Long", paramType = "path", required = true)
    public List<EngineerWorkOrderVO> getEngineerWorkOrder(@PathVariable Integer engineerId) {
        return orderFeign.listWorkOrderByEngineerId(engineerId);
    }

    /**
     * 用户--经销商订单--详情--查看合同
     *
     * @param distributorOrderId
     * @return
     */
    @GetMapping(value = "/distributor/protocol/view/{distributorOrderId}")
    @ApiOperation(value = "查看合同 ", notes = "查看合同")
    @ApiImplicitParam(name = "distributorOrderId", value = "distributorOrderId", required = true, dataType = "Long", paramType = "path")
    public Object previewDistributorProtocol(@PathVariable(value = "distributorOrderId") Long distributorOrderId) {
        Map<String, String> resultMap = new HashMap<>();
        String url = userFeign.previewDistributorProtocol(distributorOrderId);
        resultMap.put("url", url);
        return ResponseEntity.ok(resultMap);
    }
}
