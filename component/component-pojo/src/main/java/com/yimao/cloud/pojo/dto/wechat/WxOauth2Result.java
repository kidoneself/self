package com.yimao.cloud.pojo.dto.wechat;

import lombok.Getter;
import lombok.Setter;

/**
 * 描述：微信授权接口返回结果
 *
 * @Author Zhang Bo
 * @Date 2019/9/12
 */
@Getter
@Setter
public class WxOauth2Result {

    //接口调用凭证
    private String accessToken;
    //access_token接口调用凭证超时时间，单位（秒）
    private Integer expiresIn;
    //用户刷新access_token
    private String refreshToken;
    //授权用户唯一标识
    private String openid;
    //用户授权的作用域，使用逗号（,）分隔
    private String scope;

    //错误码
    private Integer errcode;
    //错误信息
    private String errmsg;

}
