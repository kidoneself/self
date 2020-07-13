package com.yimao.cloud.base.baideApi.utils;

import org.apache.commons.codec.binary.Base64;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

public class RSASignatureUtil {

    private static final String SIGN_ALGORITHMS = "SHA1withRSA";
    private static final String UTF8 = "UTF-8";

    /**
     * 私钥签名
     *
     * @param data
     * @param privateKey
     */
    public static String sign(String data, PrivateKey privateKey) {
        try {
            Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
            signature.initSign(privateKey);
            signature.update(data.getBytes(UTF8));
            byte[] signed = signature.sign();
            return new String(Base64.encodeBase64(signed));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 验证签名
     *
     * @param data
     * @param sign
     * @param publicKey
     * @return
     */
    public static boolean doCheck(String data, String sign, PublicKey publicKey) {
        try {
            Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
            signature.initVerify(publicKey);
            signature.update(data.getBytes(UTF8));
            return signature.verify(Base64.decodeBase64(sign));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
