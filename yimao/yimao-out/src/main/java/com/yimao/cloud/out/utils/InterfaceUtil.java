package com.yimao.cloud.out.utils;

import com.yimao.cloud.base.baideApi.utils.StringUtil;
import net.sf.json.JSONObject;

import java.util.Map;

public class InterfaceUtil {
    public static final String BAIDE_SUCCECC_CODE = "00000000";

    public InterfaceUtil() {
    }

    public static boolean checkBaideExecuteResult(Map map) {
        return !ObjectUtil.isNull(map) && map.size() > 0 && map.containsKey("code") && "00000000".equals(map.get("code").toString());
    }

    public static String getResponseCode(Map map) {
        return !ObjectUtil.isNull(map) && map.size() > 0 && map.containsKey("code") ? map.get("code").toString() : "";
    }

    public static String analysisMapData(Map<String, Object> map, String key) {
        if (!checkBaideExecuteResult(map)) {
            return "";
        } else if (!StringUtil.isEmpty(key) && map.containsKey("data")) {
            Object value = JSONObject.fromObject(map.get("data")).get(key);
            return value == null ? "" : value.toString();
        } else {
            return "";
        }
    }
}
