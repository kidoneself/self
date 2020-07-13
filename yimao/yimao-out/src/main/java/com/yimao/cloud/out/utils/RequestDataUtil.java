package com.yimao.cloud.out.utils;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.RSAUtil;
import com.yimao.cloud.out.entity.RequestMap;
import org.apache.commons.lang.StringUtils;

import java.security.PublicKey;
import java.util.UUID;

public final class RequestDataUtil {

    private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDkuWhTYcrR4Qlal430f8kAzmeKETMa62Q58PbTzQpdBSPAkmD2lvbya9qO0YsclAtJ6AXAt002mqtyXx9+ovuQFX744Z9Qy7HYLrOTJP2UOc8m0hL113yPm1zu+UKEd4LvA673X3sCiuCX8EcPKJPEwS+IzH1EzC6wa3JjrBvjVwIDAQAB";
    private static final String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOS5aFNhytHhCVqXjfR/yQDOZ4oRMxrrZDnw9tPNCl0FI8CSYPaW9vJr2o7RixyUC0noBcC3TTaaq3JfH36i+5AVfvjhn1DLsdgus5Mk/ZQ5zybSEvXXfI+bXO75QoR3gu8DrvdfewKK4JfwRw8ok8TBL4jMfUTMLrBrcmOsG+NXAgMBAAECgYEAsaFwbTlIe6DqU8nZvCW1pAOok6hx6RkgmIMUHj9iKgXA02CbH0b0+CZc8Ye5IvOypK4mMZnpxFtwhP1tBztqY+6tL8Qy8SOrHao7sTXlJofkojdLJYkSjiWQaf7isiyyanvXdnp+H6DTyAFU4qYmvo719xuKY5wj1r4tPxSkxUECQQD8cJTraO0I7XMcGyHYFiG8IZsLsfUie1+1hY9xb3xIQX1oBTmXzIsk5yBG1eD3bAithwgSXY4EMJQkNZzZ6kvhAkEA5/MzzgqLJAvtLSVUQW3rwNrJxvcv+cazK0jSXmFwfrPuLxsbCYDGgHBlp53qT2MczkKIt4A5cRsu34yAjkhWNwJAW+jNf4xbSPXTBDeosdkU/T8rCS4Itz4+EYQIPt+9Wz1k4FuOpYMWYiA4czHpz4uo+S0BtlYTn9jkBu7yJbEVYQJAFG966vZG2AAs/0NArxsOIEmmaV8x3OHCu1eJIUOYc7FKlN/ge+/ajUpZynDBSglDanC2NuSXKv3oHU31rZN19QJBAOqLvXkJd0fQedMrBPshX8UD982wg8iJStsogg+7nAHtBqE1mHbIDjbOHY2+WEKROtX6FNdG383vXaC8wwG7Tx0=";

    /**
     * 获取随机密钥，采用UUID实现
     */
    private static String getSecretKey() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 24);
    }

    /**
     * 对请求参数进行加密
     *
     * @param data 请求参数
     */
    public static JSONObject encryptRequestData(JSONObject data) {
        try {
            //获取密钥
            String secret = getSecretKey();
            //将明文转换成JSON字符串并加密
            String reqInfo = DESedeUtil.encrypt(data.toString(), secret);
            JSONObject json = new JSONObject();
            json.put("reqInfo", reqInfo);
            //对密钥进行加密
            PublicKey publicKey = RSAUtil.loadPublicKey(PUBLIC_KEY);
            json.put("reqSecret", RSAUtil.encrypt(secret, publicKey));
            return json;
        } catch (Exception e) {
            throw new YimaoException("对请求参数进行加密失败。");
        }
    }

    /**
     * 对请求参数进行解密
     *
     * @param data 请求参数
     */
    public static void decryptRequestData(RequestMap data) {
        if (StringUtils.isNotEmpty(data.getReqInfo())) {
            try {
                //使用私钥解密请求发送时生成的密钥
                String secret = RSAUtil.decrypt(data.getReqSecret(), RSAUtil.loadPrivateKey(PRIVATE_KEY));
                // 使用密钥对数据进行解密
                if (secret != null && secret.length() == 24) {
                    String reqInfo = DESedeUtil.decrypt(data.getReqInfo(), secret);
                    data.setReqInfo(reqInfo);
                    data.setReqMap(JSONObject.parseObject(reqInfo));
                }
            } catch (Exception e) {
                throw new YimaoException("对请求参数进行解密失败。");
            }
        }
    }

}
