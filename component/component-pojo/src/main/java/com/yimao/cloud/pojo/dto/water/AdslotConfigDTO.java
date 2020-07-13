package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 描述：广告位配置信息
 *
 * @Author Zhang Bo
 * @Date 2019/2/20 17:34
 * @Version 1.0
 */
@Data
@ApiModel(description = "广告位配置信息")
public class AdslotConfigDTO implements Serializable {

    private static final long serialVersionUID = -543150484840690302L;

    @ApiModelProperty(value = "配置id")
    private Integer id;
    @ApiModelProperty(position = 1, value = "水机广告位ID")
    private String ownAdslotId;
    @ApiModelProperty(position = 2, value = "第三方平台广告位ID，当为自有广告时，值为物料ID")
    private String adslotId;
    @ApiModelProperty(position = 3, value = "广告时长（单位：秒）")
    private Integer duration;
    @ApiModelProperty(position = 4, value = "网络连接类型：0-不限；1-WIFI；3-3G；")
    private Integer connectionType;
    @ApiModelProperty(position = 5, value = "配置生效开始时间")
    private Date beginTime;
    @ApiModelProperty(position = 6, value = "配置生效结束时间")
    private Date endTime;
    @ApiModelProperty(position = 7, value = "1-大屏广告，2-小屏广告")
    private Integer screenLocation;

    @ApiModelProperty(position = 8, value = "广告平台信息（翼猫、百度、京东、科大讯飞等）")
    private PlatformConfigDTO platform;
    @ApiModelProperty(position = 9, value = "自有广告的物料信息")
    private MaterialsConfigDTO materials;
    @ApiModelProperty(position = 10, value = "后续网络连接类型：0-不限；1-WIFI；3-3G；")
    private Integer afterConnectionType;
}
