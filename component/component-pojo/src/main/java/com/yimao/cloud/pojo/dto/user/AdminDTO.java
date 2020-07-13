package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

@ApiModel(description = "总部业务系统管理员")
@Getter
@Setter
@ToString
public class AdminDTO implements Serializable {

    private static final long serialVersionUID = 3083637270714797835L;

    @ApiModelProperty(value = "管理员ID")
    public Integer id;
    @ApiModelProperty(position = 1, value = "用户名")
    public String userName;
    @ApiModelProperty(position = 2, value = "密码")
    public String password;
    @ApiModelProperty(position = 3, value = "真实姓名")
    public String realName;
    @ApiModelProperty(position = 4, value = "性别：1-男；2-女")
    public Integer sex;
    @ApiModelProperty(position = 5, value = "手机号")
    public String phone;
    @ApiModelProperty(position = 6, value = "邮箱")
    public String email;
    @ApiModelProperty(position = 7, value = "所属部门ID")
    public Integer deptId;
    @ApiModelProperty(position = 8, value = "所属部门名称")
    public String deptName;
    @ApiModelProperty(position = 9, value = "备注信息")
    public String remark;
    @ApiModelProperty(position = 10, value = "禁用状态：true-被禁用；false-可用")
    public Boolean forbidden;
    @ApiModelProperty(position = 11, value = "服务站ID")
    public Integer stationId;
    @ApiModelProperty(position = 12, value = "所属系统：2-翼猫业务管理系统；3-净水设备互动广告系统；")
    private Integer sysType;

    @ApiModelProperty(position = 15, value = "角色ID集合")
    private Set<Integer> roleIds;
    @ApiModelProperty(position = 20, value = "用户认证jwt信息")
    public String token;
    @ApiModelProperty(position = 21, value = "管理员拥有的菜单列表")
    public List<MenuDTO> menus;

    @ApiModelProperty(position = 100, value = "创建者")
    public String creator;
    @ApiModelProperty(position = 101, value = "创建时间")
    public Date createTime;
    @ApiModelProperty(position = 102, value = "更新者")
    public String updater;
    @ApiModelProperty(position = 103, value = "更新时间")
    public Date updateTime;


}
