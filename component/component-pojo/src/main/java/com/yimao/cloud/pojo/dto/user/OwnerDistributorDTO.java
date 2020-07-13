package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: 自注册经销商信息
 * @author: yu chunlei
 * @create: 2019-09-09 14:54:18
 **/
@Data
@ApiModel(description = "自注册经销商信息")
public class OwnerDistributorDTO implements Serializable {

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
    @ApiModelProperty(position = 6, value = "区代的e家号")
    private Integer userId;
    @ApiModelProperty(position = 7, value = "推荐人姓名")
    private String recommendName;
    @ApiModelProperty(position = 8, value = "推荐人id")
    private Integer recommendId;
    @ApiModelProperty(position = 9, value = "所属服务站")
    private String stationName;
    @ApiModelProperty(position = 10, value = "省")
    private String province;
    @ApiModelProperty(position = 11, value = "市")
    private String city;
    @ApiModelProperty(position = 12, value = "区")
    private String region;
    @ApiModelProperty(position = 13, value = "地址")
    private String address;



}
