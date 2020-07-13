package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 描述：省市区
 *
 * @Author Zhang Bo
 * @Date 2019/2/18 13:45
 * @Version 1.0
 */
@Getter
@Setter
@ToString
@ApiModel(description = "地区")
public class AreaDTO implements Serializable {

    @ApiModelProperty(position = 1, value = "ID")
    private Integer id;
    @ApiModelProperty(position = 2, value = "名称")
    private String name;
    @ApiModelProperty(position = 3, value = "级别 1：省  2：市  3：县")
    private Integer level;
    @ApiModelProperty(position = 4, value = "排序")
    private Integer sorts;
    @ApiModelProperty(position = 5, value = "父级id  默认为0")
    private Integer pid;
    @ApiModelProperty(position = 6, value = "省会  1：省会  0：普通市")
    private Integer capital;
    @ApiModelProperty(position = 7, value = "状态  0：启用  -1：禁用")
    private Boolean deleted;
}
