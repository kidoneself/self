package com.yimao.cloud.out.controller.openapi;

import com.yimao.cloud.base.constant.AliConstant;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.constant.WechatConstant;
import com.yimao.cloud.base.enums.OrderType;
import com.yimao.cloud.base.enums.PayPlatform;
import com.yimao.cloud.base.enums.PayReceiveType;
import com.yimao.cloud.base.enums.StatusEnum;
import com.yimao.cloud.base.enums.SystemType;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.out.constant.PayConstant;
import com.yimao.cloud.out.enums.ApiStatusCode;
import com.yimao.cloud.out.feign.OrderFeign;
import com.yimao.cloud.out.feign.SystemFeign;
import com.yimao.cloud.out.utils.ApiResult;
import com.yimao.cloud.out.utils.ResultUtil;
import com.yimao.cloud.pojo.dto.order.AliPayRequest;
import com.yimao.cloud.pojo.dto.order.WechatPayRequest;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import com.yimao.cloud.pojo.dto.system.OnlineAreaDTO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;

/***
 * 安装工工单支付
 *
 * @author zhangbaobao
 * @date 2019/10/16
 */
@RestController
@Api(tags = "WorkOrderPayController")
@Slf4j
public class WorkOrderPayController {

    @Resource
    private OrderFeign orderFeign;
    @Resource
    private SystemFeign systemFeign;

    @Resource
    private DomainProperties domainProperties;

    /**
     * 原微服务接口-扫码支付获取二维码链接 (支付宝、微信统一)
     *
     * @param paymentId 工单号
     */
    @GetMapping(value = "/pay/getCodeUrl")
    public Map<String, Object> getCodeUrl(HttpServletRequest request,
                                          @RequestParam(required = false) String body,
                                          @RequestParam(required = false) String spbill_create_ip,
                                          @RequestParam String paymentId) {
        try {
            log.info("============安装工获取支付二维码参数====paymentId" + paymentId + ",body=" + body + ",spbill_create_ip=" + spbill_create_ip);
            WorkOrderDTO workOrder = orderFeign.getWorkOrderById(paymentId);
            if (workOrder == null) {
                return ApiResult.error(request, ApiStatusCode.WORKORDER_NOT_EXSISTS);
            }
            if (workOrder.getPay() != null && workOrder.getPay()) {
                return ApiResult.error(request, "184", "工单已支付");
            }

            // 微信获取支付二维码
            WechatPayRequest payRequest = new WechatPayRequest();
            payRequest.setOut_trade_no(paymentId + PayConstant.SUFFIX_000);
            payRequest.setTotal_fee(workOrder.getFee().doubleValue());
            payRequest.setBody(Constant.PAY_BODY);
            payRequest.setSpbillCreateIp(spbill_create_ip);
            payRequest.setTradeType(WechatConstant.NATIVE);
            payRequest.setNotifyUrl(domainProperties.getApi() + WechatConstant.WXPAY_NOTIFY_URL_FOUR);
            payRequest.setOrderType(OrderType.WORKORDER.value);
            payRequest.setPlatform(PayPlatform.WECHAT.value);
            payRequest.setClientType(SystemType.ENGINEER.value);
            payRequest.setReceiveType(PayReceiveType.ONE.value);
            String code_url = orderFeign.wechatScanCodePay(payRequest);
            Map<String, Object> data = new HashMap<>();
            if (StringUtils.isNotEmpty(code_url)) {
                data.put("wx_codeUrl", code_url);
            }
            // 支付宝获取二维码
            AliPayRequest aliPayRequest = new AliPayRequest();
            aliPayRequest.setOutTradeNo(paymentId + PayConstant.SUFFIX_000);
            aliPayRequest.setTotalAmount(workOrder.getFee().doubleValue());
            aliPayRequest.setSubject(Constant.PAY_BODY);
            aliPayRequest.setNotifyUrl(domainProperties.getApi() + AliConstant.ALIPAY_NOTIFY_URL_FOUR);
            aliPayRequest.setOrderType(OrderType.WORKORDER.value);
            aliPayRequest.setPlatform(PayPlatform.ALI.value);
            aliPayRequest.setClientType(SystemType.ENGINEER.value);
            aliPayRequest.setReceiveType(PayReceiveType.ONE.value);
            String qrCode = orderFeign.aliScanCodePay(aliPayRequest);
            if (StringUtil.isNotEmpty(qrCode)) {
                data.put("alipay_codeUrl", qrCode);
            }
            return ApiResult.result(request, data);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ApiResult.error(request, ApiStatusCode.SER_EXCEPTION);
        }
    }

