package com.yimao.cloud.pojo.vo.out;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 设备故障信息及解决方式
 *
 * @author Liu Yi
 * @date 2019-3-20
 */
@Getter
@Setter
@ApiModel(description = "设备故障")
public class WaterDeviceRepairFactFaultDescribeInfoVO {
    @ApiModelProperty(value = "主键ID")
    private String id;
    @ApiModelProperty(value = "工单号")
    private String workCode;
    @ApiModelProperty(value = "工单类型名：1-维修单，2-维护单,默认维修工单")
    private String workOrderIndex;
    @ApiModelProperty(value = "设备id")
    private String deviceId;
    @ApiModelProperty(value = "sn")
    private String deviceSncode;
    @ApiModelProperty(value = "故障描述id")
    private String factFaultDescribeId;
    @ApiModelProperty(value = "故障描述")
    private String factFaultDescribe;
    @ApiModelProperty(value = " 实际故障原因id")
    private String factFaultReasonId;
    @ApiModelProperty(value = "实际故障原因")
    private String factFaultReason;
    @ApiModelProperty(value = " 解决措施id")
    private String solveMeasureId;
    @ApiModelProperty(value = " 解决措施")
    private String solveMeasure;
    @ApiModelProperty(value = "创建时间")
    private Long createTime;
    @ApiModelProperty(value = "更新时间")
    private Long updateTime;
    @ApiModelProperty(value = "创建用户")
    private String createUser;
    @ApiModelProperty(value = "更新用户")
    private String updateUser;
    @ApiModelProperty(value = "是否已删除：Y-已删除 ，N-未删除")
    private String delStatus;
    @ApiModelProperty(value = "删除时间")
    private Long deleteTime;
    @ApiModelProperty(value = "Y-可用 ，N-不可用")
    private String idStatus;
}
