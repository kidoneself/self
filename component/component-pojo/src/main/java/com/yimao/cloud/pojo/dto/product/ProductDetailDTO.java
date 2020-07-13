package com.yimao.cloud.pojo.dto.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @auther: liu.lin
 * @date: 2018/11/29
 */
@ApiModel(description = "产品详情DTO对象")
@Getter
@Setter
public class ProductDetailDTO {

    @ApiModelProperty(position = 1, value = "产品ID")
    private Integer productId;
    @ApiModelProperty(position = 2, value = "文本信息")
    private String text;
    @ApiModelProperty(position = 3, value = "创建时间")
    private Date createTime;
    @ApiModelProperty(position = 4, value = "修改时间")
    private Date updateTime;

}