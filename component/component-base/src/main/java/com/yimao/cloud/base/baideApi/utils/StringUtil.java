package com.yimao.cloud.base.baideApi.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtil {
    private static Long randomIndex = System.currentTimeMillis() / 100000L;
    private static final String regEx = "[一-龥]";
    private static final Pattern pat = Pattern.compile("[一-龥]");

    public StringUtil() {
    }

    public static boolean isEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }

    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

    public static String encodeMD5(String s) {
        if (s != null && s.trim().length() != 0) {
            MessageDigest md = null;

            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException var9) {
                return null;
            }

            char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
            md.update(s.getBytes());
            byte[] datas = md.digest();
            int len = datas.length;
            char[] str = new char[len * 2];
            int k = 0;

            for(int i = 0; i < len; ++i) {
                byte byte0 = datas[i];
                str[k++] = hexDigits[byte0 >>> 4 & 15];
                str[k++] = hexDigits[byte0 & 15];
            }

            return new String(str);
        } else {
            return null;
        }
    }

    public static boolean ifContainSymbolOrChinese(String input) {
        for(int i = 0; i < input.length(); ++i) {
            char bb = input.charAt(i);
            if ("`¬!\"£$%^*()~=#{}[];':,./?/*-_+，·@#￥……&。、‘！".contains(bb + "")) {
                return false;
            }
        }

        Matcher matcher = pat.matcher(input);
        if (matcher.find()) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean ifAllNumbers(String input) {
        boolean isNum = input.matches("[0-9]+");
        return isNum;
    }

    public static String randomString(int length) {
        String ALLCHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuffer sb = new StringBuffer();
        Random random = new Random();

        for(int i = 0; i < length; ++i) {
            sb.append(ALLCHAR.charAt(random.nextInt(ALLCHAR.length())));
        }

        return sb.toString();
    }

    public static String getExt(String filename) {
        String ext = null;
        int index = filename.lastIndexOf(".");
        if (index > 0) {
            ext = filename.substring(index + 1).toLowerCase();
        }

        return ext;
    }

    public static synchronized String localSafeRandomString(int len) {
        Long var1 = randomIndex;
        randomIndex = randomIndex + 1L;
        StringBuffer sb = new StringBuffer(Long.toString(randomIndex, 36));

        while(len > sb.length()) {
            sb.append("0");
        }

        return sb.toString();
    }

    public static String removeTransferChar(String str) {
        return str == null ? null : str.replaceAll("\n", "").replaceAll("\r", "").replaceAll("\b", "");
    }
}
