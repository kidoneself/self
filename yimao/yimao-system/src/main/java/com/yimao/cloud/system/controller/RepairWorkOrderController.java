package com.yimao.cloud.system.controller;


import com.yimao.cloud.pojo.dto.order.RepairWorkOrderDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.out.RepairWorkOrderVO;
import com.yimao.cloud.system.feign.OrderFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 维修工单
 *
 * @author Liu Yi
 * @date 2019/3/20.
 */
@RestController
@Slf4j
@Api(tags = "WorkOrderRepairController")
public class RepairWorkOrderController {
    @Resource
    private OrderFeign orderFeign;

    /**
     * @param isFather
     * @param distributorId
     * @param engineerId
     * @param state
     * @param orderStatus
     * @param search
     * @param pageNum
     * @param pageSize
     * @return
     * @description 查询维修工单列表
     * @author Liu Yi
     */
    @GetMapping(value = "/order/repairWorkOrder/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询维修工单列表", notes = "查询维修工单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isFather", required = true, value = "是否是企业帐号Y-是，N-否", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "distributorId", value = "经销商ID", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "维修工单状态：2-已受理,3-处理中,4-已完成", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "engineerId", value = "安装工id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "orderStatus", value = "订单类型", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "search", value = "搜索条件", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "第几页", defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", dataType = "Long", paramType = "path")
    })
    public Object page(@RequestParam(value = "isFather") String isFather,
                       @RequestParam(value = "distributorId", required = false) String distributorId,
                       @RequestParam(value = "engineerId", required = false) Integer engineerId,
                       @RequestParam(value = "state", required = false) Integer state,
                       @RequestParam(value = "orderStatus", required = false) String orderStatus,
                       @RequestParam(value = "search", required = false) String search,
                       @PathVariable("pageNum") Integer pageNum,
                       @PathVariable("pageSize") Integer pageSize) {
        PageVO<RepairWorkOrderDTO> page = orderFeign.page(isFather, distributorId, engineerId, state, orderStatus, search, pageNum, pageSize);
        return ResponseEntity.ok(page);
    }

    /**
     * @param id
     * @return
     * @description 根据id查询维修工单
     * @author Liu Yi
     */
    @GetMapping(value = "/order/repairWorkOrder/{id}")
    @ApiOperation(value = "根据id查询维修工单", notes = "根据id查询维修工单")
    @ApiImplicitParam(name = "id", required = true, value = "工单ID", dataType = "Long", paramType = "path")
    public Object getWorkOrderRepairById(@PathVariable("id") Integer id) {
        RepairWorkOrderVO vo = orderFeign.getWorkOrderRepairById(id);
        return ResponseEntity.ok(vo);
    }

}
