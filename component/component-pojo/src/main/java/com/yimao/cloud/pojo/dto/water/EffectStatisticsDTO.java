package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 描述：效果统计
 */
@Data
@ApiModel(description = "效果统计")
public class EffectStatisticsDTO implements Serializable {

    private static final long serialVersionUID = 1573836755485035727L;

    @ApiModelProperty(position = 1, value = "id")
    private Integer id;
    @ApiModelProperty(position = 2, value = "广告位ID")
    private String adslotId;
    @ApiModelProperty(position = 3, value = "物料名称")
    private String materielName;
    @ApiModelProperty(position = 4, value = "广告主")
    private String advertisers;
    @ApiModelProperty(position = 5, value = "屏幕位置：1-大屏；2-小屏")
    private Integer screenLocation;
    @ApiModelProperty(position = 6, value = "广告平台：0-翼猫；1-百度；2-京东；3-科大讯飞")
    private Integer platform;
    @ApiModelProperty(position = 7, value = "物料ID")
    private Integer materielId;
    @ApiModelProperty(position = 8, value = "投放配置ID")
    private Integer advertisingId;
    @ApiModelProperty(position = 9, value = "设备编码")
    private String snCode;
    @ApiModelProperty(position = 10, value = "用户单日点击数")
    private Integer clicks;
    @ApiModelProperty(position = 11, value = "广告单日播放次数")
    private Integer playAmount;
    @ApiModelProperty(position = 12, value = "投放开始时间")
    private Date beginTime;
    @ApiModelProperty(position = 13, value = "投放结束时间")
    private Date endTime;
    @ApiModelProperty(position = 14, value = "广告播放时间")
    private Date playTime;
    @ApiModelProperty(position = 15,value = "广告平台名称")
    private String platformName;
    @ApiModelProperty(position = 16,value = "广告播放时段")
    private String timeFormat;
    @ApiModelProperty(position = 17,value = "屏幕位置名称")
    private String screenLocationName;
    @ApiModelProperty(position = 18, value = "设备组：1-用户组，2-服务站组")
    private Integer deviceGroup;

    @ApiModelProperty(position = 90, value = "单日展示设备总数")
    private Integer deviceDayAmount;
    @ApiModelProperty(position = 91, value = "单日广告展示总量")
    private Integer playDayAmount;
    @ApiModelProperty(position = 92, value = "单日用户点击总量")
    private Integer clicksDayCount;

    @ApiModelProperty(position = 100, value = "广告到达数量")
    private Integer snCodeSum;
    @ApiModelProperty(position = 101, value = "用户点击数")
    private Integer clicksSum;
    @ApiModelProperty(position = 102, value = "广告播放次数")
    private Integer playAmountSum;
    @ApiModelProperty(position = 103, value = "投放设备数量")
    private Integer deviceSum;
    @ApiModelProperty(position = 104, value = "投放到达率")
    private String percentage;

    @ApiModelProperty(position = 110, value = "创建时间")
    private Date createTime;

    @ApiModelProperty(position = 111, value = "型号")
    private String modle;

    public  void setEffect(ConditionalAdvertisingDTO dto) {
        this.id = dto.getId();
        this.platform = dto.getPlatform();
        this.adslotId = dto.getOwnAdslotId();
        this.beginTime = dto.getEffectiveBeginTime();
        this.endTime = dto.getEffectiveEndTime();
        this.screenLocation = dto.getScreenLocation();
        this.deviceSum = dto.getDeviceCount();
        this.deviceGroup = dto.getDeviceGroup();
    }
}