package com.yimao.cloud.pojo.dto.station;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * 服务站站务系统管理员操作记录表
 *
 * @author Liu long jie
 * @date 2019/12/31.
 */
@Data
@ApiModel(description = "服务站站务系统管理员操作记录表")
public class StationAdminLogDTO {

    @ApiModelProperty(value = "主键id")
    private Integer id;
    @ApiModelProperty(value = "管理员id")
    private Integer adminId;
    @ApiModelProperty(value = "管理员用户名")
    private String userName;
    @ApiModelProperty(value = "管理员真实姓名")
    private String realName;
    @ApiModelProperty(value = "角色类型id")
    private Integer roleId;
    @ApiModelProperty(value = "操作类型  1-登陆 2- .......")
    private Integer osType;
    @ApiModelProperty(value = "登录ip")
    private String ip;
    @ApiModelProperty(value = "操作时间")
    private Date time;


}
