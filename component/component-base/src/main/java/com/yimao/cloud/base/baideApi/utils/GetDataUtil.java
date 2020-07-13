package com.yimao.cloud.base.baideApi.utils;

import com.yimao.cloud.base.baideApi.constant.Constant;
import com.yimao.cloud.base.context.SpringContextHolder;
import com.yimao.cloud.base.enums.EnvironmentEnum;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import okhttp3.Response;

import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public final class GetDataUtil {
    public GetDataUtil() {
    }

    public static String PUBLIC_KEY;

    static {
        PUBLIC_KEY = Constant.PUBLIC_KEY_PRO;
        if (EnvironmentEnum.PRO.code.equalsIgnoreCase(SpringContextHolder.getEnvironment())) {
            PUBLIC_KEY = Constant.PUBLIC_KEY_PRO;
        } else {
            PUBLIC_KEY = Constant.PUBLIC_KEY_TEST;
        }
    }

    public static String post(Map<String, Object> params, String url) throws Exception {
        String key = StringUtil.randomString(24);
        JSONObject object = JSONObject.fromObject(params);
        String reqInfo = ThreeDESUtil.encrypt(object.toString(), key);
        RSAPublicKey rsaPublicKey = RSAEncryptUtil.loadPublicKeyByStr(PUBLIC_KEY);
        Map<String, String> urlMap = new HashMap();
        urlMap.put("reqInfo", reqInfo);
        urlMap.put("reqSecret", RSAEncryptUtil.encrypt(rsaPublicKey, key));
        String json = JSONObject.fromObject(urlMap).toString();

        Response response = HttpUtil.post(url, json);
        String result = response.body().string();
        // log.info(result);
        return result;
    }

    public static Map<String, Object> getData(String data) throws Exception {
        Map<String, Object> map = new HashMap();
        if (!StringUtil.isEmpty(data)) {
            JSONObject object = JSONObject.fromObject(data);
            map.put("code", object.getString("rtnCode"));
            if (object.getString("rtnCode").equals("00000000")) {
                if (RSASignatureUtil.doCheck(object.getString("rtnInfo"), object.getString("rtnSign"), RSAEncryptUtil.loadPublicKeyByStr(PUBLIC_KEY)) && !StringUtil.isEmpty(object.getString("rtnInfo"))) {
                    String rtnSecret = RSAEncryptUtil.decrypt(RSAEncryptUtil.loadPublicKeyByStr(PUBLIC_KEY), object.getString("rtnSecret"));
                    map.put("data", ThreeDESUtil.decrypt(object.getString("rtnInfo"), rtnSecret));
                }
            } else {
                map.put("msg", object.getString("rtnMsg"));
            }
        }

        return map;
    }
    
}
