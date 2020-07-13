package com.yimao.cloud.order.controller;


import com.yimao.cloud.order.service.MaintenanceWorkOrderService;
import com.yimao.cloud.pojo.dto.order.MaintenanceDTO;
import com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderDTO;
import com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderQueryDTO;
import com.yimao.cloud.pojo.dto.order.RenewDTO;
import com.yimao.cloud.pojo.query.station.StationMaintenanceOrderQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 维护工单
 *
 * @author Liu Yi
 * @date 2019/4/1.
 */
@RestController
@Slf4j
@Api(tags = "MaintenanceWorkOrderController")
public class MaintenanceWorkOrderController {
    @Resource
    private MaintenanceWorkOrderService workOrderMaintenanceService;

    /**
     * 新增维护工单
     *
     * @param dto
     */
    @PostMapping(value = "/order/maintenanceWorkOrder")
    @ApiOperation(value = "新增维护工单", notes = "新增维护工单")
    @ApiImplicitParam(name = "dto", value = "维护工单信息", required = true, dataType = "MaintenanceWorkOrderDTO", paramType = "body")
    public void create(@RequestBody MaintenanceWorkOrderDTO dto) {
        workOrderMaintenanceService.createWorkOrderMaintenance(dto);
    }

    /**
     * 更新维护工单信息
     *
     * @param dto
     */
    @PutMapping(value = "/order/maintenanceWorkOrder")
    @ApiOperation(value = "更新维护工单信息", notes = "更新维护工单信息")
    @ApiImplicitParam(name = "dto", required = true, value = "维护工单信息", dataType = "MaintenanceWorkOrderDTO", paramType = "body")
    public void updateWorkOrderMaintenance(@RequestBody MaintenanceWorkOrderDTO dto) {
        workOrderMaintenanceService.updateWorkOrderMaintenance(dto);
    }

