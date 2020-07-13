package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Zhang Bo
 * @date 2018/11/2.
 */
@ApiModel(description = "总部业务系统菜单")
@Getter
@Setter
@ToString
public class MenuDTO implements Serializable {

    private static final long serialVersionUID = 7634976540618364983L;

    @ApiModelProperty(value = "菜单ID")
    private Integer id;
    @ApiModelProperty(position = 1, value = "菜单名")
    private String name;
    @ApiModelProperty(position = 2, value = "菜单描述")
    private String description;
    @ApiModelProperty(position = 3, value = "菜单url")
    private String url;
    @ApiModelProperty(position = 4, value = "菜单图标")
    private String icon;
    @ApiModelProperty(position = 5, value = "上级菜单ID")
    private Integer pid;
    @ApiModelProperty(position = 6, value = "菜单层级")
    private Integer level;
    @ApiModelProperty(position = 7, value = "排序")
    private Integer sorts;
    @ApiModelProperty(position = 8, value = "所属系统：2-翼猫业务管理系统；3-净水设备互动广告系统；")
    private Integer sysType;

    @ApiModelProperty(position = 20, value = "标识该菜单是否被拥有")
    private Boolean have;
    @ApiModelProperty(position = 21, value = "子菜单集合")
    private List<MenuDTO> subMenus;
    @ApiModelProperty(position = 22, value = "权限集合")
    private List<PermissionDTO> permissions;


    @ApiModelProperty(position = 100, value = "创建者")
    private String creator;
    @ApiModelProperty(position = 101, value = "创建时间")
    private Date createTime;
    @ApiModelProperty(position = 102, value = "更新者")
    private String updater;
    @ApiModelProperty(position = 103, value = "更新时间")
    private Date updateTime;



}