    /**
     * 原微服务接口-查询支付结果
     *
     * @param paymentId 工单号
     */
    @GetMapping(value = "/pay/payStatus")
    public Map<String, Object> payStatus(HttpServletRequest request, @RequestParam String paymentId) {
        try {
            Thread.sleep(1000);
            WorkOrderDTO workorder = orderFeign.getWorkOrderById(paymentId);
            if (null != workorder) {
                return ApiResult.result(request, workorder.getPay());
            }
            return ApiResult.error(request, ApiStatusCode.WORKORDER_NOT_EXSISTS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ApiResult.result(request, false);
        }
    }

    /**
     * 原微服务接口-安装工APP微信APP支付
     *
     * @param paymentId 工单号
     */
    @GetMapping(value = "/pay/wxpay/getPrepayId")
    public Map<String, Object> wechatpay(HttpServletRequest request,
                                         @RequestParam Integer type,
                                         @RequestParam String body,
                                         @RequestParam String spbill_create_ip,
                                         @RequestParam String paymentId) {
        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(paymentId);
        if (workOrder == null) {
            return ApiResult.error(request, ApiStatusCode.WORKORDER_NOT_EXSISTS);
        }
        if (workOrder.getPay() != null && workOrder.getPay()) {
            return ApiResult.error(request, "184", "工单已支付");
        }
        // 微信获取支付二维码
        WechatPayRequest payRequest = new WechatPayRequest();
        payRequest.setOut_trade_no(paymentId + PayConstant.SUFFIX_999);
        payRequest.setTotal_fee(workOrder.getFee().doubleValue());
        payRequest.setBody(Constant.PAY_BODY);
        payRequest.setSpbillCreateIp(spbill_create_ip);
        payRequest.setTradeType(WechatConstant.APP);
        payRequest.setNotifyUrl(domainProperties.getApi() + WechatConstant.WXPAY_NOTIFY_URL_FOUR);
        payRequest.setOrderType(OrderType.WORKORDER.value);
        payRequest.setPlatform(PayPlatform.WECHAT.value);
        payRequest.setClientType(SystemType.ENGINEER.value);
        payRequest.setReceiveType(PayReceiveType.ONE.value);
        SortedMap<String, Object> map = orderFeign.wechatAppPay(payRequest);
        if (map != null && !map.isEmpty() && map.get("prepayid") != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("prepay_id", map.get("prepayid"));
            data.put("timestamp", map.get("timestamp"));
            data.put("nonce_str", map.get("noncestr"));
            data.put("mchId", map.get("partnerid"));
            data.put("sign", map.get("sign"));
            return ApiResult.result(request, data);
        } else {
            return ApiResult.error(request, "支付遇到问题");
        }
    }

    /**
     * 支付宝获取支付订单信息
     *
     * @param paymentId 工单号
     */
    @GetMapping(value = "/pay/alipay/getOrderStr")
    public Map<String, Object> alipay(HttpServletRequest request,
                                      @RequestParam String paymentId,
                                      @RequestParam String subject) {
        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(paymentId);
        if (workOrder == null) {
            return ApiResult.error(request, ApiStatusCode.WORKORDER_NOT_EXSISTS);
        }
        if (workOrder.getPay() != null && workOrder.getPay()) {
            return ApiResult.error(request, "184", "工单已支付");
        }
        // 支付宝获取二维码
        AliPayRequest aliPayRequest = new AliPayRequest();
        aliPayRequest.setOutTradeNo(paymentId + PayConstant.SUFFIX_999);
        aliPayRequest.setTotalAmount(workOrder.getFee().doubleValue());
        aliPayRequest.setSubject(Constant.PAY_BODY);
        aliPayRequest.setNotifyUrl(domainProperties.getApi() + AliConstant.ALIPAY_NOTIFY_URL_FOUR);
        aliPayRequest.setOrderType(OrderType.WORKORDER.value);
        aliPayRequest.setPlatform(PayPlatform.ALI.value);
        aliPayRequest.setClientType(SystemType.ENGINEER.value);
        aliPayRequest.setReceiveType(PayReceiveType.ONE.value);
        Map<String, String> map = orderFeign.aliAppPay(aliPayRequest);
        if (map != null && !map.isEmpty() && map.get("orderStr") != null) {
            return ApiResult.result(request, map.get("orderStr"));
        } else {
            return ApiResult.error(request, "支付遇到问题");
        }
    }

    /**
     * 原云平台接口-扫码支付获取二维码链接（微信）
     */
    @PostMapping(value = "/pay/wxpay/geturl")
    public Map<String, Object> wxpayurl(@RequestParam String body,
                                        @RequestParam String nonce_str,
                                        @RequestParam String spbill_create_ip,
                                        @RequestParam String out_trade_no) {
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        try {
            log.info("============老流程安装工获取微信支付二维码参数====body" + body
                    + ",nonce_str=" + nonce_str + ",spbill_create_ip=" + spbill_create_ip + ",out_trade_no" + out_trade_no);

            String workOrderId = getWorkOrderId(out_trade_no);

            WorkOrderDTO workOrder = orderFeign.getWorkOrderById(workOrderId);
            if (workOrder == null) {
                ResultUtil.error(ru, "59", "工单不存在");
                return ru;
            }
            if (workOrder.getPay() != null && workOrder.getPay()) {
                ResultUtil.error(ru, "59", "工单已支付");
                return ru;
            }
            String province = workOrder.getProvince();
            String city = workOrder.getCity();
            String region = workOrder.getRegion();
            OnlineAreaDTO onlineArea = systemFeign.getOnlineAreaByPCR(province, city, region);
            //服务站正在上线新流程校验
            if (onlineArea != null && !Objects.equals(onlineArea.getSyncState(), StatusEnum.YES.value())) {
                ResultUtil.error(ru, "73", "您所在地区正在升级新流程，用户支付不允许支付!");
                return ru;
            }

            // 微信获取支付二维码
            WechatPayRequest payRequest = new WechatPayRequest();
            payRequest.setOut_trade_no(workOrderId + PayConstant.SUFFIX_000);
            payRequest.setTotal_fee(workOrder.getFee().doubleValue());
            payRequest.setBody(Constant.PAY_BODY);
            payRequest.setSpbillCreateIp(spbill_create_ip);
            payRequest.setTradeType(WechatConstant.NATIVE);
            payRequest.setNotifyUrl(domainProperties.getApi() + WechatConstant.WXPAY_NOTIFY_URL_FOUR);
            payRequest.setOrderType(OrderType.WORKORDER.value);
            payRequest.setPlatform(PayPlatform.WECHAT.value);
            payRequest.setClientType(SystemType.ENGINEER.value);
            payRequest.setReceiveType(PayReceiveType.ONE.value);
            String code_url = orderFeign.wechatScanCodePay(payRequest);
            if (StringUtils.isNotEmpty(code_url)) {
                ru.put("codeurl", code_url);
                return ru;
            }
            ResultUtil.error(ru, "00", "支付遇到问题");
            return ru;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ResultUtil.error(ru, "00", "系统异常");
            return ru;
        }
    }

    /**
     * 原云平台接口-扫码支付获取二维码链接（支付宝）
     */
    @PostMapping(value = "/pay/alipay/geturl")
    public Map<String, Object> alipayurl(@RequestParam String out_trade_no, @RequestParam String subject) {
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        try {
            log.info("============老流程安装工获取支付宝支付二维码参数====out_trade_no" + out_trade_no + ",subject=" + subject);
            WorkOrderDTO workOrder = orderFeign.getWorkOrderById(out_trade_no);
            if (workOrder == null) {
                ResultUtil.error(ru, "59", "工单不存在");
                return ru;
            }
            if (workOrder.getPay() != null && workOrder.getPay()) {
                ResultUtil.error(ru, "59", "工单已支付");
                return ru;
            }
            String province = workOrder.getProvince();
            String city = workOrder.getCity();
            String region = workOrder.getRegion();
            OnlineAreaDTO onlineArea = systemFeign.getOnlineAreaByPCR(province, city, region);
            //服务站正在上线新流程校验
            if (onlineArea != null && !Objects.equals(onlineArea.getSyncState(), StatusEnum.YES.value())) {
                ResultUtil.error(ru, "73", "您所在地区正在升级新流程，用户支付不允许支付!");
                return ru;
            }

            // 支付宝获取二维码
            AliPayRequest aliPayRequest = new AliPayRequest();
            aliPayRequest.setOutTradeNo(out_trade_no + PayConstant.SUFFIX_000);
            aliPayRequest.setTotalAmount(workOrder.getFee().doubleValue());
            aliPayRequest.setSubject(Constant.PAY_BODY);
            aliPayRequest.setNotifyUrl(domainProperties.getApi() + AliConstant.ALIPAY_NOTIFY_URL_FOUR);
            aliPayRequest.setOrderType(OrderType.WORKORDER.value);
            aliPayRequest.setPlatform(PayPlatform.ALI.value);
            aliPayRequest.setClientType(SystemType.ENGINEER.value);
            aliPayRequest.setReceiveType(PayReceiveType.ONE.value);
            String qrCode = orderFeign.aliScanCodePay(aliPayRequest);
            if (StringUtil.isNotEmpty(qrCode)) {
                ru.put("codeurl", qrCode);
                return ru;
            }
            ResultUtil.error(ru, "00", "支付遇到问题");
            return ru;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ResultUtil.error(ru, "00", "系统异常");
            return ru;
        }
    }

    /**
     * 原云平台接口-微信APP支付
     */
    @PostMapping(value = "/pay/wxpay")
    public Map<String, Object> wxpay(@RequestParam(required = false, defaultValue = "0") Integer type,
                                     @RequestParam String body,
                                     @RequestParam String nonce_str,
                                     @RequestParam String spbill_create_ip,
                                     @RequestParam String out_trade_no) {
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        String workOrderId = getWorkOrderId(out_trade_no);
        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(workOrderId);
        if (workOrder == null) {
            ResultUtil.error(ru, "59", "工单不存在");
            return ru;
        }
        if (workOrder.getPay() != null && workOrder.getPay()) {
            ResultUtil.error(ru, "59", "工单已支付");
            return ru;
        }
        // 微信获取支付二维码
        WechatPayRequest payRequest = new WechatPayRequest();
        payRequest.setOut_trade_no(workOrderId + PayConstant.SUFFIX_999);
        payRequest.setTotal_fee(workOrder.getFee().doubleValue());
        payRequest.setBody(Constant.PAY_BODY);
        payRequest.setSpbillCreateIp(spbill_create_ip);
        payRequest.setTradeType(WechatConstant.APP);
        payRequest.setNotifyUrl(domainProperties.getApi() + WechatConstant.WXPAY_NOTIFY_URL_FOUR);
        payRequest.setOrderType(OrderType.WORKORDER.value);
        payRequest.setPlatform(PayPlatform.WECHAT.value);
        payRequest.setClientType(SystemType.ENGINEER.value);
        payRequest.setReceiveType(PayReceiveType.ONE.value);
        SortedMap<String, Object> map = orderFeign.wechatAppPay(payRequest);
        if (map != null && !map.isEmpty() && map.get("prepayid") != null) {
            ru.put("prepay_id", map.get("prepayid"));
            ru.put("timestamp", map.get("timestamp"));
            ru.put("nonce_str", map.get("noncestr"));
            ru.put("mchId", map.get("partnerid"));
            ru.put("sign", map.get("sign"));
            return ru;
        } else {
            ResultUtil.error(ru, "00", "支付遇到问题");
            return ru;
        }
    }

    /**
     * 原云平台接口-支付宝APP支付
     */
    @PostMapping(value = "/pay/alipay/getOrderStr")
    public Map<String, Object> alipay2(@RequestParam String out_trade_no, @RequestParam String subject) {
        Map<String, Object> ru = new HashMap<>();
        ResultUtil.success(ru);
        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(out_trade_no);
        if (workOrder == null) {
            ResultUtil.error(ru, "59", "工单不存在");
            return ru;
        }
        if (workOrder.getPay() != null && workOrder.getPay()) {
            ResultUtil.error(ru, "59", "工单已支付");
            return ru;
        }
        // 支付宝获取二维码
        AliPayRequest aliPayRequest = new AliPayRequest();
        aliPayRequest.setOutTradeNo(out_trade_no + PayConstant.SUFFIX_999);
        aliPayRequest.setTotalAmount(workOrder.getFee().doubleValue());
        aliPayRequest.setSubject(Constant.PAY_BODY);
        aliPayRequest.setNotifyUrl(domainProperties.getApi() + AliConstant.ALIPAY_NOTIFY_URL_FOUR);
        aliPayRequest.setOrderType(OrderType.WORKORDER.value);
        aliPayRequest.setPlatform(PayPlatform.ALI.value);
        aliPayRequest.setClientType(SystemType.ENGINEER.value);
        aliPayRequest.setReceiveType(PayReceiveType.ONE.value);
        Map<String, String> map = orderFeign.aliAppPay(aliPayRequest);
        if (map != null && !map.isEmpty() && map.get("orderStr") != null) {
            ru.put("orderStr", map.get("orderStr"));
            return ru;
        } else {
            ResultUtil.error(ru, "00", "支付遇到问题");
            return ru;
        }
    }

        private String getWorkOrderId(String out_trade_no) {
        if (out_trade_no.startsWith("AZ")) {
            return out_trade_no.substring(0, 15);
        } else if (out_trade_no.length() > 23) {
            return out_trade_no.substring(0, 21);
        } else if (StringUtils.isNumeric(out_trade_no)) {
            return out_trade_no.substring(0, 21);
        } else {
            return out_trade_no.substring(0, 17);
        }
    }

}
