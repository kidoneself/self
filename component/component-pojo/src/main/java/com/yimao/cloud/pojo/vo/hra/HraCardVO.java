package com.yimao.cloud.pojo.vo.hra;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 描述：TODO
 *
 * @Author Zhang Bo
 * @Date 2019/5/28
 */
@ApiModel(description = "HRA评估卡VO")
@Getter
@Setter
public class HraCardVO {

    private Integer id;
    @ApiModelProperty(position = 1, value = "HRA评估券号")
    private String ticketNo;
    @ApiModelProperty(position = 2, value = "服务站门店ID")
    private Integer stationId;
    @ApiModelProperty(position = 3, value = "服务站门店名称")
    private String stationName;
    @ApiModelProperty(position = 4, value = "服务站省")
    private String stationProvince;
    @ApiModelProperty(position = 5, value = "服务站市")
    private String stationCity;
    @ApiModelProperty(position = 6, value = "服务站区")
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

}
