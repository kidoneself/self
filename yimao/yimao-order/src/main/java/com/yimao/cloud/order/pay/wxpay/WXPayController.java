package com.yimao.cloud.order.pay.wxpay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.constant.WechatConstant;
import com.yimao.cloud.base.enums.OrderType;
import com.yimao.cloud.base.enums.PayPlatform;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.AES256ECBUtil;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.IpUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.UUIDUtil;
import com.yimao.cloud.base.utils.WXPayUtil;
import com.yimao.cloud.framework.cache.RedisLock;
import com.yimao.cloud.order.feign.UserFeign;
import com.yimao.cloud.order.mapper.RefundRecordMapper;
import com.yimao.cloud.order.po.OrderMain;
import com.yimao.cloud.order.po.OrderRenew;
import com.yimao.cloud.order.po.RefundRecord;
import com.yimao.cloud.order.service.OrderMainService;
import com.yimao.cloud.order.service.OrderRenewService;
import com.yimao.cloud.order.service.PayAccountService;
import com.yimao.cloud.order.service.PayRecordService;
import com.yimao.cloud.order.service.WorkOrderService;
import com.yimao.cloud.order.service.WxPayService;
import com.yimao.cloud.pojo.dto.order.PayAccountDetail;
import com.yimao.cloud.pojo.dto.order.PayRecordDTO;
import com.yimao.cloud.pojo.dto.order.WechatPayRequest;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import com.yimao.cloud.pojo.dto.user.DistributorOrderDTO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

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

    private static final String WX_CALLBACK_LOCK = "WX_CALLBACK_LOCK";
    private static final String WX_CALLBACK_LOCK_1 = "WX_CALLBACK_LOCK_1";
    private static final String WX_CALLBACK_LOCK_2 = "WX_CALLBACK_LOCK_2";
    private static final String WX_CALLBACK_LOCK_3 = "WX_CALLBACK_LOCK_3";
    private static final String WX_CALLBACK_LOCK_4 = "WX_CALLBACK_LOCK_4";
    private static final String WX_CALLBACK_LOCK_REFUND = "WX_CALLBACK_LOCK_REFUND";

    // private Lock lock = new ReentrantLock();
    @Resource
    private RedisLock redisLock;

    @Resource
    private WxPayService wxPayService;
    @Resource
    private PayRecordService payRecordService;
    @Resource
    private OrderMainService mainOrderService;
    @Resource
    private OrderRenewService orderRenewService;
    @Resource
    private PayAccountService payAccountService;
    @Resource
    private UserFeign userFeign;

    @Resource
    private RefundRecordMapper refundRecordMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private WorkOrderService workOrderService;

    /**
     * 微信统一下单
     */
    @PostMapping(value = "/wxpay/unified/order", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object unifiedorder(@RequestBody WechatPayRequest payRequest, HttpServletRequest request) {
        if (StringUtil.isBlank(payRequest.getBody())) {
            throw new YimaoException("商品描述不能为空！");
        }
        if (StringUtil.isBlank(payRequest.getOut_trade_no())) {
            throw new YimaoException("订单号不能为空！");
        }
        if (payRequest.getTotal_fee() == null) {
            throw new YimaoException("订单支付金额不能为空！");
        }
        if (StringUtil.isBlank(payRequest.getNotifyUrl())) {
            throw new YimaoException("支付回调地址不能为空！");
        }
        if (StringUtil.isBlank(payRequest.getTradeType())) {
            throw new YimaoException("交易类型不能为空！");
        }
        if (StringUtil.isBlank(payRequest.getOpenId()) && Objects.equals(WechatConstant.JSAPI, payRequest.getTradeType())) {
            log.error("======获取到的openid为======" + payRequest.getOpenId());
            throw new YimaoException("用户标识不能为空！");
        }

        //订单信息校验
        String out_trade_no = payRequest.getOut_trade_no();
        Double total_fee = payRequest.getTotal_fee();
        //1-商品订单 2-经销商订单 3-续费订单
        if (payRequest.getOrderType() == OrderType.PRODUCT.value) {
            //普通产品订单校验
            OrderMain mainOrder = mainOrderService.findById(Long.valueOf(out_trade_no));
            if (mainOrder == null) {
                throw new BadRequestException("订单号不存在");
            }
            if (mainOrder.getPay()) {
                throw new BadRequestException("不能重复支付");
            }
            if (mainOrder.getOrderAmountFee().doubleValue() != total_fee) {
                log.info(">>>>>>>>>>>总金额：" + mainOrder.getOrderAmountFee().doubleValue() + ",前端传入金额：" + total_fee + ">>>>>>>>>>>>");
                throw new YimaoException("订单金额有误，不能支付");
            }
            //查询支付账号需要用到产品公司ID
            Integer companyId = mainOrderService.getProductCompanyIdByOutTradeNo(Long.valueOf(out_trade_no));
            payRequest.setCompanyId(companyId);
        } else if (payRequest.getOrderType() == OrderType.RENEW.value) {
            //水机续费订单校验
            OrderRenew orderRenew = orderRenewService.getPayInfoById(out_trade_no);
            if (orderRenew == null) {
                throw new YimaoException("订单号不存在");
            }
            if (orderRenew.getPay()) {
                throw new YimaoException("不能重复支付");
            }
            if (Constant.PRO_ENVIRONMENT) {
                if (orderRenew.getAmountFee().doubleValue() != total_fee) {
                    throw new YimaoException("订单金额有误，不能支付");
                }
            }
            //查询支付账号需要用到产品公司ID
            payRequest.setCompanyId(10000);
        } else if (payRequest.getOrderType() == OrderType.DISTRIBUTOR.value) {
            //经销商订单校验
            DistributorOrderDTO distributorOrder = userFeign.findBasisDistributorOrderById(Long.valueOf(out_trade_no));
            if (distributorOrder == null) {
                throw new YimaoException("订单号不存在");
            }
            if (distributorOrder.getPayState() == 2) {
                throw new YimaoException("不能重复支付");
            }
            if (distributorOrder.getPrice().doubleValue() != total_fee) {
                throw new YimaoException("订单金额有误，不能支付");
            }
            //查询支付账号需要用到产品公司ID
            payRequest.setCompanyId(10000);
        } else if (payRequest.getOrderType() == OrderType.WORKORDER.value) {
            //工单订单校验
            WorkOrderDTO workorder = workOrderService.getWorkOrderById(out_trade_no.substring(0, out_trade_no.length() - 3));
            if (workorder == null) {
                throw new YimaoException("工单号不存在");
            }
            if (workorder.getPay()) {
                throw new YimaoException("不能重复支付");
            }
            if (workorder.getFee().doubleValue() != total_fee) {
                throw new YimaoException("订单金额有误，不能支付");
            }
            //查询支付账号需要用到产品公司ID
            payRequest.setCompanyId(10000);
        }

        if (!Constant.PRO_ENVIRONMENT) {
            log.info("/wxpay/unified/order---payRequest---" + JSON.toJSONString(payRequest));
        }

        //获取IP
        String ip = IpUtil.getIp(request);
        payRequest.setSpbillCreateIp(StringUtil.isEmpty(ip) ? "127.0.0.1" : ip);

        //获取支付账号信息
        PayAccountDetail payAccount = payAccountService.getPayAccountDetail(payRequest.getCompanyId(), payRequest.getPlatform(), payRequest.getClientType(), payRequest.getReceiveType());
        if (payAccount == null) {
            throw new YimaoException("下单失败，服务端缺少支付账号信息");
        }
        SortedMap<String, String> resp = wxPayService.unifiedOrder(payRequest, payAccount);
        if (Objects.equals(resp.get("return_code"), WechatConstant.FAIL)) {
            log.error(resp.get("return_msg"));
            log.error("======统一下单请求结果为======" + resp);
            throw new YimaoException(resp.get("return_msg"));
        }
        if (Objects.equals(resp.get("result_code"), WechatConstant.FAIL)) {
            log.error(resp.get("err_code") + " : " + resp.get("err_code_des"));
            log.error("======统一下单请求结果为======" + resp);
            throw new YimaoException(resp.get("err_code") + " : " + resp.get("err_code_des"));
        }
        if (Objects.equals(WechatConstant.JSAPI, payRequest.getTradeType())) {
            //组装微信内H5JS调起支付所需参数
            SortedMap<String, Object> map = new TreeMap<>();
            map.put("appId", payAccount.getAppid());
            map.put("nonceStr", UUIDUtil.longuuid32());
            map.put("package", "prepay_id=" + resp.get("prepay_id"));
            map.put("signType", WechatConstant.MD5);
            map.put("timeStamp", Long.toString(System.currentTimeMillis() / 1000));
            map.put("paySign", WXPayUtil.createSign(map, true, WechatConstant.MD5, payAccount.getKey()));
            log.info("======组装微信内JS调起支付所需参数为======" + map);
            return ResponseEntity.ok(map);
        } else if (Objects.equals(WechatConstant.APP, payRequest.getTradeType())) {
            SortedMap<String, Object> map = new TreeMap<>();
            map.put("appid", payAccount.getAppid());
            map.put("partnerid", payAccount.getMchId());
            map.put("prepayid", resp.get("prepay_id"));
            map.put("package", "Sign=WXPay");
            map.put("noncestr", UUIDUtil.longuuid32());
            map.put("timestamp", Long.toString(System.currentTimeMillis() / 1000));
            map.put("sign", WXPayUtil.createSign(map, true, WechatConstant.MD5, payAccount.getKey()));
            log.info("======组装微信APP支付所需参数======" + map);
            return ResponseEntity.ok(map);
        } else if (Objects.equals(WechatConstant.MWEB, payRequest.getTradeType())) {
            //H5支付
            SortedMap<String, Object> map = new TreeMap<>();
            map.put("appId", payAccount.getAppid());
            map.put("mchId", payAccount.getMchId());
            map.put("nonceStr", UUIDUtil.longuuid32());
            map.put("package", "prepay_id=" + resp.get("prepay_id"));
            map.put("mweburl", resp.get("mweb_url"));
            map.put("prepayid", resp.get("prepay_id"));
            map.put("signType", WechatConstant.MD5);
            map.put("outTradeNo", out_trade_no);
            map.put("timeStamp", Long.toString(System.currentTimeMillis() / 1000));
            map.put("sign", WXPayUtil.createSign(map, true, WechatConstant.MD5, payAccount.getKey()));
            log.info("微信H5支付，统一下单返回结果：" + map);
            return ResponseEntity.ok(map);
        } else if (Objects.equals(WechatConstant.NATIVE, payRequest.getTradeType())) {//扫码支付
            String code_url = resp.get("code_url");
            log.info("微信扫码支付模式二，统一下单返回结果：" + code_url);
            if (StringUtil.isNotEmpty(code_url)) {
                return ResponseEntity.ok(code_url);
            }
        }
        log.error("======统一下单请求结果为======" + resp);
        throw new YimaoException("统一下单失败。");
    }

    /**
     * 微信支付回调
     */
    private String callback(String wxpayCallbackResult, String queueName) {
        try {
            SortedMap<String, String> map = WXPayUtil.xmlToMap(wxpayCallbackResult);

            log.info("===========================================");
            log.info("微信支付回调返回结果为===" + map);
            log.info("===========================================");

            if (Objects.equals("SUCCESS", map.get("return_code"))) {
                if (Objects.equals(WechatConstant.SUCCESS, map.get("result_code"))) {
                    //自定义参数（支付套餐配置相关）
                    String attach = map.get("attach");
                    String[] arr = attach.split("_");
                    //获取支付账号信息
                    PayAccountDetail payAccount = payAccountService.getPayAccountDetail(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), Integer.parseInt(arr[2]), Integer.parseInt(arr[3]));
                    if (payAccount == null) {
                        log.error("维信支付回调发生错误，未获取到支付账号信息，自定义参数attach={}", attach);
                        return WXPayUtil.getPayCallback("FAIL", "自定义参数接收失败");
                    }
                    //签名校验
                    if (WXPayUtil.signValid(map, payAccount.getKey())) {
                        //订单号
                        String outTradeNo = map.get("out_trade_no");
                        boolean exists = payRecordService.existsWithOutTradeNo(outTradeNo);
                        //解决异步多次通知
                        if (!exists) {
                            payRecordService.saveWxPayRecord(map, queueName);
                        }
                        //回复微信
                        return WXPayUtil.getPayCallback("SUCCESS", "OK");
                    } else {
                        //回复微信
                        return WXPayUtil.getPayCallback("FAIL", "签名校验失败");
                    }
                } else {
                    //回复微信
                    return WXPayUtil.getPayCallback("FAIL", "result_code不正确");
                }
            } else {
                //回复微信
                return WXPayUtil.getPayCallback("FAIL", "return_code不正确");
            }
        } catch (Exception e) {
            log.error("微信回调发生异常：", e);
            return WXPayUtil.getPayCallback("FAIL", "程序发生错误");
        }
    }

    /**
     * 公众号微信支付回调
     *
     * @param wxpayCallbackResult 微信回调的入参信息
     */
    @GetMapping(value = "/wxjsapipay/notify")
    public String wxJspaiPayCallback(@RequestParam String wxpayCallbackResult) {
        try {
            //获取锁
            if (!redisLock.lock(WX_CALLBACK_LOCK)) {
                return WXPayUtil.getPayCallback("FAIL", "程序没有获取到执行权限");
            }
            return callback(wxpayCallbackResult, RabbitConstant.ORDER_PAY_CALLBACK);
        } finally {
            redisLock.unLock(WX_CALLBACK_LOCK);
        }
    }

    /**
     * 微信支付回调（普通商品）
     */
    @RequestMapping(value = "/wxpay/notify/one", produces = MediaType.APPLICATION_XML_VALUE)
    public String wxPayNotifyOne(HttpServletRequest request) {
        if (!Constant.PRO_ENVIRONMENT) {
            log.info("微信支付回调（普通商品）");
        }
        try {
            //获取锁
            if (!redisLock.lock(WX_CALLBACK_LOCK_1)) {
                return WXPayUtil.getPayCallback("FAIL", "程序没有获取到执行权限");
            }
            //从HTTP请求中获取微信返回信息
            String wxpayCallbackResult = getWxpayCallbackResult(request);
            return callback(wxpayCallbackResult, RabbitConstant.ORDER_PAY_CALLBACK);
        } finally {
            redisLock.unLock(WX_CALLBACK_LOCK_1);
        }
    }

    /**
     * 微信支付回调（续费）
     */
    @RequestMapping(value = "/wxpay/notify/two", produces = MediaType.APPLICATION_XML_VALUE)
    public String wxPayNotifyTwo(HttpServletRequest request) {
        if (!Constant.PRO_ENVIRONMENT) {
            log.info("微信支付回调（续费）");
        }
        try {
            //获取锁
            if (!redisLock.lock(WX_CALLBACK_LOCK_2)) {
                return WXPayUtil.getPayCallback("FAIL", "程序没有获取到执行权限");
            }
            //从HTTP请求中获取微信返回信息
            String wxpayCallbackResult = getWxpayCallbackResult(request);
            return callback(wxpayCallbackResult, RabbitConstant.RENEWORDER_PAY_CALLBACK);
        } finally {
            redisLock.unLock(WX_CALLBACK_LOCK_2);
        }
    }

    /**
     * @description 微信支付回调（经销商订单）
     * @author Liu Yi
     * @date 2019/9/21 11:28
     */
    @RequestMapping(value = "/wxpay/notify/three", produces = MediaType.APPLICATION_XML_VALUE)
    public String wxPayNotifyThree(HttpServletRequest request) {
        if (!Constant.PRO_ENVIRONMENT) {
            log.info("微信支付回调（经销商订单）");
        }
        try {
            //获取锁
            if (!redisLock.lock(WX_CALLBACK_LOCK_3)) {
                return WXPayUtil.getPayCallback("FAIL", "程序没有获取到执行权限");
            }
            //从HTTP请求中获取微信返回信息
            String wxpayCallbackResult = getWxpayCallbackResult(request);
            return callback(wxpayCallbackResult, RabbitConstant.DISTRIBUTOR_ORDER_PAY_CALLBACK);
        } finally {
            redisLock.unLock(WX_CALLBACK_LOCK_3);
        }
    }

    /**
     * 微信退款回调
     */
    @RequestMapping(value = "/wxrefund/notify/one", produces = MediaType.APPLICATION_XML_VALUE)
    public String wxRefundNotifyOne(HttpServletRequest request) {
        if (!Constant.PRO_ENVIRONMENT) {
            log.info("微信申请退款回调");
        }
        try {
            //获取锁
            if (!redisLock.lock(WX_CALLBACK_LOCK_REFUND)) {
                return WXPayUtil.getPayCallback("FAIL", "程序没有获取到执行权限");
            }
            //从HTTP请求中获取微信返回信息
            String wxpayCallbackResult = getWxpayCallbackResult(request);
            return refundCallback(wxpayCallbackResult, RabbitConstant.REFUND_CALLBACK);
        } finally {
            redisLock.unLock(WX_CALLBACK_LOCK_REFUND);
        }
    }

    /**
     * 申请退款回调
     */
    private String refundCallback(String wxpayCallbackResult, String queueName) {
        try {
            SortedMap<String, String> map = WXPayUtil.xmlToMap(wxpayCallbackResult);
            log.info("===========================================");
            log.info("微信退款回调返回结果为===" + map);
            log.info("===========================================");
            if (Objects.equals("SUCCESS", map.get("return_code"))) {
                String appid = map.get("appid");
                //获取支付账号信息
                PayAccountDetail payAccount = payAccountService.getPayAccountDetail(appid, PayPlatform.WECHAT.value);
                log.info("===========微信退款回调payAccount=" + JSONObject.toJSONString(payAccount));
                if (payAccount == null) {
                    log.error("维信退款回调发生错误，未获取到支付账号信息，appid={}", appid);
                    return WXPayUtil.getPayCallback("FAIL", "自定义参数接收失败");
                }
                //加密信息
                String reqInfo = AES256ECBUtil.decryptData(map.get("req_info"), payAccount.getKey());
                log.info("===========微信退款回调reqInfo=" + reqInfo);
                if (StringUtil.isEmpty(reqInfo)) {
                    return WXPayUtil.getPayCallback("FAIL", "解密reqInfo失败");
                }
                SortedMap<String, String> resultMap = WXPayUtil.xmlToMap(reqInfo);
                log.info("=========微信退款回调解析参数resultMap========" + resultMap);
                boolean exists = refundRecordMapper.existsWithOutRefundNo(resultMap.get("out_refund_no"), PayPlatform.WECHAT.value);
                log.info("=========微信退款回调解析参数exists========" + exists);
                if (!exists) {
                    RefundRecord record = new RefundRecord();
                    record.setOutTradeNo(resultMap.get("out_trade_no"));
                    record.setTradeNo(resultMap.get("transaction_id"));
                    record.setTotalFee(new BigDecimal(resultMap.get("total_fee")));
                    record.setOutRefundNo(resultMap.get("out_refund_no"));
                    record.setRefundTradeNo(resultMap.get("refund_id"));
                    record.setRefundFee(new BigDecimal(resultMap.get("refund_fee")));
                    record.setPlatform(PayPlatform.WECHAT.value);
                    Date now = new Date();
                    if (StringUtil.isNotEmpty(resultMap.get("success_time"))) {
                        record.setRefundTime(DateUtil.transferStringToDate(resultMap.get("success_time"), "yyyy-MM-dd HH:mm:ss"));
                    } else {
                        record.setRefundTime(now);
                    }
                    //退款状态：1-成功；2-失败；
                    String refundStatus = resultMap.get("refund_status");
                    if ("SUCCESS".equalsIgnoreCase(refundStatus)) {
                        record.setStatus(1);
                    } else {
                        record.setStatus(2);
                    }
                    record.setCreateTime(now);
                    refundRecordMapper.insert(record);
                    rabbitTemplate.convertAndSend(queueName, record);
                }
                //回复微信
                return WXPayUtil.getPayCallback("SUCCESS", "OK");
            } else {
                //回复微信
                return WXPayUtil.getPayCallback("FAIL", "return_code不正确");
            }
        } catch (Exception e) {
            log.error("微信退款回调发生异常：", e);
            return WXPayUtil.getPayCallback("FAIL", "程序发生错误");
        }
    }

    /**
     * 从HTTP请求中获取微信返回信息
     */
    private String getWxpayCallbackResult(HttpServletRequest request) {
        InputStream inStream = null;
        ByteArrayOutputStream outSteam = null;
        try {
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
            return new String(outSteam.toByteArray(), "utf-8");
        } catch (Exception e) {
            log.error("微信回调发生异常：" + e.getMessage(), e);
            return null;
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
        }
    }

    /**
     * 订单查询
     */
    @PostMapping(value = "/wxpay/query/order")
    public Object orderquery(@RequestBody WechatPayRequest payRequest) {
        try {
            log.info("wx 订单查询");
            JSONObject json = new JSONObject();
            Thread.sleep(1000);//暂停1S等待支付完成再去查询支付状态
            SortedMap<String, Object> orderQueryMap = new TreeMap<>();
            String outTradeNo = payRequest.getOut_trade_no();
            if (outTradeNo == null) {
                throw new BadRequestException("请传递正确的订单号。");
            }
            PayRecordDTO record = payRecordService.findPayRecordByOutTradeNo(outTradeNo);
            if (record == null) {
                Thread.sleep(1000);
                record = payRecordService.findPayRecordByOutTradeNo(outTradeNo);
            }
            if (record == null) {
                json.put("state", "NOTPAY");
                return json;
            }
            //订单号
            orderQueryMap.put("out_trade_no", payRequest.getOut_trade_no());
            if (StringUtil.isNotEmpty(record.getTradeNo())) {
                //微信支付流水号
                orderQueryMap.put("transaction_id", record.getTradeNo());
            }
            //获取支付账号信息
            PayAccountDetail payAccount = payAccountService.getPayAccountDetail(record.getCompanyId(), record.getPlatform(), record.getClientType(), record.getReceiveType());
            if (payAccount == null) {
                throw new YimaoException("查询失败【002】");
            }
            orderQueryMap.put("appid", payAccount.getAppid());
            orderQueryMap.put("mch_id", payAccount.getMchId());
            orderQueryMap.put("nonce_str", UUIDUtil.longuuid32());
            //签名
            orderQueryMap.put("sign_type", WechatConstant.MD5);
            orderQueryMap.put("sign", WXPayUtil.createSign(orderQueryMap, true, WechatConstant.MD5, payAccount.getKey()));

            SortedMap<String, String> resp = wxPayService.orderQuery(orderQueryMap);

            if (Objects.equals(resp.get("return_code"), WechatConstant.FAIL)) {
                throw new YimaoException(resp.get("return_msg"));
            }
            if (Objects.equals(resp.get("result_code"), WechatConstant.FAIL)) {
                throw new YimaoException(resp.get("err_code") + " : " + resp.get("err_code_des"));
            }
            /*
             * SUCCESS—支付成功
             * REFUND—转入退款
             * NOTPAY—未支付
             * CLOSED—已关闭
             * REVOKED—已撤销（刷卡支付）
             * USERPAYING--用户支付中
             * PAYERROR--支付失败(其他原因，如银行返回失败)
             */
            if (StringUtil.isNotEmpty(resp.get("trade_state"))) {
                json.put("state", resp.get("trade_state"));
            } else {
                json.put("state", "SUCCESS");
            }

            //如果是公众号,需要创建时间，订单号。 其他不需要
//            if (Objects.nonNull(payRequest.getOrderType()) && Objects.equals(payRequest.getOrderType(), 1)) {
//                OrderMain orderMain = mainOrderService.findById(Long.parseLong(outTradeNo));
//                if (orderMain != null) {
//                    json.put("mainOrderId", orderMain.getId());
//                    json.put("createTime", DateUtil.transferDateToString(orderMain.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
//                    json.put("count", orderMain.getCount());
//                }
//            }

            json.put("outTradeNo", record.getMainOrderId());
            json.put("payTime", DateUtil.transferDateToString(record.getPayTime(), "yyyy-MM-dd HH:mm:ss"));
            json.put("fee", record.getAmountTotal());
            return json;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new YimaoException("查询出错，请稍后重试。");
        }
    }

    /**
     * 安装工APP微信支付回调（工单）
     */
    @RequestMapping(value = "/wxpay/notify/four", produces = MediaType.APPLICATION_XML_VALUE)
    public String wxRefundNotifyFour(HttpServletRequest request) {
        if (!Constant.PRO_ENVIRONMENT) {
            log.info("微信支付回调（工单）");
        }
        try {
            //获取锁
            if (!redisLock.lock(WX_CALLBACK_LOCK_4)) {
                return WXPayUtil.getPayCallback("FAIL", "程序没有获取到执行权限");
            }
            //从HTTP请求中获取微信返回信息
            String wxpayCallbackResult = getWxpayCallbackResult(request);
            return callback(wxpayCallbackResult, RabbitConstant.WORK_ORDER_PAY_CALLBACK);
        } finally {
            redisLock.unLock(WX_CALLBACK_LOCK_4);
        }
    }

}
