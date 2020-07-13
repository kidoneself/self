package com.yimao.cloud.order.pay.alipay;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.context.SpringContextHolder;
import com.yimao.cloud.base.enums.EnvironmentEnum;
import com.yimao.cloud.base.enums.OrderType;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.framework.cache.RedisLock;
import com.yimao.cloud.order.feign.UserFeign;
import com.yimao.cloud.order.po.OrderMain;
import com.yimao.cloud.order.po.OrderRenew;
import com.yimao.cloud.order.service.AliPayService;
import com.yimao.cloud.order.service.OrderMainService;
import com.yimao.cloud.order.service.OrderRenewService;
import com.yimao.cloud.order.service.PayRecordService;
import com.yimao.cloud.order.service.WorkOrderService;
import com.yimao.cloud.pojo.dto.order.AliPayRequest;
import com.yimao.cloud.pojo.dto.order.PayRecordDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import com.yimao.cloud.pojo.dto.user.DistributorOrderDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝接口
 */
@RestController
@Slf4j
public class AlipayController {

    private static final String ALIPAY_CALLBACK_LOCK_1 = "ALIPAY_CALLBACK_LOCK_1";
    private static final String ALIPAY_CALLBACK_LOCK_2 = "ALIPAY_CALLBACK_LOCK_2";
    private static final String ALIPAY_CALLBACK_LOCK_3 = "ALIPAY_CALLBACK_LOCK_3";
    private static final String ALIPAY_CALLBACK_LOCK_4 = "ALIPAY_CALLBACK_LOCK_4";

    @Resource
    private RedisLock redisLock;

    @Resource
    private AliPayService aliPayService;
    @Resource
    private OrderMainService mainOrderService;
    @Resource
    private OrderRenewService orderRenewService;
    @Resource
    private PayRecordService payRecordService;
    @Resource
    private UserFeign userFeign;
    @Resource
    private WorkOrderService workOrderService;

