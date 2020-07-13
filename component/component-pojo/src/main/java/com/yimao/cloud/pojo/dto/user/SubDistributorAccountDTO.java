package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * @description: 发展经销商子账号相关
 * @author: yu chunlei
 * @create: 2019-09-11 12:04:37
 **/
@Data
@ApiModel(description = "经销商子账号信息")
public class SubDistributorAccountDTO implements Serializable {

    @ApiModelProperty(position = 1, value = "真实姓名")
    private String realName;
    @ApiModelProperty(position = 2, value = "性别")
    private Integer sex;
    @ApiModelProperty(position = 3, value = "身份证")
    private String idCard;
    @ApiModelProperty(position = 4, value = "手机号")
    private String mobile;
    @ApiModelProperty(position = 5, value = "邮箱")
    private String email;
    @ApiModelProperty(position = 6, value = "公司名称")
    private String companyName;
    @ApiModelProperty(position = 7, value = "企业版主账号的e家号")
    private Integer userId;
    @ApiModelProperty(position = 8, value = "省")
    private String province;
    @ApiModelProperty(position = 9, value = "市")
    private String city;
    @ApiModelProperty(position = 10, value = "区")
    private String region;
}
