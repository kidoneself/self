package com.yimao.cloud.order.service.impl;

import com.yimao.cloud.base.constant.WechatConstant;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.utils.HttpsUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.UUIDUtil;
import com.yimao.cloud.base.utils.WXPayUtil;
import com.yimao.cloud.order.service.WxPayService;
import com.yimao.cloud.pojo.dto.order.PayAccountDetail;
import com.yimao.cloud.pojo.dto.order.WechatPayRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Zhang Bo
 * @date 2017/12/4.
 */
@Service
@Slf4j
public class WxPayServiceImpl implements WxPayService {

    @Resource
    private DomainProperties domainProperties;

    /**
     * 微信支付统一下单<br>
     * 场景：公共号支付、扫码支付、APP支付
     *
     * @return API返回数据
     */
    @Override
    public SortedMap<String, String> unifiedOrder(WechatPayRequest payRequest, PayAccountDetail payAccount) {
        try {
            SortedMap<String, Object> requestMap = new TreeMap<>();
            requestMap.put("appid", payAccount.getAppid());
            requestMap.put("mch_id", payAccount.getMchId());
            if (StringUtil.isNotBlank(payRequest.getOpenId())) {
                requestMap.put("openid", payRequest.getOpenId());
            }

            //H5支付必传
            if (Objects.equals(WechatConstant.MWEB, payRequest.getTradeType())) {
                requestMap.put("scene_info", "{\"h5_info\": {\"type\": \"Wap\",\"wap_url\": " + domainProperties.getWechat() + ",\"wap_name\": \"商品分享H5支付\"}}");
            }
            //异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。
            requestMap.put("notify_url", payRequest.getNotifyUrl());
            requestMap.put("trade_type", payRequest.getTradeType());
            //自定义参数，可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB"
            // unifiedOrderMap.put("device_info", "WEB");
            // if (StringUtil.isNotEmpty(unifiedOrder.getDevice_info())) {
            //     unifiedOrderMap.put("device_info", unifiedOrder.getDevice_info());
            // }
            requestMap.put("nonce_str", UUIDUtil.longuuid32());
            //商品简单描述范，该字段请按照规传递，具体请见
            requestMap.put("body", payRequest.getBody());
            //附加数据，在查询API和支付通知中原样返回，可作为自定义参数使用。
            // if (StringUtil.isNotEmpty(unifiedOrder.getAttach())) {
            //     unifiedOrderMap.put("attach", unifiedOrder.getAttach());
            // }
            requestMap.put("attach", payRequest.getCompanyId() + "_" + payRequest.getPlatform() + "_" + payRequest.getClientType() + "_" + payRequest.getReceiveType());
            //商户订单号
            requestMap.put("out_trade_no", payRequest.getOut_trade_no());
            //订单总金额，单位为分
            requestMap.put("total_fee", (int) (payRequest.getTotal_fee() * 100));
            //终端IP
            requestMap.put("spbill_create_ip", payRequest.getSpbillCreateIp());
            //签名
            requestMap.put("sign", WXPayUtil.createSign(requestMap, true, WechatConstant.MD5, payAccount.getKey()));

            log.info("======统一下单请求参数为======" + requestMap);

            Object sign = requestMap.get("sign");
            if (sign == null || StringUtil.isEmpty(sign.toString())) {
                throw new YimaoException("统一下单请求参数缺少签名");
            }

            String reqBody = WXPayUtil.mapToXml(requestMap);

            String response = HttpsUtil.post(WechatConstant.WX_UNIFIEDORDER_URL, reqBody);

            return WXPayUtil.xmlToMap(response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new YimaoException("微信统一下单时发生异常");
        }
    }

    /**
     * 微信支付查询订单<br>
     *
     * @return API返回数据
     */
    @Override
    public SortedMap<String, String> orderQuery(SortedMap<String, Object> reqMap) {
        try {
            Object sign = reqMap.get("sign");
            if (sign == null || StringUtil.isEmpty(sign.toString())) {
                throw new Exception("请求参数缺少签名！");
            }

            String reqBody = WXPayUtil.mapToXml(reqMap);

            log.info("微信支付查询订单，请求参数：" + reqBody);

            String response = HttpsUtil.post(WechatConstant.WX_ORDERQUERY_URL, reqBody);

            log.info("微信支付查询订单，" + response);

            return WXPayUtil.xmlToMap(response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new YimaoException("查询微信支付订单时发生异常！");
        }
    }

}
