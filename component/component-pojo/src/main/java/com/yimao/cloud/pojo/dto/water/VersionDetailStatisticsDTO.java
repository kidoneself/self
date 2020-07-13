package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 描述：版本详情统计
 *
 */
@Data
@ApiModel(description = "版本详情统计")
public class VersionDetailStatisticsDTO {

    @ApiModelProperty(position = 1,value = "id")
    private Integer id;
    @ApiModelProperty(position = 2,value = "水机版本号")
    private String version;
    @ApiModelProperty(position = 3,value = "sn编码")
    private String snCode;
    @ApiModelProperty(position = 4,value = "消耗流量类型：1-WIFI；3-3G；")
    private Integer consumeFlowType;
    @ApiModelProperty(position = 5,value = "设备更新版本时间")
    private Date updateVersionTime;
    @ApiModelProperty(position = 6, value = "设备组：1-用户组，2-服务站组")
    private Integer deviceGroup;
}