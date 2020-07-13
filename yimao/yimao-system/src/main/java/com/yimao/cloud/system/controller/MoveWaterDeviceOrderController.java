package com.yimao.cloud.system.controller;

import com.yimao.cloud.pojo.dto.order.MoveWaterDeviceOrderDTO;
import com.yimao.cloud.pojo.query.order.MoveWaterDeviceOrderQuery;
import com.yimao.cloud.system.feign.OrderFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 移机工单
 *
 * @author Liu Long Jie
 * @date 2020-6-29
 */
@RestController
@Slf4j
@Api(tags = "MoveWaterDeviceOrderController")
public class MoveWaterDeviceOrderController {

    @Resource
    private OrderFeign orderFeign;

    /**
     * 业务管理后台 - 创建移机工单
     *
     * @param dto
     */
    @PostMapping(value = "/move/water/device/order/save")
    @ApiOperation(value = "创建移机工单")
    @ApiImplicitParam(name = "dto", value = "移机工单数据", dataType = "MoveWaterDeviceOrderDTO", paramType = "body")
    public Object save(@RequestBody MoveWaterDeviceOrderDTO dto) {
        orderFeign.save(dto);
        return ResponseEntity.noContent().build();
    }


    /**
     * 移机工单分页展示
     *
     * @param pageNum
     * @param pageSize
     * @param query
     * @return
     */
    @GetMapping(value = "/move/water/device/order/{pageNum}/{pageSize}")
    public Object page(@PathVariable(value = "pageNum") Integer pageNum,
                       @PathVariable(value = "pageSize") Integer pageSize,
                       @RequestBody MoveWaterDeviceOrderQuery query) {

        return ResponseEntity.ok(orderFeign.moveWaterOrderPage(pageNum, pageSize, query));
    }


    @GetMapping(value = "/move/water/device/order/{id}")
    public Object getMoveWaterDeviceOrderDetailsById(@PathVariable(value = "id") String id) {
        return orderFeign.getMoveWaterDeviceOrderById(id);
    }

}
