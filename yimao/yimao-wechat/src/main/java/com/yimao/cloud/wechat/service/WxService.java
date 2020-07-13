package com.yimao.cloud.wechat.service;


import com.yimao.cloud.pojo.dto.wechat.WxOauth2Result;
import com.yimao.cloud.pojo.dto.wechat.WxUserInfo;

import java.io.File;
import java.util.Map;

/**
 * @author Zhang Bo
 * @date 2017/12/22.
 */
public interface WxService {

    String getJSAPIAccessToken() throws Exception;

    String getMiniProgramAccessToken() throws Exception;

    String getStoreCodeAccessToken() throws Exception;

    WxOauth2Result getAccessTokenByCode(String code, String appid, String secret);

    WxUserInfo getWxUserInfo(String accessToken, String openid);

    WxUserInfo getWxUserInfo(String openid);

    String getQRCodeWithParam(Integer userId, Integer shareType, String shareNo, Long dateTime);

    String getWxTicket();

    void sendTemplateMessage(Map<String, Object> msgMap) ;

    String getMediaId(String msgType, File image) throws Exception;

    String getWxacode(Integer userId, String scene, String page, String headImg, String oldWxacode) throws Exception;

    void replyUserRequest(String openid, String msgtype, String content) throws Exception;

    //小程序商城-生成店铺二维码
    String getStoreCode(Integer userId, String scene, String page);

    /**
     * 获取用户专属二维码
     *
     * @param openid openId
     * @return file
     */
    File myFixedQRCodeImage(String openid);
}
