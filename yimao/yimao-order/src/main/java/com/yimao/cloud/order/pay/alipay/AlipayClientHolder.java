package com.yimao.cloud.order.pay.alipay;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.yimao.cloud.base.constant.AliConstant;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里支付客戶端<br>
 * alipayClient只需要初始化一次，后续调用不同的API都可以使用同一个alipayClient对象。
 */
public class AlipayClientHolder {

    // private static AlipayClient holder = null;
    private static Map<String, AlipayClient> holderMap = new HashMap<>();

    public static AlipayClient instance(String appid, String privateKey, String publicKey) {
        if (holderMap.get(appid) == null) {
            synchronized (AlipayClientHolder.class) {
                if (holderMap.get(appid) == null) {
                    //养未来和生物科技使用RSA2加密
                    if (AliConstant.APPID_YWL.equals(appid) || AliConstant.APPID_SWKJ.equals(appid)) {
                        holderMap.put(appid, new DefaultAlipayClient(AliConstant.URL, appid, privateKey, AliConstant.FORMAT, AliConstant.CHARSET, publicKey, AliConstant.RSA2));
                    } else {
                        holderMap.put(appid, new DefaultAlipayClient(AliConstant.URL, appid, privateKey, AliConstant.FORMAT, AliConstant.CHARSET, publicKey, AliConstant.RSA));
                    }
                }
            }
        }
        return holderMap.get(appid);
        // if (holder == null) {
        //     synchronized (AlipayClientHolder.class) {
        //         if (holder == null) {
        //             // holder = new DefaultAlipayClient(AliConstant.URL, AliConstant.APPID,
        //             //         AliConstant.RSA_PRIVATE_KEY, AliConstant.FORMAT, AliConstant.CHARSET,
        //             //         AliConstant.ALIPAY_PUBLIC_KEY, AliConstant.SIGNTYPE);
        //             holder = new DefaultAlipayClient(AliConstant.URL, appid,
        //                     privateKey, AliConstant.FORMAT, AliConstant.CHARSET, publicKey);
        //         }
        //     }
        // }
        // return holder;
    }
}
