package com.yimao.cloud.engineer.controller;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.engineer.feign.OrderFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 移机工单
 *
 * @author Liu Long Jie
 * @date 2020-7-1 11:51:44
 */
@RestController
@Slf4j
@Api(tags = "MoveWaterDeviceOrderController")
public class MoveWaterDeviceOrderController {

    @Resource
    private OrderFeign orderFeign;
    @Resource
    private UserCache userCache;

    /**
     * 安装工app - 移机工单 - 待处理移机工单列表
     */
    @GetMapping(value = "/move/water/device/order/waitDispose/list")
    @ApiOperation(value = "待处理移机工单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "longitude", value = "安装工所在位置经度", required = true, dataType = "Double", paramType = "query"),
            @ApiImplicitParam(name = "latitude", value = "安装工所在位置纬度", required = true, dataType = "Double", paramType = "query"),
            @ApiImplicitParam(name = "serviceType", value = "移机工单服务类型 0-全部 1-移出拆机 2-移入安装", required = true, dataType = "Double", paramType = "query"),
            @ApiImplicitParam(name = "sort", value = "排序 true-根据服务时间顺序排序 false-根据距离排序", dataType = "Boolean", paramType = "query")
    })
    public Object getWaitDisposeList(@RequestParam(value = "sort", required = false) Boolean sort,
                                     @RequestParam(value = "serviceType") Integer serviceType,
                                     @RequestParam(value = "longitude") Double longitude,
                                     @RequestParam(value = "latitude") Double latitude) {
        Integer engineerId = userCache.getUserId();
        return ResponseEntity.ok(orderFeign.getWaitDisposeList(engineerId, sort, serviceType, longitude, latitude));
    }

    /**
     * 安装工app - 移机工单 - 处理中移机工单列表
     */
    @GetMapping(value = "/move/water/device/order/dispose/list")
    @ApiOperation(value = "处理中移机工单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "longitude", value = "安装工所在位置经度", required = true, dataType = "Double", paramType = "query"),
            @ApiImplicitParam(name = "latitude", value = "安装工所在位置纬度", required = true, dataType = "Double", paramType = "query"),
            @ApiImplicitParam(name = "sort", value = "排序 true-根据服务时间顺序排序 false-根据距离排序", dataType = "Boolean", paramType = "query")
    })
    public Object getDisposeList(@RequestParam(value = "sort", required = false) Boolean sort,
                                 @RequestParam(value = "longitude") Double longitude,
                                 @RequestParam(value = "latitude") Double latitude) {
        Integer engineerId = userCache.getUserId();
        return ResponseEntity.ok(orderFeign.getDisposeList(engineerId, sort, longitude, latitude));
    }

    /**
     * 安装工app - 移机工单 - 挂单列表
     */
    @GetMapping(value = "/move/water/device/order/pending/list")
    @ApiOperation(value = "挂单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "longitude", value = "安装工所在位置经度", required = true, dataType = "Double", paramType = "query"),
            @ApiImplicitParam(name = "latitude", value = "安装工所在位置纬度", required = true, dataType = "Double", paramType = "query"),
            @ApiImplicitParam(name = "sort", value = "排序 true-根据服务时间顺序排序 false-根据距离排序", dataType = "Boolean", paramType = "query")
    })
    public Object getPendingList(@RequestParam(value = "sort", required = false) Boolean sort,
                                 @RequestParam(value = "longitude") Double longitude,
                                 @RequestParam(value = "latitude") Double latitude) {
        Integer engineerId = userCache.getUserId();
        return ResponseEntity.ok(orderFeign.getPendingList(engineerId, sort, longitude, latitude));
    }

    /**
     * 安装工app - 移机工单 - 已完成移机工单列表
     */
    @GetMapping(value = "/move/water/device/order/complete/list/{pageNum}/{pageSize}")
    @ApiOperation(value = "已完成移机工单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "sort", value = "完成时间排序 true-正序 false-逆序", dataType = "Boolean", paramType = "query")
    })
    public Object getCompleteList(@PathVariable(value = "pageNum") Integer pageNum,
                                  @PathVariable(value = "pageSize") Integer pageSize,
                                  @RequestParam(value = "sort", required = false) Boolean sort) {
        Integer engineerId = userCache.getUserId();
        return ResponseEntity.ok(orderFeign.getCompleteList(pageNum, pageSize, engineerId, sort));
    }

    /**
     * 拆机服务开始
     *
     * @param id 移机工单id
     */
    @PatchMapping(value = "/move/water/device/order/dismantle/{id}")
    @ApiOperation(value = "拆机服务开始")
    @ApiImplicitParam(name = "id", value = "移机工单id", required = true, dataType = "String", paramType = "path")
    public Object dismantle(@PathVariable(value = "id") String id) {

        return ResponseEntity.ok(orderFeign.dismantle(id));
    }

    /**
     * 继续拆机服务
     *
     * @param id 移机工单id
     */
    @PatchMapping(value = "/move/water/device/order/continueDismantle/{id}")
    @ApiOperation(value = "继续拆机服务")
    @ApiImplicitParam(name = "id", value = "移机工单id", required = true, dataType = "String", paramType = "path")
    public Object continueDismantle(@PathVariable(value = "id") String id) {

        return ResponseEntity.ok(orderFeign.continueDismantle(id));
    }

    /**
     * 点击“待移入地处理”回显拆机安装工信息
     *
     * @param id 移机工单id
     */
    @PatchMapping(value = "/move/water/device/order/waitDismantle/{id}")
    @ApiOperation(value = "点击“待移入地处理”回显拆机安装工信息")
    @ApiImplicitParam(name = "id", value = "移机工单id", required = true, dataType = "String", paramType = "path")
    public Object waitDismantle(@PathVariable(value = "id") String id) {

        return ResponseEntity.ok(orderFeign.waitDismantle(id));
    }

    /**
     * 完成拆机
     *
     * @param id 移机工单id
     */
    @PatchMapping(value = "/move/water/device/order/completeDismantle/{id}")
    @ApiOperation(value = "完成拆机")
    @ApiImplicitParam(name = "id", value = "移机工单id", required = true, dataType = "String", paramType = "path")
    public Object completeDismantle(@PathVariable(value = "id") String id) {
        Integer engineerId = userCache.getUserId();
        return ResponseEntity.ok(orderFeign.completeDismantle(id, engineerId));
    }

    /**
     * 移入安装服务开始
     *
     * @param id 移机工单id
     */
    @PatchMapping(value = "/move/water/device/order/install/{id}")
    @ApiOperation(value = "移入安装服务开始")
    @ApiImplicitParam(name = "id", value = "移机工单id", required = true, dataType = "String", paramType = "path")
    public Object install(@PathVariable(value = "id") String id) {

        return ResponseEntity.ok(orderFeign.install(id));
    }

    /**
     * 继续移入安装服务
     *
     * @param id 移机工单id
     */
    @PatchMapping(value = "/move/water/device/order/continueInstall/{id}")
    @ApiOperation(value = "继续移入安装服务")
    @ApiImplicitParam(name = "id", value = "移机工单id", required = true, dataType = "String", paramType = "path")
    public Object continueInstall(@PathVariable(value = "id") String id) {

        return ResponseEntity.ok(orderFeign.continueInstall(id));
    }

    /**
     * 点击“等待拆机完成”回显装机安装工信息
     *
     * @param id 移机工单id
     */
    @PatchMapping(value = "/move/water/device/order/waitInstall/{id}")
    @ApiOperation(value = "点击“等待拆机完成”回显装机安装工信息")
    @ApiImplicitParam(name = "id", value = "移机工单id", required = true, dataType = "String", paramType = "path")
    public Object waitInstall(@PathVariable(value = "id") String id) {

        return ResponseEntity.ok(orderFeign.waitInstall(id));
    }

    /**
     * 完成移机工单
     *
     * @param id 移机工单id
     */
    @PatchMapping(value = "/move/water/device/order/complete/{id}")
    @ApiOperation(value = "完成移机工单")
    @ApiImplicitParam(name = "id", value = "移机工单id", required = true, dataType = "String", paramType = "path")
    public Object complete(@PathVariable(value = "id") String id) {
        orderFeign.complete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 新安装工app 移机工单完成列表获取工单详情
     *
     * @param id 移机工单id
     */
    @GetMapping(value = "/move/water/device/order/app/details/{id}")
    @ApiOperation(value = "移机工单完成列表获取工单详情")
    @ApiImplicitParam(name = "id", value = "移机工单id", required = true, dataType = "String", paramType = "path")
    public Object appGetMoveWaterDeviceDetails(@PathVariable(value = "id") String id) {

        return ResponseEntity.ok(orderFeign.appGetMoveWaterDeviceDetails(id));
    }

    /**
     * 新安装工app 移机工单挂单操作
     *
     * @param id        移机工单id
     * @param type      1-拆机挂单 2-移入挂单
     * @param cause     改约原因
     * @param startTime 改约后服务时间（开始）
     * @param endTime   改约后服务时间（结束）
     */
    @GetMapping(value = "/move/water/device/order/hangUp/{id}")
    @ApiOperation(value = "移机工单挂单操作")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "移机工单id", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "type", value = "1-拆机挂单 2-移入挂单", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "cause", value = "改约原因", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "改约后服务时间（开始）", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "改约后服务时间（结束）", required = true, dataType = "String", paramType = "query")
    })
    public Object hangUp(@PathVariable(value = "id") String id,
                         @RequestParam(value = "type") Integer type,
                         @RequestParam(value = "cause") String cause,
                         @RequestParam(value = "startTime") Date startTime,
                         @RequestParam(value = "endTime") Date endTime) {
        Integer engineerId = userCache.getUserId();
        orderFeign.hangUp(id, type, cause, startTime, endTime, engineerId);
        return ResponseEntity.noContent().build();
    }

}
