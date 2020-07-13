package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * @author Lizhqiang
 * @date 2019/1/17
 */
@ApiModel(description = "区县级公司服务区域")
@Getter
@Setter
@ToString
public class StationCompanyServiceAreaDTO {

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(position = 1, value = "区县级公司id")
    private Integer stationCompanyId;

    @ApiModelProperty(position = 2, value = "区县级公司名称")
    private String stationCompanyName;

    @ApiModelProperty(position = 3, value = "地区id")
    private Integer areaId;

    @ApiModelProperty(position = 4, value = "服务站公司服务省")
    private String province;

    @ApiModelProperty(position = 5, value = "服务站公司服务市")
    private String city;

    @ApiModelProperty(position = 6, value = "服务站公司服务区")
    private String region;

    @ApiModelProperty(position = 7, value = "服务类型 0-售前+售后；1-售前；2-售后")
    private Integer serviceType;

    private String creator;
    private Date createTime;
    private String updater;
    private Date updateTime;

    @ApiModelProperty(value = "售前是否可选 false-不可选，true-可选")
    private Boolean usablePreSale;

    @ApiModelProperty(value = "售后是否可选 false-不可选，true-可选")
    private Boolean usableAfterSale;

    @ApiModelProperty(value = "是否拥有售前权限 false-无，true-有")
    private Boolean havePreSale;

    @ApiModelProperty(value = "是否拥有售后权限 false-无，true-有")
    private Boolean haveAfterSale;

}
