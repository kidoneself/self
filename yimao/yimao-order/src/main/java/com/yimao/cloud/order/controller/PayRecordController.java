package com.yimao.cloud.order.controller;

import com.yimao.cloud.order.service.PayRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Zhang Bo
 * @date 2018/11/7.
 */
@RestController
@Api(tags = "PayRecordController")
public class PayRecordController {

    @Resource
    private PayRecordService payRecordService;

    /**
     * @description  根据支付记录id查询支付记录信息
     * @param payId
     */
    @GetMapping(value = {"/order/pay/record/{payId}"})
    @ApiOperation(value = "根据支付记录id查询支付记录信息", notes = "根据支付记录id查询支付记录信息")
    @ApiImplicitParam(name = "payId", value = "订单ID", dataType = "Long", required = true, paramType = "path")
    public Object findPayRecordById(@PathVariable("payId") Integer payId) {
        return ResponseEntity.ok(payRecordService.findPayRecordById(payId));
    }

}
