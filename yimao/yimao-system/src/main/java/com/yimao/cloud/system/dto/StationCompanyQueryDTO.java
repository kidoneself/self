package com.yimao.cloud.system.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author Lizhqiang
 * @date 2019/1/21
 */
@Data
public class StationCompanyQueryDTO {
    @ApiModelProperty(value = "服务站公司id")
    private Integer id;
    @ApiModelProperty(value = "服务站公司名称")
    private String name;
    @ApiModelProperty(value = "服务站公司联系人")
    private String person;
    @ApiModelProperty(value = "服务站公司联系电话")
    private String phone;
    @ApiModelProperty(value = "是否云签  0-未云签，1-已云签")
    private Integer signUp;
    @ApiModelProperty(value = "服务站公司服务省")
    private String province;
    @ApiModelProperty(value = "服务站公司服务市")
    private String city;
    @ApiModelProperty(value = "服务站公司服务区")
    private String region;
    @ApiModelProperty(value = "上线状态：0-上线，1-未上线，默认0")
    private Integer onlineState;
    @ApiModelProperty(value = "服务站公司上线开始时间")
    private Date onlineBeginTime;
    @ApiModelProperty(value = "服务站公司上线结束时间")
    private Date onlineEndTime;
}
