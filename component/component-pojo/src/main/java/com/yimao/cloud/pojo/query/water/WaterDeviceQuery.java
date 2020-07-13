package com.yimao.cloud.pojo.query.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.yimao.cloud.pojo.dto.user.EngineerServiceAreaDTO;

/**
 * 描述：水机设备列表查询条件
 *
 * @Author Zhang Bo
 * @Date 2019/3/12
 */
@ApiModel(description = "水机设备列表查询条件")
@Getter
@Setter
public class WaterDeviceQuery implements Serializable {

    private static final long serialVersionUID = 5477650154792452087L;

    @ApiModelProperty(position = 1, value = "SN码")
    private String sn;

    @ApiModelProperty(position = 2, value = "工单号")
    private String workOrderId;

    @ApiModelProperty(position = 4, value = "批次码")
    private String batchCode;

    @ApiModelProperty(position = 5, value = "水机型号")
    private String deviceModel;

    @ApiModelProperty(position = 6, value = "安装区域-省")
    private String province;

    @ApiModelProperty(position = 7, value = "安装区域-市")
    private String city;

    @ApiModelProperty(position = 8, value = "安装区域-区")
    private String region;

    @ApiModelProperty(position = 9, value = "设备在线状态：0-不在线；1-在线；")
    private Boolean online;

    @ApiModelProperty(position = 10, value = "客户表主键ID")
    private Integer deviceUserId;

    @ApiModelProperty(position = 10, value = "客户姓名")
    private String deviceUserName;

    @ApiModelProperty(position = 11, value = "客户手机号")
    private String deviceUserPhone;

    @ApiModelProperty(position = 12, value = "安装工ID")
    private Integer engineerId;

    @ApiModelProperty(position = 13, value = "安装工姓名")
    private String engineerName;

    @ApiModelProperty(position = 14, value = "经销商姓名")
    private String distributorName;

    @ApiModelProperty(position = 15, value = "开始时间")
    private Date startTime;

    @ApiModelProperty(position = 16, value = "结束时间")
    private Date endTime;

    @ApiModelProperty(position = 17, value = "安装工ID")
    private String keyWords;

    @ApiModelProperty(position = 18, value = "设备网络连接类型：1-WIFI；3-3G；")
    private Integer connectionType;

    @ApiModelProperty(position = 19, value = "设备摆放地")
    private String place;
    private String activatingTime;
    private Integer pageSize;
    private String createTime;

    @ApiModelProperty(position = 20, value = "续费状态：0-未续费；1-已续费；")
    private Boolean renewStatus;

    @ApiModelProperty(position = 21, value = "维护状态：0-未维护；1-已维护；")
    private Boolean maintainStatus;

    @ApiModelProperty(position = 22, value = "安装工服务区域集合")
    private List<EngineerServiceAreaDTO> serviceAreaList;


    @ApiModelProperty(position = 22, value = "是否变更ICCID：0-未变更；1-变更；")
    private Boolean ICCIDChange;


    @Override
    public String toString() {
        return "WaterDeviceQuery{" +
                "sn='" + sn + '\'' +
                ", workOrderId='" + workOrderId + '\'' +
                ", batchCode='" + batchCode + '\'' +
                ", deviceModel='" + deviceModel + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", region='" + region + '\'' +
                ", online=" + online +
                ", deviceUserId=" + deviceUserId +
                ", deviceUserName='" + deviceUserName + '\'' +
                ", deviceUserPhone='" + deviceUserPhone + '\'' +
                ", engineerId=" + engineerId +
                ", engineerName='" + engineerName + '\'' +
                ", distributorName='" + distributorName + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", keyWords='" + keyWords + '\'' +
                ", connectionType=" + connectionType +
                ", place='" + place + '\'' +
                ", renewStatus=" + renewStatus +
                ", maintainStatus=" + maintainStatus +
                '}';
    }
}
