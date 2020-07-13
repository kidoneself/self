package com.yimao.cloud.engineer.utils;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.yimao.cloud.engineer.enums.ApiStatusCode;

public final class ApiResult {
    public ApiResult() {
    }

    public static Map<String, Object> success(HttpServletRequest request) {
        Map map = initMap(ApiStatusCode.SUCCESS);
        return map;
    }
    public static Map<String, Object> success() {
        Map map = initMap(ApiStatusCode.SUCCESS);
        return map;
    }

    public static Map<String, Object> error(HttpServletRequest request, String code, String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("code", code);
        map.put("msg", message);
        request.setAttribute("executeResult", map);
        return map;
    }

    public static Map<String, Object> error(HttpServletRequest request, ApiStatusCode statusCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("code", statusCode.getCode());
        map.put("msg", statusCode.getTextZh());
        request.setAttribute("executeResult", map);
        return map;
    }

    public static Map<String, Object> error(HttpServletRequest request, String msg) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("code", ApiStatusCode.BAIDE_ERROR.getCode());
        map.put("msg", msg);
        request.setAttribute("executeResult", map);
        return map;
    }

    public static Map<String, Object> error(String msg) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("code", ApiStatusCode.BAIDE_ERROR.getCode());
        map.put("msg", msg);
        return map;
    }

    public static Map<String, Object> result(HttpServletRequest request, String code, String msg, Object result) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        map.put("code", code);
        map.put("msg", msg);
        map.put("result", result);
        request.setAttribute("executeResult", map);
        return map;
    }

    public static Map<String, Object> result(HttpServletRequest request, Object result) {
        Map<String, Object> map = success(request);
        map.put("data", result);
        request.setAttribute("executeResult", map);
        return map;
    }

    private static Map<String, Object> initMap(ApiStatusCode e) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        map.put("code", e.getCode());
        map.put("msg", e.getTextZh());
        return map;
    }
}
