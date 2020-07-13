package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @description: 客户地址
 * @author: yu chunlei
 * @create: 2019-09-17 15:47:30
 **/
@Data
public class CustomerAddressDTO {

    private Integer id;
    @ApiModelProperty(position = 2, value = "收货人姓名")
    private String contact;
    @ApiModelProperty(position = 3, value = "联系方式")
    private String mobile;
    @ApiModelProperty(position = 4, value = "省")
    private String province;
    @ApiModelProperty(position = 5, value = "市")
    private String city;
    @ApiModelProperty(position = 6, value = "区")
    private String region;
    @ApiModelProperty(position = 7, value = "街道")
    private String street;
    @ApiModelProperty(position = 8, value = "1-个人 2-企业")
    private Integer type;
    @ApiModelProperty(position = 9, value = "性别  1：男 2: 女")
    private Integer sex;
    @ApiModelProperty(position = 10, value = "创建时间")
    private Date createTime;

}
