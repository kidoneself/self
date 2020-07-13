package com.yimao.cloud.wechat.controller;

import com.yimao.cloud.base.constant.WechatConstant;
import com.yimao.cloud.base.context.SpringContextHolder;
import com.yimao.cloud.base.enums.EnvironmentEnum;
import com.yimao.cloud.base.enums.OrderType;
import com.yimao.cloud.base.enums.PayPlatform;
import com.yimao.cloud.base.enums.PayReceiveType;
import com.yimao.cloud.base.enums.SystemType;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.WXPayUtil;
import com.yimao.cloud.pojo.dto.order.WechatPayRequest;
import com.yimao.cloud.wechat.feign.OrderFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    private Lock lock = new ReentrantLock();

    @Resource
    private OrderFeign orderFeign;
    @Resource
    private DomainProperties domainProperties;

    /**
     * 统一下单
     */
    @PostMapping(value = "/wxpay/unified/order", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "微信统一下单（公众号端）")
    @ApiImplicitParam(name = "payRequest", value = "统一下单请求对象", required = true, dataType = "WechatPayRequest", paramType = "body")
    public Object unifiedorder(@RequestBody WechatPayRequest payRequest) {
        //校验
        if (StringUtil.isBlank(payRequest.getOut_trade_no())) {
            throw new BadRequestException("订单号不能为空！");
        }
        if (payRequest.getTotal_fee() == null) {
            throw new BadRequestException("订单支付金额不能为空！");
        }
        if (StringUtil.isBlank(payRequest.getOpenId())) {
            throw new BadRequestException("用户openid不能为空！");
        }
        payRequest.setTradeType(WechatConstant.JSAPI);
        payRequest.setNotifyUrl(domainProperties.getApi() + WechatConstant.WXPAY_NOTIFY_URL_ONE);
        payRequest.setOrderType(OrderType.PRODUCT.value);

        payRequest.setPlatform(PayPlatform.WECHAT.value);
        payRequest.setClientType(SystemType.WECHAT.value);
        payRequest.setReceiveType(PayReceiveType.ONE.value);
        if (SpringContextHolder.getEnvironment().equalsIgnoreCase(EnvironmentEnum.PRO.code)) {
            payRequest.setBody("翼猫健康e家商品");
        } else {
            payRequest.setBody("翼猫健康e家商品测试");
        }
        return orderFeign.unifiedorder(payRequest);
    }

    /**
     * 微信回调
     */
    @RequestMapping(value = "/wxpay/notify", produces = MediaType.APPLICATION_XML_VALUE)
    public String gzhCallback(HttpServletRequest request) {
        log.info("=======================微信公众号支付回调开始===========");
        InputStream inStream = null;
        ByteArrayOutputStream outSteam = null;
        try {
            lock.lock();
            inStream = request.getInputStream();
            outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            outSteam.flush();
            outSteam.close();
            inStream.close();
            String wxpayCallbackResult = new String(outSteam.toByteArray(), "utf-8");//获取微信调用我们notify_url的返回信息
            log.info("===========================================");
            log.info("微信支付回调返回结果为===" + wxpayCallbackResult);
            log.info("===========================================");
            return orderFeign.wxJsapiPayCallback(wxpayCallbackResult);
        } catch (Exception e) {
            log.error("微信回调发生异常：" + e.getMessage(), e);
            return WXPayUtil.getPayCallback("FAIL", "程序发生错误");
        } finally {
            if (outSteam != null) {
                try {
                    outSteam.close();
                } catch (IOException e) {
                    log.error("微信回调关闭输出流时发生异常：");
                }
            }
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    log.error("微信回调关闭输入流时发生异常：");
                }
            }
            lock.unlock();
        }
    }

    /**
     * 订单查询
     */
    @PostMapping(value = "/wxpay/query/order", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "微信订单查询")
    @ApiImplicitParam(name = "payRequest", value = "支付订单查询实体类", required = true, dataType = "WechatPayRequest", paramType = "body")
    public Object orderQuery(@RequestBody WechatPayRequest payRequest) {
        log.info("wx支付订单查询");
        payRequest.setTradeType(WechatConstant.JSAPI);
        return orderFeign.orderQuery(payRequest);
    }
}
