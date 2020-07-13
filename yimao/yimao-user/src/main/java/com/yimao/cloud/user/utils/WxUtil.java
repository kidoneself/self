package com.yimao.cloud.user.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 描述：TODO
 *
 * @Author Zhang Bo
 * @Date 2019/8/8
 */
public class WxUtil {

    /**
     * 过滤掉微信昵称中无法保存到数据的特殊字符
     */
    public static String filterNickName(String name) {
        if (name == null) {
            return null;
        }
        if ("".equals(name.trim())) {
            return "";
        }
        Pattern patter = Pattern.compile("[a-zA-Z0-9\u4e00-\u9fa5]");
        Matcher match = patter.matcher(name);
        StringBuffer buffer = new StringBuffer();
        while (match.find()) {
            buffer.append(match.group());
        }
        return buffer.toString();
    }

}