    /***
     * 功能描述: 后台编辑维护工单信息
     *
     * @param:
     * @auther: liu yi
     * @date: 2019/5/17 14:22
     * @return:
     */
    @PatchMapping(value = "/order/maintenanceWorkOrder/system")
    @ApiOperation(value = "后台编辑维护工单信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "维护工单Id", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "materielDetailIds", value = "耗材id", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "materielDetailNames", value = "耗材名称", dataType = "String", required = true, paramType = "query")
    })
    public Object editworkOrderMaintenanceBySystem(@RequestParam String id, @RequestParam String materielDetailIds, @RequestParam String materielDetailNames) {
        workOrderMaintenanceService.editworkOrderMaintenanceBySystem(id, materielDetailIds, materielDetailNames);

        return ResponseEntity.noContent().build();
    }


    @PostMapping(value = "/order/maintenanceWorkOrder/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "查询维护工单列表", notes = "查询维护工单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", dataType = "Long", paramType = "path")
    })
    public Object listMaintenanceWorkOrder(@RequestBody MaintenanceWorkOrderQueryDTO queryDTO,
                                           @PathVariable("pageNum") Integer pageNum,
                                           @PathVariable("pageSize") Integer pageSize) {
        PageVO<MaintenanceWorkOrderDTO> page = workOrderMaintenanceService.listMaintenanceWorkOrder(pageNum, pageSize, queryDTO);

        return ResponseEntity.ok(page);
    }

    /**
     * @param id
     * @return
     * @description 根据工单号查询维护工单
     * @author Liu Yi
     */
    @GetMapping(value = "/order/maintenanceWorkOrder/{id}")
    @ApiOperation(value = "根据订单号查询维护工单", notes = "根据订单号查询维护工单")
    @ApiImplicitParam(name = "id", required = true, value = "id", dataType = "String", paramType = "path")
    public Object getWorkOrderMaintenanceById(@PathVariable("id") String id) {
        MaintenanceWorkOrderDTO dto = workOrderMaintenanceService.getWorkOrderMaintenanceById(id);

        return ResponseEntity.ok(dto);
    }

    /**
     * @param workCode
     * @return
     * @description 根据工单号查询维护工单
     * @author Liu Yi
     */
    @GetMapping(value = "/order/maintenanceWorkOrder")
    @ApiOperation(value = "根据订单号查询维护工单", notes = "根据订单号查询维护工单")
    @ApiImplicitParam(name = "workCode", required = true, value = "workCode", dataType = "String", paramType = "query")
    public Object getWorkOrderMaintenanceByWorkCode(@RequestParam("workCode") String workCode) {
        MaintenanceWorkOrderDTO dto = workOrderMaintenanceService.getWorkOrderMaintenanceByWorkCode(workCode);

        return ResponseEntity.ok(dto);
    }

    /**
     * @param sncode
     * @param state
     * @param workOrderCompleteStatus
     * @return
     * @description 根据SN查询维护工单
     * @author Liu Yi
     */
    @GetMapping(value = "/order/maintenanceWorkOrder/sn")
    @ApiOperation(value = "根据SN查询维护工单", notes = "根据SN查询维护工单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sncode", value = "SN", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "维护工单状态：1-待维护 2-处理中 3-挂单 4-已完成", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "workOrderCompleteStatus", value = "完成状态：Y-完成 ，N-未完成", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "source", value = "来源：1-自动生成 2-总部添加", dataType = "Long", paramType = "query")
    })
    public Object getWorkOrderMaintenanceBySnCode(@RequestParam(value = "sncode", required = false) String sncode,
                                                  @RequestParam(value = "state", required = false) Integer state,
                                                  @RequestParam(value = "workOrderCompleteStatus", required = false) String workOrderCompleteStatus,
                                                  @RequestParam(value = "source", required = false) Integer source) {
        List<MaintenanceWorkOrderDTO> dtoList = workOrderMaintenanceService.getWorkOrderMaintenanceBySnCode(sncode, state, workOrderCompleteStatus, source);

        return ResponseEntity.ok(dtoList);
    }

    /***
     * 功能描述:检查没有完成的维护工单
     *
     * @param: [deviceSncode, materiels, engineerId]
     * @auther: liu yi
     * @date: 2019/4/10 15:46
     * @return: java.lang.Boolean
     */
    @GetMapping(value = "/order/maintenanceWorkOrder/getNotComplete")
    @ApiOperation(value = "检查没有完成的维护工单", notes = "检查没有完成的维护工单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceSncode", value = "SN", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "engineerId", value = "安装工id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "source", value = "来源：1-自动生成 2-总部添加，默认自动生成", dataType = "Long", paramType = "query")
    })
    public Object getNotCompleteWorkOrderMaintenance(@RequestParam(value = "deviceSncode", required = false) String deviceSncode,
                                                     @RequestParam(value = "engineerId", required = false) Integer engineerId,
                                                     @RequestParam(value = "source", required = false) Integer source) {
        return workOrderMaintenanceService.getNotCompleteWorkOrderMaintenance(deviceSncode, engineerId, source);
    }

    @DeleteMapping(value = "/order/maintenanceWorkOrder/{id}")
    @ApiOperation(value = "根据id删除维护工单", notes = "根据id删除维护工单")
    @ApiImplicitParam(name = "id", value = "维护工单信息", required = true, dataType = "String", paramType = "path")
    public Object delete(@PathVariable(value = "id") String id) {
        workOrderMaintenanceService.deleteMaintenanceWorkOrderById(id);
        return ResponseEntity.noContent().build();
    }

    /***
     * 功能描述:审核
     *
     * @param: [ids, effective]
     * @auther: liu yi
     * @date: 2019/5/16 16:53
     * @return: java.lang.Object
     */
    @PatchMapping(value = "/order/maintenanceWorkOrder/{id}/audit")
    @ApiOperation(value = "审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "维护工单id", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "recordIds", value = "记录ID集合", dataType = "Long", paramType = "query", allowMultiple = true),
            @ApiImplicitParam(name = "effective", value = "生效状态：0-否；1-是", defaultValue = "1", dataType = "Long", paramType = "query")
    })
    public Object auditMaintenanceWorkOrder(@PathVariable(value = "id") String id,
                                            @RequestParam(value = "recordIds", required = false) Integer[] recordIds,
                                            @RequestParam(value = "effective", defaultValue = "1") Integer effective) {
        workOrderMaintenanceService.auditMaintenanceWorkOrder(id, recordIds, effective);
        return ResponseEntity.noContent().build();
    }

    /***
     * 功能描述:同步更新最后完成时间
     *
     * @param: [hour]
     * @auther: liu yi
     * @date: 2019/5/30 17:30
     * @return: java.lang.Object
     */
    @PatchMapping(value = "/order/maintenanceWorkOrder/{hour}/finishTime")
    @ApiOperation(value = "更新最后完成时间")
    @ApiImplicitParam(name = "hour", value = "小时", required = true, defaultValue = "1", dataType = "Long", paramType = "path")
    public Object updateLasteFinishTime(@PathVariable(value = "hour") Integer hour) {
        workOrderMaintenanceService.updateLasteFinishTime(hour);
        return ResponseEntity.noContent().build();
    }


    /**
     * 服务统计-工单服务-维护统计
     *
     * @param completeTime
     * @param engineerId
     * @return
     */
    @GetMapping(value = "/order/maintenanceWorkOrder/statistics")
    public List<RenewDTO> getMaintenanceWorkOrderList(@RequestParam(value = "completeTime") String completeTime,
                                                      @RequestParam(value = "engineerId") Integer engineerId,
                                                      @RequestParam(value = "timeType") Integer timeType) {
        return workOrderMaintenanceService.getMaintenanceWorkOrderList(completeTime, engineerId, timeType);
    }


    /**
     * 市场服务-维护模块-工单数量统计
     *
     * @param engineerId
     * @return
     */
    @GetMapping(value = "/order/maintenance/workorder/statistics/count")
    public Map<String, Integer> getMaintenanceWorkOrderCount(@RequestParam(value = "engineerId") Integer engineerId) {
        return workOrderMaintenanceService.getMaintenanceWorkOrderCount(engineerId);
    }


    /**
     * 维护模块总工单数量 待维护、处理中、挂单
     * @param engineerId
     * @return
     */
    @GetMapping(value = "/order/maintenance/workorder/count/total")
    public Integer getMaintenanceModelWorkOrderTotalCount(@RequestParam(value = "engineerId") Integer engineerId){
        return workOrderMaintenanceService.getMaintenanceModelWorkOrderTotalCount(engineerId);
    }


    //=====================================================================

    /**
     * @param distributorId 经销商ID
     * @param engineerId    安装工ID
     * @param state         状态
     * @param search        查询条件
     * @return
     * @description 安装工APP查询维护工单列表
     */
    @GetMapping(value = "/order/maintenanceWorkOrder/app/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询维护工单列表", notes = "查询维护工单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "distributorId", value = "经销商ID", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "engineerId", value = "安装工id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "维护工单状态：1-待维护 2-处理中 3-挂单 4-已完成", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "search", value = "搜索条件", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "longitude", value = "经度", dataType = "DOUBLE", paramType = "query"),
            @ApiImplicitParam(name = "latitude", value = "纬度", dataType = "DOUBLE", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "第几页", defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", dataType = "Long", paramType = "path")
    })
    public List<MaintenanceDTO> listMaintenanceWorkOrderForClient(@RequestParam(value = "distributorId", required = false) String distributorId,
                                                                  @RequestParam(value = "engineerId", required = false) Integer engineerId,
                                                                  @RequestParam(value = "state", required = false) Integer state,
                                                                  @RequestParam(value = "search", required = false) String search,
                                                                  @RequestParam(value = "longitude", required = false) Double longitude,
                                                                  @RequestParam(value = "latitude", required = false) Double latitude,
                                                                  @PathVariable("pageNum") Integer pageNum,
                                                                  @PathVariable("pageSize") Integer pageSize) {
        return workOrderMaintenanceService.listMaintenanceWorkOrderForClient(pageNum, pageSize, state, distributorId, engineerId, search, longitude, latitude);
    }


    /**
     * 功能描述：根据状态查询维护工单数量
     *
     * @param engineerId 安装工id
     */
    @GetMapping(value = "/order/maintenanceWorkOrder/count")
    @ApiOperation(value = "根据状态查询维护工单数量", notes = "根据状态查询维护工单数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "state", value = "维护工单状态： 1-待维护,2-处理中,3-挂单 4-已完成 -1-全部", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "engineerId", value = "安装工id", dataType = "String", paramType = "query")
    })
    public Object getWorkOrderMaintenanceCount(@RequestParam(value = "engineerId", required = false) Integer engineerId) {
        Map<Integer, Object> maintenanceWorkOrderMap = new HashMap<>(8);
        //待维护数量
        maintenanceWorkOrderMap.put(1, workOrderMaintenanceService.getWorkOrderMaintenanceCount(engineerId, 1));
        //处理中数量
        maintenanceWorkOrderMap.put(2, workOrderMaintenanceService.getWorkOrderMaintenanceCount(engineerId, 2));
        //挂单数量
        maintenanceWorkOrderMap.put(3, workOrderMaintenanceService.getWorkOrderMaintenanceCount(engineerId, 3));
        //已完成数量
        maintenanceWorkOrderMap.put(4, workOrderMaintenanceService.getWorkOrderMaintenanceCount(engineerId, 4));
        //总数量
        maintenanceWorkOrderMap.put(-1, workOrderMaintenanceService.getWorkOrderMaintenanceCount(engineerId, null));
        return ResponseEntity.ok(maintenanceWorkOrderMap);
    }

    /**
     * 功能描述：修改维护工单信息
     * 场景：开始维护/改约/确认维护完成
     */
    @PutMapping(value = "/order/maintenanceWorkOrder/hang")
    @ApiOperation(value = "修改维护工单信息", notes = "适用：开始维护/改约/完成")
    @ApiImplicitParam(name = "maintenanceDTO", value = "维护工单信息", required = true, dataType = "MaintenanceDTO", paramType = "body")
    public ResponseEntity hangMaintenanceWorkOrder(@RequestBody MaintenanceDTO maintenanceDTO) {
        workOrderMaintenanceService.hangMaintenanceWorkOrder(maintenanceDTO);
        return ResponseEntity.noContent().build();
    }


    /**
     * 描述：已完成维护工单
     *
     * @param engineerId 安装工ID
     * @return
     * @description 已完成维护工单
     */
    @GetMapping(value = "/order/maintenanceWorkOrder/app/complete/{pageNum}/{pageSize}")
    @ApiOperation(value = "已完成维护工单", notes = "已完成维护工单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "engineerId", value = "安装工id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "sortType", value = "排序:1-升序 2-降序", defaultValue = "2", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageNum", value = "第几页", defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", dataType = "Long", paramType = "path")
    })
    public Object maintenanceWorkOrderCompleteList(@RequestParam(value = "engineerId", required = false) Integer engineerId,
                                                   @RequestParam("sortType") Integer sortType,
                                                   @PathVariable("pageNum") Integer pageNum,
                                                   @PathVariable("pageSize") Integer pageSize) {
        return ResponseEntity.ok(workOrderMaintenanceService.maintenanceWorkOrderCompleteList(engineerId, sortType, pageNum, pageSize));
    }


    /**
     * 描述：APP维护工单记录详情
     * 区分：同一个SN,同一状态多次维护，单独显示
     *
     * @param engineerId   安装工ID
     * @param deviceSncode SN码
     * @return
     * @description 维护记录详情
     */
    @GetMapping(value = "/order/maintenanceWorkOrder/app/record/detail")
    @ApiOperation(value = "维护工单记录详情", notes = "维护工单记录详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "engineerId", value = "安装工id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "deviceSncode", value = "SN", dataType = "String", paramType = "query")
    })
    public Object maintenanceWorkOrderRecordDetail(@RequestParam(value = "engineerId") Integer engineerId,
                                                   @RequestParam(value = "deviceSncode") String deviceSncode) {
        return ResponseEntity.ok(workOrderMaintenanceService.maintenanceWorkOrderRecordDetail(engineerId, deviceSncode));
    }


