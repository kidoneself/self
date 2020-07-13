package com.yimao.cloud.base.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * MD5工具类
 */
public class MD5Util {
    private static Logger logger = LoggerFactory.getLogger(MD5Util.class);
    private static final char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static MessageDigest messagedigest = null;

    static {
        try {
            //指定算法
            messagedigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            logger.error("MD5FileUtil messagedigest初始化失败", e);
        }
    }

    public static String getMD5String(String str) {
        return getMD5String(str.getBytes());
    }

    private static String getMD5String(byte[] bytes) {
        //使用指定的byte数组更新摘要。
        messagedigest.update(bytes);
        //通过.digest()执行诸如填充之类的最终操作完成哈希计算。在调用此方法之后，摘要被重置。
        return bufferToHex(messagedigest.digest());
    }

    private static String bufferToHex(byte[] bytes) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte[] bytes, int paramInt1, int paramInt2) {
        StringBuffer localStringBuffer = new StringBuffer(2 * paramInt2);
        int i = paramInt1 + paramInt2;
        for (int j = paramInt1; j < i; j++) {
            appendHexPair(bytes[j], localStringBuffer);
        }
        return localStringBuffer.toString();
    }

    private static void appendHexPair(byte b, StringBuffer paramStringBuffer) {
        char c1 = hexDigits[(b & 0xF0) >> 4];
        char c2 = hexDigits[b & 0xF];
        paramStringBuffer.append(c1);
        paramStringBuffer.append(c2);
    }

    public static boolean checkPassword(String s1, String s2) {
        String str = getMD5String(s1);
        return Objects.equals(str, s2);
    }

    public static String encodeMD5(String str) {
        if (str == null || str.trim().length() == 0) {
            return null;
        }
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            logger.error("MD5Util Can't encode to md5 string", ex);
            return null;
        }
        md.update(str.getBytes());
        byte[] datas = md.digest();
        int len = datas.length;
        char[] chars = new char[len * 2];
        int k = 0;
        for (byte byte0 : datas) {
            chars[k++] = hexDigits[byte0 >>> 4 & 0xf];
            chars[k++] = hexDigits[byte0 & 0xf];
        }
        return new String(chars);
    }

}