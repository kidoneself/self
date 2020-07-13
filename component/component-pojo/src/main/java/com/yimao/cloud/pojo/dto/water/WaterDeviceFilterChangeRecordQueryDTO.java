package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 维护工单记录内部DTO
 *
 * @author Liu Yi
 * @date 2018/12/19
 */
@Data
public class WaterDeviceFilterChangeRecordQueryDTO implements Serializable {

    private static final long serialVersionUID = -6047391967687534960L;
    @ApiModelProperty(value = "ID")
    private Integer id;
    @ApiModelProperty(value = "SN码")
    private String sn;
    @ApiModelProperty(value = "维护工单id")
    private String maintenanceWorkOrderId;
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
    @ApiModelProperty(value = "来源：1-安装工提交 2-客户提交 3-自动生成")
    private Integer source;
    @ApiModelProperty(value = "生效状态：0-否；1-是")
    private Integer effective;
    @ApiModelProperty("开始时间")
    private Date startTime;
    @ApiModelProperty("结束时间")
    private Date endTime;
}