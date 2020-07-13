package com.yimao.cloud.pojo.query.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 描述：滤芯参数配置
 *
 * @Author Zhang Bo
 * @Date 2019/7/16
 */
@ApiModel(description = "滤芯参数配置查询条件")
@Getter
@Setter
public class FilterSettingQuery {

    @ApiModelProperty(position = 1, value = "省")
    private String province;

    @ApiModelProperty(position = 2, value = "市")
    private String city;

    @ApiModelProperty(position = 3, value = "区")
    private String region;

    @ApiModelProperty(position = 4, value = "设备型号：1601T、1602T、1603T、1601L")
    private String deviceModel;

    @ApiModelProperty(position = 101, value = "开始时间")
    private Date startTime;
    @ApiModelProperty(position = 102, value = "结束时间")
    private Date endTime;

}