    /**
     * 支付宝app支付接口
     */
    @PostMapping(value = "/alipay/tradeapp", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object tradeapp(@RequestBody AliPayRequest payRequest) {
        //校验
        if (StringUtil.isBlank(payRequest.getOutTradeNo())) {
            throw new YimaoException("订单号不能为空！");
        }
        if (payRequest.getTotalAmount() == null) {
            throw new YimaoException("订单支付金额不能为空！");
        }
        if (SpringContextHolder.getEnvironment().equalsIgnoreCase(EnvironmentEnum.PRO.code)) {
            payRequest.setSubject("翼猫APP商品");
        } else {
            payRequest.setSubject("翼猫APP商品测试");
        }
        //订单信息校验
        this.checkOrderInfo(payRequest);
        String orderStr = aliPayService.tradeapp(payRequest);
        Map<String, String> m = new HashMap<>(8);
        m.put("orderStr", orderStr);
        return m;
    }

    /**
     * 支付宝扫码支付接口
     */
    @PostMapping(value = "/alipay/tradeprecreate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String tradeprecreate(@RequestBody AliPayRequest payRequest) {
        //校验
        if (StringUtil.isBlank(payRequest.getOutTradeNo())) {
            throw new YimaoException("订单号不能为空！");
        }
        if (payRequest.getTotalAmount() == null) {
            throw new YimaoException("订单支付金额不能为空！");
        }
        //订单信息校验
        this.checkOrderInfo(payRequest);
        return aliPayService.tradeprecreate(payRequest);
    }

    private void checkOrderInfo(AliPayRequest payRequest) {
        //订单信息校验
        String outTradeNo = payRequest.getOutTradeNo();
        Double totalAmount = payRequest.getTotalAmount();
        //1-商品订单 2-经销商订单 3-续费订单
        if (payRequest.getOrderType() == OrderType.PRODUCT.value) {
            //普通产品订单校验
            OrderMain mainOrder = mainOrderService.findById(Long.valueOf(outTradeNo));
            if (mainOrder == null) {
                throw new BadRequestException("订单号不存在");
            }
            if (mainOrder.getPay()) {
                throw new BadRequestException("不能重复支付");
            }
            if (mainOrder.getOrderAmountFee().doubleValue() != totalAmount) {
                throw new YimaoException("订单金额有误，不能支付");
            }
            //查询支付账号需要用到产品公司ID
            Integer companyId = mainOrderService.getProductCompanyIdByOutTradeNo(Long.valueOf(outTradeNo));
            payRequest.setCompanyId(companyId);
        } else if (payRequest.getOrderType() == OrderType.RENEW.value) {
            //水机续费订单校验
            OrderRenew orderRenew = orderRenewService.getPayInfoById(outTradeNo);
            if (orderRenew == null) {
                throw new YimaoException("订单号不存在");
            }
            if (orderRenew.getPay()) {
                throw new YimaoException("不能重复支付");
            }
            if (orderRenew.getAmountFee().doubleValue() != totalAmount) {
                throw new YimaoException("订单金额有误，不能支付");
            }
            //查询支付账号需要用到产品公司ID
            payRequest.setCompanyId(10000);
        } else if (payRequest.getOrderType() == OrderType.DISTRIBUTOR.value) {
            //经销商订单校验
            DistributorOrderDTO distributorOrder = userFeign.findBasisDistributorOrderById(Long.valueOf(outTradeNo));
            if (distributorOrder == null) {
                throw new YimaoException("订单号不存在");
            }
            if (distributorOrder.getPayState() == 2) {
                throw new YimaoException("不能重复支付");
            }
            if (distributorOrder.getPrice().doubleValue() != totalAmount) {
                throw new YimaoException("订单金额有误，不能支付");
            }
            //查询支付账号需要用到产品公司ID
            payRequest.setCompanyId(10000);
        } else if (payRequest.getOrderType() == OrderType.WORKORDER.value) {
            //工单订单校验
            WorkOrderDTO workorder = workOrderService.getWorkOrderById(outTradeNo.substring(0, outTradeNo.length() - 3));
            if (workorder == null) {
                throw new YimaoException("订单号不存在");
            }
            if (workorder.getPay()) {
                throw new YimaoException("不能重复支付");
            }
            if (workorder.getFee().doubleValue() != totalAmount) {
                throw new YimaoException("订单金额有误，不能支付");
            }
            //查询支付账号需要用到产品公司ID
            payRequest.setCompanyId(10000);
        }
    }

    /**
     * 支付宝交易查询
     */
    @PostMapping(value = "/alipay/tradequery/order", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object tradeQuery(@RequestBody AliPayRequest payRequest) {
        try {
            JSONObject json = new JSONObject();
            Thread.sleep(1000);//暂停1S等待支付完成再去查询支付状态
            //校验
            if (StringUtil.isBlank(payRequest.getOutTradeNo())) {
                throw new YimaoException("订单号不能为空！");
            }
            PayRecordDTO record = payRecordService.findPayRecordByOutTradeNo(String.valueOf(payRequest.getOutTradeNo()));
            if (record == null) {
                Thread.sleep(1000);
                record = payRecordService.findPayRecordByOutTradeNo(String.valueOf(payRequest.getOutTradeNo()));
            }
            if (record == null) {
                /*
                 * 交易状态：
                 * WAIT_BUYER_PAY（交易创建，等待买家付款）、
                 * TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）、
                 * TRADE_SUCCESS（交易支付成功）、
                 * TRADE_FINISHED（交易结束，不可退款）
                 */
                json.put("state", "TRADE_CLOSED");
                return json;
            }
            payRequest.setCompanyId(record.getCompanyId());
            payRequest.setPlatform(record.getPlatform());
            payRequest.setClientType(record.getClientType());
            payRequest.setReceiveType(record.getReceiveType());
            return aliPayService.tradeQuery(payRequest);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new YimaoException("查询出错，请稍后重试。");
        }
    }

    /**
     * 支付宝支付回调（普通商品）
     */
    @RequestMapping(value = "/alipay/notify/one")
    public String callbackOne(HttpServletRequest request) {
        if (!Constant.PRO_ENVIRONMENT) {
            log.info("支付宝支付回调（普通商品）");
        }
        try {
            //获取锁
            if (!redisLock.lock(ALIPAY_CALLBACK_LOCK_1)) {
                log.error("支付宝支付回调（普通商品）没有获取到分布式锁");
                return "failure";
            }
            return aliPayService.callback(request, RabbitConstant.ORDER_PAY_CALLBACK);
        } finally {
            redisLock.unLock(ALIPAY_CALLBACK_LOCK_1);
        }
    }

    /**
     * 支付宝支付回调（水机续费）
     */
    @RequestMapping(value = "/alipay/notify/two")
    public String callbackTwo(HttpServletRequest request) {
        if (!Constant.PRO_ENVIRONMENT) {
            log.info("支付宝支付回调（水机续费）");
        }
        try {
            //获取锁
            if (!redisLock.lock(ALIPAY_CALLBACK_LOCK_2)) {
                log.error("支付宝支付回调（水机续费）没有获取到分布式锁");
                return "failure";
            }
            return aliPayService.callback(request, RabbitConstant.RENEWORDER_PAY_CALLBACK);
        } finally {
            redisLock.unLock(ALIPAY_CALLBACK_LOCK_2);
        }
    }

    /**
     * 支付宝支付回调（经销商订单）
     */
    @RequestMapping(value = "/alipay/notify/three")
    public String callbackThree(HttpServletRequest request) {
        if (!Constant.PRO_ENVIRONMENT) {
            log.info("支付宝支付回调（经销商订单）");
        }
        try {
            //获取锁
            if (!redisLock.lock(ALIPAY_CALLBACK_LOCK_3)) {
                log.error("支付宝支付回调（经销商订单）没有获取到分布式锁");
                return "failure";
            }
            return aliPayService.callback(request, RabbitConstant.DISTRIBUTOR_ORDER_PAY_CALLBACK);
        } finally {
            redisLock.unLock(ALIPAY_CALLBACK_LOCK_3);
        }
    }

    /**
     * 安装工APP支付宝支付回调（工单）
     */
    @RequestMapping(value = "/alipay/notify/four")
    public String callbackFour(HttpServletRequest request) {
        if (!Constant.PRO_ENVIRONMENT) {
            log.info("支付宝支付回调（工单）");
        }
        try {
            //获取锁
            if (!redisLock.lock(ALIPAY_CALLBACK_LOCK_4)) {
                log.error("安装工APP支付宝支付回调（工单）没有获取到分布式锁");
                return "failure";
            }
            return aliPayService.callback(request, RabbitConstant.WORK_ORDER_PAY_CALLBACK);
        } finally {
            redisLock.unLock(ALIPAY_CALLBACK_LOCK_4);
        }
    }

}



