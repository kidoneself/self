package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;

/**
 * 描述：广告条件投放-区域-关联表
 *
 * @Author Zhang Bo
 * @Date 2019/1/29 17:41
 * @Version 1.0
 */
@Data
@ApiModel(description = "广告条件投放-区域")
public class ConditionalAdvertisingAreaDTO implements Serializable {

    private static final long serialVersionUID = 1573836755485035727L;

    @ApiModelProperty(position = 1, value = "广告投放ID")
    private Integer advertisingId;
    @ApiModelProperty(position = 2, value = "区域ID")
    private Integer areaId;
    @ApiModelProperty(position = 3, value = "区域级别")
    private Integer areaLevel;
    @ApiModelProperty(position = 4, value = "区域名称")
    private String areaName;
    @ApiModelProperty(position = 5, value = "父级id")
    private Integer pid;

}
