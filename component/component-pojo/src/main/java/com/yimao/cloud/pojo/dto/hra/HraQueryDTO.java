package com.yimao.cloud.pojo.dto.hra;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @description: 评估卡管理查询条件
 * @author: yu chunlei
 * @create: 2019-05-16 15:21:49
 **/
@Data
public class HraQueryDTO implements Serializable {

    private static final long serialVersionUID = -8033991235655407766L;
    @ApiModelProperty(value = "省")
    private String province;

    @ApiModelProperty(value = "市")
    private String city;

    @ApiModelProperty(value = "区")
    private String region;

    @ApiModelProperty(value = "当前e家号")
    private Integer currentUserId;

    @ApiModelProperty(value = "门店")
    private String stationName;

    @ApiModelProperty(value = "体检卡号")
    private String ticketNo;

    @ApiModelProperty(value = "用户身份")
    private Integer userType;

    @ApiModelProperty(value = "体检卡状态")
    private Integer ticketStatus;

    @ApiModelProperty(value = "剩余可用次数最小值")
    private Integer minSurplus;

    @ApiModelProperty(value = "剩余可用次数最大值")
    private Integer maxSurplus;

    @ApiModelProperty(value = "分配开始时间", example = "2018-12-28 11:00:00")
    private Date beginTime;

    @ApiModelProperty(value = "分配结束时间", example = "2018-12-28 11:00:00")
    private Date endTime;

    @ApiModelProperty(value = "体检卡型号")
    private String cardType;

    @ApiModelProperty(value = "分配端")
    private Integer reserveFrom;//预约操作来源：1终端用户app，2微信公众号，3评估系统，4经销商APP 5后台管理系统

    @ApiModelProperty(value = "服务站Ids")
    private List<Integer> stationIds;

    @ApiModelProperty(value = "用户Ids")
    private List<Integer> ids;

    @ApiModelProperty(value = "是否到期")
    private String isExpire;

    @ApiModelProperty(value = "是否禁用")
    private String state;

}
