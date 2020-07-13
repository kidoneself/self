package com.yimao.cloud.base.utils.yunSignUtil;

import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class YunSignResult {
    static Logger Log = LogManager.getLogger(YunSignResult.class);
    private String query = null;
    private Long time = null;
    private Long readCount = 0L;
    private Boolean isSuccess = false;
    private String code;
    private String desc;
    private String resultData;

    private YunSignResult() {
    }

    public static YunSignResult err(String info) {
        YunSignResult result = new YunSignResult();
        result.code = "-1";
        result.desc = info;
        result.resultData = "";
        return result;
    }

    /** @deprecated */
    @Deprecated
    public static YunSignResult success(String info, String resultData) {
        YunSignResult result = new YunSignResult();
        result.code = "000";
        result.desc = info;
        result.resultData = resultData;
        result.isSuccess = true;
        return result;
    }

    public static YunSignResult initFromJson(String query, String jsonStr) {
        YunSignResult result = new YunSignResult();
        JSONObject json = JSONObject.fromObject(jsonStr);
        result.query = query;
        result.time = System.currentTimeMillis();
        result.readCount = 1L;
        if (json.get("code") != null) {
            result.isSuccess = false;
            result.code = json.getString("code");
            if (result.code.equals("000")) {
                result.isSuccess = true;
            }
        }

        if (json.get("desc") != null) {
            result.desc = json.getString("desc");
        }

        if (json.get("resultData") != null) {
            result.resultData = json.getString("resultData");
        }

        Log.info("云签返回： 接口= [ " + query + " ] code = " + result.getCode() + "  desc = " + result.getDesc() + " resultData= [ " + result.getResultData() + " ]");
        return result;
    }

    public Boolean getSuccess() {
        return this.isSuccess;
    }

    public String getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    public String getResultData() {
        return this.resultData;
    }

    public String getQuery() {
        return this.query;
    }

    public Long getTime() {
        return this.time;
    }

    public Long getReadCount() {
        return this.readCount;
    }

    public Boolean isSuccess() {
        return this.isSuccess;
    }
}
