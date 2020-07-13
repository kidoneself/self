package com.yimao.cloud.pojo.vo.station;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 服务站服务区域
 *
 * @author liu long jie
 * @date 2019/12/26
 */
@ApiModel(description = "服务站服务区域VO")
@Getter
@Setter
public class StationServiceAreaVO {

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(position = 1, value = "服务站门店id")
    private Integer stationId;

    @ApiModelProperty(position = 3, value = "区域id")
    private Integer areaId;

    @ApiModelProperty(position = 4, value = "省")
    private String province;

    @ApiModelProperty(position = 5, value = "市")
    private String city;

    @ApiModelProperty(position = 6, value = "区")
    private String region;
}
