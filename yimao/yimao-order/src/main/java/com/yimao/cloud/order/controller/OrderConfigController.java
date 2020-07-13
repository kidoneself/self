package com.yimao.cloud.order.controller;

import com.yimao.cloud.order.po.OrderConfig;
import com.yimao.cloud.order.service.OrderConfigService;
import com.yimao.cloud.pojo.dto.order.OrderConfigDTO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @description: 订单设置
 * @author: yu chunlei
 * @create: 2019-08-08 16:23:03
 **/
@RestController
@Slf4j
@Api(tags = "OrderConfigController")
public class OrderConfigController {

    @Resource
    private OrderConfigService orderConfigService;


    /**
     * @Author ycl
     * @Description 添加订单设置
     * @Date 16:35 2019/8/8
     * @Param
     **/
    @PostMapping(value = "/order/orderConfig")
    public Object addOrderConfig(@RequestBody OrderConfigDTO orderConfigDTO) {
        OrderConfig orderConfig = new OrderConfig(orderConfigDTO);
        orderConfigService.addOrderConfig(orderConfig);
        return ResponseEntity.noContent().build();
    }


    /**
     * @Author ycl
     * @Description 编辑订单设置
     * @Date 16:37 2019/8/8
     * @Param
     **/
    @PutMapping(value = "/order/orderConfig")
    public Object updateOrderConfig(@RequestBody OrderConfigDTO orderConfigDTO) {
        OrderConfig orderConfig = new OrderConfig(orderConfigDTO);
        orderConfigService.updateOrderConfig(orderConfig);
        return ResponseEntity.noContent().build();
    }


    @GetMapping(value = "/order/orderConfig")
    public OrderConfigDTO getOrderConfig() {
        return orderConfigService.getOrderConfig();
    }


}
