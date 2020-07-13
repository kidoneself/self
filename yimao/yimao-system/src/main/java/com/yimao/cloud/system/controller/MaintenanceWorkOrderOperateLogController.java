package com.yimao.cloud.system.controller;

import com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderOperateLogDTO;
import com.yimao.cloud.system.feign.OrderFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 维护工单操作日志
 *
 * @author Liu Yi
 * @date 2019/4/1.
 */
@RestController
@Slf4j
@Api(tags = "MaintenanceWorkOrderOperateLogController")
public class MaintenanceWorkOrderOperateLogController {
    @Resource
    private OrderFeign orderFeign;


    /***
     * 功能描述:查询维护工单操作日志列表
     *
     * @param: [queryDTO, pageNum, pageSize]
     * @auther: liu yi
     * @date: 2019/5/15 14:24
     * @return: java.lang.Object
     */
    @GetMapping(value = "/order/maintenanceWorkOrderOperateLog")
    @ApiOperation(value = "查询维护工单操作日志列表", notes = "查询维护工单操作日志列表")
    @ApiImplicitParam(name = "maintenanceWorkOrderId", value = "维护工单id", dataType = "String", required = true,paramType = "query")
    public Object getListByMaintenanceWorkOrderId(@RequestParam(value = "maintenanceWorkOrderId")String maintenanceWorkOrderId){
        List<MaintenanceWorkOrderOperateLogDTO> list= orderFeign.getListByMaintenanceWorkOrderId(maintenanceWorkOrderId);

        return ResponseEntity.ok(list);
    }
}
