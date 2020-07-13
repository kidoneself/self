package com.yimao.cloud.pojo.dto.hra;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: 设备上线信息
 * @author: yu chunlei
 * @create: 2019-02-28 17:16:25
 **/
@Data
public class HraDeviceOnlineDTO implements Serializable {


    private static final long serialVersionUID = -4654535414199956L;
    @ApiModelProperty(position = 1,value = "ID")
    private Integer id;
    @ApiModelProperty(position = 2,value = "省")
    private String province;
    @ApiModelProperty(position = 3,value = "市")
    private String city;
    @ApiModelProperty(position = 4,value = "区")
    private String region;
//    @ApiModelProperty(position = 4,value = "服务站门店名称")
//    private String name;
    @ApiModelProperty(position = 5,value = "服务站公司名称")
    private String companyName;
    @ApiModelProperty(position = 6,value = "设备型号")
    private Integer deviceType;
    @ApiModelProperty(position = 7,value = "设备ID")
    private String deviceId;
    @ApiModelProperty(position = 8,value = "门店ID")
    private Integer stationId;
    @ApiModelProperty(position = 9,value = "门店名称")
    private String stationName;
    @ApiModelProperty(position = 10,value = "门店地址")
    private String stationAddress;
    @ApiModelProperty(position = 11,value = "门店联系方式")
    private String stationTel;
    @ApiModelProperty(position = 12,value = "设备状态：1可用，2锁定")
    private Integer deviceStatus;//设备状态：1可用，2锁定




}
