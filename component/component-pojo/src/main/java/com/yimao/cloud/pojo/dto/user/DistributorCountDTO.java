package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
@ApiModel(description = "经销商统计信息")
public class DistributorCountDTO implements Serializable {

    private static final long serialVersionUID = 2663603156827213824L;

    Map<Object, Object> map;

    @ApiModelProperty("经销商总数")
    private Integer totalNumber;

    @ApiModelProperty("昨日转化数量")
    private Integer yetNumber;

    @ApiModelProperty("上周转化数量")
    private Integer weekNumber;

    @ApiModelProperty("体验版经销商数量")
    private Integer experienceNumber;
}
