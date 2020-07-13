package com.yimao.cloud.out.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateUtils {
    private static final String V_INTEGER = "^-?[1-9]\\d*$";
    private static final String V_Z_INDEX = "^[1-9]\\d*$";
    private static final String V_NEGATIVE_INTEGER = "^-[1-9]\\d*$";
    private static final String V_NUMBER = "^([+-]?)\\d*\\.?\\d+$";
    private static final String V_POSITIVE_NUMBER = "^[1-9]\\d*|0$";
    private static final String V_NEGATINE_NUMBER = "^-[1-9]\\d*|0$";
    private static final String V_FLOAT = "^([+-]?)\\d*\\.\\d+$";
    private static final String V_POSTTIVE_FLOAT = "^[1-9]\\d*.\\d*|0.\\d*[1-9]\\d*$";
    private static final String V_NEGATIVE_FLOAT = "^-([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*)$";
    private static final String V_UNPOSITIVE_FLOAT = "^[1-9]\\d*.\\d*|0.\\d*[1-9]\\d*|0?.0+|0$";
    private static final String V_UN_NEGATIVE_FLOAT = "^(-([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*))|0?.0+|0$";
    private static final String V_EMAIL = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
    private static final String V_COLOR = "^[a-fA-F0-9]{6}$";
    private static final String V_URL = "^http[s]?:\\/\\/([\\w-]+\\.)+[\\w-]+([\\w-./?%&=]*)?$";
    private static final String V_CHINESE = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D]+$";
    private static final String V_ASCII = "^[\\x00-\\xFF]+$";
    private static final String V_ZIPCODE = "^\\d{6}$";
    private static final String V_MOBILE = "^(1)[0-9]{10}$";
    private static final String V_IP4 = "^(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)$";
    private static final String V_NOTEMPTY = "^\\S+$";
    private static final String V_PICTURE = "(.*)\\.(jpg|bmp|gif|ico|pcx|jpeg|tif|png|raw|tga)$";
    private static final String V_RAR = "(.*)\\.(rar|zip|7zip|tgz)$";
    private static final String V_DATE = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-)) (20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d$";
    private static final String V_QQ_NUMBER = "^[1-9]*[1-9][0-9]*$";
    private static final String V_TEL = "^(([0\\+]\\d{2,3}-)?(0\\d{2,3})-)?(\\d{7,8})(-(\\d{3,}))?$";
    private static final String V_USERNAME = "^\\w+$";
    private static final String V_LETTER = "^[A-Za-z]+$";
    private static final String V_LETTER_U = "^[A-Z]+$";
    private static final String V_LETTER_I = "^[a-z]+$";
    private static final String V_IDCARD = "^(\\d{15}$|^\\d{18}$|^\\d{17}(\\d|X|x))$";
    private static final String V_PASSWORD_REG = "[A-Za-z]+[0-9]";
    private static final String V_PASSWORD_LENGTH = "^\\d{6,18}$";
    private static final String V_TWO＿POINT = "^[0-9]+(.[0-9]{2})?$";
    private static final String V_31DAYS = "^((0?[1-9])|((1|2)[0-9])|30|31)$";

    private ValidateUtils() {
    }

    public static boolean Integer(String value) {
        return match("^-?[1-9]\\d*$", value);
    }

    public static boolean Z_index(String value) {
        return match("^[1-9]\\d*$", value);
    }

    public static boolean Negative_integer(String value) {
        return match("^-[1-9]\\d*$", value);
    }

    public static boolean Number(String value) {
        return match("^([+-]?)\\d*\\.?\\d+$", value);
    }

    public static boolean PositiveNumber(String value) {
        return match("^[1-9]\\d*|0$", value);
    }

    public static boolean NegatineNumber(String value) {
        return match("^-[1-9]\\d*|0$", value);
    }

    public static boolean Is31Days(String value) {
        return match("^((0?[1-9])|((1|2)[0-9])|30|31)$", value);
    }

    public static boolean ASCII(String value) {
        return match("^[\\x00-\\xFF]+$", value);
    }

    public static boolean Chinese(String value) {
        return match("^[\\u4E00-\\u9FA5\\uF900-\\uFA2D]+$", value);
    }

    public static boolean Color(String value) {
        return match("^[a-fA-F0-9]{6}$", value);
    }

    public static boolean Date(String value) {
        return match("^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-)) (20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d$", value);
    }

    public static boolean Email(String value) {
        return match("^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$", value);
    }

    public static boolean Float(String value) {
        return match("^([+-]?)\\d*\\.\\d+$", value);
    }

    public static boolean IDcard(String value) {
        return match("^(\\d{15}$|^\\d{18}$|^\\d{17}(\\d|X|x))$", value);
    }

    public static boolean IP4(String value) {
        return match("^(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)$", value);
    }

    public static boolean Letter(String value) {
        return match("^[A-Za-z]+$", value);
    }

    public static boolean Letter_i(String value) {
        return match("^[a-z]+$", value);
    }

    public static boolean Letter_u(String value) {
        return match("^[A-Z]+$", value);
    }

    public static boolean Mobile(String value) {
        return match("^(1)[0-9]{10}$", value);
    }

    public static boolean Negative_float(String value) {
        return match("^-([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*)$", value);
    }

    public static boolean Notempty(String value) {
        return match("^\\S+$", value);
    }

    public static boolean Number_length(String value) {
        return match("^\\d{6,18}$", value);
    }

    public static boolean Password_reg(String value) {
        return match("[A-Za-z]+[0-9]", value);
    }

    public static boolean Picture(String value) {
        return match("(.*)\\.(jpg|bmp|gif|ico|pcx|jpeg|tif|png|raw|tga)$", value);
    }

    public static boolean Posttive_float(String value) {
        return match("^[1-9]\\d*.\\d*|0.\\d*[1-9]\\d*$", value);
    }

    public static boolean QQnumber(String value) {
        return match("^[1-9]*[1-9][0-9]*$", value);
    }

    public static boolean Rar(String value) {
        return match("(.*)\\.(rar|zip|7zip|tgz)$", value);
    }

    public static boolean Tel(String value) {
        return match("^(([0\\+]\\d{2,3}-)?(0\\d{2,3})-)?(\\d{7,8})(-(\\d{3,}))?$", value);
    }

    public static boolean Two_point(String value) {
        return match("^[0-9]+(.[0-9]{2})?$", value);
    }

    public static boolean Un_negative_float(String value) {
        return match("^(-([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*))|0?.0+|0$", value);
    }

    public static boolean Unpositive_float(String value) {
        return match("^[1-9]\\d*.\\d*|0.\\d*[1-9]\\d*|0?.0+|0$", value);
    }

    public static boolean Url(String value) {
        return match("^http[s]?:\\/\\/([\\w-]+\\.)+[\\w-]+([\\w-./?%&=]*)?$", value);
    }

    public static boolean UserName(String value) {
        return match("^\\w+$", value);
    }

    public static boolean Zipcode(String value) {
        return match("^\\d{6}$", value);
    }

    private static boolean match(String regex, String str) {
        if (str == null) {
            return false;
        } else {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(str);
            return matcher.matches();
        }
    }
	
}
