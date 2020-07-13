package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * @description: 企业客户DTO
 * @author: yu chunlei
 * @create: 2019-01-17 17:18:50
 **/
@Data
public class CompanyCustomerDTO implements Serializable {

    private static final long serialVersionUID = -1990597667036662737L;


    private Integer id;
    @ApiModelProperty(position = 1, value = "收货人姓名",required = true)
    private String contact;
    @ApiModelProperty(position = 2, value = "联系方式",required = true)
    private String mobile;
    @ApiModelProperty(position = 3, value = "公司名称",required = true)
    private String companyName;
    @ApiModelProperty(position = 4, value = "行业",required = true)
    private String industry;
    @ApiModelProperty(position = 5, value = "场景标签",required = true)
    private String label;
    @ApiModelProperty(position = 6, value = "服务人数",required = true)
    private String number;
    @ApiModelProperty(position = 7, value = "省",required = true)
    private String province;
    @ApiModelProperty(position = 8, value = "市",required = true)
    private String city;
    @ApiModelProperty(position = 9, value = "区",required = true)
    private String region;
    @ApiModelProperty(position = 10, value = "街道",required = true)
    private String street;
    @ApiModelProperty(position = 11, value = "添加该客户的经销商的e家号")
    private Integer pid;
    @ApiModelProperty(position = 12, value = "性别",required = true)
    private Integer sex;
    @ApiModelProperty(position = 13, value = "年龄")
    private Integer age;

}
