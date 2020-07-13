package com.yimao.cloud.pojo.dto.wechat;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Zhang Bo
 * @date 2017/12/22.
 */
@Getter
@Setter
public class WxUserInfo implements Serializable {

    private static final long serialVersionUID = 30608007492084859L;

    private String openid;
    private String nickname;
    private Integer sex;
    private String headimgurl;
    private String unionid;
    private String province;
    private String city;

    //错误码
    private Integer errcode;
    //错误信息
    private String errmsg;

}
