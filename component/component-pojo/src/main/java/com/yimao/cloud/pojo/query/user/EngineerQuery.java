package com.yimao.cloud.pojo.query.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 描述：安装工查询条件
 *
 * @Author Zhang Bo
 * @Date 2019/7/8
 */
@ApiModel(description = "安装工查询条件")
@Getter
@Setter
public class EngineerQuery implements Serializable {

    private static final long serialVersionUID = -3444542738489195014L;

    @ApiModelProperty(value = "安装工账号")
    private String userName;

    @ApiModelProperty(value = "安装工姓名")
    private String realName;

    @ApiModelProperty(value = "联系方式")
    private String phone;

    @ApiModelProperty(value = "服务站公司")
    private String stationCompanyName;

    @ApiModelProperty(value = "服务站门店")
    private String stationName;

    @ApiModelProperty(value = "省")
    private String province;

    @ApiModelProperty(value = "市")
    private String city;

    @ApiModelProperty(value = "区")
    private String region;

    @ApiModelProperty(value = "是否禁用：0-否，1-是")
    private Boolean forbidden;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;
    @ApiModelProperty(value = "服务区域id")
    private Integer areaId;

}
