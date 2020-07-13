package com.yimao.cloud.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.yimao.cloud.base.constant.AliConstant;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.order.pay.alipay.AlipayClientHolder;
import com.yimao.cloud.order.service.AliPayService;
import com.yimao.cloud.order.service.PayAccountService;
import com.yimao.cloud.order.service.PayRecordService;
import com.yimao.cloud.pojo.dto.order.AliPayRequest;
import com.yimao.cloud.pojo.dto.order.PayAccountDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 支付宝支付
 */
@Service
@Slf4j
public class AliPayServiceImpl implements AliPayService {

    @Resource
    private PayRecordService payRecordService;
    @Resource
    private PayAccountService payAccountService;

    @Override
    public String tradewap(AliPayRequest payRequest) {
        try {
            //获取支付账号信息
            PayAccountDetail payAccount = payAccountService.getPayAccountDetail(payRequest.getCompanyId(), payRequest.getPlatform(), payRequest.getClientType(), payRequest.getReceiveType());
            if (payAccount == null) {
                throw new YimaoException("下单失败，服务端缺少支付账号信息");
            }
            AlipayClient alipayClient = AlipayClientHolder.instance(payAccount.getAppid(), payAccount.getPrivateKey(), payAccount.getPublicKey());
            AlipayTradeWapPayRequest wapPayRequest = new AlipayTradeWapPayRequest();
            AlipayTradeWapPayModel payModel = new AlipayTradeWapPayModel();
            //对一笔交易的具体描述信息。如果是多种商品，请将商品描述字符串累加传给body。
            if (StringUtil.isNotBlank(payRequest.getBody())) {
                payModel.setBody(payRequest.getBody());
            }
            //商户网站唯一订单号
            payModel.setOutTradeNo(payRequest.getOutTradeNo());
            //商品的标题/交易标题/订单标题/订单关键字等。
            payModel.setSubject(payRequest.getSubject());
            //订单总金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000]
            payModel.setTotalAmount(String.valueOf(payRequest.getTotalAmount()));
            wapPayRequest.setBizModel(payModel);
            //设置支付成功异步通知地址（服务器间交互）
            wapPayRequest.setNotifyUrl(payRequest.getNotifyUrl());
            //设置支付成功同步回调地址（页面交互）
            wapPayRequest.setReturnUrl(AliConstant.RETURN_URL);
            AlipayTradeWapPayResponse payResponse = alipayClient.pageExecute(wapPayRequest);
            log.info("支付宝wap下单返回：" + payResponse.getBody());
            return payResponse.getBody();
        } catch (AlipayApiException e) {
            log.error(e.getMessage(), e);
            throw new YimaoException("手机网站支付失敗");
        }
    }

    @Override
    public String tradeapp(AliPayRequest payRequest) {
        try {
            //获取支付账号信息
            PayAccountDetail payAccount = payAccountService.getPayAccountDetail(payRequest.getCompanyId(), payRequest.getPlatform(), payRequest.getClientType(), payRequest.getReceiveType());
            if (payAccount == null) {
                throw new YimaoException("下单失败，服务端缺少支付账号信息");
            }
            AlipayClient alipayClient = AlipayClientHolder.instance(payAccount.getAppid(), payAccount.getPrivateKey(), payAccount.getPublicKey());
            AlipayTradeAppPayRequest appPayRequest = new AlipayTradeAppPayRequest();
            AlipayTradeAppPayModel payModel = new AlipayTradeAppPayModel();

            //对一笔交易的具体描述信息。如果是多种商品，请将商品描述字符串累加传给body。
            if (StringUtil.isNotBlank(payRequest.getBody())) {
                payModel.setBody(payRequest.getBody());
            }
            //商户网站唯一订单号
            payModel.setOutTradeNo(payRequest.getOutTradeNo());//20171206xxxxxx
            //商品的标题/交易标题/订单标题/订单关键字等。
            payModel.setSubject(payRequest.getSubject());
            //自定义参数
            payModel.setBody(payRequest.getCompanyId() + "_" + payRequest.getPlatform() + "_" + payRequest.getClientType() + "_" + payRequest.getReceiveType());
            //订单总金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000]
            payModel.setTotalAmount(String.valueOf(payRequest.getTotalAmount()));
            //该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。
            //该参数数值不接受小数点， 如 1.5h，可转换为 90m。注：若为空，则默认为15d。
            payModel.setTimeoutExpress("15m");
            appPayRequest.setBizModel(payModel);
            // 设置支付成功异步通知地址（服务器间交互）
            appPayRequest.setNotifyUrl(payRequest.getNotifyUrl());
            log.info("支付宝APP支付请求参数为：" + JSONObject.toJSONString(appPayRequest));
            AlipayTradeAppPayResponse payResponse = alipayClient.sdkExecute(appPayRequest);
            log.info("支付宝app下单返回：" + payResponse.getBody());
            return payResponse.getBody();
        } catch (AlipayApiException e) {
            log.error(e.getMessage(), e);
            throw new YimaoException("支付宝APP支付发生异常");
        }
    }

