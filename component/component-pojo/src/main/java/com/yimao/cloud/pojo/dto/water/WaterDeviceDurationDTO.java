package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 水机亮灭屏时长
 *
 * @author Chen Hui Yang
 * @date 2019/2/15
 */
@Data
@ApiModel(description = "水机亮灭屏时长")
public class WaterDeviceDurationDTO implements Serializable {

    private static final long serialVersionUID = 1573836755485035727L;

    @ApiModelProperty(position = 1,value = "id")
    private Integer id;
    @ApiModelProperty(position = 2,value = "设备型号")
    private String deviceModel;
    @ApiModelProperty(position = 3,value = "灭屏时长（单位：分钟）")
    private Integer offDuration;
    @ApiModelProperty(position = 4,value = "亮屏时长（单位：分钟）")
    private Integer onDuration;

    @ApiModelProperty(position = 100, value = "创建者")
    private String creator;
    @ApiModelProperty(position = 101, value = "创建时间")
    private Date createTime;
    @ApiModelProperty(position = 102, value = "更新者")
    private String updater;
    @ApiModelProperty(position = 103, value = "更新时间")
    private Date updateTime;
}
