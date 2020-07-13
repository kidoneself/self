package com.yimao.cloud.pojo.vo.out;

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
public class AreaVO implements Serializable {

    @ApiModelProperty(position = 1, value = "ID")
    private Integer aId;
    @ApiModelProperty(position = 2, value = "名称")
    private String name;
    @ApiModelProperty(position = 5, value = "父级id  默认为0")
    private Integer pId;
}
