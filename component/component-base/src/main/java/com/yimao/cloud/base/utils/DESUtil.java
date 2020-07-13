package com.yimao.cloud.base.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * @author Zhang Bo
 * @date 2018/8/29.
 */
public class DESUtil {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    public static final String KEY = "yimao018";

    /**
     * DESCBC加密
     *
     * @param text 源数据
     * @param key  密钥，长度必须是8的倍数
     */
    public static String encrypt(final String text, final String key) {
        try {
            // --生成key,同时制定是des还是DESede,两者的key长度要求不同
            final DESKeySpec desKeySpec = new DESKeySpec(key.getBytes(UTF8));
            final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            final SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

            // --加密向量
            final IvParameterSpec iv = new IvParameterSpec(key.getBytes(UTF8));

            // --通过Chipher执行加密得到的是一个byte的数组,Cipher.getInstance("DES")就是采用ECB模式,cipher.init(Cipher.ENCRYPT_MODE,secretKey)就可以了.
            final Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            final byte[] bytes = cipher.doFinal(text.getBytes(UTF8));

            // --通过base64,将加密数组转换成字符串
            return toHexString(bytes);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * DESCBC解密
     *
     * @param text 加密数据
     * @param key  密钥，长度必须是8的倍数
     */
    public static String decrypt(final String text, final String key) {
        try {
            // --通过base64,将字符串转成byte数组
            final byte[] bytesrc = convertHexString(text);

            // --解密的key
            final DESKeySpec desKeySpec = new DESKeySpec(key.getBytes(UTF8));
            final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            final SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

            // --加密向量
            final IvParameterSpec iv = new IvParameterSpec(key.getBytes(UTF8));

            // --Chipher对象解密Cipher.getInstance("DES")就是采用ECB模式,cipher.init(Cipher.DECRYPT_MODE,secretKey)就可以了.
            final Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            final byte[] bytes = cipher.doFinal(bytesrc);

            return new String(bytes);
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * @param
     * @return java.lang.String
     * @description 安装工app（注：百得售后不能共用）
     * @author Liu Yi
     * @date 2019/10/18 13:55
     */
    public static String decryptByEngineerApp(String message, String key) {
        String result = "";
        try {
            byte[] bytesrc = convertHexString(message);

            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            byte[] retByte = cipher.doFinal(bytesrc);
            result = new String(retByte);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
                | UnsupportedEncodingException | InvalidKeySpecException
                | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static byte[] convertHexString(String ss) {
        byte digest[] = new byte[ss.length() / 2];
        for (int i = 0; i < digest.length; i++) {
            String byteString = ss.substring(2 * i, 2 * i + 2);
            int byteValue = Integer.parseInt(byteString, 16);
            digest[i] = (byte) byteValue;
        }

        return digest;
    }

    // // 3DESECB加密,key必须是长度大于等于 3*8 = 24 位哈
    // public static String encryptThreeDESECB(final String src, final String key) throws Exception {
    //     final DESedeKeySpec dks = new DESedeKeySpec(key.getBytes("UTF-8"));
    //     final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
    //     final SecretKey securekey = keyFactory.generateSecret(dks);
    //
    //     final Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
    //     cipher.init(Cipher.ENCRYPT_MODE, securekey);
    //     final byte[] b = cipher.doFinal(src.getBytes());
    //
    //     // final BASE64Encoder encoder = new BASE64Encoder();
    //     return Base64.encodeBase64String(b).replaceAll("\r", "").replaceAll("\n", "");
    //
    // }
    //
    // // 3DESECB解密,key必须是长度大于等于 3*8 = 24 位哈
    // public static String decryptThreeDESECB(final String src, final String key) throws Exception {
    //     // --通过base64,将字符串转成byte数组
    //     // final BASE64Decoder decoder = new BASE64Decoder();
    //     // final byte[] bytesrc = decoder.decodeBuffer(src);
    //     final byte[] bytesrc = Base64.decodeBase64(src);
    //     // --解密的key
    //     final DESedeKeySpec dks = new DESedeKeySpec(key.getBytes("UTF-8"));
    //     final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
    //     final SecretKey securekey = keyFactory.generateSecret(dks);
    //
    //     // --Chipher对象解密
    //     final Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
    //     cipher.init(Cipher.DECRYPT_MODE, securekey);
    //     final byte[] retByte = cipher.doFinal(bytesrc);
    //
    //     return new String(retByte);
    // }

    /**
     * 将字节数组转换成16进制字符串
     *
     * @param bytes 字节数组
     */
    public static String toHexString(byte b[]) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            String plainText = Integer.toHexString(0xff & b[i]);
            if (plainText.length() < 2) plainText = "0" + plainText;
            hexString.append(plainText);
        }
        return hexString.toString();
    }
    //
    // /**
    //  * 将16进制字符串转换成字节数组
    //  *
    //  * @param text 16进制字符串
    //  */
    // public static byte[] convertHexString(String text) {
    //     byte[] digest = new byte[text.length() / 2];
    //     for (int i = 0; i < digest.length; ++i) {
    //         String byteString = text.substring(2 * i, 2 * i + 2);
    //         int byteValue = Integer.parseInt(byteString, 16);
    //         digest[i] = (byte) byteValue;
    //     }
    //     return digest;
    // }

    // public static void main(String[] args) {
    //     System.out.println(encrypt("164975", IV));
    // }

}
