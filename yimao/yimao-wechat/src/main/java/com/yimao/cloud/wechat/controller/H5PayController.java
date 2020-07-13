package com.yimao.cloud.wechat.controller;

import com.yimao.cloud.base.constant.WechatConstant;
import com.yimao.cloud.base.context.SpringContextHolder;
import com.yimao.cloud.base.enums.*;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.order.WechatPayRequest;
import com.yimao.cloud.wechat.feign.OrderFeign;
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
 * @author liuhao
 */
@RestController
@Slf4j
@Api(tags = "H5PayController")
public class H5PayController {

    @Resource
    private OrderFeign orderFeign;
    @Resource
    private DomainProperties domainProperties;

    /**
     * 统一下单（H5）
     */
    @PostMapping(value = "/h5pay/unified/order", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "微信统一下单（H5端）")
    @ApiImplicitParam(name = "payRequest", value = "统一下单请求对象", required = true, dataType = "WechatPayRequest", paramType = "body")
    public Object unifiedorder(@RequestBody WechatPayRequest payRequest) {
        //校验
        if (StringUtil.isBlank(payRequest.getOut_trade_no())) {
            throw new BadRequestException("订单号不能为空！");
        }
        if (payRequest.getTotal_fee() == null) {
            throw new BadRequestException("订单支付金额不能为空！");
        }
//        payRequest.setTradeType(WechatConstant.MWEB);
        //用的是微信内置浏览器-属于jsapi
        payRequest.setTradeType(WechatConstant.JSAPI);
        payRequest.setNotifyUrl(domainProperties.getApi() + WechatConstant.WXPAY_NOTIFY_URL_ONE);
        payRequest.setOrderType(OrderType.PRODUCT.value);

        //支付平台 微信支付-h5支付
        payRequest.setPlatform(PayPlatform.WECHAT.value);
        //支付客户端-H5页面
        payRequest.setClientType(SystemType.H5.value);
        payRequest.setReceiveType(PayReceiveType.ONE.value);
        if (SpringContextHolder.getEnvironment().equalsIgnoreCase(EnvironmentEnum.PRO.code)) {
            payRequest.setBody("翼猫健康e家商品");
        } else {
            payRequest.setBody("翼猫健康e家商品测试");
        }
        return orderFeign.unifiedorder(payRequest);
    }


    /**
     * 订单查询
     */
    @PostMapping(value = "/h5pay/query/order", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "微信订单查询")
    @ApiImplicitParam(name = "payRequest", value = "支付订单查询实体类", required = true, dataType = "WechatPayRequest", paramType = "body")
    public Object orderQuery(@RequestBody WechatPayRequest payRequest) {
        log.info("H5支付订单查询");
        payRequest.setTradeType(WechatConstant.MWEB);
        return orderFeign.orderQuery(payRequest);
    }
}
