package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 服务站门店（原服务站）
 *
 * @author Lizhqiang
 * @date 2019/1/17
 */
@ApiModel(description = "服务站门店DTO（原服务站）")
@Getter
@Setter
public class StationDTO {

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(position = 1, value = "名称")
    private String name;

    @ApiModelProperty(position = 2, value = "门店编号")
    private String code;

    @ApiModelProperty(position = 3, value = "门店类型：1-加盟店，2-连锁店，3-旗舰店")
    private Integer type;

    @ApiModelProperty(position = 4, value = "服务站所在省")
    private String province;

    @ApiModelProperty(position = 5, value = "服务站所在市")
    private String city;

    @ApiModelProperty(position = 6, value = "服务站所在区")
    private String region;

    @ApiModelProperty(position = 7, value = "地址")
    private String address;

    @ApiModelProperty(position = 8, value = "经度")
    private Double longitude;

    @ApiModelProperty(position = 9, value = "纬度")
    private Double latitude;

    @ApiModelProperty(position = 10, value = "上线状态：0-未上线；1-上线；")
    private Integer online;

    @ApiModelProperty(position = 12, value = "是否展示")
    private Integer display;

    @ApiModelProperty(position = 11, value = "上线时间")
    private Date onlineTime;

    @ApiModelProperty(position = 21, value = "联系人")
    private String contact;

    @ApiModelProperty(position = 22, value = "联系人手机号")
    private String contactPhone;

    @ApiModelProperty(position = 23, value = "站长姓名")
    private String masterName;

    @ApiModelProperty(position = 24, value = "站长电话")
    private String masterPhone;

    @ApiModelProperty(position = 25, value = "站长身份证")
    private String masterIdCard;

    @ApiModelProperty(position = 26, value = "服务站公司名称")
    private String companyName;

    @ApiModelProperty(position = 31, value = "是否承包：0-未承保；1-已承包；")
    private Boolean contract;

    @ApiModelProperty(position = 32, value = "承包人姓名")
    private String contractor;

    @ApiModelProperty(position = 33, value = "承包人电话")
    private String contractorPhone;

    @ApiModelProperty(position = 34, value = "承包人身份证号码")
    private String contractorIdCard;

    @ApiModelProperty(position = 35, value = "承包开始时间")
    private Date contractStartTime;

    @ApiModelProperty(position = 36, value = "承包结束时间")
    private Date contractEndTime;

    @ApiModelProperty(position = 41, value = "成立时间")
    private Date establishedTime;

    @ApiModelProperty(position = 42, value = "满意度")
    private Double satisfaction;

    @ApiModelProperty(position = 43, value = "员工数量")
    private Integer employeeNum;

    @ApiModelProperty(position = 44, value = "营业开始时间")
    private String businessHoursStart;

    @ApiModelProperty(position = 45, value = "营业结束时间")
    private String businessHoursEnd;

    @ApiModelProperty(position = 46, value = "是否推荐 0:否 1:是")
    private Boolean recommend;

    @ApiModelProperty(position = 47, value = "服务站图片,多张图片用逗号隔开")
    private String imgs;

    @ApiModelProperty(position = 48, value = "服务站封面图片")
    private String coverImage;

    @ApiModelProperty(position = 49, value = "门店规模 单位:平方米")
    private Double stationArea;

    @ApiModelProperty(position = 50, value = "服务理念")
    private String purpose;

    @ApiModelProperty(position = 51, value = "资质授权")
    private String aptitude;

    @ApiModelProperty(position = 52, value = "服务站介绍")
    private String introduction;

    @ApiModelProperty(position = 53, value = "排序字段")
    private Integer sorts;

    @ApiModelProperty(position = 61, value = "服务站服务区域")
    private List<StationServiceAreaDTO> serviceAreas;

    @ApiModelProperty(position = 62, value = "区县级公司ID")
    private List<Integer> stationCompanyIds;

    @ApiModelProperty(position = 63, value = "距离")
    private Double distance;

    @ApiModelProperty(position = 64, value = "公司名称")
    private String stationCompanyName;

    @ApiModelProperty(position = 100, value = "创建人")
    private String creator;

    @ApiModelProperty(position = 101, value = "创建时间")
    private Date createTime;

    @ApiModelProperty(position = 102, value = "更新人")
    private String updater;

    @ApiModelProperty(position = 103, value = "更新时间")
    private Date updateTime;


    @ApiModelProperty(position = 104, value = "区域所在省")
    private String areaProvince;

    @ApiModelProperty(position = 105, value = "区域所在市")
    private String areaCity;

    @ApiModelProperty(position = 106, value = "区域所在区")
    private String areaRegion;

    @ApiModelProperty(position = 107, value = "删除标识：0-未删除；1-已删除；")
    private Boolean deleted;

    @ApiModelProperty(position = 108, value = "mongdb数据库中的ID")
    private String oldId;

    @ApiModelProperty(position = 109, value = "经销商账号Id")
    private Integer masterDistributorId;
    
    @ApiModelProperty(value = "服务站门店是否拥有true-有 false-没有(站务系统判断)")
    private boolean stationHave;

    @ApiModelProperty(value = "绑定服务站门店权限类型 0-售前+售后；1-售前 ； 2-售后")
    private Integer serviceType;

}
