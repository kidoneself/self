package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 服务站服务区域
 *
 * @author Lizhqiang
 * @date 2019/1/17
 */
@ApiModel(description = "服务站服务区域")
@Getter
@Setter
public class StationServiceAreaDTO {

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(position = 1, value = "服务站门店id")
    private Integer stationId;

    @ApiModelProperty(position = 2, value = "服务站门店名称")
    private String stationName;

    @ApiModelProperty(position = 3, value = "区域id")
    private Integer areaId;

    @ApiModelProperty(position = 4, value = "省")
    private String province;

    @ApiModelProperty(position = 5, value = "市")
    private String city;

    @ApiModelProperty(position = 6, value = "区")
    private String region;

    @ApiModelProperty(position = 7, value = "服务类型 0-售前+售后；1-售前；2-售后")
    private Integer serviceType;

    @ApiModelProperty(position = 8, value = "创建者")
    private String creator;
    @ApiModelProperty(position = 9, value = "创建时间")
    private Date createTime;
    @ApiModelProperty(position = 10, value = "更新人")
    private String updater;
    @ApiModelProperty(position = 11, value = "更新时间")
    private Date updateTime;
    @ApiModelProperty(value = "服务站服务区域是否拥有true-有 false-没有")
    private boolean areaHave;
}
