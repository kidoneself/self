package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Zhang Bo
 * @date 2018/11/2.
 */
@ApiModel(description = "总部业务系统权限")
@Getter
@Setter
@ToString
public class PermissionDTO implements Serializable {

    private static final long serialVersionUID = -1565402715164026844L;

    @ApiModelProperty(value = "权限ID")
    private Integer id;
    @ApiModelProperty(position = 1, value = "权限名")
    private String name;
    @ApiModelProperty(position = 2, value = "权限CODE（请求地址）")
    private String code;
    @ApiModelProperty(position = 3, value = "权限方法（请求类型）")
    private String method;
    @ApiModelProperty(position = 4, value = "菜单ID")
    private Integer menuId;
    @ApiModelProperty(position = 5, value = "所属系统：2-翼猫业务管理系统；3-净水设备互动广告系统；")
    private Integer sysType;

    @ApiModelProperty(position = 10, value = "标识该权限是否被拥有")
    private Boolean have;

    @ApiModelProperty(position = 100, value = "创建者")
    private String creator;
    @ApiModelProperty(position = 101, value = "创建时间")
    private Date createTime;
    @ApiModelProperty(position = 102, value = "更新者")
    private String updater;
    @ApiModelProperty(position = 103, value = "更新时间")
    private Date updateTime;


}
