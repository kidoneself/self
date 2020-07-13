package com.yimao.cloud.base.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;

public final class StringUtil {

    private static final Logger logger = LoggerFactory.getLogger(StringUtil.class);

    private static final String CHARSET_NAME = "UTF-8";
    public static final String EMPTY = "";

    /**
     * 判断字符串是否为空
     *
     * @param s
     */
    public static boolean isEmpty(final String s) {
        return s == null || s.trim().length() == 0;
    }

    public static boolean isNotEmpty(final String s) {
        return !isEmpty(s);
    }

    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    public static boolean isNoneBlank(final CharSequence... css) {
        return !isAnyBlank(css);
    }

    public static boolean isAnyBlank(final CharSequence... css) {
        if (ArrayUtil.isEmpty(css)) {
            return true;
        }
        for (final CharSequence cs : css) {
            if (isBlank(cs)) {
                return true;
            }
        }
        return false;
    }

    public static String reverse(final String str) {
        if (str == null) {
            return null;
        }
        return new StringBuilder(str).reverse().toString();
    }

    /**
     * 获取文件的后缀名
     *
     * @param filename
     */
    public static String getExt(String filename) {
        int index = filename.lastIndexOf(".");
        if (index > 0) {
            return filename.substring(index + 1).toLowerCase();
        }
        return null;
    }

    /**
     * 字节数组转换为字符串
     *
     * @param bytes
     */
    public static String toString(byte[] bytes) {
        try {
            return new String(bytes, CHARSET_NAME);
        } catch (UnsupportedEncodingException e) {
            return EMPTY;
        }
    }

    /**
     * 字符串转换为字节数组
     *
     * @param str
     */
    public static byte[] getBytes(String str) {
        if (str != null) {
            try {
                return str.getBytes(CHARSET_NAME);
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        } else {
            return null;
        }
    }


    /**
     * 字符串转换为数组
     *
     * @param str
     */
    public static String[] spiltStr(String str) {
        try {
            if (str != null) {
                if (StringUtil.isNotEmpty(str)) {
                    String[] idArray = new String[]{};
                    if (str.contains(",")) {
                        idArray = str.split(",");
                    } else {
                        idArray = new String[]{str};
                    }
                    return idArray;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 字符串转换为List
     *
     * @param str
     */
    public static List<String> spiltListByStr(String str) {
        try {
            if (str != null) {
                if (StringUtil.isNotEmpty(str)) {
                    String[] idArray = new String[]{};
                    if (str.contains(",")) {
                        idArray = str.split(",");
                    } else {
                        idArray = new String[]{str};
                    }
                    return Arrays.asList(idArray);
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将集合元素以逗号格式转换成String类型的字符串格式
     *
     * @param collection 要转换的集合对象
     */

    public static String collectionToString(Collection<? extends CharSequence> collection) {
        return collectionToString(collection, ",");
    }

    /**
     * 将集合元素以指定分隔符的格式转换成String类型的字符串格式
     *
     * @param collection 要转换的集合对象
     * @param separator  分隔符
     */
    public static String collectionToString(Collection<? extends CharSequence> collection, String separator) {
        if (collection == null || collection.size() < 1) {
            return null;
        }
        Iterator<? extends CharSequence> iterator = collection.iterator();
        StringBuffer sb = new StringBuffer();
        while (iterator.hasNext()) {
            CharSequence text = iterator.next();
            sb.append(text).append(separator);
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");
        System.out.println(collectionToString(list));
    }

    /**
     * 驼峰格式字符串转换为下划线格式字符串
     *
     * @param param
     */
    public static String camelToUnderline(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append("_");
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 转换业务ids
     */
    public static List<Integer> convertProcesIds(String Ids) throws NumberFormatException {
        if (StringUtil.isEmpty(Ids)) {
            return null;
        }
        String[] SPRids = Ids.split(",");
        if (SPRids.length <= 0) {
            return null;
        }
        Integer[] IPRids = new Integer[SPRids.length];
        for (int i = 0; i < SPRids.length; i++) {
            IPRids[i] = Integer.valueOf(SPRids[i]);
        }
        List<Integer> Ilist = Arrays.asList(IPRids);
        return Ilist;
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


    public static String removeTransferChar(String str) {
        return str == null ? null : str.replaceAll("\n", "").replaceAll("\r", "").replaceAll("\b", "");
    }

}
