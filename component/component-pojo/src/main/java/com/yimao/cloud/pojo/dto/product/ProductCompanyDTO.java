package com.yimao.cloud.pojo.dto.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 产品公司
 *
 * @auther: Liu Yi
 * @date: 2018/12/25
 */
@ApiModel(description = "产品公司DTO对象")
@Getter
@Setter
public class ProductCompanyDTO {

    @ApiModelProperty(value = "产品公司ID")
    private Integer id;
    @ApiModelProperty(position = 1, value = "产品公司名称")
    private String name;
    @ApiModelProperty(position = 2, value = "公司编号")
    private String code;
    @ApiModelProperty(position = 3, value = "公司简称")
    private String shortName;
    @ApiModelProperty(position = 4, value = "公司logo")
    private String logo;
    @ApiModelProperty(position = 8, value = "公司介绍")
    private String profile;

    @ApiModelProperty(position = 9, value = "删除标识：0-未删除；1-已删除")
    private Boolean deleted;

    @ApiModelProperty(position = 100, value = "创建人")
    private String creator;
    @ApiModelProperty(position = 101, value = "创建时间")
    private Date createTime;
    @ApiModelProperty(position = 102, value = "更新人")
    private String updater;
    @ApiModelProperty(position = 103, value = "更新时间")
    private Date updateTime;

}