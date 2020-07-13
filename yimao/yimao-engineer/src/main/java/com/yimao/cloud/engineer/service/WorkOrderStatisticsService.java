package com.yimao.cloud.engineer.service;

import java.util.Map;

/**
 * @ClassName WorkOrderStatisticsService
 * @Description WorkOrderStatisticsService
 * @Author yuchunlei
 * @Date 2020/7/2 17:13
 * @Version 1.0
 */
public interface WorkOrderStatisticsService {

    /**
     * 服务统计-已完成工单统计
     * @param completeTime
     * @return
     */
    Map<String,Object> statisticsCompleteWorkOrder(String completeTime,Integer engineerId,Integer timeType);


    /**
     * 移机模块工单统计
     * @param engineerId
     * @return
     */
    Map<String,Integer> getMoveWaterDeviceCount(Integer engineerId);


    /**
     * 安装模块工单统计
     * @param engineerId
     * @return
     */
    Map<String, Integer> getInstallWaterDeviceCount(Integer engineerId);


    /**
     * 维修模块工单统计
     * @param engineerId
     * @return
     */
    Map<String,Integer> getRepairOrderCount(Integer engineerId);

    /**
     * 维护模块工单统计
     * @param engineerId
     * @return
     */
    Map<String, Integer> getMaintenanceWorkOrderCount(Integer engineerId);

    Map<String, Integer> getWorkOrderBackCount(Integer engineerId);

    Map<String, Integer> getEachModelWorkOrderTotalCount(Integer engineerId);
}
