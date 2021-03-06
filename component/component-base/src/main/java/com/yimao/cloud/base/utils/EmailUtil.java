package com.yimao.cloud.base.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description: 邮箱校验工具类
 * @author: yu chunlei
 * @create: 2019-11-15 16:44:20
 **/
public class EmailUtil {

    public static boolean isEmail(String email){
        if (null==email || "".equals(email)){
            return false;
        }
        String regEx1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p = Pattern.compile(regEx1);
        Matcher m = p.matcher(email);
        if(m.matches()){
            return true;
        }else{
            return false;
        }
    }

}
