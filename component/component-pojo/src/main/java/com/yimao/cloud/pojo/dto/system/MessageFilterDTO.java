package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/***
 * 功能描述:消息过滤
 *
 * @auther: liu yi
 * @date: 2019/4/29 10:23
 */

@Getter
@Setter
@ApiModel(description = "消息过滤")
public class MessageFilterDTO implements Serializable {

    private static final long serialVersionUID = -8091399636536265113L;
    @ApiModelProperty(value = "主键id")
    private Integer id;
    @ApiModelProperty(value = "省")
    private String province;
    @ApiModelProperty(value = "市")
    private String city;
    @ApiModelProperty(value = "区县")
    private String region;
    @ApiModelProperty(value = "商品分类id")
    private Integer categoryId;
    @ApiModelProperty(value = "商品分类名称")
    private String categoryName;
    @ApiModelProperty(value = "频次(小时)")
    private Integer hours;
    @ApiModelProperty(value = "推送内容:0-推送内容 1-余额不足 4-滤芯报警")
    private Integer type;
    @ApiModelProperty(value = "推送内容:推送内容、余额不足、滤芯报警")
    private String typeName;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}
