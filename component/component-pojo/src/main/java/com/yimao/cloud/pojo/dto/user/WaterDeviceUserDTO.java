package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 描述：水机设备用户
 *
 * @Author Zhang Bo
 * @Date 2019/5/17
 */
@ApiModel(description = "水机设备用户DTO")
@Getter
@Setter
public class WaterDeviceUserDTO {

    private Integer id;
    private String realName;
    private String phone;
    private String province;
    private String city;
    private String region;
    private String address;
    private String job;
    private Integer sex;
    private Integer age;
    private Integer type;//类别 1-个人 2-公司
    private String idCard;
    private String degree;//文化程度
    private String email;
    private Date createTime;
    private Date updateTime;

    //mongo数据库中的额主键
    private String oldId;

    //经销商e家号
    private Integer distributorUserId;
    //经销商ID
    private Integer distributorId;
    //mongo库中经销商ID
    private String oldDistributorId;
    //子经销商ID
    private Integer childDistributorId;
    private String oldChildDistributorId;

    //用户来源方式
    private Integer origin;
    private Integer originTerminal;

    private String companyName;
    private String companyIndustry;
    private String sceneTag;
    private String serviceNum;

    private String distributorAccount;
    private String distributorName;
    //经销商角色等级：-50-折机版；50-体验版；350-微创版；650-个人版；950-企业版（主）；1000-企业版（子
    private Integer roleLevel;
    private String roleName;
    private String distributorPhone;
    private String distributorIdCard;
    private String distributorProvince;
    private String distributorCity;
    private String distributorRegion;
    private String distributorAddress;
    private String childDistributorName;
    private String childDistributorAccount;
    private String childDistributorPhone;


}
