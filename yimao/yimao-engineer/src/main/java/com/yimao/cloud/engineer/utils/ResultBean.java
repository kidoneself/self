package com.yimao.cloud.engineer.utils;

import com.yimao.cloud.base.baideApi.utils.StringUtil;
import com.yimao.cloud.engineer.enums.ServiceStatusCode;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
public class ResultBean<T> implements Serializable {
    private static final long serialVersionUID = 9021835659879882941L;
    String code;
    T resultData;
    String statusText;
    String errMsg;

    public ResultBean(String code, T resultData) {
        this.code = code;
        this.resultData = resultData;
        this.statusText = ServiceStatusCode.getByCode(code).getTextZh();
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getResultData() {
        if (this.isSuccess()) {
            return this.resultData;
        } else {
            log.info(this.toString() + "result bean 并未成功，请不要随意返回！");
            return this.resultData;
        }
    }

    public void setResultData(T resultData) {
        this.resultData = resultData;
    }

    public String getStatusText() {
        return this.statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public boolean isSuccess() {
        return ServiceStatusCode.SUCCESS.getCode().equals(this.code);
    }

    public static ResultBean fastFail(String code) {
        return new ResultBean(code, (Object)null);
    }

    public static ResultBean fastFail(ServiceStatusCode code) {
        return new ResultBean(code.getCode(), (Object)null);
    }

    public static ResultBean success(Object t) {
        return new ResultBean(ServiceStatusCode.SUCCESS.getCode(), t);
    }

    public static ResultBean fastFailNoData() {
        return fastFail(ServiceStatusCode.SER_NO_DATA);
    }

    public static ResultBean fastFailServerException() {
        return fastFail(ServiceStatusCode.SER_EXCEPTION);
    }

    public static ResultBean fastFailServerException(String errMsg) {
        ResultBean resultBean = fastFailServerException();
        resultBean.setErrMsg(errMsg);
        return resultBean;
    }

    public static ResultBean fastFailFail() {
        return fastFail(ServiceStatusCode.SER_FAILURE);
    }

    public String toString() {
        try {
            return this.code + " " + this.getStatusText() + " " + this.resultData;
        } catch (Exception var2) {
            var2.printStackTrace();
            return super.toString();
        }
    }

    public String getErrMsg() {
        return this.errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getMsg() {
        return StringUtil.isEmpty(this.errMsg) ? this.statusText : this.errMsg;
    }
}
