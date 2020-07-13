package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @description: 绑定信息
 * @author: yu chunlei
 * @create: 2019-01-03 10:34:47
 **/
@Getter
@Setter
@ApiModel(description = "绑定手机号或经销商传入的实体")
public class UserBindDTO implements Serializable {

    private static final long serialVersionUID = -6327287468010270475L;
    @ApiModelProperty("登录名")
    private String loginName;
    @ApiModelProperty("手机号")
    private String mobile;
    @ApiModelProperty("密码")
    private String password;
    @ApiModelProperty("国家代码")
    private String countryCode;
    @ApiModelProperty("验证码")
    private String verifyCode;
    @ApiModelProperty("登录IP")
    private String loginIp;
    @ApiModelProperty("app类型")
    private Integer appType;
    @ApiModelProperty("APP版本")
    private String appVersion;

    @ApiModelProperty("openId")
    private String openId;

    @ApiModelProperty("unionId")
    private String unionId;

    @ApiModelProperty("用户e家号")
    private Integer userId;

    @ApiModelProperty("分享者用户e家号，建立用户关系时使用")
    private Integer sharerId;

}
