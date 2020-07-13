package com.yimao.cloud.pojo.dto.station;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(description = "服务站站务系统菜单")
@ToString
public class StationMenuDTO implements Serializable {

    private static final long serialVersionUID = 7634456540618364974L;

    @ApiModelProperty(value = "菜单ID")
    private Integer id;
    @ApiModelProperty(position = 1, value = "菜单名")
    private String name;
    @ApiModelProperty(position = 2, value = "菜单描述")
    private String description;
    @ApiModelProperty(position = 3, value = "菜单url")
    private String url;
    @ApiModelProperty(position = 4, value = "上级菜单ID")
    private Integer pid;
    @ApiModelProperty(position = 5, value = "菜单层级")
    private Integer level;
    @ApiModelProperty(position = 6, value = "排序")
    private Integer sorts;
    @ApiModelProperty(position = 7, value = "创建者")
    private Integer creator;
    private String creatorName;//创建者姓名
    @ApiModelProperty(position = 8, value = "创建时间")
    private Date createTime;
    @ApiModelProperty(position = 9, value = "更新者")
    private Integer updater;
    private Integer updaterTime;//更新者姓名
    @ApiModelProperty(position = 10, value = "更新时间")
    private Date updateTime;
    @ApiModelProperty(position = 11, value = "菜单图标")
    private String icon;
    @ApiModelProperty(position = 20, value = "标识该菜单是否被拥有")
    private Boolean have;
    @ApiModelProperty(position = 21, value = "子菜单集合")
    private List<StationMenuDTO> subMenus;
    @ApiModelProperty(position = 22, value = "权限集合")
    private List<StationPermissionDTO> permissions;


}