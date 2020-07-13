package com.yimao.cloud.app.controller;

import com.yimao.cloud.app.feign.OrderFeign;
import com.yimao.cloud.base.constant.WechatConstant;
import com.yimao.cloud.base.context.SpringContextHolder;
import com.yimao.cloud.base.enums.EnvironmentEnum;
import com.yimao.cloud.base.enums.OrderType;
import com.yimao.cloud.base.enums.PayPlatform;
import com.yimao.cloud.base.enums.PayReceiveType;
import com.yimao.cloud.base.enums.SystemType;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.order.WechatPayRequest;
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
 * 微信支付
 *
 * @author Zhang Bo
 * @date 2017/12/4.
 */
@RestController
@Slf4j
@Api(tags = "WXPayController")
public class WXPayController {

    @Resource
    private OrderFeign orderFeign;
    @Resource
    private DomainProperties domainProperties;

    /**
     * 统一下单
     */
    @PostMapping(value = "/wxpay/unified/order", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "微信统一下单（APP端）")
    @ApiImplicitParam(name = "payRequest", value = "统一下单请求对象", required = true, dataType = "WechatPayRequest", paramType = "body")
    public Object unifiedorder(@RequestBody WechatPayRequest payRequest) {
        //校验
        if (StringUtil.isBlank(payRequest.getOut_trade_no())) {
            throw new YimaoException("订单号不能为空！");
        }
        if (payRequest.getTotal_fee() == null) {
            throw new YimaoException("订单支付金额不能为空！");
        }
        payRequest.setTradeType(WechatConstant.APP);
        payRequest.setNotifyUrl(domainProperties.getApi() + WechatConstant.WXPAY_NOTIFY_URL_ONE);
        payRequest.setOrderType(OrderType.PRODUCT.value);

        payRequest.setPlatform(PayPlatform.WECHAT.value);
        payRequest.setClientType(SystemType.JXSAPP.value);
        payRequest.setReceiveType(PayReceiveType.ONE.value);
        if (SpringContextHolder.getEnvironment().equalsIgnoreCase(EnvironmentEnum.PRO.code)) {
            payRequest.setBody("翼猫APP商品");
        } else {
            payRequest.setBody("翼猫APP商品测试");
        }
        return orderFeign.unifiedorder(payRequest);
    }

    /**
     * 经销商订单统一下单(APP端)
     */
    @PostMapping(value = "/wxpay/unified/distributorOrder", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "经销商订单微信统一下单（APP端）")
    @ApiImplicitParam(name = "payRequest", value = "统一下单请求对象", required = true, dataType = "WechatPayRequest", paramType = "body")
    public Object unifiedDistributorOrder(@RequestBody WechatPayRequest payRequest) {
        //校验
        if (StringUtil.isBlank(payRequest.getOut_trade_no())) {
            throw new YimaoException("订单号不能为空！");
        }
        if (payRequest.getTotal_fee() == null) {
            throw new YimaoException("订单支付金额不能为空！");
        }
        payRequest.setTradeType(WechatConstant.APP);
        payRequest.setNotifyUrl(domainProperties.getApi() + WechatConstant.WXPAY_NOTIFY_URL_THREE);
        payRequest.setOrderType(OrderType.DISTRIBUTOR.value);

        payRequest.setPlatform(PayPlatform.WECHAT.value);
        payRequest.setClientType(SystemType.JXSAPP.value);
        payRequest.setReceiveType(PayReceiveType.TWO.value);
        if (SpringContextHolder.getEnvironment().equalsIgnoreCase(EnvironmentEnum.PRO.code)) {
            payRequest.setBody("翼猫APP经销商升级续费");
        } else {
            payRequest.setBody("翼猫APP经销商升级续费测试");
        }
        return orderFeign.unifiedorder(payRequest);
    }

    /**
     * 订单查询
     */
    @PostMapping(value = "/wxpay/query/order", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "微信订单查询")
    @ApiImplicitParam(name = "payRequest", value = "支付订单查询实体类", required = true, dataType = "WechatPayRequest", paramType = "body")
    public Object orderQuery(@RequestBody WechatPayRequest payRequest) {
        payRequest.setTradeType(WechatConstant.APP);
        return orderFeign.orderQuery(payRequest);
    }

}
