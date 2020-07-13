package com.yimao.cloud.out.utils;


import com.yimao.cloud.out.enums.OpenApiStatusCode;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public final class OpenApiResult {
    public OpenApiResult() {
    }

    public static Map<String, Object> success(HttpServletRequest request) {
        Map map = initMap(OpenApiStatusCode.SUCCESS);
        if (request != null) {
            try {
                request.setAttribute("executeResult", map);
            } catch (Exception var3) {
                ;
            }
        }

        return map;
    }

    public static Map<String, Object> error(HttpServletRequest request, OpenApiStatusCode statusCode) {
        Map<String, Object> map = new HashMap();
        map.put("code", statusCode.getCode());
        map.put("msg", statusCode.getMsg());
        request.setAttribute("executeResult", map);
        return map;
    }

    public static Map<String, Object> error(HttpServletRequest request, String msg) {
        Map<String, Object> map = new HashMap();
        map.put("code", OpenApiStatusCode.YUNSIGN_ERROR.getCode());
        map.put("msg", msg);
        request.setAttribute("executeResult", map);
        return map;
    }

    public static Map<String, Object> result(HttpServletRequest request, String code, String msg, Object result) {
        Map<String, Object> map = new HashMap();
        map.put("code", code);
        map.put("msg", msg);
        map.put("result", result);
        request.setAttribute("executeResult", map);
        return map;
    }

    public static Map<String, Object> result(HttpServletRequest request, Object result) {
        Map<String, Object> map = success(request);
        map.put("result", result);
        request.setAttribute("executeResult", map);
        return map;
    }

    public static Map<String, Object> DATA_NOT_FOUND() {
        return initMap(OpenApiStatusCode.DATA_NOT_FOUND);
    }

    public static Map<String, Object> APPID_NOT_EXISTED() {
        return initMap(OpenApiStatusCode.APPID_NOT_EXISTED);
    }

    private static Map<String, Object> initMap(OpenApiStatusCode e) {
        Map<String, Object> map = new HashMap();
        map.put("code", e.getCode());
        map.put("msg", e.getMsg());
        return map;
    }
}
