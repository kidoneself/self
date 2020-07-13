package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 描述：水机设备更换记录
 *
 * @Author Zhang Bo
 * @Date 2019/4/23
 */
@ApiModel(description = "水机设备更换记录DTO")
@Getter
@Setter
public class WaterDeviceReplaceRecordDTO {

    @ApiModelProperty(value = "ID")
    private Integer id;
    @ApiModelProperty(position = 1, value = "旧SN码")
    private String oldSn;
    @ApiModelProperty(position = 2, value = "新SN码")
    private String newSn;
    @ApiModelProperty(position = 3, value = "旧ICCID")
    private String oldIccid;
    @ApiModelProperty(position = 4, value = "新ICCID")
    private String newIccid;
    @ApiModelProperty(position = 5, value = "旧生产批次号")
    private String oldBatchCode;
    @ApiModelProperty(position = 6, value = "新生产批次号")
    private String newBatchCode;
    @ApiModelProperty(position = 7, value = "累计使用时长")
    private Integer time;
    @ApiModelProperty(position = 8, value = "累计使用流量")
    private Integer flow;
    @ApiModelProperty(position = 9, value = "计费方式ID")
    private Integer costId;
    @ApiModelProperty(position = 10, value = "计费方式名称")
    private String costName;
    @ApiModelProperty(position = 11, value = "设备余额")
    private BigDecimal money;
    @ApiModelProperty(position = 12, value = "设备型号")
    private String deviceModel;
    @ApiModelProperty(position = 13, value = "安装区域省")
    private String province;
    @ApiModelProperty(position = 14, value = "安装区域市")
    private String city;
    @ApiModelProperty(position = 15, value = "安装区域区")
    private String region;
    @ApiModelProperty(position = 15, value = "变更后经度")
    private String longitude;
    @ApiModelProperty(position = 15, value = "变更后纬度")
    private String latitude;

    @ApiModelProperty(position = 20, value = "操作人")
    private String creator;
    @ApiModelProperty(position = 21, value = "操作时间")
    private Date createTime;

}
