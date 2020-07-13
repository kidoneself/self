package com.yimao.cloud.engineer.service.impl;

import com.yimao.cloud.engineer.feign.OrderFeign;
import com.yimao.cloud.engineer.service.WorkOrderStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName WorkOrderStatisticsServiceImpl
 * @Description WorkOrderStatisticsServiceImpl
 * @Author yuchunlei
 * @Date 2020/7/2 17:15
 * @Version 1.0
 */
@Slf4j
@Service
public class WorkOrderStatisticsServiceImpl implements WorkOrderStatisticsService {

    @Resource
    private OrderFeign orderFeign;

    @Override
    public Map<String, Object> statisticsCompleteWorkOrder(String completeTime,Integer engineerId,Integer timeType) {
        Map<String,Object> map = new HashMap<>();
        //安装
        map.put("InstallWorderOrder",orderFeign.statisticsInstallWorderOrder(completeTime, engineerId,timeType));
        //维修
        map.put("RepairWorkOrder",orderFeign.statisticsRepairWorkOrder(completeTime, engineerId,timeType));
        //维护
        map.put("MaintenanceWorkOrder",orderFeign.getMaintenanceWorkOrderList(completeTime, engineerId,timeType));
        //退机
        map.put("WorkOrderBack",orderFeign.statisticsWorkOrderBack(completeTime, engineerId,timeType));
        //移机
        map.put("MoveWaterDevice",orderFeign.getMoveWaterDeviceList(completeTime, engineerId,timeType));
        return map;
    }

    @Override
    public Map<String, Integer> getMoveWaterDeviceCount(Integer engineerId) {
        return orderFeign.getMoveWaterDeviceCount(engineerId);
    }



    @Override
    public Map<String, Integer> getInstallWaterDeviceCount(Integer engineerId) {
        return orderFeign.getInstallWaterDeviceCount(engineerId);
    }

    @Override
    public Map<String, Integer> getRepairOrderCount(Integer engineerId) {
        return orderFeign.getRepairOrderCount(engineerId);
    }


    @Override
    public Map<String, Integer> getMaintenanceWorkOrderCount(Integer engineerId) {
        return orderFeign.getMaintenanceWorkOrderCount(engineerId);
    }


    @Override
    public Map<String, Integer> getWorkOrderBackCount(Integer engineerId) {
        return orderFeign.getWorkOrderBackCount(engineerId);
    }


    @Override
    public Map<String, Integer> getEachModelWorkOrderTotalCount(Integer engineerId) {
        Map<String, Integer> resMap = new HashMap<>();
        resMap.put("InstallModelTotal",orderFeign.getInstallWaterDeviceTotalNum(engineerId));//安装模块总数量
        resMap.put("RepairModelTotal",orderFeign.getRepairModelTotalCount(engineerId));//维修模块工单总数量
        resMap.put("MaintainModelTotal",orderFeign.getMaintenanceModelWorkOrderTotalCount(engineerId));//维护模块工单总数量
        resMap.put("MoveModelTotal",orderFeign.getMoveModelTotalCount(engineerId));//移机模块工单总数量
        resMap.put("BackModelTotal",orderFeign.getBackModelTotalCount(engineerId));//退机模块工单总数量
        return resMap;
    }
}
