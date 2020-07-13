package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 服务收益结算对象记录
 *
 * @author Liu Yi
 * @date 2019-1-17
 */
@ApiModel(description = "服务收益结算对象记录")
@Getter
@Setter
public class ServiceIncomeRecordPartDTO {

    @ApiModelProperty(value = "主键ID")
    private Integer id;
    @ApiModelProperty(position = 1, value = "服务收益记录主表id")
    private Integer recordId;

    @ApiModelProperty(position = 11, value = "收益主体id")
    private Integer subjectId;
    @ApiModelProperty(position = 12, value = "收益主体类型code")
    private String subjectCode;
    @ApiModelProperty(position = 13, value = "收益主体名称")
    private String subjectName;
    @ApiModelProperty(position = 14, value = "收益主体手机号")
    private String subjectPhone;
    @ApiModelProperty(position = 15, value = "收益主体省")
    private String subjectProvince;
    @ApiModelProperty(position = 16, value = "收益主体市")
    private String subjectCity;
    @ApiModelProperty(position = 17, value = "收益主体区县")
    private String subjectRegion;
    @ApiModelProperty(position = 18, value = "收益主体比例")
    private BigDecimal subjectRatio;
    @ApiModelProperty(position = 19, value = "收益主体金额")
    private BigDecimal subjectMoney;

    @ApiModelProperty(position = 21, value = "结算主体ID")
    private Integer settlementSubjectId;
    @ApiModelProperty(position = 22, value = "结算主体CODE")
    private String settlementSubjectCode;
    @ApiModelProperty(position = 23, value = "结算主体名称")
    private String settlementSubjectName;

    @ApiModelProperty(position = 31, value = "结算时间", example = "2018-12-28 11:00:00")
    private Date settlementTime;
    @ApiModelProperty(position = 100, value = "创建时间", example = "2018-12-28 11:00:00")
    private Date createTime;
    @ApiModelProperty(position = 101, value = "更新时间", example = "2018-12-28 11:00:00")
    private Date updateTime;

}