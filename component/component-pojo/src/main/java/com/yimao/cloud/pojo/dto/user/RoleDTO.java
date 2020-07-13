package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * 翼猫业务系统管理员角色表
 *
 * @author Zhang Bo
 * @date 2018/10/30.
 */
@ApiModel(description = "总部业务系统角色")
@Getter
@Setter
@ToString
public class RoleDTO implements Serializable {

    private static final long serialVersionUID = 7960218705148045792L;

    @ApiModelProperty(value = "角色ID")
    private Integer id;
    @ApiModelProperty(position = 1, value = "角色名称")
    private String name;
    @ApiModelProperty(position = 2, value = "所属系统：2-翼猫业务管理系统；3-净水设备互动广告系统；")
    private Integer sysType;

    @ApiModelProperty(position = 10, value = "权限ID集合")
    private Set<Integer> permissionIds;

    @ApiModelProperty(position = 100, value = "创建者")
    private String creator;
    @ApiModelProperty(position = 101, value = "创建时间")
    private Date createTime;
    @ApiModelProperty(position = 102, value = "更新者")
    private String updater;
    @ApiModelProperty(position = 103, value = "更新时间")
    private Date updateTime;

}
