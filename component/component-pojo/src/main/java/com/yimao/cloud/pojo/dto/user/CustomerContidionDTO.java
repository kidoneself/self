package com.yimao.cloud.pojo.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Transient;
import java.io.Serializable;

/**
 * @description: 客户查询实体
 * @author: yu chunlei
 * @create: 2019-08-13 15:34:20
 **/
@Data
@ApiModel(description = "客户查询实体")
public class CustomerContidionDTO implements Serializable {

    private static final long serialVersionUID = -5464452797674971146L;
    @ApiModelProperty(position = 1, value = "水机客户表主键ID")
    private Integer id;
    @ApiModelProperty("水机客户姓名")
    private String name;
    @ApiModelProperty("水机客户手机号")
    private String phone;
    @ApiModelProperty("客户类型")
    private String type;
    @ApiModelProperty("公司名称")
    private String companyName;
    @ApiModelProperty("来源端：1-健康e家公众号 2-经销商APP 3-净水设备")
    private String originTerminal;
    @ApiModelProperty("所在省")
    private String province;
    @ApiModelProperty("所在市")
    private String city;
    @ApiModelProperty("所在区")
    private String region;
    @ApiModelProperty("经销商e家号")
    private String distributorUserId;
    @ApiModelProperty("经销商账户")
    private String distributorAccount;
    @ApiModelProperty("经销商姓名")
    private String distributorName;
    @ApiModelProperty("开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String startTime;
    @ApiModelProperty("结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String endTime;
    @Transient
    private Integer pageSize;
    @Transient
    private Long queryId;
    @Transient
    private String createTime;

}
