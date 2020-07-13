package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * @author Lizhqiang
 * @date 2019/1/17
 */
@ApiModel(description = "区县级公司DTO")
@Getter
@Setter
public class StationCompanyDTO {

    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(position = 1, value = "区县级公司名称")
    private String name;

    @ApiModelProperty(position = 2, value = "区县级公司编码")
    private String code;

    @ApiModelProperty(position = 3, value = "区县级公司类型：1-自营；2-联营；3-仅售后承包；")
    private Integer type;

    @ApiModelProperty(position = 4, value = "省")
    private String province;

    @ApiModelProperty(position = 5, value = "市")
    private String city;

    @ApiModelProperty(position = 6, value = "区")
    private String region;

    @ApiModelProperty(position = 29, value = "服务省")
    private String serviceProvince;

    @ApiModelProperty(position = 30, value = "服务市")
    private String serviceCity;

    @ApiModelProperty(position = 31, value = "服务区")
    private String serviceRegion;


    @ApiModelProperty(position = 7, value = "地址")
    private String address;

    @ApiModelProperty(position = 11, value = "联系人")
    private String contact;

    @ApiModelProperty(position = 12, value = "联系电话")
    private String contactPhone;

    @ApiModelProperty(position = 13, value = "身份证")
    private String contactIdCard;

    @ApiModelProperty(position = 14, value = "邮箱")
    private String email;

    @ApiModelProperty(position = 15, value = "统一社会信用代码")
    private String creditCode;

    @ApiModelProperty(position = 16, value = "法人代表姓名")
    private String legalPerson;

    @ApiModelProperty(position = 17, value = "营业执照")
    private String businessLicense;

    @ApiModelProperty(position = 21, value = "开户行")
    private String bank;

    @ApiModelProperty(position = 22, value = "账号")
    private String bankAccount;

    @ApiModelProperty(position = 23, value = "行号")
    private String bankNumber;

    @ApiModelProperty(position = 24, value = "税号")
    private String taxNumber;

    @ApiModelProperty(position = 25, value = "上线状态：0-未上线；1-上线；")
    private Boolean online;

    @ApiModelProperty(position = 26, value = "上线时间")
    private Date onlineTime;

    @ApiModelProperty(position = 27, value = "门店数量")
    private Integer stationAmount;

    @ApiModelProperty(position = 28, value = "区县级公司服务区域")
    private List<StationCompanyServiceAreaDTO> serviceAreas;

    @ApiModelProperty(position = 100, value = "创建人")
    private String creator;

    @ApiModelProperty(position = 101, value = "创建时间")
    private Date createTime;

    @ApiModelProperty(position = 102, value = "更新人")
    private String updater;

    @ApiModelProperty(position = 103, value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(position = 104, value = "中国云签 帐号 绑定")
    private String yunSignId;

    @ApiModelProperty(position = 105, value = "注册云签的时间")
    private Date yunSignTime;

    @ApiModelProperty(position = 106, value = "是否注册云签 0-否 ， 1- 是")
    private Integer signUp;

    @ApiModelProperty(position = 107, value = "是否设置服务地区 0-否；1-是")
    private Boolean isSetServiceArea;
        
    @ApiModelProperty(position = 108, value = "0-售前+售后；1-售前 ； 2-售后")
    private Integer serviceType;

    @ApiModelProperty(position = 109, value = "是否允许转让 true-允许 false-不允许")
    private Boolean allowTransfer;

}
