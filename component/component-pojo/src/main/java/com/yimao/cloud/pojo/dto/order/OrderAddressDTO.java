package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Zhang Bo
 * @date 2018/11/12.
 */
@ApiModel(description = "订单收货地址")
@Getter
@Setter
public class OrderAddressDTO {

    @ApiModelProperty(position = 1, value = "订单收货地址ID")
    private Long id;
    @ApiModelProperty(position = 2, value = "联系人")
    private String contact;
    @ApiModelProperty(position = 3, value = "性别：1-男；2-女")
    private Integer sex;
    @ApiModelProperty(position = 4, value = "收货人手机号")
    private String phone;
    @ApiModelProperty(position = 5, value = "省")
    private String province;
    @ApiModelProperty(position = 6, value = "市")
    private String city;
    @ApiModelProperty(position = 7, value = "区")
    private String region;
    @ApiModelProperty(position = 8, value = "街道")
    private String street;
    @ApiModelProperty(position = 9, value = "邮编")
    private String postCode;
    @ApiModelProperty(position = 10, value = "创建时间")
    private Date createTime;
    @ApiModelProperty(position = 11, value = "更新时间")
    private Date updateTime;
    @ApiModelProperty(position = 12, value = "公司名称")
    private String companyName;

}
