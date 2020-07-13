package com.yimao.cloud.pojo.query.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Lizhqiang
 * @date 2019/1/24
 */
@ApiModel(description = "服务站门店查询条件")
@Getter
@Setter
public class StationQuery implements Serializable {


    @ApiModelProperty(value = "服务站门店ID")
    private Integer id;

    @ApiModelProperty(value = "服务站门店名称")
    private String stationName;

    @ApiModelProperty(value = "站长或者承包人姓名")
    private String realName;

    @ApiModelProperty(value = "是否承包：0-未承包；1-已承包；")
    private Boolean contract;

    @ApiModelProperty(value = "区县级公司名称")
    private String stationCompanyName;

    @ApiModelProperty(value = "服务区域ID")
    private Integer areaId;

    @ApiModelProperty(value = "所在域省")
    private String province;

    @ApiModelProperty(value = "所在域市")
    private String city;

    @ApiModelProperty(value = "所在域区")
    private String region;

    @ApiModelProperty(value = "服务省")
    private String serviceProvince;

    @ApiModelProperty(value = "服务市")
    private String serviceCity;

    @ApiModelProperty(value = "服务区")
    private String serviceRegion;

    @ApiModelProperty(value = "是否上线：0-未上线；1-已上线；")
    private Integer online;

    @ApiModelProperty(value = "是否推荐：0-未推荐；1-已推荐；")
    private Boolean recommend;

    @ApiModelProperty(value = "是否推荐：0-未上线；1-已上线；")
    private Boolean hraIsOnline;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "服务类型 0-售前+售后；1-售前；2-售后")
    private Integer serviceType;
    
    @ApiModelProperty(value = "服务站id集合")
    private Set<Integer> ids;
    
    @ApiModelProperty(value = "是否展示：0-；1-展示；")
    private Integer display;

}
