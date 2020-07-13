package com.yimao.cloud.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.constant.WechatConstant;
import com.yimao.cloud.base.enums.PayPlatform;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.UUIDUtil;
import com.yimao.cloud.base.utils.WXPayUtil;
import com.yimao.cloud.order.mapper.PayRecordMapper;
import com.yimao.cloud.order.mapper.RefundRecordMapper;
import com.yimao.cloud.order.pay.alipay.AlipayClientHolder;
import com.yimao.cloud.order.po.RefundRecord;
import com.yimao.cloud.order.service.PayAccountService;
import com.yimao.cloud.order.service.RefundService;
import com.yimao.cloud.pojo.dto.order.PayAccountDetail;
import com.yimao.cloud.pojo.dto.order.PayRecordDTO;
import com.yimao.cloud.pojo.dto.order.RefundRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.security.KeyStore;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 描述：退款
 *
 * @Author Zhang Bo
 * @Date 2019/10/14
 */
@Service
@Slf4j
public class RefundServiceImpl implements RefundService {

    @Resource
    private DomainProperties domainProperties;
    @Resource
    private PayAccountService payAccountService;
    @Resource
    private PayRecordMapper payRecordMapper;
    @Resource
    private RefundRecordMapper refundRecordMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 退款
     */
    @Override
    public void refund(RefundRequest refund) {

        if(StringUtil.isEmpty(refund.getPayTradeNo())){
            log.error("退款失败，获取支付流水号失败");
            throw new YimaoException("退款失败，获取支付流水号失败");
        }
//        PayRecordDTO payRecord = payRecordService.findPayRecordByOutTradeNo(refund.getOutTradeNo());
        PayRecordDTO payRecord = payRecordMapper.findPayRecordByPayTradeNo(refund.getPayTradeNo());
        if (payRecord == null) {
            throw new YimaoException("申请退款失败，获取原始支付记录失败");
        }
        if (refund.getRefundFee().compareTo(payRecord.getAmountTotal()) > 0) {
            throw new YimaoException("申请退款失败，退款总金额大于订单总金额");
        }
        refund.setOutTradeNo(payRecord.getMainOrderId()); //支付流水号
        refund.setTotalFee(payRecord.getAmountTotal());
        //获取支付账号信息
        PayAccountDetail payAccount = payAccountService.getPayAccountDetail(payRecord.getCompanyId(), payRecord.getPlatform(), payRecord.getClientType(), payRecord.getReceiveType());
        if (payAccount == null) {
            throw new YimaoException("申请退款失败，服务端缺少支付账号信息");
        }
        //支付类型：1-微信；2-支付宝；
        Integer payType = payRecord.getPayType();
        if (payType == PayPlatform.WECHAT.value) {
            //微信退款
            wechatRefund(refund, payAccount);
        } else if (payType == PayPlatform.ALI.value) {
            //支付宝退款
            aliRefund(refund, payAccount);
        }
    }

