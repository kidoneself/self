package com.yimao.cloud.app.controller;

import com.yimao.cloud.app.feign.OrderFeign;
import com.yimao.cloud.base.constant.AliConstant;
import com.yimao.cloud.base.context.SpringContextHolder;
import com.yimao.cloud.base.enums.EnvironmentEnum;
import com.yimao.cloud.base.enums.OrderType;
import com.yimao.cloud.base.enums.PayPlatform;
import com.yimao.cloud.base.enums.PayReceiveType;
import com.yimao.cloud.base.enums.SystemType;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.order.AliPayRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 支付宝支付
 *
 * @author Zhang Bo
 * @date 2019/9/9
 */
@RestController
@Slf4j
@Api(tags = "AlipayController")
public class AlipayController {

    @Resource
    private OrderFeign orderFeign;
    @Resource
    private DomainProperties domainProperties;

    /**
     * 支付宝下单
     */
    @PostMapping(value = "/alipay/tradeapp", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "支付宝下单（APP端）")
    @ApiImplicitParam(name = "payRequest", value = "支付宝支付下单请求参数", required = true, dataType = "AliPayRequest", paramType = "body")
    public Object tradeapp(@RequestBody AliPayRequest payRequest) {
        //校验
        if (StringUtil.isBlank(payRequest.getOutTradeNo())) {
            throw new YimaoException("订单号不能为空！");
        }
        if (payRequest.getTotalAmount() == null) {
            throw new YimaoException("订单支付金额不能为空！");
        }
        if (SpringContextHolder.getEnvironment().equalsIgnoreCase(EnvironmentEnum.PRO.code)) {
            payRequest.setSubject("APP端商品下单");
        } else {
            payRequest.setSubject("APP支付测试");
        }
        payRequest.setNotifyUrl(domainProperties.getApi() + AliConstant.ALIPAY_NOTIFY_URL_ONE);
        payRequest.setOrderType(OrderType.PRODUCT.value);

        payRequest.setPlatform(PayPlatform.ALI.value);
        payRequest.setClientType(SystemType.JXSAPP.value);
        payRequest.setReceiveType(PayReceiveType.ONE.value);
        return orderFeign.tradeapp(payRequest);
    }

    /**
     * 经销商订单下单
     */
    @PostMapping(value = "/alipay/tradeapp/distributorOrder", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "经销商订单支付宝下单（APP端）")
    @ApiImplicitParam(name = "payRequest", value = "支付宝支付下单请求参数", required = true, dataType = "AliPayRequest", paramType = "body")
    public Object tradeappForDistributorOrder(@RequestBody AliPayRequest payRequest) {
        //校验
        if (StringUtil.isBlank(payRequest.getOutTradeNo())) {
            throw new YimaoException("订单号不能为空！");
        }
        if (payRequest.getTotalAmount() == null) {
            throw new YimaoException("订单支付金额不能为空！");
        }
        if (SpringContextHolder.getEnvironment().equalsIgnoreCase(EnvironmentEnum.PRO.code)) {
            payRequest.setSubject("翼猫APP经销商升级续费");
        } else {
            payRequest.setSubject("翼猫APP经销商升级续费测试");
        }
        payRequest.setNotifyUrl(domainProperties.getApi() + AliConstant.ALIPAY_NOTIFY_URL_THREE);
        payRequest.setOrderType(OrderType.DISTRIBUTOR.value);

        payRequest.setPlatform(PayPlatform.ALI.value);
        payRequest.setClientType(SystemType.JXSAPP.value);
        payRequest.setReceiveType(PayReceiveType.TWO.value);
        return orderFeign.tradeapp(payRequest);
    }

    /**
     * 订单查询
     */
    @PostMapping(value = "/alipay/tradequery/order", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "支付宝订单查询")
    @ApiImplicitParam(name = "payRequest", value = "支付宝支付下单请求参数", required = true, dataType = "AliPayRequest", paramType = "body")
    public Object tradequeryOrder(@RequestBody AliPayRequest payRequest) {
        //校验
        if (StringUtil.isBlank(payRequest.getOutTradeNo())) {
            throw new YimaoException("订单号不能为空！");
        }
        return orderFeign.tradequery(payRequest);
    }

}
