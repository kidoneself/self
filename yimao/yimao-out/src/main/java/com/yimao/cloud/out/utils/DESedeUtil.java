package com.yimao.cloud.out.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;

public final class DESedeUtil {

    private static final String DESede = "DESede";
    private static final Charset UTF8 = Charset.forName("UTF-8");

    public DESedeUtil() {
    }

    public static String encrypt(String data, String secretKey) {
        SecretKeySpec deskey = new SecretKeySpec(secretKey.getBytes(), DESede);
        try {
            Cipher cipher = Cipher.getInstance(DESede);
            cipher.init(1, deskey);
            byte[] output = cipher.doFinal(data.getBytes(UTF8));
            return Base64.encodeBase64String(output);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String decrypt(String data, String secretKey) {
        SecretKeySpec deskey = new SecretKeySpec(secretKey.getBytes(), DESede);

        try {
            Cipher cipher = Cipher.getInstance(DESede);
            cipher.init(2, deskey);
            byte[] output = cipher.doFinal(Base64.decodeBase64(data));
            return new String(output, UTF8);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
