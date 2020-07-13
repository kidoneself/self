package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 描述：效果统计每日详情
 */
@Data
@ApiModel(description = "效果统计每日详情")
public class EffectStatisticsDayAppDTO implements Serializable {

    private static final long serialVersionUID = 1573836755485035727L;

    @ApiModelProperty(position = 1, value = "用户单日点击数")
    private Integer clicks;
    @ApiModelProperty(position = 2, value = "广告单日播放次数")
    private Integer playAmount;
    @ApiModelProperty(position = 110, value = "广告播放日期")
    private Date playTime;



}