package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 描述：版本统计
 */
@Data
@ApiModel(description = "版本统计")
public class VersionStatisticsDTO implements Serializable {

    private static final long serialVersionUID = 1573836755485035727L;

    @ApiModelProperty(position = 1, value = "id")
    private Integer id;
    @ApiModelProperty(position = 2, value = "水机版本号")
    private String version;
    @ApiModelProperty(position = 3, value = "升级成功设备数")
    private Integer successCount;
    @ApiModelProperty(position = 4, value = "投放到达率")
    private String percentage;
    @ApiModelProperty(position = 5, value = "设备总数")
    private Integer deviceCount;

}