package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Liu Yi
 * @description 经销商app注册
 * @date 2019/8/28 10:38
 */
@Data
public class UserDistributorRegisterDTO implements Serializable {

    private static final long serialVersionUID = -312767999222246149L;
    @ApiModelProperty(value = "手机号")
    private String mobile;
    @ApiModelProperty(value = "国家代码")
    private String countryCode;
    @ApiModelProperty(value = "验证码")
    private String smsCode;
    @ApiModelProperty(value = "注册类型")
    private Integer registLevel;
    @ApiModelProperty(value = "企业信息")
    private UserCompanyApplyDTO userCompany;
}
