package com.yimao.cloud.order.controller;

import com.yimao.cloud.order.service.WorkOrderBackService;
import com.yimao.cloud.pojo.dto.order.RenewDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderBackDTO;
import com.yimao.cloud.pojo.query.order.WorkOrderBackQueryDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Liu Yi
 * @description 退机工单控制层
 * @date 2020/6/22 11:01
 */
@RestController
@Slf4j
@Api(tags = "WorkOrderBackController")
public class WorkOrderBackController {
    @Resource
    private WorkOrderBackService workOrderBackService;

    /**
     * 根据安装工查询退机工单数量
     */
    @GetMapping(value = "/workorderBack/engineer/count")
    @ApiOperation(value = "根据安装工查询退机工单数量")
    public List<Map<String, Object>> getWorkOrderBackCountByEngineerId() {
        List<Map<String, Object>> list = workOrderBackService.getWorkOrderBackCountByEngineerId();

        return list;
    }

    /**
     * 根据条件查询退机工单信息
     */
    @GetMapping(value = "/workorderBack/{pageNum}/{pageSize}")
    @ApiOperation(value = "根据条件查询安装工单信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "查询信息", dataType = "WorkOrderBackQueryDTO", paramType = "body"),
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", required = true, paramType = "path")
    })
    public ResponseEntity<PageVO<WorkOrderBackDTO>> getWorkOrderBackList(@RequestBody WorkOrderBackQueryDTO query, @PathVariable(value = "pageNum") Integer pageNum, @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<WorkOrderBackDTO> orderList = workOrderBackService.getWorkOrderBackList(query, pageNum, pageSize);
        return ResponseEntity.ok(orderList);
    }

    /**
     * 描述：根据工单id获取退机工单信息
     *
     * @param id 退机工单ID
     **/
    @GetMapping(value = "/workorderBack/{id}")
    @ApiOperation(value = "根据工单id获取退机工单信息", notes = "根据工单id获取退机工单信息")
    @ApiImplicitParam(name = "id", value = "工单ID", dataType = "Long", required = true, paramType = "path")
    public WorkOrderBackDTO getWorkOrderBackById(@PathVariable(value = "id") Integer id) {
        WorkOrderBackDTO dto = workOrderBackService.getWorkOrderBackById(id);
        return dto;
    }

    /**
     * 修改退机工单
     *
     * @param workOrderBackDTO 工单
     */
    @PutMapping(value = "/workorderBack")
    @ApiOperation(value = "修改退机工单", notes = "修改退机工单")

    public void updateWorkOrderBack(@RequestBody WorkOrderBackDTO workOrderBackDTO) {
        workOrderBackService.updateWorkOrderBack(workOrderBackDTO);
    }

    /**
     * 描述：确认提交
     **/
    @PutMapping(value = "/workorderBack/{id}/finsh")
    @ApiOperation(value = "校验sn是否为拆机对应的设备", notes = "校验sn是否为拆机对应的设备")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "工单ID", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "snCode", value = "snCode", dataType = "Long", required = true, paramType = "query")
    })
    public void finishWorkOrderBack(@PathVariable(value = "id") Integer id, @RequestParam(value = "snCode") String snCode) {
        workOrderBackService.finishWorkOrderBack(id, snCode);
    }

    /**
     * 服务统计-工单服务-退机统计
     *
     * @param completeTime
     * @param engineerId
     * @return
     */
    @GetMapping(value = "/workorderBack/statistics/list")
    public List<RenewDTO> statisticsWorkOrderBack(@RequestParam(value = "completeTime") String completeTime,
                                                  @RequestParam(value = "engineerId") Integer engineerId,
                                                  @RequestParam(value = "timeType") Integer timeType) {
        return workOrderBackService.queryWorkOrderBackList(completeTime, engineerId, timeType);
    }


    /**
     * 市场服务-退机模块各个状态的工单数量
     *
     * @param engineerId
     * @return
     */
    @GetMapping(value = "/workorder/back/statistics/count")
    public Map<String, Integer> getWorkOrderBackCount(@RequestParam(value = "engineerId") Integer engineerId) {
        return workOrderBackService.getWorkOrderBackCount(engineerId);
    }


    /**
     * 退机模块总工单数量 待处理、处理中、挂单
     * @param engineerId
     * @return
     */
    @GetMapping(value = "/back/model/water/order/total")
    public Integer getBackModelTotalCount(@RequestParam(value = "engineerId") Integer engineerId) {
        return workOrderBackService.getBackModelTotalCount(engineerId);
    }

    /**
     * 站务系统 - 退机工单 - 更换安装工
     *
     * @param id          移机工单号
     * @param engineerId  替换原服务人员的安装工id
     * @param engineerIds 操作人管理的售后服务站门店下的安装工
     * @param source      来源端
     * @param operator    操作人
     */
    @PostMapping(value = "/workorderBack/changeEngineer/{id}")
    public Object workOrderBackChangeEngineer(@PathVariable(value = "id") Integer id,
                                              @RequestParam(value = "engineerId") Integer engineerId,
                                              @RequestParam(value = "engineerIds") List<Integer> engineerIds,
                                              @RequestParam(value = "source") Integer source,
                                              @RequestParam(value = "operator") String operator) {
        workOrderBackService.workOrderBackChangeEngineer(id, engineerId, engineerIds, source,operator);
        return ResponseEntity.noContent().build();
    }

}
