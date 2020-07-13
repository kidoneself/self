package com.yimao.cloud.order.controller;

import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.order.service.MaintenanceWorkOrderOperateLogService;
import com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderOperateLogDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    private MaintenanceWorkOrderOperateLogService workOrderMaintenanceOperateLogService;

    /***
     * 功能描述:新增维护工单操作日志
     *
     * @param: [dto]
     * @auther: liu yi
     * @date: 2019/5/15 14:24
     * @return: java.lang.Object
     */
    @PostMapping(value = "/order/maintenanceWorkOrderOperateLog")
    @ApiOperation(value = "新增维护工单操作日志", notes = "新增维护工单操作日志")
    @ApiImplicitParam(name = "dto", value = "维护工单操作日志信息", required = true, dataType = "MaintenanceWorkOrderOperateLogDTO", paramType = "body")
    public Object create(@RequestBody MaintenanceWorkOrderOperateLogDTO dto) {
        try {
            workOrderMaintenanceOperateLogService.save(dto);
        }catch (Exception e) {
            throw new YimaoException("维护工单操作日志创建失败！");
        }
     return ResponseEntity.noContent().build();
    }


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
    @ApiImplicitParam(name = "maintenanceWorkOrderId", value = "维护工单id", dataType = "String", paramType = "query")
    public Object getListByMaintenanceWorkOrderId(@RequestParam(value = "maintenanceWorkOrderId")String maintenanceWorkOrderId){
        List<MaintenanceWorkOrderOperateLogDTO> list= workOrderMaintenanceOperateLogService.getListByMaintenanceWorkOrderId(maintenanceWorkOrderId);

        return ResponseEntity.ok(list);
    }

    /**
     * @description 根据id查询维护工单操作日志
     * @param id
     * @return
     * @author Liu Yi
     */
    @GetMapping(value = "/order/maintenanceWorkOrderOperateLog/{id}")
    @ApiOperation(value = "根据id查询维护工单操作日志", notes = "根据订单号查询维护工单操作日志")
    @ApiImplicitParam(name = "id", required = true, value = "id",dataType = "Long", paramType = "path")
    public Object getWorkOrderMaintenanceById(@PathVariable("id")Integer id){
        MaintenanceWorkOrderOperateLogDTO dto= workOrderMaintenanceOperateLogService.getMaintenanceWorkOrderOperateLogById(id);

        return ResponseEntity.ok(dto);
    }
}
