package com.yimao.cloud.pojo.dto.station;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Zhang Bo
 * @date 2018/11/2.
 */
@ApiModel(description = "服务站站务系统权限")
@Getter
@Setter
@ToString
public class StationPermissionDTO implements Serializable {

    private static final long serialVersionUID = -1565402747164026884L;

    @ApiModelProperty(value = "权限ID")
    private Integer id;
    @ApiModelProperty(position = 1, value = "权限名")
    private String name;
    @ApiModelProperty(position = 2, value = "权限CODE（请求地址）")
    private String url;
    @ApiModelProperty(position = 3, value = "权限方法（请求类型）")
    private String method;
    @ApiModelProperty(position = 4, value = "菜单ID")
    private Integer menuId;

    @ApiModelProperty(position = 10, value = "标识该权限是否被拥有")
    private Boolean have;

    @ApiModelProperty(position = 100, value = "创建者")
    private Integer creator;
    private String creatorName; //创建人姓名
    @ApiModelProperty(position = 101, value = "创建时间")
    private Date createTime;
    @ApiModelProperty(position = 102, value = "更新者")
    private Integer updater;
    private String updaterName;//更新者姓名
    @ApiModelProperty(position = 103, value = "更新时间")
    private Date updateTime;
    
    @ApiModelProperty(position = 104, value = "售前售后属性")
    private Integer type;

}
