package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 描述：安装工程师
 *
 * @Author Zhang Bo
 * @Date 2019/2/25 15:56
 * @Version 1.0
 */
@ApiModel(description = "安装工")
@Getter
@Setter
public class EngineerDTO implements Serializable {

    private static final long serialVersionUID = 9025079860462950079L;

    @ApiModelProperty(value = "安装工ID")
    private Integer id;

    @ApiModelProperty(position = 1, value = "用户名")
    private String userName;

    @ApiModelProperty(position = 2, value = "密码")
    private String password;

    @ApiModelProperty(position = 3, value = "姓名")
    private String realName;

    @ApiModelProperty(position = 4, value = "手机号")
    private String phone;

    @ApiModelProperty(position = 5, value = "头像")
    private String headImg;

    @ApiModelProperty(position = 6, value = "省")
    private String province;

    @ApiModelProperty(position = 7, value = "市")
    private String city;

    @ApiModelProperty(position = 8, value = "区")
    private String region;

    @ApiModelProperty(position = 9, value = "性别（1：男，2：女）")
    private Integer sex;

    @ApiModelProperty(position = 10, value = "邮箱")
    private String email;

    @ApiModelProperty(position = 11, value = "地址")
    private String address;

    @ApiModelProperty(position = 12, value = "身份证号码")
    private String idCard;

    @ApiModelProperty(position = 13, value = "安装工工作状态：1-忙碌，0-空闲")
    private Integer state;

    @ApiModelProperty(position = 14, value = "登录的手机系统：1-Android；2-ios")
    private Integer appType;

    @ApiModelProperty(position = 15, value = "正在进行的工单数")
    private Integer count;

    @ApiModelProperty(position = 16, value = "账号是否被禁用：0-否，1-是")
    private Boolean forbidden;

    @ApiModelProperty(position = 17, value = "累计登录次数")
    private Integer loginCount;

    @ApiModelProperty(position = 18, value = "最后登录时间")
    private Date lastLoginTime;

    @ApiModelProperty(position = 19, value = "当前下载使用的app版本")
    private String version;

    @ApiModelProperty(position = 20, value = "工号")
    private String workId;

    @ApiModelProperty(position = 21, value = "服务站门店ID")
    private Integer stationId;

    @ApiModelProperty(position = 22, value = "服务站门店名称")
    private String stationName;

    @ApiModelProperty(position = 23, value = "服务站公司ID")
    private Integer stationCompanyId;

    @ApiModelProperty(position = 24, value = "服务站公司名称")
    private String stationCompanyName;

    @ApiModelProperty(position = 25, value = "安装工登录的那台手机号对应的ICCID")
    private String iccid;

    @ApiModelProperty(position = 26, value = "云平台系统安装工的ID")
    private String oldId;

    @ApiModelProperty(position = 27, value = "云平台系统安装工对应的服务站ID")
    private String oldSiteId;

    @ApiModelProperty(position = 28, value = "地区id")
    private Integer areaId;


    @ApiModelProperty(position = 101, value = "创建人")
    private String creator;
    @ApiModelProperty(position = 102, value = "创建时间", example = "1970-01-01 00:00:00")
    private Date createTime;
    @ApiModelProperty(position = 103, value = "更新人")
    private String updater;
    @ApiModelProperty(position = 104, value = "更新时间", example = "1970-01-01 00:00:00")
    private Date updateTime;


    @ApiModelProperty(position = 130, value = "安装工登录验证token")
    private String token;


    @ApiModelProperty(position = 150, value = "变更记录，查看详情时返回")
    private List<EngineerChangeRecordDTO> changeRecords;
    
    @ApiModelProperty(position = 151, value = "安装工服务区域list")
    private List<EngineerServiceAreaDTO> serviceAreaList;

}
