package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 描述：广告条件投放。
 *
 * @Author Zhang Bo
 * @Date 2019/1/30 14:45
 * @Version 1.0
 */
@Data
@ApiModel(description = "广告条件投放")
public class ConditionalAdvertisingDTO implements Serializable {

    private static final long serialVersionUID = 1573836755485035727L;

    @ApiModelProperty(value = "配置ID")
    private Integer id;
    @ApiModelProperty(position = 1, value = "广告平台：0-翼猫；1-百度；2-京东；3-科大讯飞")
    private Integer platform;
    @ApiModelProperty(position = 2, value = "广告位ID")
    private String ownAdslotId;
    @ApiModelProperty(position = 3, value = "第三方平台广告位ID，当为自有广告时，值为物料ID")
    private String adslotId;
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
    @ApiModelProperty(position = 10, value = "生效状态：0-未生效；1-已生效")
    private Boolean effective;
    @ApiModelProperty(position = 12, value = "下架状态：0-未下架；1-已下架")
    private Boolean forbidden;
    @ApiModelProperty(position = 13, value = "删除状态：0-未删除；1-删除")
    private Boolean deleted;
    @ApiModelProperty(position = 14, value = "1-大屏广告，2-小屏广告")
    private Integer screenLocation;
    @ApiModelProperty(position = 15, value = "1-条件投放，2-精准投放")
    private Integer advertisingType;
    @ApiModelProperty(position = 16, value = "设备在线开始时间")
    private Date lastOnlineBeginTime;
    @ApiModelProperty(position = 17, value = "设备在线结束时间")
    private Date lastOnlineEndTime;
    @ApiModelProperty(position = 18, value = "投放设备数量")
    private Integer deviceCount;
    @ApiModelProperty(position = 19, value = "播放状态")
    private String status;
    @ApiModelProperty(position = 21, value = "投放位置名称")
    private String screenLocationName;
    @ApiModelProperty(position = 22, value = "有效开始时间格式化")
    private String beginTimeFormat;
    @ApiModelProperty(position = 23, value = "有效结束时间格式化")
    private String endTimeFormat;
    @ApiModelProperty(position = 24, value = "创建时间格式化")
    private String createTimeFormat;
    @ApiModelProperty(position = 24, value = "设备位置标签：null-不限")
    private String locationTab;
    @ApiModelProperty(position = 25, value = "广告时长（单位：秒）")
    private Integer duration;
    @ApiModelProperty(position = 25, value = "null-全部,1-用户组，2服务站组")
    private Integer deviceGroup;

    @ApiModelProperty(position = 31, value = "广告主名称")
    private String advertisers;
    @ApiModelProperty(position = 32, value = "物料名称")
    private String materialsName;
    @ApiModelProperty(position = 33, value = "物料ID")
    private String materialsId;

    @ApiModelProperty(position = 41, value = "设备编号的集合")
    private Set<String> snList;
    @ApiModelProperty(position = 51, value = "区域集合")
    private List<ConditionalAdvertisingAreaDTO> areaList;

    @ApiModelProperty(position = 100, value = "创建者")
    private String creator;
    @ApiModelProperty(position = 101, value = "创建时间")
    private Date createTime;
    @ApiModelProperty(position = 102, value = "更新者")
    private String updater;
    @ApiModelProperty(position = 103, value = "更新时间")
    private Date updateTime;
    @ApiModelProperty(position = 104, value = "后续网络连接类型：null-不限；1-WIFI；3-3G")
    private Integer afterConnectionType;

}