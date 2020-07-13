package com.yimao.cloud.pojo.dto.out;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * 描述：净水设备实体类
 *
 * @Author Zhang Bo
 * @Date 2019/1/31 10:18
 * @Version 1.0
 */
@Data
@ApiModel(description = "净水设备实体类")
public class DeviceDTO implements Serializable {

    private static final long serialVersionUID = -4829011919890027993L;

    private String id;
    @ApiModelProperty(position = 1, value = "sn码")
    private String snCode;
    @ApiModelProperty(position = 2, value = "sim卡")
    private String simCard;
    @ApiModelProperty(position = 3, value = "SIM卡是否激活")
    private Boolean actived;
    @ApiModelProperty(position = 4, value = "经度")
    private String longitude;
    @ApiModelProperty(position = 5, value = "纬度")
    private String latitude;
    @ApiModelProperty(position = 6, value = "省份")
    private String province;
    @ApiModelProperty(position = 7, value = "城市")
    private String city;
    @ApiModelProperty(position = 8, value = "地区")
    private String region;
    @ApiModelProperty(position = 9, value = "详细地址")
    private String address;
    @ApiModelProperty(position = 10, value = "计费套餐Id")
    private String cost;
    @ApiModelProperty(position = 11, value = "计费套餐名称")
    private String costName;
    @ApiModelProperty(position = 12, value = "上一次累计使用时长")
    private Integer lastTotalTime;
    @ApiModelProperty(position = 13, value = "上一次累计使用流量")
    private Integer lastTotalFlow;
    @ApiModelProperty(position = 14, value = "当前使用总时长")
    private Integer currentTime;
    @ApiModelProperty(position = 15, value = "当前使用总流量")
    private Integer currentFlow;
    @ApiModelProperty(position = 16, value = "设备初始金额")
    private Double subMoney;
    @ApiModelProperty(position = 17, value = "设备余额")
    private Double money;
    @ApiModelProperty(position = 18, value = "设备摆放地")
    private String place;
    @ApiModelProperty(position = 19, value = "激活时间")
    private Date activeTime;
    private Date renewTime;
    private Date changeCostTime;
    private Date ppChangeTime;
    private Date udfChangeTime;
    private Date ctoChangeTime;
    private Date t33ChangeTime;
    @ApiModelProperty(position = 91, value = "最后在线时间")
    private Date lastOnlineTime;
    private String logisticsCoding;
    @ApiModelProperty(position = 92, value = "是否在线")
    private Boolean online;
    private Boolean isChange;
    @ApiModelProperty(position = 93, value = "当前更换套餐标记")
    private String changeCost;
    @ApiModelProperty(position = 94, value = "sn码是否绑定水机")
    private Boolean bind;
    @ApiModelProperty(position = 95, value = "解绑标记")
    private Boolean isUnbundling;
    private String version;
    @ApiModelProperty(position = 96, value = "设备类型")
    private String deviceType;
    @ApiModelProperty(position = 97, value = "设备范围")
    private String deviceScope;
    @ApiModelProperty(position = 98, value = "设备型号")
    private String deviceModel;
    private String simCardAccountId;
    private Date snCodeTime;
    private String serverModifyAddress;
    private String serverModifyAddressUser;
    private Date serverModifyAddressTime;
    private Integer renewStatus;
    private String renewStatusZHText;
    private Date deviceArrearageDate;
    private Integer renewTimes;
    private Integer internetType;
    private String internetTypeText;
    private String lockState;
    @ApiModelProperty(position = 100, value = "亮屏时间")
    private String ontime;
    @ApiModelProperty(position = 101, value = "灭屏时间")
    private String offtime;
    @ApiModelProperty(position = 110, value = "可投广告位名称")
    private Set<String> adslotStock;

    //版本信息
    @ApiModelProperty(position = 202,value = "水机版本号")
    private String appVersion;
    @ApiModelProperty(position = 203,value = "消耗流量类型：1-WIFI；3-3G；")
    private Integer consumeFlowType;
    @ApiModelProperty(position = 204,value = "消耗流量类型名称：1-WIFI；3-3G；")
    private String consumeFlowTypeName;
    @ApiModelProperty(position = 205,value = "设备更新版本时间")
    private Date updateVersionTime;
    @ApiModelProperty(position = 206,value = "设备更新版本时间")
    private String updateVersionTimeFormat;
    @ApiModelProperty(position = 207, value = "是否最新版本")
    private String isUpdate;
    @ApiModelProperty(position = 208, value = "设备组：1-用户组，2-服务站组")
    private String deviceGroup;

    private Double k;
    private Double t;
    private String oldTdsId;
    private String oldUserId;
    private String oldUserName;
    private String oldUserPhone;
    private String oldCustomerId;
    private String oldDistributorId;
    private String oldSimAccountId;
}
