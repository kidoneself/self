package com.yimao.cloud.pojo.dto.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * @auther: liu.lin
 * @date: 2018/11/29
 */
@Getter
@Setter
@ApiModel(description = "产品属性DTO")
public class ProductPropertyDTO {

    @ApiModelProperty(value = "属性id")
    private Integer id;                 //属性id
    @ApiModelProperty(position = 1, value = "属性名称")
    private String name;                //属性名称
    @ApiModelProperty(position = 2, value = "产品大类：1-实物商品；2-电子卡券；3-租赁商品；")
    private Integer typeId;             //产品大类：1-实物商品；2-电子卡券；3-租赁商品；
    @ApiModelProperty(position = 3, value = "排序")
    private Integer sorts;              //排序
    @ApiModelProperty(position = 4, value = "创建人")
    private String creator;             //创建人
    @ApiModelProperty(position = 5, value = "创建时间")
    private Date createTime;            //创建时间
    @ApiModelProperty(position = 6, value = "更新人")
    private String updater;             //更新人
    @ApiModelProperty(position = 7, value = "更新时间")
    private Date updateTime;            //更新时间
    @ApiModelProperty(position = 8, value = "删除标识：0-未删除；1-已删除")
    private Boolean deleted;            //删除标识：0-未删除；1-已删除

    @ApiModelProperty(value = "产品属性值", position = 8)
    private String propertyValueStr;//产品属性值

}