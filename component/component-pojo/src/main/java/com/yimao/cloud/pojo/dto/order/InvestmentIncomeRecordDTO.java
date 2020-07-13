package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 招商收益记录
 *
 * @author Liu Yi
 * @date 2019-1-17
 */
@Data
@ApiModel(description = "招商收益记录")
public class InvestmentIncomeRecordDTO   implements Serializable {
    private static final long serialVersionUID = -5459312852936119632L;

    @ApiModelProperty(value = "主键ID")
    private Integer id;
    @ApiModelProperty(value = "订单id")
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
    @ApiModelProperty(value = "经销商级别：350-微创版、650-个人版、950-企业版")
    private Integer distributorLevel;
    @ApiModelProperty(value = "升级经销商的等级类型：350-微创版、650-个人版、950-企业版")
    private Integer destDistributorLevel;
    @ApiModelProperty(value = "经销商姓名")
    private String distributorName;
    @ApiModelProperty(value = "经销商账户")
    private String distributorAccount;
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
    /*private String settlementSubjectName;*/
    private String paySubject;//付款主体(推荐人所在的区县级公司)
    @ApiModelProperty(value = "订单完成状态：0-未完成 1-(已完成)可结算 2-已结算 3-已失效")
    private Integer orderCompleteStatus;
    @ApiModelProperty(value = "订单完成时间",example = "2018-12-28 11:00:00")
    private Date orderCompleteTime;
    @ApiModelProperty(value = "结算时间")
    private Date settlementTime;
    @ApiModelProperty(value = "创建时间",example = "2018-12-28 11:00:00")
    private Date createTime;
    @ApiModelProperty(value = "创建人")
    private String creator;
    @ApiModelProperty(value = "更新时间",example = "2018-12-28 11:00:00")
    private Date updateTime;
    @ApiModelProperty(value = "更新人")
    private String updater;

    @ApiModelProperty(value = "经销商省")
    private String province;
    @ApiModelProperty(value = "经销商市")
    private String city;
    @ApiModelProperty(value = "经销商区")
    private String region;
    @ApiModelProperty(value = "支付方式/支付类型：1-微信；2-支付宝；3-POS机；4-转账；")
    private Integer payType;
    @ApiModelProperty(value = "支付时间")
    private Date payTime;
    @ApiModelProperty(value = "支付流水号/线上付款的流水号")
    private String tradeNo;

    List<InvestmentIncomeRecordPartDTO> investmentIncomeRecordPart;
}