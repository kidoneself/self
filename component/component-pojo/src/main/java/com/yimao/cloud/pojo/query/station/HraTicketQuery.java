package com.yimao.cloud.pojo.query.station;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Liu long jie
 * @description 预约评估列表查询条件
 * @date 2019-12-26
 **/


@Getter
@Setter
@ApiModel(description = "预约评估列表查询条件")
public class HraTicketQuery extends BaseQuery implements Serializable {

    private static final long serialVersionUID = 4730339047651401753L;

    @ApiModelProperty(value = "体检卡号")
    private String ticketNo;
    @ApiModelProperty(value = "用户来源")
    private Integer userSource;
    @ApiModelProperty(value = "用户e家号")
    private Integer userId;
    @ApiModelProperty(value = "姓名")
    private String name;
    @ApiModelProperty(value = "手机号")
    private String mobile;
    @ApiModelProperty(value = "预约体检时间（始）/实际体检日期（始）")
    private String beginTime;
    @ApiModelProperty(value = "预约体检时间（终）/实际体检日期（终）")
    private String endTime;
    @ApiModelProperty(value = "体检报告是否上传")
    private Integer hasUpload;
    @ApiModelProperty(value = "体检卡型号")
    private String ticketType;
    @ApiModelProperty(value = "预约状态  1-预约中 2-预约到期")
    private Integer reserveStatus;

}
