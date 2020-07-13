package com.yimao.cloud.base.baideApi.utils;

import com.yimao.cloud.base.baideApi.constant.Constant;
import com.yimao.cloud.base.context.SpringContextHolder;
import com.yimao.cloud.base.enums.EnvironmentEnum;

/**
 * @description   动态判断不同环境请求的售后
 * @author Liu Yi
 * @date 2019/9/23 16:55
 * @param
 * @return
 */
public final class GetBaiDeUtil {

    /**
     * @description   动态判断不同环境请求的售后地址
     * @author Liu Yi
     * @date 2019/9/23 17:02
     * @param
     * @return java.lang.String
     */
    public static String getBaideUrl(){
        String url;
        if (EnvironmentEnum.PRO.code.equalsIgnoreCase(SpringContextHolder.getEnvironment())) {
            url = Constant.BAIDE_HTTP_URL_PRO;
        } else if (EnvironmentEnum.TEST.code.equalsIgnoreCase(SpringContextHolder.getEnvironment())) {
            url = Constant.BAIDE_HTTP_URL_TEST;
        } else if (EnvironmentEnum.DEV.code.equalsIgnoreCase(SpringContextHolder.getEnvironment())) {
            url = Constant.BAIDE_HTTP_URL_DEV;
        }/*else if (EnvironmentEnum.LOCAL.code.equalsIgnoreCase(SpringContextHolder.getEnvironment())) {
            url = Constant.BAIDE_HTTP_URL_LOCAL;
        }*/else{
            url = Constant.BAIDE_HTTP_URL_LOCAL;
        }
       return  url;
    }

    /**
     * @description   动态判断不同环境请求的售后公钥
     * @author Liu Yi
     * @date 2019/9/23 17:02
     * @param
     * @return java.lang.String
     */
   /* public static String getBaidePublicKey(){
        String url;
        if (EnvironmentEnum.PRO.code.equalsIgnoreCase(SpringContextHolder.getEnvironment())) {
            url = Constant.PUBLIC_KEY_PRO;
        }else{
            url = Constant.PUBLIC_KEY_TEST;
        }
        return url;
    }*/
}
