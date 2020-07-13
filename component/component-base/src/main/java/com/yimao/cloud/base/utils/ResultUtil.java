package com.yimao.cloud.base.utils;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhilin.he
 * @description 结果集
 * @date 2019/8/13 15:44
 **/
public class ResultUtil {

    public ResultUtil() {
    }

    public static JSONObject success() {
        JSONObject object = new JSONObject();
        object.put("success", true);
        return object;
    }

    public static JSONObject success(JSONObject object) {
        object.put("success", true);
        return object;
    }

    public static JSONObject success(JSONObject object, String msg) {
        object.put("success", true);
        object.put("msg", msg);
        return object;
    }

    public static JSONObject error(JSONObject object) {
        object.put("success", false);
        return object;
    }

    public static JSONObject error(JSONObject object, String code, String str) {
        object.put("success", false);
        object.put("errorCode", code);
        object.put("errorMsg", str);
        return object;
    }

    public static JSONObject error(JSONObject object, String str) {
        object.put("success", false);
        object.put("errorMsg", str);
        return object;
    }

}
