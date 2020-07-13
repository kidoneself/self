package com.yimao.cloud.pojo.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * @description: 用户条件
 * @author: yu chunlei
 * @create: 2019-03-07 15:43:23
 **/
@Data
@ApiModel(description = "用户查询实体")
public class UserContidionDTO implements Serializable {

    private static final long serialVersionUID = 4773503020650213309L;
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

    @ApiModelProperty("来源端：1-健康e家公众号 2-经销商APP 3-净水设备")
    private String originTerminal;

    @ApiModelProperty("健康大使")
    private String ambassador;

    @ApiModelProperty("经销商")
    private String distributor;

    @ApiModelProperty("健康大使e家号")
    private Integer ambassadorNumber;

    @ApiModelProperty("经销商e家号")
    private Integer distributorId;

    @ApiModelProperty("经销商所在省")
    private String distributorProvince;

    @ApiModelProperty("经销商所在市")
    private String distributorCity;

    @ApiModelProperty("经销商所在区")
    private String distributorRegion;

    @ApiModelProperty("经销商账号")
    private String distributorAccount;


    @ApiModelProperty("用户身份")
    private Integer userType;

    @ApiModelProperty("开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String startTime;

    @ApiModelProperty("结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String endTime;

   /* @ApiModelProperty(value = "页码 默认为1")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "分页步长 默认10")
    private Integer pageSize = 10;*/

   private List<Integer> distributorIds;
}
