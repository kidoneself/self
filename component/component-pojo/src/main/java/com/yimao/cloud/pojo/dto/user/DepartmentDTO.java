package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Zhang Bo
 * @date 2018/11/13.
 */
@Data
@ApiModel(description = "总部业务系统部门")
public class DepartmentDTO implements Serializable {

    private static final long serialVersionUID = -6722744905885967930L;

    @ApiModelProperty(value = "部门ID")
    private Integer id;
    @ApiModelProperty(position = 1, value = "部门名称")
    private String name;
    @ApiModelProperty(position = 2, value = "部门描述")
    private String remark;//部门描述
    @ApiModelProperty(position = 3, value = "该部门下管理员账号数量")
    private Integer accountNumber;
    @ApiModelProperty(position = 4, value = "所属系统：2-翼猫业务管理系统；3-净水设备互动广告系统；")
    private Integer sysType;

    @ApiModelProperty(position = 100, value = "创建者")
    private String creator;
    @ApiModelProperty(position = 101, value = "创建时间")
    private Date createTime;
    @ApiModelProperty(position = 102, value = "更新者")
    private String updater;
    @ApiModelProperty(position = 103, value = "更新时间")
    private Date updateTime;

}
