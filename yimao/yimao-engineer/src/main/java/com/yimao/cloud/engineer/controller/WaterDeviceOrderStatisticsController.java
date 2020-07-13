package com.yimao.cloud.engineer.controller;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.engineer.service.WorkOrderStatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 * @ClassName WaterDeviceOrderStatistics
 * @Description 水机工单统计
 * @Author yuchunlei
 * @Date 2020/7/1 17:24
 * @Version 1.0
 */
@RestController
@Slf4j
@Api(tags = "WaterDeviceOrderStatisticsController")
public class WaterDeviceOrderStatisticsController {


    @Resource
    private WorkOrderStatisticsService workOrderStatisticsService;
    @Resource
    private UserCache userCache;


    /**
     * 市场服务-移机模块工单统计
     * @param engineerId
     * @return
     */
    @GetMapping(value = "/move/water/device/statistics/count")
    @ApiOperation(notes = "市场服务-移机模块工单统计", value = "市场服务-移机模块工单统计")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "engineerId", value = "安装工ID", dataType = "Long", required = true, paramType = "query")
    })
    public Map<String,Integer> getMoveWaterDeviceCount(@RequestParam(value = "engineerId") Integer engineerId){
        Integer currentEngineerId = userCache.getCurrentEngineerId();
        if(Objects.isNull(currentEngineerId)){
            throw new BadRequestException("参数错误");
        }
        return workOrderStatisticsService.getMoveWaterDeviceCount(engineerId);
    }



    /**
     * 市场服务-安装模块工单统计
     * @param engineerId
     * @return
     */
    @GetMapping(value = "/workorder/install/device/statistics/count")
    @ApiOperation(notes = "市场服务-安装模块工单统计", value = "市场服务-安装模块工单统计")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "engineerId", value = "安装工ID", dataType = "Long", required = true, paramType = "query")
    })
    public Map<String,Integer> getInstallDeviceCount(@RequestParam(value = "engineerId") Integer engineerId){
        Integer currentEngineerId = userCache.getCurrentEngineerId();
        if(Objects.isNull(currentEngineerId)){
            throw new BadRequestException("参数错误");
        }

        return workOrderStatisticsService.getInstallWaterDeviceCount(engineerId);
    }



    /**
     * 市场服务-维修模块工单统计
     * @param engineerId
     * @return
     */
    @GetMapping(value = "/repair/workorder/count/census")
    @ApiOperation(notes = "市场服务-维修模块工单统计", value = "市场服务-维修模块工单统计")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "engineerId", value = "安装工ID", dataType = "Long", required = true, paramType = "query")
    })
    public Map<String,Integer> getRepairOrderCount(@RequestParam(value = "engineerId") Integer engineerId){
        return workOrderStatisticsService.getRepairOrderCount(engineerId);
    }


    /**
     * 市场服务-维护模块工单统计
     * @param engineerId
     * @return
     */
    @GetMapping(value = "/order/maintenance/workorder/statistics/count")
    @ApiOperation(notes = "市场服务-维护模块工单统计", value = "市场服务-维护模块工单统计")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "engineerId", value = "安装工ID", dataType = "Long", required = true, paramType = "query")
    })
    public Map<String,Integer> getMaintenanceWorkOrderCount(@RequestParam(value = "engineerId") Integer engineerId){
        return workOrderStatisticsService.getMaintenanceWorkOrderCount(engineerId);
    }


    /**
     *  市场服务-退机模块工单统计
     * @param engineerId
     * @return
     */
    @GetMapping(value = "/workorder/back/statistics/count")
    @ApiOperation(notes = "市场服务-退机模块工单统计", value = "市场服务-退机模块工单统计")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "engineerId", value = "安装工ID", dataType = "Long", required = true, paramType = "query")
    })
    public Map<String,Integer> getWorkOrderBackCount(@RequestParam(value = "engineerId") Integer engineerId){
        return workOrderStatisticsService.getWorkOrderBackCount(engineerId);
    }



    /**
     *安装、维修、维护、移机、退机、地图工单总数量
     * @param engineerId
     * @return
     */
    @GetMapping(value = "/workorder/each/model/total")
    @ApiOperation(notes = "安装、维修、维护、移机、退机、地图工单总数量", value = "安装、维修、维护、移机、退机、地图工单总数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "engineerId", value = "安装工ID", dataType = "Long", required = true, paramType = "query")
    })
    public Map<String,Integer> getEachModelWorkOrderTotalCount(@RequestParam(value = "engineerId") Integer engineerId){
        return workOrderStatisticsService.getEachModelWorkOrderTotalCount(engineerId);
    }

}