    @Override
    public String tradeprecreate(AliPayRequest payRequest) {
        try {
            //获取支付账号信息
            PayAccountDetail payAccount = payAccountService.getPayAccountDetail(payRequest.getCompanyId(), payRequest.getPlatform(), payRequest.getClientType(), payRequest.getReceiveType());
            if (payAccount == null) {
                throw new YimaoException("下单失败，服务端缺少支付账号信息");
            }
            AlipayClient alipayClient = AlipayClientHolder.instance(payAccount.getAppid(), payAccount.getPrivateKey(), payAccount.getPublicKey());
            AlipayTradePrecreateRequest precreateRequest = new AlipayTradePrecreateRequest();
            precreateRequest.setBizContent("{" +
                    "\"out_trade_no\":\"" + payRequest.getOutTradeNo() + "\"," +
                    "\"total_amount\":" + String.valueOf(payRequest.getTotalAmount()) + "," +
                    "\"subject\":\"" + payRequest.getSubject() + "\"," +
                    "\"body\":\"" + payRequest.getCompanyId() + "_" + payRequest.getPlatform() + "_" + payRequest.getClientType() + "_" + payRequest.getReceiveType() + "\"," +
                    "\"timeout_express\":\"15m\"}");
            // AlipayTradePrecreateModel payModel = new AlipayTradePrecreateModel();
            // //商户网站唯一订单号
            // payModel.setOutTradeNo(payRequest.getOutTradeNo());//20171206xxxxxx
            // //商品的标题/交易标题/订单标题/订单关键字等。
            // payModel.setSubject(payRequest.getSubject());
            // //自定义参数
            // payModel.setBody(payRequest.getCompanyId() + "_" + payRequest.getPlatform() + "_" + payRequest.getClientType() + "_" + payRequest.getReceiveType());
            // //订单总金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000]
            // payModel.setTotalAmount(String.valueOf(payRequest.getTotalAmount()));
            // //该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。
            // //该参数数值不接受小数点， 如 1.5h，可转换为 90m。注：若为空，则默认为15d。
            // payModel.setTimeoutExpress("15m");
            // precreateRequest.setBizModel(payModel);
            // 设置支付成功异步通知地址（服务器间交互）
            precreateRequest.setNotifyUrl(payRequest.getNotifyUrl());
            log.info("支付宝扫码支付请求参数为：" + JSONObject.toJSONString(precreateRequest));
            AlipayTradePrecreateResponse payResponse = alipayClient.execute(precreateRequest);
            log.info("支付宝扫码下单返回：" + payResponse.getBody());
            if (payResponse.isSuccess()) {
                return payResponse.getQrCode();
            }
            return null;
        } catch (AlipayApiException e) {
            log.error(e.getMessage(), e);
            throw new YimaoException("支付宝APP支付发生异常");
        }
    }

