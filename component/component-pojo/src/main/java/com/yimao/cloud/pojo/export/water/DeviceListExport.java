package com.yimao.cloud.pojo.export.water;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @description: 设备列表导出实体类
 * @author: yu chunlei
 * @create: 2019-11-30 18:51:02
 **/
@Getter
@Setter
public class DeviceListExport {

    @ApiModelProperty(position = 1, value = "ID")
    private Long id;
    @ApiModelProperty(position = 1, value = "SN码")
    private String sn;
    @ApiModelProperty(position = 2, value = "批次码")
    private String logisticsCode;
    @ApiModelProperty(position = 3, value = "计费方式")
    private String costName;
    @ApiModelProperty(position = 4, value = "省份")
    private String province;
    @ApiModelProperty(position = 5, value = "城市")
    private String city;
    @ApiModelProperty(position = 6, value = "地区")
    private String region;
    @ApiModelProperty(position = 7, value = "经销商登录名")
    private String distributorName;
    @ApiModelProperty(position = 8, value = "经销商姓名")
    private String distributorRealName;
    @ApiModelProperty(position = 9, value = "用户姓名")
    private String deviceUserName;
    @ApiModelProperty(position = 10, value = "用户手机号")
    private String deviceUserPhone;
    @ApiModelProperty(position = 11, value = "客服姓名")
    private String engineerName;
    @ApiModelProperty(position = 12, value = "客服手机号")
    private String engineerPhone;
    @ApiModelProperty(position = 13, value = "型号")
    private String deviceModel;
    @ApiModelProperty(position = 14, value = "激活时间")
    private String snEntryTime;
    @ApiModelProperty(position = 15, value = "SIM卡号")
    private String iccid;
    @ApiModelProperty(position = 16, value = "最后时间")
    private String lastOnlineTime;
    @ApiModelProperty(position = 17, value = "是否在线")
    private String online;
    @ApiModelProperty(position = 18, value = "金额")
    private BigDecimal money;
    @ApiModelProperty(position = 19, value = "时长")
    private Integer currentTotalTime;
    @ApiModelProperty(position = 20, value = "流量")
    private Integer currentTotalFlow;
    @ApiModelProperty(position = 21, value = "版本号")
    private String version;
    @ApiModelProperty(position = 22, value = "是否更换计费方式")
    private String costChanged;
    @ApiModelProperty(position = 23, value = "当前计费方式")
    private String costType;
    @ApiModelProperty(position = 24, value = "sim卡运营商")
    private String simCompany;
    @ApiModelProperty(position = 25, value = "网络状态")
    private String connectionType;
    @ApiModelProperty(position = 25, value = "创建时间")
    private String createTime;


}
