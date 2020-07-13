package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(description = "提现查询参数")
public class WithdrawQueryDTO implements Serializable {

    private static final long serialVersionUID = 1010989542715729154L;

    @ApiModelProperty(value = "订单号")
    private Long orderId;
    @ApiModelProperty(value = "云平台工单号或者HRA体检号")
    private String refer;
    @ApiModelProperty(value = "提现单号")
    private String partnerTradeNo; // 提现单号过长 long ->string
    @ApiModelProperty(value = "用户id")
    private Integer userId;
    @ApiModelProperty(value = "用户身份")
    private Integer userType;
    @ApiModelProperty(value = "提现金额")
    private BigDecimal withdrawFee;
    @ApiModelProperty(value = "提现方式 微信")
    private Integer withdrawType;
    @ApiModelProperty(value = "提现流水号")
    private String paymentNo;
    @ApiModelProperty(value = "状态: 1-审核通过 2-审核不通过 3-待审核")
    private Integer status;
    @ApiModelProperty(value = "审核原因")
    private String auditReason;
    @ApiModelProperty(value = "申请提现开始时间")
    private String applyStartTime;
    @ApiModelProperty(value = "申请提现结束时间")
    private String applyEndTime;
    @ApiModelProperty(value = "用户手机号")
    private String phone;
    @ApiModelProperty(value = "用户姓名")
    private String userName;
    @ApiModelProperty(value = "产品公司名称")
    private String productCompanyName;
    @ApiModelProperty(value = "提现到账开始时间")
    private String paymentStartTime;
    @ApiModelProperty(value = "提现到账结束时间")
    private String paymentEndTime;
    @ApiModelProperty(value = "提现审核开始时间")
    private String auditStartTime;
    @ApiModelProperty(value = "提现审核结束时间")
    private String auditEndTime;
    @ApiModelProperty(value = "是否是公众号查询 0-否 1-是")
    private Boolean sign;
    @ApiModelProperty(value = "收益类型 1-产品收益 2-续费收益")
    private String incomeType;
    @ApiModelProperty(value = "提现审核结束时间")
    private String startTime;
    
    private String endTime;
    
}
