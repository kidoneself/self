package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * 描述：广告投放筛选。
 *
 * @Author Zhang Bo
 * @Date 2019/1/30 14:45
 * @Version 1.0
 */
@Data
@ApiModel(description = "广告投放筛选")
public class FilterAdvertisingDTO implements Serializable {

    private static final long serialVersionUID = 1573836755485035727L;

    @ApiModelProperty(value = "配置ID")
    private Integer id;
    @ApiModelProperty(position = 1, value = "广告平台：0-翼猫；1-百度；2-京东；3-科大讯飞")
    private Integer platform;
    @ApiModelProperty(position = 2, value = "广告位ID")
    private String ownAdslotId;
    @ApiModelProperty(position = 3, value = "第三方平台广告位ID，当为自有广告时，值为物料ID")
    private String adslotId;
    @ApiModelProperty(position = 4, value = "广告时长（单位：秒）")
    private Integer duration;
    @ApiModelProperty(position = 5, value = "省市区：多选以逗号分隔，不限无需设置")
    private String areas;
    @ApiModelProperty(position = 6, value = "水机型号：多选以逗号分隔，不限无需设置")
    private String models;
    @ApiModelProperty(position = 7, value = "网络连接类型：null-不限；1-WIFI；3-3G")
    private Integer connectionType;
    @ApiModelProperty(position = 8, value = "配置生效开始时间")
    private Date effectiveBeginTime;
    @ApiModelProperty(position = 9, value = "配置生效结束时间")
    private Date effectiveEndTime;
    @ApiModelProperty(position = 14, value = "1-大屏广告，2-小屏广告")
    private Integer screenLocation;
    @ApiModelProperty(position = 15, value = "1-条件投放，2-精准投放")
    private Integer advertisingType;
    @ApiModelProperty(position = 16, value = "设备组：1-用户组，2服务站组")
    private Integer deviceGroup;

    @ApiModelProperty(position = 31, value = "区域id")
    private Integer areaId;
    @ApiModelProperty(position = 32, value = "区域名称")
    private String areaName;
    @ApiModelProperty(position = 33, value = "型号")
    private String model;

    @ApiModelProperty(position = 34, value = "后续网络连接类型：null-不限；1-WIFI；3-3G")
    private Integer afterConnectionType;

}