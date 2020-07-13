package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 描述：水机滤芯更换记录
 *
 * @Author Zhang Bo
 * @Date 2019/4/23
 */
@ApiModel(description = "水机滤芯更换记录DTO")
@Getter
@Setter
public class WaterDeviceFilterChangeRecordDTO {

    private Integer id;
    @ApiModelProperty(value = "设备ID")
    private Integer deviceId;
    @ApiModelProperty(value = "设备SN码")
    private String sn;
    @ApiModelProperty(value = "滤芯名称")
    private String filterName;
    @ApiModelProperty(value = "省份")
    private String province;
    @ApiModelProperty(value = "城市")
    private String city;
    @ApiModelProperty(value = "县")
    private String region;
    @ApiModelProperty(value = "详细地址")
    private String address;
    @ApiModelProperty(value = "激活时间")
    private Date activatingTime;
    @ApiModelProperty(value = "创建时间（更换时间）")
    private Date createTime;
    @ApiModelProperty(value = "维护工单id")
    private String maintenanceWorkOrderId;
    @ApiModelProperty(value = " 工程师id")
    private Integer engineerId;
    @ApiModelProperty(value = " 工程师")
    private String engineerName;
    @ApiModelProperty(value = " 工程师电话")
    private String engineerPhone;
    @ApiModelProperty(value = "客户id")
    private Integer consumerId;
    @ApiModelProperty(value = "客户名")
    private String consumerName;
    @ApiModelProperty(value = "客户电话")
    private String consumerPhone;

    @ApiModelProperty(value = "批次码")
    private String deviceBatchCode;
    @ApiModelProperty(value = "设备型号")
    private String deviceModelName;
    @ApiModelProperty(value = "产品范围")
    private String deviceScope;
    @ApiModelProperty(value = "设备ICCID")
    private String deviceSimcard;
    @ApiModelProperty(value = "用户组-1,服务站组-2")
    private Integer deviceGroup;
    @ApiModelProperty(value = "来源：1-安装工提交 2-客户提交 3-自动生成")
    private Integer source;
    @ApiModelProperty(value = "生效状态：0-否；1-是")
    private Integer effective;

}
