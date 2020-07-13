package com.yimao.cloud.pojo.dto.product;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import java.util.Date;

/**
 * 产品属性值
 *
 * @auther: liu.lin
 * @date: 2018/11/29
 */
@Data
@ApiOperation(value = "属性值")
public class ProductPropertyValueDTO {

    @ApiModelProperty("属性值ID")
    private Integer id;//属性id
    @ApiModelProperty("属性ID")
    private Integer propertyId;//属性ID
    @ApiModelProperty("属性值")
    private String propertyValue;//属性值

    @ApiModelProperty("创建人")
    private String creator;//创建人
    @ApiModelProperty("创建时间")
    private Date createTime;//创建时间
    @ApiModelProperty("更新人")
    private String updater;//更新人
    @ApiModelProperty("更新时间")
    private Date updateTime;//更新时间
}