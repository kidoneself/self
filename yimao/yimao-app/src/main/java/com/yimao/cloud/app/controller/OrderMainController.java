package com.yimao.cloud.app.controller;

import com.yimao.cloud.app.feign.OrderFeign;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.RemoteCallException;
import com.yimao.cloud.pojo.dto.order.OrderMainDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "OrderMainController")
public class OrderMainController {

    @Resource
    private OrderFeign orderFeign;

    @Resource
    private UserCache userCache;


    /**
     * @description   下单
     * @author zhilin.he
     * @date 2018/12/28 17:28
     * @param orderDTO
     * @param type
     * @return java.lang.Object
     */
    // @PostMapping(value = "/order/{type}")
    // @ApiOperation(value = "下单", notes = "下单")
    // @ApiImplicitParams({
    //         @ApiImplicitParam(name = "orderDTO", value = "订单信息", required = true, dataType = "OrderDTO", paramType = "body"),
    //         @ApiImplicitParam(name = "type", value = "下单类型", required = true, dataType = "String", paramType = "path")
    // })
    // public Object insert(@RequestBody OrderDTO orderDTO, @PathVariable("type") String type) {
    //     if (orderDTO == null) {
    //         throw new BadRequestException("订单不能为空！");
    //     }
    //     orderDTO.setTerminal(OrderFrom.DIS_APP.value);
    //     Integer result = orderFeign.insert(orderDTO,type);
    //     if(result!= null && result == -1){
    //         throw new RemoteCallException();
    //     }
    //     return ResponseEntity.noContent().build();
    // }

    /**
     * @description   根据id查询主订单
     * @author zhilin.he
     * @date 2018/12/28 17:28
     * @param id
     * @return org.springframework.http.ResponseEntity
     */
    @GetMapping(value = "/order/{id}")
    @ApiOperation(value = "根据id查询主订单", notes = "根据id查询主订单")
    @ApiImplicitParam(name = "id", value = "主订单id", required = true, dataType = "Long", paramType = "path")
    public ResponseEntity findById(@PathVariable("id") Long id) {
        OrderMainDTO orderMainDTO = orderFeign.findById(id);
        if(orderMainDTO.getId() == -1){
            throw new RemoteCallException();
        }
        return ResponseEntity.ok(orderMainDTO);
    }



}
