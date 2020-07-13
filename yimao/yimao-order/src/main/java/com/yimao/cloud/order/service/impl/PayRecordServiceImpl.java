package com.yimao.cloud.order.service.impl;

import com.yimao.cloud.base.constant.AliConstant;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.PayType;
import com.yimao.cloud.base.mail.MailSender;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.order.mapper.PayRecordMapper;
import com.yimao.cloud.order.po.PayRecord;
import com.yimao.cloud.order.service.PayRecordService;
import com.yimao.cloud.pojo.dto.order.PayRecordDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;

/**
 * @author Zhang Bo
 * @date 2018/11/12.
 */
@Service
@Slf4j
public class PayRecordServiceImpl implements PayRecordService {

    @Resource
    private DomainProperties domainProperties;
    @Resource
    private PayRecordMapper payRecordMapper;
    @Resource
    private MailSender mailSender;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public boolean existsWithOutTradeNo(String outTradeNo) {
        return payRecordMapper.existsWithOutTradeNo(outTradeNo);
    }

    @Override
    public PayRecordDTO findPayRecordByOutTradeNo(String outTradeNo) {
        return payRecordMapper.selectByOutTradeNo(outTradeNo);
    }

    /**
     * 微信回调成功，保存支付记录和修改订单状态
     */
    @Override
    public void saveWxPayRecord(SortedMap<String, String> notifyMap, String queueName) {
        try {
            String transaction_id = notifyMap.get("transaction_id");//微信支付订单号
            String out_trade_no = notifyMap.get("out_trade_no");//订单号
            String total_fee = notifyMap.get("total_fee");//订单金额
            String time_end = notifyMap.get("time_end");//支付完成时间
            String openid = notifyMap.get("openid");//用户标识
            String trade_type = notifyMap.get("trade_type");//用户标识

            //自定义参数（支付套餐配置相关）
            String attach = notifyMap.get("attach");
            String[] arr = attach.split("_");

            PayRecord record = new PayRecord();
            log.info("订单号为："+ out_trade_no);
            record.setMainOrderId(this.getOutTradeNo(queueName, out_trade_no));
            record.setOrderType(this.getOrderType(queueName));
            record.setTradeNo(transaction_id);
            //支付类型：1-微信；2-支付宝；3-POS机；4-转账；
            record.setPayType(PayType.WECHAT.value);
            //JSAPI -JSAPI支付、NATIVE -Native支付、APP -APP支付
            record.setTradeType(trade_type);
            //支付金额（单位：元）
            record.setAmountTotal(new BigDecimal(total_fee).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));//单位（元）
            record.setOpenid(openid);
            //支付时间
            record.setPayTime(DateUtil.transferStringToDate(time_end, "yyyyMMddHHmmss"));
            record.setCreateTime(new Date());
            record.setCompanyId(Integer.parseInt(arr[0]));
            record.setPlatform(Integer.parseInt(arr[1]));
            record.setClientType(Integer.parseInt(arr[2]));
            record.setReceiveType(Integer.parseInt(arr[3]));
            payRecordMapper.insert(record);
            log.info("订单号 MainOrderId："+ record.getMainOrderId());

            System.out.println("=============微信付款记录本地保存成功======================");
            rabbitTemplate.convertAndSend(queueName, record);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String subject = "微信支付回调失败提醒===" + domainProperties.getApi();
            String content = "微信支付回调时出错。orderId=" + notifyMap.get("out_trade_no") + "\n" + sw.toString();
            mailSender.send(null, subject, content);
        }
    }

    /**
     * 支付宝回调成功，保存支付记录和修改订单状态
     */
    @Override
    public void saveAliPayRecord(Map<String, String> notifyMap, String queueName) {
        try {
            String notify_time = notifyMap.get("notify_time");//支付宝交易时间
            String trade_no = notifyMap.get("trade_no");//支付宝交易号
            String out_trade_no = notifyMap.get("out_trade_no");//商户订单号
            String total_amount = notifyMap.get("total_amount");//订单金额（元）
            String passback_params = notifyMap.get("passback_params");//商品描述

            //自定义参数（支付套餐配置相关）
            String body = notifyMap.get("body");
            String[] arr = body.split("_");

            String trade_type = null;
            if (Objects.equals(passback_params, AliConstant.ALI_TRADE_WAP)) {
                trade_type = "H5/WAP";
            } else if (Objects.equals(passback_params, AliConstant.ALI_TRADE_APP)) {
                trade_type = "APP";
            }
            //添加支付记录
            PayRecord record = new PayRecord();
            record.setMainOrderId(this.getOutTradeNo(queueName, out_trade_no));
            record.setOrderType(this.getOrderType(queueName));
            record.setTradeNo(trade_no);
	        log.info("1========================================================================,MainOrderId=" + record.getMainOrderId());
	        log.info("2========================================================================,TradeNo=" + record.getTradeNo());

            //支付类型：1-微信；2-支付宝；3-POS机；4-转账；
            record.setPayType(PayType.ALIPAY.value);
            //JSAPI -JSAPI支付、NATIVE -Native支付、APP -APP支付
            record.setTradeType(trade_type);
            record.setAmountTotal(new BigDecimal(total_amount));//单位（元）
            record.setPayTime(DateUtil.transferStringToDate(notify_time, "yyyy-MM-dd HH:mm:ss"));
            record.setCreateTime(new Date());
            record.setCompanyId(Integer.parseInt(arr[0]));
            record.setPlatform(Integer.parseInt(arr[1]));
            record.setClientType(Integer.parseInt(arr[2]));
            record.setReceiveType(Integer.parseInt(arr[3]));
            payRecordMapper.insert(record);

            System.out.println("=============支付宝付款记录本地保存成功======================");
            rabbitTemplate.convertAndSend(queueName, record);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String subject = "支付宝支付回调失败提醒===" + domainProperties.getApi();
            String content = "支付宝支付回调时出错。orderId=" + notifyMap.get("out_trade_no") + "\n" + sw.toString();
            mailSender.send(null, subject, content);
        }
    }

    /**
     * @param payId
     * @return com.yimao.cloud.pojo.dto.order.PayRecordDTO
     * @description 根据支付记录id查询支付记录信息
     */
    @Override
    public PayRecordDTO findPayRecordById(Integer payId) {
        PayRecordDTO dto = new PayRecordDTO();
        PayRecord payRecord = payRecordMapper.selectByPrimaryKey(payId);
        payRecord.convert(dto);
        return dto;
    }

    /**
     * 获取交易单号（特殊情况需要在本地订单号的基础上加上特定字符再进行交易）
     */
    private String getOutTradeNo(String queueName, String outTradeNo) {
        if (RabbitConstant.WORK_ORDER_PAY_CALLBACK.equalsIgnoreCase(queueName)) {
            //安装工APP的支付单号后三位是为了避免重复添加的，需要去除
            return outTradeNo.substring(0, outTradeNo.length() - 3);
        }
        return outTradeNo;
    }

    /**
     * 获取交易单类型：1、普通订单  2、续费订单 3、经销商订单 4、安装工单
     */
    private Integer getOrderType(String queueName) {
        if (RabbitConstant.ORDER_PAY_CALLBACK.equalsIgnoreCase(queueName)) {
            return 1;
        } else if (RabbitConstant.RENEWORDER_PAY_CALLBACK.equalsIgnoreCase(queueName)) {
            return 2;
        } else if (RabbitConstant.DISTRIBUTOR_ORDER_PAY_CALLBACK.equalsIgnoreCase(queueName)) {
            return 3;
        } else if (RabbitConstant.WORK_ORDER_PAY_CALLBACK.equalsIgnoreCase(queueName)) {
            return 4;
        }
        return null;
    }

}
