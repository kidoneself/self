package com.yimao.cloud.station.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class CheckUtils {
	private static String phoneRegex="^[1](([3|5|8][\\d])|([4][4,5,6,7,8,9])|([6][2,5,6,7])|([7][^9])|([9][1,8,9]))[\\d]{8}$";
    /**
     * 校验手机格式
     * @param str
     * @return
     */
    public static boolean isMobile(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
    
        if(StringUtils.isNotBlank(str)){
            p = Pattern.compile(phoneRegex);
            m = p.matcher(str);
            b = m.matches();
        }
        return b;
    }
}
