package com.yimao.cloud.system.controller;

import com.yimao.cloud.pojo.dto.order.OrderConfigDTO;
import com.yimao.cloud.system.feign.OrderFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @description: 订单配置
 * @author: yu chunlei
 * @create: 2019-08-08 15:59:28
 **/
@RestController
@Slf4j
@Api(tags = "OrderConfigController")
public class OrderConfigController {

    @Resource
    private OrderFeign orderFeign;


    /**
     * @Author ycl
     * @Description 添加订单设置
     * @Date 16:15 2019/8/8
     * @Param
     **/
    @PostMapping(value = "/order/orderConfig")
    @ApiOperation(value = "添加订单配置信息", notes = "添加订单配置信息")
    @ApiImplicitParam(name = "orderConfigDTO", value = "订单配置实体类", required = true, dataType = "OrderConfigDTO", paramType = "body")
    public Object addOrderConfig(@RequestBody OrderConfigDTO orderConfigDTO) {
        orderFeign.addOrderConfig(orderConfigDTO);
        return ResponseEntity.noContent().build();
    }

    /**
     * @Author ycl
     * @Description 编辑订单设置
     * @Date 16:42 2019/8/8
     * @Param
     **/
    @PutMapping(value = "/order/orderConfig")
    @ApiOperation(value = "编辑订单设置", notes = "编辑订单设置")
    @ApiImplicitParam(name = "orderConfigDTO", value = "订单配置实体类", required = true, dataType = "OrderConfigDTO", paramType = "body")
    public Object updateOrderConfig(@RequestBody OrderConfigDTO orderConfigDTO) {
        orderFeign.updateOrderConfig(orderConfigDTO);
        return ResponseEntity.noContent().build();
    }


    @GetMapping(value = "/order/orderConfig")
    @ApiOperation(value = "查询订单设置", notes = "查询订单设置")
    public OrderConfigDTO getOrderConfig() {
        return orderFeign.getOrderConfig();
    }


}
