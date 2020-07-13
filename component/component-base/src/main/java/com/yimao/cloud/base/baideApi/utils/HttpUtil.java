package com.yimao.cloud.base.baideApi.utils;

import net.sf.json.JSONObject;
import okhttp3.FormBody.Builder;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public final class HttpUtil {
    private static final String sKey = "bugull.yimaokeji.2018";

    public HttpUtil() {
    }

    private static OkHttpClient getInstance() {
        return HttpUtil.Instance.OK_HTTP_CLIENT;
    }

    public static String yimaoSign(long timestamp) {
        return StringUtil.encodeMD5(timestamp + "bugull.yimaokeji.2018").toLowerCase();
    }

    public static Response post(String url, Map<String, Object> params) throws IOException {
        Long timestamp = System.currentTimeMillis();
        Builder builder = new Builder();
        Iterator var4 = params.entrySet().iterator();

        while(var4.hasNext()) {
            Entry<String, Object> stringObjectEntry = (Entry)var4.next();
            builder.add((String)stringObjectEntry.getKey(), stringObjectEntry.getValue() == null ? "" : stringObjectEntry.getValue().toString());
        }

        Request request = (new Request.Builder()).header("ymtime", timestamp + "").header("ymsign", yimaoSign(timestamp)).header("ymname", "miryimao").url(url).post(builder.build()).build();
        return getInstance().newCall(request).execute();
    }

    public static Response postXML(String url, String xml) throws IOException {
        MediaType XML = MediaType.parse("text/xml; charset=utf-8");
        RequestBody body = RequestBody.create(XML, xml);
        Request request = (new Request.Builder()).url(url).post(body).build();
        Response response = getInstance().newCall(request).execute();
        return response;
    }

    public static Response post(String url, String json) throws IOException {
        Long timestamp = System.currentTimeMillis();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);
        Request request = (new Request.Builder()).header("ymtime", timestamp + "").header("ymsign", yimaoSign(timestamp)).header("ymname", "miryimao").url(url).post(body).build();
        Response response = getInstance().newCall(request).execute();
        return response;
    }

    public static Response get(String url) throws IOException {
        Long timestamp = System.currentTimeMillis();
        Request request = (new Request.Builder()).header("ymtime", timestamp + "").header("ymsign", yimaoSign(timestamp)).header("ymname", "miryimao").url(url).get().build();
        return getInstance().newCall(request).execute();
    }

    public static Map<String, Object> getPostResultMap(String resultStr) {
        Map<String, Object> result = new HashMap();
        if (!StringUtil.isEmpty(resultStr)) {
            JSONObject jsonObject = JSONObject.fromObject(resultStr);
            Iterator keys = jsonObject.keys();

            while(keys.hasNext()) {
                String key = keys.next().toString();
                result.put(key, jsonObject.getString(key));
            }
        }

        return result;
    }

    public static Map<String, Object> getPostResultMap(Response response) {
        String resultStr = null;

        try {
            resultStr = response.body().string();
        } catch (IOException var3) {
            var3.printStackTrace();
        }

        return getPostResultMap(resultStr);
    }

    private static class Instance {
        public static OkHttpClient OK_HTTP_CLIENT;

        private Instance() {
        }

        static {
            OK_HTTP_CLIENT = (new OkHttpClient()).newBuilder().connectTimeout(2L, TimeUnit.MINUTES).writeTimeout(2L, TimeUnit.MINUTES).readTimeout(3L, TimeUnit.MINUTES).build();
        }
    }
}