    @Override
    public String callback(HttpServletRequest request, String queueName) {
        try {
            Map<String, String> notifyMap = convertRequestParamsToMap(request);
            String trade_status = notifyMap.get("trade_status");//交易状态
            String out_trade_no = notifyMap.get("out_trade_no");//商户订单号

            log.info("支付宝支付异步通知结果为：" + notifyMap);

            if (Objects.equals(AliConstant.TRADE_SUCCESS, trade_status)) {
                //自定义参数（支付套餐配置相关）
                String body = notifyMap.get("body");
                String[] arr = body.split("_");
                //获取支付账号信息
                PayAccountDetail payAccount = payAccountService.getPayAccountDetail(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), Integer.parseInt(arr[2]), Integer.parseInt(arr[3]));
                if (payAccount == null) {
                    log.error("支付宝支付回调发生错误，未获取到支付账号信息，自定义参数body={}", body);
                    return "failure";
                }
                //支付宝支付异步通知结果验签
                boolean flag;
                if (AliConstant.APPID_YWL.equals(payAccount.getAppid()) || AliConstant.APPID_SWKJ.equals(payAccount.getAppid())) {
                    //养未来和生物科技使用RSA2加密
                    flag = AlipaySignature.rsaCheckV1(notifyMap, payAccount.getPublicKey(), AliConstant.CHARSET, AliConstant.RSA2);
                } else {
                    flag = AlipaySignature.rsaCheckV1(notifyMap, payAccount.getPublicKey(), AliConstant.CHARSET, AliConstant.RSA);
                }
                if (!flag) {
                    log.error("处理支付宝支付异步通知验签失败！");
                    return "failure";
                }
                boolean exists = payRecordService.existsWithOutTradeNo(out_trade_no);
                //解决异步多次通知
                if (!exists) {
                    //保存回调记录
                    payRecordService.saveAliPayRecord(notifyMap, queueName);
                }
                return "success";
            }
            return "failure";
        } catch (Exception e) {
            log.error("支付宝回调发生异常" + e.getMessage(), e);
            return "failure";
        }
    }

    /**
     * 加签方法（如果不用SDK调用，推荐用该方法加签）
     *
     * @param content    待签名字符串
     * @param privateKey 加签私钥
     * @param charset    加签字符集
     * @param sign_type  签名方式
     **/
    public String rsaSign(String content, String privateKey, String charset, String sign_type) {
        String reaSign = null;
        try {
            reaSign = AlipaySignature.rsaSign(content, privateKey, charset, sign_type);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return reaSign;
    }


    // 将request中的参数转换成Map
    private static Map<String, String> convertRequestParamsToMap(HttpServletRequest request) {
        Map<String, String> retMap = new HashMap<>();

        Set<Map.Entry<String, String[]>> entrySet = request.getParameterMap().entrySet();

        for (Map.Entry<String, String[]> entry : entrySet) {
            String name = entry.getKey();
            String[] values = entry.getValue();
            int valLen = values.length;

            if (valLen == 1) {
                retMap.put(name, values[0]);
            } else if (valLen > 1) {
                StringBuilder sb = new StringBuilder();
                for (String val : values) {
                    sb.append(",").append(val);
                }
                retMap.put(name, sb.toString().substring(1));
            } else {
                retMap.put(name, "");
            }
        }

        return retMap;
    }

    @Override
    public Object tradeQuery(AliPayRequest payRequest) {
        try {
            JSONObject json = new JSONObject();
            //获取支付账号信息
            PayAccountDetail payAccount = payAccountService.getPayAccountDetail(payRequest.getCompanyId(), payRequest.getPlatform(), payRequest.getClientType(), payRequest.getReceiveType());
            if (payAccount == null) {
                throw new YimaoException("下单失败，服务端缺少支付账号信息");
            }
            AlipayClient alipayClient = AlipayClientHolder.instance(payAccount.getAppid(), payAccount.getPrivateKey(), payAccount.getPublicKey());
            AlipayTradeQueryRequest queryRequest = new AlipayTradeQueryRequest();
            queryRequest.setBizContent("{\"out_trade_no\":\"" + payRequest.getOutTradeNo() + "\"}");
            AlipayTradeQueryResponse response = alipayClient.execute(queryRequest);
            if (response != null && response.isSuccess()) {
                /*
                 * 交易状态：
                 * WAIT_BUYER_PAY（交易创建，等待买家付款）、
                 * TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）、
                 * TRADE_SUCCESS（交易支付成功）、
                 * TRADE_FINISHED（交易结束，不可退款）
                 */
                json.put("state", response.getTradeStatus());
                // json.put("outTradeNo", response.getOutTradeNo());
                // json.put("payTime", DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
                // json.put("fee", response.getTotalAmount());
                return json;
            } else {
                log.info("调用支付宝查询接口失败！");
                throw new YimaoException("查询支付宝信息失败");
            }
        } catch (AlipayApiException e) {
            log.error(e.getMessage(), e);
            throw new YimaoException("查询支付宝信息失败");
        }
    }

}
