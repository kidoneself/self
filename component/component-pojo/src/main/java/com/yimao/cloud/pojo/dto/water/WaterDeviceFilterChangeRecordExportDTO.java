package com.yimao.cloud.pojo.dto.water;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/***
 * 功能描述:水机滤芯更换记录
 *
 * @auther: liu yi
 * @date: 2019/4/1 9:58
 */
@Getter
@Setter
@ApiModel(description = "导出水机滤芯更换记录记录")
public class WaterDeviceFilterChangeRecordExportDTO implements Serializable {
    @ApiModelProperty(value = "ID")
    private Integer id;
    @ApiModelProperty(value = "维护工单id")
    private String maintenanceWorkOrderId;

    @ApiModelProperty(value = "滤芯名称")
    private String filterName;
    @ApiModelProperty(value = "客户名")
    private String consumerName;
    @ApiModelProperty(value = "客户电话")
    private String consumerPhone;
    @ApiModelProperty(value = "批次码")
    private String deviceBatchCode;
    @ApiModelProperty(value = "设备型号")
    private String deviceModelName;
    @ApiModelProperty(value = "设备SN码")
    private String deviceSncode;
    @ApiModelProperty(value = "设备ICCID")
    private String deviceSimcard;
    @ApiModelProperty(value = "省份")
    private String province;
    @ApiModelProperty(value = "城市")
    private String city;
    @ApiModelProperty(value = "县")
    private String region;
    @ApiModelProperty(value = "地址")
    private String address;
    @ApiModelProperty(value = "来源：1-安装工提交 2-客户提交")
    private String sourceTxt;
    @ApiModelProperty(value = "创建时间")
    private String createTimeTxt;
    @ApiModelProperty(value = "生效状态：0-否；1-是")
    private String effectiveTxt;
    @ApiModelProperty(value = "更换时间")
    private String activatingTime;
    @ApiModelProperty(value = "产品范围")
    private String deviceScope;
}
