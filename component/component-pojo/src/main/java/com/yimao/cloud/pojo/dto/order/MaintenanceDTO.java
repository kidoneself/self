package com.yimao.cloud.pojo.dto.order;


import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public class MaintenanceDTO {
    @ApiModelProperty(value = "设备SN码")
    private String deviceSncode;
    @ApiModelProperty(value = "设备ICCID")
    private String deviceSimcard;
    @ApiModelProperty(value = "批次码")
    private String deviceBatchCode;
    @ApiModelProperty(value = "计费方式id")
    private Integer costId;
    @ApiModelProperty(value = "计费方式名称")
    private String costName;
    @ApiModelProperty(value = "产品型号名称")
    private String deviceModelName;
    @ApiModelProperty(value = "用户id")
    private Integer consumerId;
    @ApiModelProperty(value = "用户名")
    private String consumerName;
    @ApiModelProperty(value = "用户电话")
    private String consumerPhone;

    @ApiModelProperty(value = "完成时间")
    private Date workOrderCompleteTime;
    @ApiModelProperty(value = "省份")
    private String addrProvince;
    @ApiModelProperty(value = "城市")
    private String addrCity;
    @ApiModelProperty(value = "县")
    private String addrRegion;
    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "水机所在经度")
    private Double longitude;
    @ApiModelProperty(value = "水机所在纬度")
    private Double latitude;
    @ApiModelProperty(value = "距离")
    private Double distance;

    @ApiModelProperty(value = "是否挂单 1-是 2-否")
    private Integer hasHang;
    @ApiModelProperty(value = "挂单时间")
    private Date hangTime;
    @ApiModelProperty(value = "挂单理由")
    private String hangReason;
    @ApiModelProperty(value = "挂单改约备注")
    private String hangRemark;
    @ApiModelProperty(value = "改约服务开始时间")
    private Date hangStartTime;
    @ApiModelProperty(value = "改约服务结束时间")
    private Date hangEndTime;
    @ApiModelProperty(value = "标识：是否是同一次维护")
    private Long handselFlag;

    @ApiModelProperty(value = " 1-待维护 2-处理中 3-挂单 4-已完成")
    private Integer state;
    @ApiModelProperty(value = "工单状态描述")
    private String stateText;

    @ApiModelProperty(value = "需更换滤芯集合")
    private List<Map<String, String>> filterChangeMap;

    @ApiModelProperty(value = "维护工单ID,多个用逗号隔开")
    private String workCode;

}
