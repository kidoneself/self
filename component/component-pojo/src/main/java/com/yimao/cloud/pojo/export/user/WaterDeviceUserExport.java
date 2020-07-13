package com.yimao.cloud.pojo.export.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @author: yu chunlei
 * @create: 2019-08-20 18:44:50
 **/
@Data
public class WaterDeviceUserExport implements Serializable {

    private static final long serialVersionUID = 8820110339050138286L;

    private Long id;
    private String realName;
    private String sex;
    private String phone;
    private String idCard;
    private String originTerminal;
    @ApiModelProperty(value = "订单完成时间", example = "2018-12-28 11:00:00")
    private String createTime;
    private String type;
    private String companyIndustry;
    private String sceneTag;
    private String serviceNum;
    private String companyName;
    private String province;
    private String city;
    private String region;
    private String address;
    private String distributorUserId;
    private String distributorId;
    private String distributorAccount;
    private String distributorName;
    private String roleName;
    private String distributorPhone;
    private String distributorIdCard;
    private String distributorProvince;
    private String distributorCity;
    private String distributorRegion;
    private String distributorAddress;


}
