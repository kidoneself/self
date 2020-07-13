package com.yimao.cloud.user.dto;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description 用户查询实体
 * @author hhf
 * @date 2018/12/3
 */
@Data
@ApiModel(description = "用户查询实体")
public class UserQueryDTO implements Serializable {

    private static final long serialVersionUID = -5331620762553837640L;

    @ApiModelProperty("用户ID")
    private Integer id;

    @ApiModelProperty("用户姓名")
    private String name;

    @ApiModelProperty("用户手机号")
    private String phone;

    /*@ApiModelProperty("用户身份")
    private String code;*/

    @ApiModelProperty("用户来源")
    private String origin;

    @ApiModelProperty("健康大使")
    private String ambassador;

    @ApiModelProperty("经销商")
    private String distributor;

    @ApiModelProperty("健康大使ID")
    private Integer ambassadorId;

    @ApiModelProperty("经销商ID")
    private Integer distributorId;

    @ApiModelProperty("经销商所在省")
    private String distributorProvince;

    @ApiModelProperty("经销商所在市")
    private String distributorCity;

    @ApiModelProperty("经销商所在区")
    private String distributorRegion;

    @ApiModelProperty("开始时间")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private String startTime;

    @ApiModelProperty("结束时间")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private String endTime;

   /* @ApiModelProperty(value = "页码 默认为1")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "分页步长 默认10")
    private Integer pageSize = 10;*/

}