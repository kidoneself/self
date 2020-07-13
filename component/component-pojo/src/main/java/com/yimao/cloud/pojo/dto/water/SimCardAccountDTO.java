package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 描述：SIM运营商分配的权限账号
 *
 * @Author Zhang Bo
 * @Date 2019/4/25
 */
@ApiModel(description = "SIM运营商分配的权限账号DTO")
@Getter
@Setter
public class SimCardAccountDTO {

    @ApiModelProperty(value = "ID")
    private Integer id;
    @ApiModelProperty(position = 1, value = "SIM运营商分配的权限账号")
    private String username;
    @ApiModelProperty(position = 2, value = "SIM运营商分配的权限密码")
    private String password;
    @ApiModelProperty(position = 3, value = "SIM运营商分配的权限密钥")
    private String licenseKey;
    @ApiModelProperty(position = 4, value = "SIM运营商公司名称")
    private String companyName;

    private String creator;
    private Date createTime;
    private String updater;
    private Date updateTime;

}