//    /**
//     * 需求变动 2020-07-09
//     * 描述：业务系统-维护工单详情
//     * 区分：同一个SN,同一状态多次维护，统一显示
//     * 返回的数据格式不同。
//     *
//     * @param state        状态
//     * @param deviceSncode SN码
//     * @return
//     * @description 维护记录详情
//     */
//    @GetMapping(value = "/order/maintenanceWorkOrder/system/record/detail")
//    @ApiOperation(value = "业务系统-维护工单详情", notes = "业务系统-维护工单详情")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "deviceSncode", value = "SN", dataType = "String", paramType = "query"),
//            @ApiImplicitParam(name = "state", value = "状态：1-未受理 2-处理中,3-改约,4-已完成", dataType = "Long", paramType = "query")
//    })
//    public Object maintenanceWorkOrderDetail(@RequestParam(value = "deviceSncode") String deviceSncode, @RequestParam(value = "state") Integer state) {
//        return ResponseEntity.ok(workOrderMaintenanceService.maintenanceWorkOrderDetail(deviceSncode, state));
//    }


    /**
     * 站务系统-查询维护工单列表
     *
     * @param query
     * @param pageNum
     * @param pageSize
     * @return
     */
    @PostMapping(value = "/order/maintenanceWorkOrder/station/{pageNum}/{pageSize}")
    @ApiOperation(value = "站务系统-查询维护工单列表", notes = "站务系统-查询维护工单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "query", value = "查询条件", dataType = "StationMaintenanceOrderQuery", paramType = "body")
    })
    public PageVO<MaintenanceWorkOrderDTO> listMaintenanceOrderToStation(@RequestBody StationMaintenanceOrderQuery query,
                                                                         @PathVariable("pageNum") Integer pageNum,
                                                                         @PathVariable("pageSize") Integer pageSize) {
        return workOrderMaintenanceService.listMaintenanceOrderToStation(pageNum, pageSize, query);

    }
}
