package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * 维护工单记录内部DTO
 * @author Liu Yi
 * @date 2018/12/19
 */
@Data
public class MaintenanceWorkOrderQueryDTO implements Serializable {

    private static final long serialVersionUID = -6047391967687534960L;
    @ApiModelProperty(value = "维护工单号")
    private String id;
    @ApiModelProperty("完成状态：Y-完成 ，N-未完成")
    private String workOrderCompleteStatus;
    @ApiModelProperty(value = "客户名")
    private String consumerName;
    @ApiModelProperty(value = "客户电话")
    private String consumerPhone;
    @ApiModelProperty(value = "设备SN码")
    private String deviceSncode;
    @ApiModelProperty(value = "省份")
    private String province;
    @ApiModelProperty(value = "城市")
    private String city;
    @ApiModelProperty(value = "县")
    private String region;
    @ApiModelProperty(value = "来源：1-自动生成 2-总部添加，默认自动生成")
    private Integer source;
    @ApiModelProperty("创建开始时间")
    private Date createStartTime;
    @ApiModelProperty("创建结束时间")
    private Date createEndTime;
    @ApiModelProperty("完成开始时间")
    private Date finshStartTime;
    @ApiModelProperty("完成结束时间")
    private Date finshEndTime;
}