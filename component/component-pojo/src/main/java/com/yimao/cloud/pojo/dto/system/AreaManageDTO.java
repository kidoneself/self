package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Lizhqiang
 * @date 2019-08-19
 */
@Data
public class AreaManageDTO {

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "区域名字")
    private String name;

    @ApiModelProperty(value = "等级")
    private Integer level;

    @ApiModelProperty(value = "众筹数量")
    private Integer numerous;

    @ApiModelProperty(value = "人口数")
    private Integer population;

    @ApiModelProperty(value = "溢价个数")
    private Integer premium;

    @ApiModelProperty(value = "是否直营 0-否 1-是 ")
    private Integer directSale;

    @ApiModelProperty(value = "服务站个数")
    private Integer siteCount;

    @ApiModelProperty(value = "上级ID")
    private Integer pid;


}