    /**
     * 微信申请退款
     */
    private void wechatRefund(RefundRequest refund, PayAccountDetail payAccount) {
        try {
            String appid = payAccount.getAppid();
            String mchid = payAccount.getMchId();
            SortedMap<String, Object> params = new TreeMap<>();
            //公众账号appid
            params.put("appid", appid);
            //商户号	mch_id
            params.put("mch_id", mchid);
            //nonce_str 随机字符串
            params.put("nonce_str", UUIDUtil.longuuid32());
            // //商户订单号
            // params.put("out_trade_no", refund.getOutTradeNo());
            //商户订单号
            params.put("transaction_id", refund.getPayTradeNo());
            //商户退款订单号
            params.put("out_refund_no", refund.getOutRefundNo());
            //total_fee 订单总金额
            params.put("total_fee", refund.getTotalFee().multiply(new BigDecimal(100)).intValue());
            //total_fee 退款金额
            params.put("refund_fee", refund.getRefundFee().multiply(new BigDecimal(100)).intValue());
            //回调地址
            params.put("notify_url", domainProperties.getApi() + WechatConstant.WXREFUND_NOTIFY_URL_ONE);
            // 签名
            params.put("sign", WXPayUtil.createSign(params, true, WechatConstant.MD5, payAccount.getKey()));

            HttpPost httpPost = new HttpPost(WechatConstant.WX_REFUND_URL);
            String param = WXPayUtil.mapToXml(params);
            if (!StringUtil.isEmpty(param)) {
                httpPost.setEntity(new StringEntity(param, "UTF-8"));
            }
            // 指定读取证书格式为PKCS12
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            // 读取本机存放的PKCS12证书文件
            FileInputStream instream = new FileInputStream(new File("/usr/wxcert/" + mchid + ".p12"));
            try {
                // 指定PKCS12的密码(商户ID)
                keyStore.load(instream, mchid.toCharArray());
            } finally {
                instream.close();
            }
            SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, mchid.toCharArray()).build();
            // 指定TLS版本
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1"}, null, new DefaultHostnameVerifier());
            // 设置httpclient的SSLSocketFactory
            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, "UTF-8");
            if (!Constant.PRO_ENVIRONMENT) {
                log.info("微信申请退款请求结果为：{}", result);
            }
            Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
            boolean flag = "SUCCESS".equals(resultMap.get("return_code")) && "SUCCESS".equals(resultMap.get("result_code"));
            log.info("==============================================================================================================================");
            log.info("微信退款结果返回：" + resultMap);
            log.info("==============================================================================================================================");
            if (flag) {
                log.info("微信申请退款请求成功");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new YimaoException("微信申请退款时发生异常！");
        }
    }

    /**
     * 支付宝申请退款
     */
    private void aliRefund(RefundRequest refund, PayAccountDetail payAccount) {
        try {
            AlipayClient alipayClient = AlipayClientHolder.instance(payAccount.getAppid(), payAccount.getPrivateKey(), payAccount.getPublicKey());
            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
            request.setBizContent("{\"trade_no\":\"" + refund.getPayTradeNo() + "\", \"refund_amount\":\""
                    + String.valueOf(refund.getRefundFee().doubleValue()) + "\", \"refund_reason\":\""
                    + refund.getRefundReason() + "\", \"out_request_no\":\"" + refund.getOutRefundNo() + "\"}");
            log.info("支付宝申请退款请求参数为：" + JSONObject.toJSONString(request));
            AlipayTradeRefundResponse response = alipayClient.execute(request);
            log.info("支付宝申请退款返回参数为：" + JSONObject.toJSONString(response));
            if (response != null && response.isSuccess()) {
                log.info("支付宝申请退款请求成功");
                boolean exists = refundRecordMapper.existsWithOutRefundNo(refund.getOutRefundNo(), PayPlatform.ALI.value);
                if (!exists) {
                    RefundRecord record = new RefundRecord();
                    record.setOutTradeNo(refund.getOutTradeNo());
                    record.setTotalFee(refund.getTotalFee());
                    record.setOutRefundNo(refund.getOutRefundNo());
                    //TODO 这个的流水号存在问题，是退款流水号还是原订单支付流水号，record.setTradeNo应该为原订单的支付流水号，record.setRefundTradeNo为退款流水号，这里应该是设置退款流水号
                    record.setTradeNo(response.getTradeNo());
                    record.setRefundFee(new BigDecimal(response.getRefundFee()));
                    record.setPlatform(PayPlatform.ALI.value);
                    record.setRefundTime(response.getGmtRefundPay());
                    //退款状态：1-成功；2-失败；
                    record.setStatus(1);
                    record.setCreateTime(new Date());
                    refundRecordMapper.insert(record);
                    rabbitTemplate.convertAndSend(RabbitConstant.REFUND_CALLBACK, record);
                }
            }
        } catch (AlipayApiException e) {
            log.error(e.getMessage(), e);
            log.error("支付宝申请退款请求失败");
        }
    }

}
