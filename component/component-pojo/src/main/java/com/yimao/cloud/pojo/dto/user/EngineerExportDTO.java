package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


@Data
public class EngineerExportDTO {

    @ApiModelProperty(value = "安装工账号")
    private String userName;

    @ApiModelProperty(value = "安装工姓名")
    private String realName;

    @ApiModelProperty(value = "身份证号")
    private String idCard;

    @ApiModelProperty(value = "性别 /1-男，2-女")
    private String sex;

    @ApiModelProperty(value = "联系方式")
    private String phone;

     @ApiModelProperty(value = "服务区域（省）")
    private String province ;

    @ApiModelProperty(value = "服务区域（市）")
    private String city ;

    @ApiModelProperty(value = "服务区域（区）")
    private String region ;

    @ApiModelProperty(value = "服务站门店")
    private String stationName ;

    @ApiModelProperty(value = "服务站公司")
    private String stationCompanyName ;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "账号状态")
    private String forbidden;

    @ApiModelProperty(value = "绑定Iccid号")
    private String iccid;



}
