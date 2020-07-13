package com.yimao.cloud.pojo.vo.order;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 招商收益记录
 *
 * @author hhf
 * @date 2019/2/26
 */
@Data
@ApiModel(description = "招商收益记录")
public class InvestmentIncomeVO implements Serializable {

    private static final long serialVersionUID = -4946612451883902947L;
    @ApiModelProperty(value = "主键ID")
    private Integer id;
    @ApiModelProperty(value = "订单id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderId;
    @ApiModelProperty(value = "用户id")
    private Integer userId;
    @ApiModelProperty(value = "用户名称")
    private String userName;
    @ApiModelProperty(value = "用户手机号")
    private String userPhone;
    @ApiModelProperty(value = "经销商id")
    private Integer distributorId;
    @ApiModelProperty(value = "经销商订单类型：0-注册、1-升级、2-续费")
    private Integer distributorOrderType;
    @ApiModelProperty(value = "经销商的等级类型：50-体验版本、350-微创版、650-个人版、950-企业版")
    private Integer distributorLevel;
    @ApiModelProperty(value = "升级经销商的等级类型：350-微创版、650-个人版、950-企业版")
    private Integer destDistributorLevel;
    @ApiModelProperty(value = "经销商姓名")
    private String distributorName;
    @ApiModelProperty(value = "经销商账户")
    private String distributorAccount;
    @ApiModelProperty(value = "经销商省")
    private String distributorProvince;
    @ApiModelProperty(value = "经销商市")
    private String distributorCity;
    @ApiModelProperty(value = "经销商区")
    private String distributorRegion;
    @ApiModelProperty(value = "推荐人经销商姓名")
    private String refereeName;
    @ApiModelProperty(value = "推荐人经销商账户")
    private String refereeAccount;
    @ApiModelProperty(value = "收益规则id")
    private Integer incomeRuleId;
    @ApiModelProperty(value = "收益规则code")
    private String incomeSubjectCode;
    @ApiModelProperty(value = "分配规则：1-按比例分配 2-按金额分配")
    private Integer allotType;
    @ApiModelProperty(value = "实付款")
    private BigDecimal realPayment;
    @ApiModelProperty(value = "应收款")
    private BigDecimal receivableMoney;
    @ApiModelProperty(value = "多收款")
    private BigDecimal moreMoney;
    @ApiModelProperty(value = "付款主体(推荐人所在的区县级公司)")
    private String paySubject;
    @ApiModelProperty(value = "订单完成时间",example = "2018-12-28 11:00:00")
    private Date orderCompleteTime;
    @ApiModelProperty(value = "结算时间")
    private String settlementTime;

}
