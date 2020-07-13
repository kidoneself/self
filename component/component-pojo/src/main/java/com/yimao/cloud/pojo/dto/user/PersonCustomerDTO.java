package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * @description: 个人客户
 * @author: yu chunlei
 * @create: 2019-01-17 10:08:39
 **/
@Data
public class PersonCustomerDTO implements Serializable {

    private static final long serialVersionUID = 6696622497305306237L;

    private Integer id;
    @ApiModelProperty(position = 2, value = "收货人姓名",required = true)
    private String contact;
    @ApiModelProperty(position = 3, value = "联系方式",required = true)
    private String mobile;
    @ApiModelProperty(position = 4, value = "省",required = true)
    private String province;
    @ApiModelProperty(position = 5, value = "市",required = true)
    private String city;
    @ApiModelProperty(position = 6, value = "区",required = true)
    private String region;
    @ApiModelProperty(position = 7, value = "街道",required = true)
    private String street;
    @ApiModelProperty(position = 8, value = "添加该客户的经销商的e家号",required = true)
    private Integer pid;
    @ApiModelProperty(position = 9, value = "性别",required = true)
    private Integer sex;
    @ApiModelProperty(position = 10, value = "年龄")
    private Integer age;

}
