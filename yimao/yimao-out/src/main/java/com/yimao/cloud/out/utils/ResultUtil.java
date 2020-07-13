package com.yimao.cloud.out.utils;

import java.util.Map;

public final class ResultUtil {
    public ResultUtil() {
    }

    public static void success(Map<String, Object> map) {
        map.put("success", true);
    }

    public static void success(Map<String, Object> map, String msg) {
        map.put("success", true);
        map.put("msg", msg);
    }

    public static void error(Map<String, Object> map) {
        map.put("success", false);
    }

    public static void error(Map<String, Object> map, String code, String str) {
        map.put("success", false);
        map.put("errorCode", code);
        map.put("errorMsg", str);
    }

    public static void error(Map<String, Object> map, String str) {
        map.put("success", false);
        map.put("errorMsg", str);
    }
}
