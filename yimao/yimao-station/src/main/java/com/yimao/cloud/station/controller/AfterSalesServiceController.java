package com.yimao.cloud.station.controller;

import com.alibaba.fastjson.JSON;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.enums.MoveWaterDeviceOrderStatusEnum;
import com.yimao.cloud.base.enums.PermissionTypeEnum;
import com.yimao.cloud.base.enums.RepairOrderStatus;
import com.yimao.cloud.base.enums.ServiceEngineerChangeSourceEnum;
import com.yimao.cloud.base.enums.StationQueryEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderDTO;
import com.yimao.cloud.pojo.dto.order.MoveWaterDeviceOrderDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderBackDTO;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.dto.user.EngineerServiceAreaDTO;
import com.yimao.cloud.pojo.query.order.MoveWaterDeviceOrderQuery;
import com.yimao.cloud.pojo.query.order.WorkOrderBackQueryDTO;
import com.yimao.cloud.pojo.query.order.WorkRepairOrderQuery;
import com.yimao.cloud.pojo.query.station.StationMaintenanceOrderQuery;
import com.yimao.cloud.pojo.query.station.StationRepairOrderQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.station.WorkRepairOrderVO;
import com.yimao.cloud.station.aop.annotation.StationQuery;
import com.yimao.cloud.station.feign.OrderFeign;
import com.yimao.cloud.station.feign.SystemFeign;
import com.yimao.cloud.station.feign.UserFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 售后模块
 *
 * @author yaoweijun
 */
@RestController
@Api(tags = "AfterSalesServiceController")
@Slf4j
public class AfterSalesServiceController {
    @Resource
    private OrderFeign orderFeign;

    @Resource
    private UserCache userCache;

    @Resource
    private UserFeign userFeign;

    @Resource
    private RedisCache redisCache;

    @Resource
    private SystemFeign systemFeign;

    /**
     * 站务系统 - 服务管理 - 移机工单 - 更换安装工
     *
     * @param id         移机工单号
     * @param engineerId 替换原服务人员的安装工id
     * @param type       1-更换拆机服务人员；2-更换装机服务人员
     */
    @PostMapping(value = "/aftersale/moveDeviceOrder/changeEngineer/{id}")
    @ApiOperation(value = "移机工单 - 更换服务人员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "移单号", dataType = "String", required = true, paramType = "path"),
            @ApiImplicitParam(name = "engineerId", value = "替换原服务人员的安装工id", dataType = "Long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "type", value = "1-更换拆机服务人员；2-更换装机服务人员", dataType = "Long", required = true, paramType = "query")
    })
    public Object changeEngineer(@PathVariable(value = "id") String id,
                                 @RequestParam(value = "engineerId") Integer engineerId,
                                 @RequestParam(value = "type") Integer type) {
        //查询售后服务区域
        Set<Integer> afterServiceAreas = userCache.getStationUserAreas(1, PermissionTypeEnum.AFTER_SALE.value);
        List<Integer> engineerIds = userFeign.getEngineerIdsByAreaIds(afterServiceAreas);
        orderFeign.changeEngineer(id, engineerId, engineerIds, type, ServiceEngineerChangeSourceEnum.STATION.value, userCache.getCurrentAdminRealName());
        return ResponseEntity.noContent().build();
    }

