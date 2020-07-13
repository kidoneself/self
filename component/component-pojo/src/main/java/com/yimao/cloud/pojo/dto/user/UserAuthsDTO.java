package com.yimao.cloud.pojo.dto.user;

import lombok.Data;

import java.util.Date;

/**
 * @author Lizhqiang
 * @date 2019-08-09
 */

@Data
public class UserAuthsDTO {


    private Integer id;
    private Integer userId;                      //用户ID（e家号）
    private Integer identityType;                //登录类别：1-健康e家公众号、2-健康自测小程序、4-H5分享页面、5-翼猫APP
    private String identifier;                   //第三方识别码
    private String mobile;                       //手机号
    private String identifierUnique;             //第三方唯一识别码
    private String credential;                   //第三方登录凭据
    private Integer state;                       //状态：0-解绑，1-绑定
    private Date createTime;
    private Date updateTime;
}
