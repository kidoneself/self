package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName WaterDeviceCompleteDTO
 * @Description 已完成状态的水机设备
 * @Author yuchunlei
 * @Date 2020/7/3 9:33
 * @Version 1.0
 */
@Getter
@Setter
public class WaterDeviceCompleteDTO implements Serializable {
    private static final long serialVersionUID = -7548536750163559264L;

    private Integer id;
    @ApiModelProperty(position = 2,value = "设备用户姓名")
    private String deviceUserName;
    @ApiModelProperty(position = 3,value = "设备型号")
    private String deviceModel;
    @ApiModelProperty(position = 4,value = "SN码")
    private String sn;
    @ApiModelProperty(position = 5,value = "iccid")
    private String iccid;
    @ApiModelProperty(position = 6,value = "批次码")
    private String logisticsCode;
    @ApiModelProperty(position = 7,value = "安装时间")
    private Date completeTime;
    @ApiModelProperty(position = 8,value = "省")
    private String province;
    @ApiModelProperty(position = 9,value = "市")
    private String city;
    @ApiModelProperty(position = 10,value = "区")
    private String region;
    @ApiModelProperty(position = 11,value = "详细地址")
    private String address;
    @ApiModelProperty(position = 12,value = "当前设备的计费方式")
    private String currentCostName;
    //设备总使用的时长(单位：分）
    @ApiModelProperty(position = 13,value = "设备总使用的时长")
    private Integer useTime;
    //设备总使用的流量（单位：升）
    @ApiModelProperty(position = 14,value = "设备总使用的流量")
    private Integer useFlow;
    @ApiModelProperty(position = 15,value = "设备余额")
    private BigDecimal money;

    @ApiModelProperty(position = 16,value = "设备状态")
    private Boolean online;
    @ApiModelProperty(position = 17,value = "设备续费状态：-1-无需续费；1-未续费；2-待续费；3-已续费；")
    private Integer renewStatus;
    @ApiModelProperty(position = 18,value = "版本")
    private String version;

    @ApiModelProperty(position = 19,value = "设备用户手机号")
    private String deviceUserPhone;
    @ApiModelProperty(position = 20,value = "经销商姓名")
    private String distributorName;



}
