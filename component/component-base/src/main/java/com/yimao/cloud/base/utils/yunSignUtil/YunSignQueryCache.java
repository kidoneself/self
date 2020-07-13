package com.yimao.cloud.base.utils.yunSignUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


public class YunSignQueryCache {
    static Logger Log = LogManager.getLogger(YunSignQueryCache.class);
    static Integer CACHE_MAX_SIZE = 1024;
    static Map<String, YunSignResult> YUNSING_QUERY_CACHE = null;

    public YunSignQueryCache() {
    }

    public static void initCache() {
        if (YUNSING_QUERY_CACHE == null) {
            YUNSING_QUERY_CACHE = new LinkedHashMap();
        }

    }

    public static void cacheQuery(String query, YunSignResult result) {
        if (YUNSING_QUERY_CACHE == null) {
            initCache();
        }

        if (YUNSING_QUERY_CACHE.size() >= CACHE_MAX_SIZE) {
            clearCache(CACHE_MAX_SIZE / 3);
        }

        if (result != null && result.getCode() != null && !result.getCode().equals("-1")) {
            YUNSING_QUERY_CACHE.put(query, result);
        }

    }

    public static YunSignResult getCache(String query) {
        if (YUNSING_QUERY_CACHE == null) {
            initCache();
        }

        if (YUNSING_QUERY_CACHE.containsKey(query)) {
            Log.info("从缓存中获取" + query);
            return (YunSignResult)YUNSING_QUERY_CACHE.get(query);
        } else {
            return null;
        }
    }

    private static void clearCache(int count) {
        if (YUNSING_QUERY_CACHE == null) {
            initCache();
        } else {
            int index = 0;

            for(Iterator it = YUNSING_QUERY_CACHE.keySet().iterator(); it.hasNext() && count > index; ++index) {
                String key = (String)it.next();
                YUNSING_QUERY_CACHE.remove(key);
            }

            Log.info("清理" + index + "项缓存数据！");
        }

    }

    static {
        initCache();
    }
}
