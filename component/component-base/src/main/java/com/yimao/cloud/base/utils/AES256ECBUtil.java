package com.yimao.cloud.base.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;

/**
 * 描述：AES256ECB解密
 *
 * @Author Zhang Bo
 * @Date 2019/10/14
 */
public class AES256ECBUtil {

    //密钥算法
    private static final String ALGORITHM = "AES";
    //加解密算法/工作模式/填充方式
    private static final String ALGORITHM_MODE_PADDING = "AES/ECB/PKCS7Padding";

    /**
     * AES256ECB解密
     */
    public static String decryptData(String data, String key) {
        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_PADDING);
            SecretKeySpec skeySpec = new SecretKeySpec(MD5Util.encodeMD5(key).toLowerCase().getBytes(), ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            Base64 base64 = new Base64();
            return new String(cipher.doFinal(base64.decode(data)), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