    /**
     * 站务系统 - 服务管理 - 移机工单分页展示
     *
     * @param pageNum
     * @param pageSize
     * @param query
     * @return
     */
    @StationQuery(value = StationQueryEnum.ListQuery, serviceType = PermissionTypeEnum.AFTER_SALE)
    @PostMapping(value = "/aftersale/moveDeviceOrder/{pageNum}/{pageSize}")
    @ApiOperation(value = "根据条件查询安装工单信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "查询信息", dataType = "WorkOrderBackQueryDTO", paramType = "body"),
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", required = true, paramType = "path")
    })
    public Object pageMoveWaterDeviceOrder(@PathVariable(value = "pageNum") Integer pageNum,
                                           @PathVariable(value = "pageSize") Integer pageSize,
                                           @RequestBody MoveWaterDeviceOrderQuery query) {
        List<Integer> engineerIds = userFeign.getEngineerIdsByAreaIds(query.getAreas());
        if (CollectionUtil.isEmpty(engineerIds)) {
            return null;
        }
        query.setEngineerIds(engineerIds);
        PageVO<MoveWaterDeviceOrderDTO> page = orderFeign.pageMoveWaterDeviceOrder(pageNum, pageSize, query);
        if (CollectionUtil.isNotEmpty(page.getResult())) {
            //查询管理员所管理的所有售后服务区域，查询这些服务区域下的所有安装工
            Set<Integer> afterAreas = userCache.getStationUserAreas(1, PermissionTypeEnum.AFTER_SALE.value);
            engineerIds = userFeign.getEngineerIdsByAreaIds(afterAreas);
            for (MoveWaterDeviceOrderDTO moveWaterDeviceOrderDTO : page.getResult()) {
                moveWaterDeviceOrderDTO.setMayChangeDismantleEngineer(false); //暂设不允许更换移入安装工
                moveWaterDeviceOrderDTO.setMayChangeInstallEngineer(false);//暂设不允许更换移出安装工
                if (moveWaterDeviceOrderDTO.getStatus() == MoveWaterDeviceOrderStatusEnum.WAIT_DISMANTLE.value &&
                        engineerIds.contains(moveWaterDeviceOrderDTO.getDismantleEngineerId())) {
                    moveWaterDeviceOrderDTO.setMayChangeDismantleEngineer(true); //允许更换移出安装工
                }
                if ((moveWaterDeviceOrderDTO.getStatus() == MoveWaterDeviceOrderStatusEnum.WAIT_DISMANTLE.value ||
                        moveWaterDeviceOrderDTO.getStatus() == MoveWaterDeviceOrderStatusEnum.DISMANTLE.value ||
                        moveWaterDeviceOrderDTO.getStatus() == MoveWaterDeviceOrderStatusEnum.WAIT_INSTALL.value) &&
                        engineerIds.contains(moveWaterDeviceOrderDTO.getInstallEngineerId())) {
                    moveWaterDeviceOrderDTO.setMayChangeInstallEngineer(true); //允许更换移入安装工
                }
            }
        }
        return page;
    }

    /**
     * 站务系统 - 服务管理 - 移机工单 - 根据移机工单id查询工单详情
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/aftersale/moveDeviceOrder/{id}")
    @ApiOperation(value = "根据移机工单id查询工单详情", notes = "根据移机工单id查询工单详情")
    @ApiImplicitParam(name = "id", value = "移机工单ID", dataType = "Long", required = true, paramType = "path")
    public Object getMoveWaterDeviceOrderDetailsById(@PathVariable(value = "id") String id) {
        return orderFeign.getMoveWaterDeviceOrderDetailsById(id);
    }

    /**
     * 站务系统 - 退机工单 - 更换安装工
     *
     * @param id         移机工单号
     * @param engineerId 替换原服务人员的安装工id
     */
    @PostMapping(value = "/aftersale/workorderBack/changeEngineer/{id}")
    @ApiOperation(value = "退机工单 - 更换服务人员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "退机工单主键", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "engineerId", value = "替换原服务人员的安装工id", dataType = "Long", required = true, paramType = "query")
    })
    public Object workOrderBackChangeEngineer(@PathVariable(value = "id") Integer id,
                                              @RequestParam(value = "engineerId") Integer engineerId) {
        //查询售后服务区域
        Set<Integer> afterServiceAreas = userCache.getStationUserAreas(1, PermissionTypeEnum.AFTER_SALE.value);
        List<Integer> engineerIds = userFeign.getEngineerIdsByAreaIds(afterServiceAreas);
        orderFeign.workOrderBackChangeEngineer(id, engineerId, engineerIds, ServiceEngineerChangeSourceEnum.STATION.value, userCache.getCurrentAdminRealName());
        return ResponseEntity.noContent().build();
    }

    /**
     * 站务系统 - 服务管理 - 根据条件查询退机工单信息
     */
    @StationQuery(value = StationQueryEnum.ListQuery, serviceType = PermissionTypeEnum.AFTER_SALE)
    @GetMapping(value = "/aftersale/workorderBack/{pageNum}/{pageSize}")
    @ApiOperation(value = "根据条件查询安装工单信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "查询信息", dataType = "WorkOrderBackQueryDTO", paramType = "body"),
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", required = true, paramType = "path")
    })
    public Object getWorkOrderBackList(@RequestBody WorkOrderBackQueryDTO query,
                                       @PathVariable(value = "pageNum") Integer pageNum,
                                       @PathVariable(value = "pageSize") Integer pageSize) {
        //查询服务区域下的所有安装工
        List<Integer> engineerIds = userFeign.getEngineerIdsByAreaIds(query.getAreas());
        if (CollectionUtil.isEmpty(engineerIds)) {
            return null;
        }
        query.setEngineerIds(engineerIds);
        return orderFeign.getWorkOrderBackList(query, pageNum, pageSize);
    }

    /**
     * 站务系统 - 服务管理  - 退机工单列表 - 根据工单id获取退机工单信息
     *
     * @param id 退机工单ID
     **/
    @GetMapping(value = "/aftersale/workorderBack/{id}")
    @ApiOperation(value = "根据工单id获取退机工单信息", notes = "根据工单id获取退机工单信息")
    @ApiImplicitParam(name = "id", value = "工单ID", dataType = "Long", required = true, paramType = "path")
    public WorkOrderBackDTO getWorkOrderBackById(@PathVariable(value = "id") Integer id) {

        return orderFeign.getWorkOrderBackById(id);
    }

    /**
     * 站务系统 - 服务管理 - 根据条件分页查询维修工单列表
     */
    @StationQuery(value = StationQueryEnum.ListQuery, serviceType = PermissionTypeEnum.AFTER_SALE)
    @PostMapping(value = "/aftersale/repairWorkorder/{pageNum}/{pageSize}")
    @ApiOperation(value = "服务管理 - 根据条件分页查询维修工单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "查询信息", dataType = "StationRepairOrderQuery", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", required = true, paramType = "path")
    })
    public Object getWorkRepairOrderList(@RequestBody StationRepairOrderQuery query,
                                         @PathVariable(value = "pageNum") Integer pageNum,
                                         @PathVariable(value = "pageSize") Integer pageSize) {
        log.info("维修工单查询={}", JSON.toJSONString(query));

        //查询区域下的安装工id集合
        List<Integer> engineerIds = userFeign.getEngineerIdsByAreaIds(query.getAreas());

        if (CollectionUtil.isEmpty(engineerIds)) {
            return null;
        }

        WorkRepairOrderQuery repairQuery = new WorkRepairOrderQuery();

        BeanUtils.copyProperties(query, repairQuery);

        repairQuery.setEngineerIds(engineerIds);

        return orderFeign.pageRepairOrders(repairQuery, pageNum, pageSize);

    }

    /**
     * 站务系统 - 服务管理  - 维修工单列表 - 根据维修工单号获取维修工单信息
     *
     * @param workOrderNo 维修工单号
     **/
    @StationQuery(StationQueryEnum.InfoQuery)
    @GetMapping(value = "/aftersale/repairWorkorder/{workOrderNo}")
    @ApiOperation(value = "根据工单号获取维修工单信息", notes = "根据工单号获取维修工单信息")
    @ApiImplicitParam(name = "workOrderNo", value = "维修工单号", dataType = "String", required = true, paramType = "path")
    public WorkRepairOrderVO getRepairOrderDetail(@PathVariable(value = "workOrderNo") String workOrderNo) {

        if (StringUtil.isEmpty(workOrderNo)) {
            throw new BadRequestException("维修工单号为空");
        }

        WorkRepairOrderVO vo = orderFeign.getRepairOrderByWorkOrderNo(workOrderNo);

        if (Objects.isNull(vo)) {
            throw new YimaoException("维修工单不存在");
        }

        Set<Integer> userStations = userCache.getStationUserAreas(0, null);

        if (Objects.isNull(vo.getStationId()) || !userStations.contains(vo.getStationId())) {
            throw new YimaoException("没有该维修工单所属门店权限");
        }

        return vo;

    }

    @PostMapping("/aftersale/repairWorkorder/replace")
    @ApiOperation(value = "更换维修安装工", notes = "更换维修安装工")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workOrderNo", value = "维修单号", dataType = "String", required = true, paramType = "body"),
            @ApiImplicitParam(name = "engineerId", value = "替换原服务人员的安装工id", dataType = "Long", required = true, paramType = "body")
    })
    public void replaceRepairEngineer(@RequestParam("workOrderNo") String workOrderNo, @RequestParam("engineerId") Integer engineerId) {
        if (StringUtil.isEmpty(workOrderNo)) {
            throw new BadRequestException("维修工单号为空");
        }

        if (Objects.isNull(engineerId)) {
            throw new YimaoException("更换安装工id为空");
        }

        String realName = userCache.getStationUserInfo().getRealName();

        orderFeign.replaceRepairEngineer(workOrderNo, engineerId, ServiceEngineerChangeSourceEnum.STATION.value, realName);

    }


    /**
     * 站务系统 - 服务管理 - 根据条件查询维护工单列表
     */
    @StationQuery(value = StationQueryEnum.ListQuery, serviceType = PermissionTypeEnum.AFTER_SALE)
    @PostMapping(value = "/aftersale/maintenanceWorkorder/{pageNum}/{pageSize}")
    @ApiOperation(value = "服务管理 - 根据条件查询维护工单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "查询信息", dataType = "StationMaintenanceOrderQuery", paramType = "body"),
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", required = true, paramType = "path")
    })
    public Object getMaintenanceOrderList(@RequestBody StationMaintenanceOrderQuery query,
                                          @PathVariable(value = "pageNum") Integer pageNum,
                                          @PathVariable(value = "pageSize") Integer pageSize) {
        log.info("维护工单查询={}", JSON.toJSONString(query));

        //查询区域下的安装工id集合
        List<Integer> engineerIds = userFeign.getEngineerIdsByAreaIds(query.getAreas());

        if (CollectionUtil.isEmpty(engineerIds)) {
            return null;
        }

        query.setEngineerIds(engineerIds);

        return orderFeign.listMaintenanceOrderToStation(query, pageNum, pageSize);
    }

    /**
     * 站务系统 - 服务管理  - 维护工单列表 - 根据维护单号获取维护工单信息
     *
     * @param workOrderNo 维修工单号
     **/
    @StationQuery(StationQueryEnum.InfoQuery)
    @GetMapping(value = "/aftersale/maintenanceWorkorder/{id}")
    @ApiOperation(value = "根据单号获取维护工单信息", notes = "根据单号获取维护工单信息")
    @ApiImplicitParam(name = "id", value = "维护工单号", dataType = "String", required = true, paramType = "path")
    public Object getMaintenanceOrderDetail(@PathVariable(value = "id") String id) {

        if (StringUtil.isEmpty(id)) {
            throw new BadRequestException("维护工单号为空");
        }

        Object dto = orderFeign.getWorkOrderMaintenanceById(id);

        return dto;

    }

    @PostMapping("/aftersale/maintenanceWorkorder/replace")
    @ApiOperation(value = "更换维护安装工", notes = "更换维护安装工")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "维护单号", dataType = "String", required = true, paramType = "body"),
            @ApiImplicitParam(name = "engineerId", value = "替换原服务人员的安装工id", dataType = "Long", required = true, paramType = "body")
    })
    public void replaceMaintenanceEngineer(@RequestParam("id") String id, @RequestParam("engineerId") Integer engineerId) {
        if (StringUtil.isEmpty(id)) {
            throw new BadRequestException("维护工单号为空");
        }

        if (Objects.isNull(engineerId)) {
            throw new YimaoException("更换安装工id为空");
        }

        String realName = userCache.getStationUserInfo().getRealName();


    }


    /**
     * 根据服务区域省市区获取安装工信息
     */
    @GetMapping(value = "/engineer/byServicePCR")
    @ApiOperation(value = "根据服务区域省市区获取安装工信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "province", value = "服务区域省", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "city", value = "服务区域市", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "region", value = "服务区域区", dataType = "String", required = true, paramType = "query")
    })
    public List<EngineerDTO> getEngineerListByServicePCR(@RequestParam String province,
                                                         @RequestParam String city,
                                                         @RequestParam String region) {
        Integer areaId = redisCache.get(Constant.AREA_CACHE + province + "_" + city + "_" + region, Integer.class);
        if (areaId == null) {
            areaId = systemFeign.getRegionIdByPCR(province, city, region);
        }
        return userFeign.listEngineerByRegion(areaId);
    }

}
