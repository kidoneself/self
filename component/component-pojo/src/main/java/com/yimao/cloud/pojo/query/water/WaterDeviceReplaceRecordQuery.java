package com.yimao.cloud.pojo.query.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 描述：水机设备更换记录查询条件
 *
 * @Author Zhang Bo
 * @Date 2019/7/16
 */
@ApiModel(description = "水机设备更换记录查询条件")
@Getter
@Setter
public class WaterDeviceReplaceRecordQuery implements Serializable {

    private static final long serialVersionUID = 8644081627971893922L;

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
    @ApiModelProperty(position = 7, value = "设备型号")
    private String deviceModel;
    @ApiModelProperty(position = 8, value = "安装区域省")
    private String province;
    @ApiModelProperty(position = 9, value = "安装区域市")
    private String city;
    @ApiModelProperty(position = 10, value = "安装区域区")
    private String region;

    @Override
    public String toString() {
        return "WaterDeviceReplaceRecordQuery{" +
                "oldSn='" + oldSn + '\'' +
                ", newSn='" + newSn + '\'' +
                ", oldIccid='" + oldIccid + '\'' +
                ", newIccid='" + newIccid + '\'' +
                ", oldBatchCode='" + oldBatchCode + '\'' +
                ", newBatchCode='" + newBatchCode + '\'' +
                ", deviceModel='" + deviceModel + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", region='" + region + '\'' +
                '}';
    }
}
