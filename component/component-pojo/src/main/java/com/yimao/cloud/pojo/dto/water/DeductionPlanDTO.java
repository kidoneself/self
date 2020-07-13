package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 描述：水机设备扣费计划
 *
 * @Author Zhang Bo
 * @Date 2019/8/30
 */
@ApiModel(description = "水机设备扣费计划DTO")
@Getter
@Setter
public class DeductionPlanDTO implements Serializable {

    private static final long serialVersionUID = -6681222585852988238L;

    @ApiModelProperty(value = "ID")
    private Integer id;
    @ApiModelProperty(position = 1, value = "设备ID")
    private Integer deviceId;
    @ApiModelProperty(position = 2, value = "设备SN码")
    private String snCode;
    @ApiModelProperty(position = 3, value = "提醒阀值")
    private Integer threshold;
    @ApiModelProperty(position = 4, value = "计费套餐ID")
    private Integer costId;
    @ApiModelProperty(position = 4, value = "计费套餐名称")
    private String costName;
    @ApiModelProperty(position = 5, value = "单位扣减金额")
    private BigDecimal unitMoney;
    @ApiModelProperty(position = 6, value = "扣费计划开始第一天设备剩余金额")
    private BigDecimal firstDayMoney;
    @ApiModelProperty(position = 7, value = "扣费计划开始第一天设备剩余流量")
    private Integer firstDayFlow = -1;
    @ApiModelProperty(position = 8, value = "计费方式：1-流量计费；2-时长计费；")
    private Integer deductionsType;
    @ApiModelProperty(position = 9, value = "扣除数量,天/升")
    private Integer deductionsNum;
    @ApiModelProperty(position = 10, value = "是否在更换扣费方式")
    private Boolean costChanged;
    @ApiModelProperty(position = 11, value = "当前扣费计划使用状态：1-待使用；2-使用中；3-已使用；")
    private Integer status;
    @ApiModelProperty(position = 12, value = "扣费计划开始时间")
    private Date startTime;
    @ApiModelProperty(position = 13, value = "扣费计划结束时间")
    private Date endTime;
    @ApiModelProperty(position = 14, value = "扣费计划创建时间")
    private Date createTime;
    @ApiModelProperty(position = 15, value = "扣费计划更新时间")
    private Date updateTime;
    @ApiModelProperty(position = 16, value = "排序字段，由小到大是扣费计划使用的顺序")
    private Integer sorts;

}
