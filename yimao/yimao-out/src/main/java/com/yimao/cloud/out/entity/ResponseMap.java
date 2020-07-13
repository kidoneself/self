package com.yimao.cloud.out.entity;

import com.yimao.cloud.base.utils.StringUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * 描述：TODO
 *
 * @Author Zhang Bo
 * @Date 2019/3/6 16:50
 * @Version 1.0
 */
@Getter
@Setter
public class ResponseMap {

    // 返回状态码
    private String rtnCode;

    // 返回数据签名
    private String rtnSign;

    // 返回数据密钥
    private String rtnSecret;

    // 返回数据加密
    private String rtnInfo;

    // 返回提示信息
    private String rtnMsg;

    /**
     * 获取默认返回码实例
     */
    public static ResponseMap getInstance() {
        ResponseMap resMap = new ResponseMap();
        resMap.setRtnCode(EnumsRtnMapResult.SUCCESS.code);
        resMap.setRtnMsg(EnumsRtnMapResult.SUCCESS.msg);
        return resMap;
    }

    /**
     * 设置返回信息为成功
     *
     * @param msg
     */
    public void success(String msg) {
        if (StringUtil.isNotEmpty(msg)) {
            this.setRtnMsg(msg);
        } else {
            this.setRtnMsg(EnumsRtnMapResult.SUCCESS.msg);
        }
        this.setRtnCode(EnumsRtnMapResult.SUCCESS.code);
    }

    /**
     * 设置放回信息为失败
     *
     * @param msg
     */
    public void failure(String msg) {
        if (StringUtil.isNotEmpty(msg)) {
            this.setRtnMsg(msg);
        } else {
            this.setRtnMsg(EnumsRtnMapResult.FAILURE.msg);
        }
        this.setRtnCode(EnumsRtnMapResult.FAILURE.msg);
    }

    /**
     * 设置返回信息为异常
     *
     * @param msg
     */
    public void exception(String msg) {
        if (StringUtil.isNotEmpty(msg)) {
            this.setRtnMsg(msg);
        } else {
            this.setRtnMsg(EnumsRtnMapResult.EXCEPTION.msg);
        }
        this.setRtnCode(EnumsRtnMapResult.EXCEPTION.msg);
    }

    public String getRtnCode() {
        return rtnCode;
    }

    public void setRtnCode(String rtnCode) {
        this.rtnCode = rtnCode;
    }

    public String getRtnSign() {
        return rtnSign;
    }

    public void setRtnSign(String rtnSign) {
        this.rtnSign = rtnSign;
    }

    public String getRtnSecret() {
        return rtnSecret;
    }

    public void setRtnSecret(String rtnSecret) {
        this.rtnSecret = rtnSecret;
    }

    public String getRtnInfo() {
        return rtnInfo;
    }

    public void setRtnInfo(String rtnInfo) {
        this.rtnInfo = rtnInfo;
    }

    public String getRtnMsg() {
        return rtnMsg;
    }

    public void setRtnMsg(String rtnMsg) {
        this.rtnMsg = rtnMsg;
    }
}
