package com.yimao.cloud.engineer.controller;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.engineer.feign.OrderFeign;
import com.yimao.cloud.engineer.feign.UserFeign;
import com.yimao.cloud.pojo.dto.order.MaintenanceDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 维护工单
 *
 * @author liuhao@yimaokeji.com
 * @date 2020-07-01
 */
@RestController
@Api(tags = "MaintenanceWorkOrderController")
@Slf4j
public class MaintenanceWorkOrderController {

    @Resource
    private OrderFeign orderFeign;
    @Resource
    private UserCache userCache;

    @GetMapping(value = "/order/maintenanceWorkOrder/app/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询维护工单列表", notes = "查询维护工单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "distributorId", value = "经销商ID", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "维护工单状态：1-待维护 2-处理中 3-挂单 4-已完成", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "search", value = "搜索条件", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "longitude", value = "经度", dataType = "DOUBLE", paramType = "query"),
            @ApiImplicitParam(name = "latitude", value = "纬度", dataType = "DOUBLE", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "第几页", defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", dataType = "Long", paramType = "path")
    })
    public Object listMaintenanceWorkOrderForClient(@RequestParam(value = "distributorId", required = false) String distributorId,
                                                    @RequestParam(value = "state") Integer state,
                                                    @RequestParam(value = "search", required = false) String search,
                                                    @RequestParam(value = "longitude", required = false) Double longitude,
                                                    @RequestParam(value = "latitude", required = false) Double latitude,
                                                    @PathVariable("pageNum") Integer pageNum,
                                                    @PathVariable("pageSize") Integer pageSize) {
        Integer engineerId = userCache.getUserId();
        return ResponseEntity.ok(orderFeign.listMaintenanceWorkOrderForClient(distributorId, engineerId, state, search, longitude, latitude, pageNum, pageSize));
    }


    /**
     * 功能描述：根据状态查询维护工单数量
     */
    @GetMapping(value = "/order/maintenanceWorkOrder/count")
    @ApiOperation(value = "根据状态查询维护工单数量", notes = "根据状态查询维护工单数量")
    public Object getWorkOrderMaintenanceCount() {
        Integer engineerId = userCache.getUserId();
        return ResponseEntity.ok(orderFeign.getWorkOrderMaintenanceCount(engineerId));
    }


    /**
     * 功能描述：修改维护工单信息
     * 场景：开始维护/改约/确认维护完成
     */
    @PutMapping(value = "/order/maintenanceWorkOrder/hang")
    @ApiOperation(value = "修改维护工单信息", notes = "适用：开始维护/改约/完成")
    @ApiImplicitParam(name = "maintenanceDTO", value = "维护工单信息", required = true, dataType = "MaintenanceDTO", paramType = "body")
    public Object hangMaintenanceWorkOrder(@RequestBody MaintenanceDTO maintenanceDTO) {
        return ResponseEntity.ok(orderFeign.hangMaintenanceWorkOrder(maintenanceDTO));
    }


    /**
     * 描述：已完成维护工单
     *
     * @return
     * @description 已完成维护工单
     */
    @GetMapping(value = "/order/maintenanceWorkOrder/app/complete/{pageNum}/{pageSize}")
    @ApiOperation(value = "已完成维护工单", notes = "已完成维护工单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sortType", value = "排序:1-升序 2-降序", defaultValue = "2", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageNum", value = "第几页", defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", dataType = "Long", paramType = "path")
    })
    public Object maintenanceWorkOrderCompleteList(@RequestParam(value = "sortType",defaultValue = "2") Integer sortType,
                                                   @PathVariable("pageNum") Integer pageNum,
                                                   @PathVariable("pageSize") Integer pageSize) {
        Integer engineerId = userCache.getUserId();
        return ResponseEntity.ok(orderFeign.maintenanceWorkOrderCompleteList(engineerId, sortType, pageNum, pageSize));
    }

    /**
     * 描述：维护工单记录列表
     *
     * @param deviceSncode SN码
     * @return
     * @description 维护记录列表
     */
    @GetMapping(value = "/order/maintenanceWorkOrder/app/record/detail")
    @ApiOperation(value = "维护工单记录详情", notes = "维护工单记录详情")
    @ApiImplicitParam(name = "deviceSncode", value = "SN", dataType = "String", paramType = "query")
    public Object maintenanceWorkOrderRecordDetail(@RequestParam(value = "deviceSncode") String deviceSncode) {
        Integer engineerId = userCache.getUserId();
        return ResponseEntity.ok(orderFeign.maintenanceWorkOrderRecordDetail(engineerId, deviceSncode));
    }
}
