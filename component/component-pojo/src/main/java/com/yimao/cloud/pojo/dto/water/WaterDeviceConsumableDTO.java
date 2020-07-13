package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 描述：水机耗材
 *
 * @Author Zhang Bo
 * @Date 2019/2/23 11:20
 * @Version 1.0
 */
@ApiModel(description = "水机耗材DTO")
@Getter
@Setter
public class WaterDeviceConsumableDTO {

    @ApiModelProperty(value = "ID")
    private Integer id;
    @ApiModelProperty(position = 1, value = "耗材名称")
    private String name;
    /*@ApiModelProperty(position = 2, value = "耗材类型ID")
    private Integer consumableTypeId;*/
    @ApiModelProperty(position = 2, value = "耗材类型：1-滤芯 2-滤网")
    private Integer type;
    @ApiModelProperty(position = 3, value = "水机设备型号：1601T、1602T、1603T、1601L")
    private String deviceModel;
    @ApiModelProperty(position = 4, value = "滤芯可使用时长（单位：天），超过此值需更换")
    private Integer time;
    @ApiModelProperty(position = 5, value = "滤芯可使用流量（单位：升），超过此值需更换")
    private Integer flow;

    private String creator;
    private Date createTime;
    private String updater;
    private Date updateTime;

    private String oldId;//老耗材id

    private String baideConsumableTypeId;//百得耗材类型id

}