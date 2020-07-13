package com.yimao.cloud.pojo.query.hra;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 体检卡查询条件
 * @author: yu chunlei
 * @create: 2019-11-26 19:27:19
 **/
@Getter
@Setter
public class HraPhysicalQuery implements Serializable {

    @ApiModelProperty(value = "省")
    private String province;

    @ApiModelProperty(value = "市")
    private String city;

    @ApiModelProperty(value = "区")
    private String region;

    @ApiModelProperty(value = "服务站门店")
    private String stationName;

    @ApiModelProperty(value = "当前e家号")
    private Integer currentUserId;

    @ApiModelProperty(value = "体检卡号")
    private String ticketNo;

    @ApiModelProperty(value = "用户身份")
    private Integer userType;

    @ApiModelProperty(value = "剩余可用次数")
    private Integer surplus;

    @ApiModelProperty(value = "状态")
    private Integer ticketStatus;

    @ApiModelProperty(value = "分配开始时间")
    private String beginTime;

    @ApiModelProperty(value = "分配结束时间")
    private String endTime;

    @ApiModelProperty(value = "体检卡型号")
    private String cardType;

    @ApiModelProperty(value = "服务站id集合")
    private List<Integer> stationIds;

    @ApiModelProperty(value = "用户e家号集合")
    private List<Integer> ids;


    @ApiModelProperty(value = "是否到期")
    private String isExpire;

    @ApiModelProperty(value = "剩余可用次数最小值")
    private Integer minSurplus;

    @ApiModelProperty(value = "剩余可用次数最大值")
    private Integer maxSurplus;

    @ApiModelProperty(value = "是否禁用")
    private String state;

}
