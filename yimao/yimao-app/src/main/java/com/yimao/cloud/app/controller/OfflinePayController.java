package com.yimao.cloud.app.controller;

import com.yimao.cloud.app.feign.OrderFeign;
import com.yimao.cloud.base.enums.PayReceiveType;
import com.yimao.cloud.base.enums.PayType;
import com.yimao.cloud.base.enums.SystemType;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.msg.CommResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 描述：线下支付（POS机、转账）
 *
 * @Author Zhang Bo
 * @Date 2019/9/18
 */
@RestController
@Slf4j
@Api(tags = "OfflinePayController")
public class OfflinePayController {

    @Resource
    private OrderFeign orderFeign;

    /**
     * 翼猫APP获取线下支付信息
     *
     * @param outTradeNo 支付单号（主订单号）
     */
    @GetMapping(value = "/offlinePayInfo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "outTradeNo", value = "支付单号/主订单号（type=2时无需传递）", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "订单类型：1-普通商品；2-经销商升级续费订单；", dataType = "Long", paramType = "query", required = true)
    })
    @ApiOperation(value = "翼猫APP获取线下支付信息")
    public Object getOtherPayInfo(@RequestParam(required = false) String outTradeNo, @RequestParam Integer type) {
        if (type == 1) {
            return CommResult.ok(orderFeign.getPayAccount(outTradeNo, null, PayType.OTHER.value, SystemType.JXSAPP.value, PayReceiveType.ONE.value));
        } else if (type == 2) {
            return CommResult.ok(orderFeign.getPayAccount(null, 10000, PayType.OTHER.value, SystemType.JXSAPP.value, PayReceiveType.ONE.value));
        } else {
            throw new BadRequestException("订单类型参数错误");
        }
    }

    /**
     * 线下支付（POS机、转账）提交支付凭证
     */
    @PostMapping(value = "/otherpay/submitcredential")
    @ApiOperation(value = "线下支付（POS机、转账）提交支付凭证")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "支付订单号", dataType = "Long", paramType = "query", required = true),
            @ApiImplicitParam(name = "payType", value = "支付类型：1-微信；2-支付宝；3-POS机；4-转账；", dataType = "Long", paramType = "query", required = true),
            @ApiImplicitParam(name = "payCredential", value = "线下支付凭证", dataType = "String", paramType = "query", required = true)
    })
    public void submitCredential(@RequestParam Long id, @RequestParam Integer payType, @RequestParam String payCredential) {
        orderFeign.otherPaySubmitCredential(id, payType, payCredential);
    }

}
