package com.yimao.cloud.hra.msg;

import java.io.Serializable;

/**
 * @author Zhang Bo
 * @date 2017/12/6.
 */
public class HraResult implements Serializable {

    private static final long serialVersionUID = 1439978390294210697L;

    private String code;

    private String msg;

    private Object data;

    public HraResult() {
    }

    public HraResult(Object data) {
        this.code = "200";
        this.msg = "SUCCESS";
        this.data = data;
    }

    private HraResult(String code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static HraResult build(String code, String msg, Object data) {
        return new HraResult(code, msg, data);
    }

    public static HraResult build(String code, String msg) {
        return new HraResult(code, msg, null);
    }

    public static HraResult ok(Object data) {
        return new HraResult(data);
    }

    public static HraResult ok() {
        return new HraResult(null);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
