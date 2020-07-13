package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Lizhqiang
 * @date 2019/1/16
 */

@Data
@ApiModel(description = "地区管理")
public class RegionMessageDTO {


    @ApiModelProperty(value = "主键", position = 1)
    private Integer id;
    @ApiModelProperty(value = "地域名", position = 2)
    private String areaName;
    @ApiModelProperty(value = "等级", position = 3)
    private Integer level;
    @ApiModelProperty(value = "排序", position = 4)
    private Integer sorts;
    @ApiModelProperty(value = "父id", position = 5)
    private Integer parentId;
    @ApiModelProperty(value = "人口数", position = 6)
    private Double population;
    @ApiModelProperty(value = "众筹数", position = 7)
    private Integer crowdFunding;
}
