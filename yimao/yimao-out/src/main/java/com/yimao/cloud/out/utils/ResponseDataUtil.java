package com.yimao.cloud.out.utils;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.baideApi.utils.RSASignatureUtil;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.RSAUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.out.entity.ResponseMap;

import java.security.PrivateKey;
import java.util.Objects;
import java.util.UUID;

public final class ResponseDataUtil {

    private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDkuWhTYcrR4Qlal430f8kAzmeKETMa62Q58PbTzQpdBSPAkmD2lvbya9qO0YsclAtJ6AXAt002mqtyXx9+ovuQFX744Z9Qy7HYLrOTJP2UOc8m0hL113yPm1zu+UKEd4LvA673X3sCiuCX8EcPKJPEwS+IzH1EzC6wa3JjrBvjVwIDAQAB";
    private static final String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOS5aFNhytHhCVqXjfR/yQDOZ4oRMxrrZDnw9tPNCl0FI8CSYPaW9vJr2o7RixyUC0noBcC3TTaaq3JfH36i+5AVfvjhn1DLsdgus5Mk/ZQ5zybSEvXXfI+bXO75QoR3gu8DrvdfewKK4JfwRw8ok8TBL4jMfUTMLrBrcmOsG+NXAgMBAAECgYEAsaFwbTlIe6DqU8nZvCW1pAOok6hx6RkgmIMUHj9iKgXA02CbH0b0+CZc8Ye5IvOypK4mMZnpxFtwhP1tBztqY+6tL8Qy8SOrHao7sTXlJofkojdLJYkSjiWQaf7isiyyanvXdnp+H6DTyAFU4qYmvo719xuKY5wj1r4tPxSkxUECQQD8cJTraO0I7XMcGyHYFiG8IZsLsfUie1+1hY9xb3xIQX1oBTmXzIsk5yBG1eD3bAithwgSXY4EMJQkNZzZ6kvhAkEA5/MzzgqLJAvtLSVUQW3rwNrJxvcv+cazK0jSXmFwfrPuLxsbCYDGgHBlp53qT2MczkKIt4A5cRsu34yAjkhWNwJAW+jNf4xbSPXTBDeosdkU/T8rCS4Itz4+EYQIPt+9Wz1k4FuOpYMWYiA4czHpz4uo+S0BtlYTn9jkBu7yJbEVYQJAFG966vZG2AAs/0NArxsOIEmmaV8x3OHCu1eJIUOYc7FKlN/ge+/ajUpZynDBSglDanC2NuSXKv3oHU31rZN19QJBAOqLvXkJd0fQedMrBPshX8UD982wg8iJStsogg+7nAHtBqE1mHbIDjbOHY2+WEKROtX6FNdG383vXaC8wwG7Tx0=";

    /**
     * 获取随机密钥，采用UUID实现
     */
    private static String getSecretKey() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 24);
    }

    /**
     * 对请求返回值进行加密
     *
     * @param data 请求返回值
     */
    public static void encryptResponseData(ResponseMap data) {
        try {
            String returnInfo = data.getRtnInfo();
            //对明文进行加密
            if (StringUtil.isNotEmpty(returnInfo)) {
                //获取密钥
                String secret = getSecretKey();
                //将明文字符串加密
                String rtnInfo = DESedeUtil.encrypt(returnInfo, secret);
                //设置返回信息
                data.setRtnInfo(rtnInfo);
                //对密钥进行加密
                PrivateKey privateKey = RSAUtil.loadPrivateKey(PRIVATE_KEY);
                String rtnSecret = RSAUtil.encrypt(secret, privateKey);
                data.setRtnSecret(rtnSecret);
                //使用私钥进行签名
                String sign = RSASignatureUtil.sign(data.getRtnInfo(), privateKey);
                data.setRtnSign(sign);
            } else {
                data.setRtnInfo("");
            }
        } catch (Exception e) {
            data.setRtnInfo("");
        }
    }

    /**
     * 对请求返回值进行解密
     *
     * @param data 请求返回值
     */
    public static JSONObject decryptResponseData(JSONObject data) {
        try {
            JSONObject map = new JSONObject();
            if (data != null) {
                map.put("code", data.getString("rtnCode"));
                if (Objects.equals(data.getString("rtnCode"), "00000000")) {
                    String rtnInfo = data.getString("rtnInfo");
                    //校验签名
                    if (RSASignatureUtil.doCheck(rtnInfo, data.getString("rtnSign"), RSAUtil.loadPublicKey(PUBLIC_KEY))) {
                        //解密密钥
                        String secret = RSAUtil.decrypt(data.getString("rtnSecret"), RSAUtil.loadPublicKey(PUBLIC_KEY));
                        //使用密钥解密密文
                        map.put("data", DESedeUtil.decrypt(rtnInfo, secret));
                    }
                } else {
                    map.put("msg", data.getString("rtnMsg"));
                }
            }
            return map;
        } catch (Exception e) {
            throw new YimaoException("对请求返回值进行解密失败。");
        }
    }

}
