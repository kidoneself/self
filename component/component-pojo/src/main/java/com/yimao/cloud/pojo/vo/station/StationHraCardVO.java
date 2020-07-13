package com.yimao.cloud.pojo.vo.station;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 描述：F卡管理列表vo类 （服务站站务系统）
 *
 * @Author Liu Long Jie
 * @Date 2019/5/28
 */
@ApiModel(description = "Hra--F卡管理列表vo类(服务站站务系统）")
@Getter
@Setter
public class StationHraCardVO {

    private Integer id;
    @ApiModelProperty(position = 1, value = "HRA评估券号")
    private String ticketNo;
    @ApiModelProperty(position = 2, value = "服务站门店名称")
    private String stationName;
    @ApiModelProperty(position = 3, value = "服务站省")
    private String stationProvince;
    @ApiModelProperty(position = 4, value = "服务站市")
    private String stationCity;
    @ApiModelProperty(position = 5, value = "服务站区")
    private String stationRegion;
    @ApiModelProperty(position = 7, value = "到期时间")
    private Date validEndTime;
    @ApiModelProperty(position = 8, value = "是否到期")
    private Boolean expired;
    @ApiModelProperty(position = 9, value = "总可用次数")
    private Integer total;
    @ApiModelProperty(position = 10, value = "剩余可用次数")
    private Integer useCount;
    @ApiModelProperty(position = 11, value = "是否限制该服务站使用")
    private Boolean selfStation;
    @ApiModelProperty(position = 12, value = "分配时间")
    private Date createTime;
    @ApiModelProperty(position = 13, value = "是否禁用：0-否；1-是；")
    private Boolean forbidden;
    @ApiModelProperty(value = "体检卡状态：1未使用，2已使用，3已禁用，4已过期")
    private Integer ticketStatus;


}
