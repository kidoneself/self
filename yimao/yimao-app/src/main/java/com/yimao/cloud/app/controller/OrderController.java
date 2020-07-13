package com.yimao.cloud.app.controller;

import com.yimao.cloud.app.feign.OrderFeign;
import com.yimao.cloud.app.feign.UserFeign;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.Terminal;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.pojo.dto.order.OrderDTO;
import com.yimao.cloud.pojo.dto.order.WxOrderDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 订单
 *
 * @author hhf
 * @date 2019/7/9
 */
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@Api(tags = "OrderController")
public class OrderController {

    @Resource
    private UserCache userCache;
    @Resource
    private OrderFeign orderFeign;
    @Resource
    private UserFeign userFeign;

    /**
     * 根据订单状态查询订单(查询所有)
     *
     * @param status    0-待付款 2-待发货 4-待收货 5-交易成功
     * @param beginTime 下单开始时间
     * @param endTime   下单结束时间
     * @param keys      收货人姓名/手机号
     * @param orderId   订单号
     */
    @GetMapping("/order/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询订单列表根据订单查询条件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "status", value = "订单状态:0-待付款 2-待发货 4-待收货 5-交易成功", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "beginTime", value = "下单开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "下单结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "keys", value = "收货人姓名/手机号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "orderId", value = "订单号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", required = true, paramType = "path")})
    public ResponseEntity orderList(@RequestParam(required = false) Integer status,
                                    @RequestParam(required = false) String beginTime,
                                    @RequestParam(required = false) String endTime,
                                    @RequestParam(required = false) String keys,
                                    @RequestParam(required = false) String orderId,
                                    @PathVariable(value = "pageNum") Integer pageNum,
                                    @PathVariable(value = "pageSize") Integer pageSize) {
        UserDTO userDTO = userFeign.getBasicUserById(userCache.getUserId());
        if (Objects.isNull(userDTO)) {
            throw new YimaoException("用户信息不存在");
        }
        PageVO<WxOrderDTO> pageVO = orderFeign.wxOrderList(userDTO.getId(), status, beginTime, endTime, keys, orderId, pageNum, pageSize);
        return ResponseEntity.ok(pageVO);
    }

    /**
     * 下单
     *
     * @param type 1-单件商品下单；2-购物车下单
     */
    @PostMapping(value = "/order/{type}")
    @ApiOperation(value = "下单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderDTO", value = "订单信息", required = true, dataType = "OrderDTO", paramType = "body"),
            @ApiImplicitParam(name = "type", value = "下单类型", required = true, dataType = "String", paramType = "path")
    })
    public ResponseEntity createOrder(@RequestBody OrderDTO orderDTO, @PathVariable(value = "type") String type) {
        orderDTO.setTerminal(Terminal.YIMAO_APP.value);
        return ResponseEntity.ok(orderFeign.createOrder(orderDTO, type));
    }

}
