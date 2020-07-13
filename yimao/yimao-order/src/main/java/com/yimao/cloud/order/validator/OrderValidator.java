package com.yimao.cloud.order.validator;

import com.yimao.cloud.base.constant.Constant;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author liuhao@yimaokeji.com
 * @version v2.0
 * @date 2018/12/27
 */
public class OrderValidator {

    /**
     * 手机号正则验证
     *
     * @param mobile 手机号
     * @return Boolean
     */
    public static boolean mobileRegex(String mobile) {
        Pattern p = Pattern.compile(Constant.MOBILE_REGEX);
        Matcher m = p.matcher(mobile);
        return m.matches();
    }
}
