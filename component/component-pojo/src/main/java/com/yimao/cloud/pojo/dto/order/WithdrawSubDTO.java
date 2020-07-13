package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Chen Hui Yang
 * @date 2018/12/26
 * 子提现实体类
 */
@Data
@ApiModel(description = "子提现订单实体类")
public class WithdrawSubDTO {

    @ApiModelProperty(value = "子提现订单")
    private String id;
    @ApiModelProperty(value = "主提现单号")
    private String mainPartnerTradeNo;
    @ApiModelProperty(value = "产品类型")
    private Integer productCategory;
    @ApiModelProperty(value = "用户id")
    private Integer userId;
    @ApiModelProperty(value = "用户身份")
    private Integer userType;
    @ApiModelProperty(value = "提现金额")
    private BigDecimal withdrawFee;
    @ApiModelProperty(value = "实际提现金额")
    private BigDecimal withdrawRealFee;
    @ApiModelProperty(value = "提现时间")
    private Date withdrawTime;
    @ApiModelProperty(value = "提现方式")
    private Integer withdrawType;
    @ApiModelProperty(value = "提现端  1-公众号  2-小程序  3-经销商APP 4-ios  默认公众号")
    private Integer terminal;
    @ApiModelProperty(value = "提现成功标志 1-成功 2-失败")
    private Integer withdrawFlag;
    @ApiModelProperty(value = "提现来源")
    private String origCash;
    @ApiModelProperty(value = "提现去向")
    private String destCash;
    @ApiModelProperty(value = "手续费")
    private BigDecimal formalitiesFee;
    @ApiModelProperty(value = "提现流水号")
    private String paymentNo;
    @ApiModelProperty(value = "提现到账时间")
    private Date paymentTime;
    @ApiModelProperty(value = "申请提现时间")
    private Date applyTime;
    @ApiModelProperty(value = "状态: 1-审核通过 2-审核不通过 3-待审核")
    private Integer status;
    @ApiModelProperty(value = "审核时间(财务)")
    private Date auditTime;
    @ApiModelProperty(value = "审核原因")
    private String auditReason;
    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "申请提现开始时间")
    private Date applyStartTime;
    @ApiModelProperty(value = "申请提现结束时间")
    private Date applyEndTime;
    @ApiModelProperty(value = "用户手机号")
    private String phone;
    @ApiModelProperty(value = "用户名称")
    private String userName;
    @ApiModelProperty(value = "用户昵称")
    private String nickName;
    @ApiModelProperty(value = "子提现订单集合")
    private List<Long> tradeNoList;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
    @ApiModelProperty(value = "更新人")
    private String updater;

    @ApiModelProperty(value = "产品公司Id")
    private Integer productCompanyId;
    @ApiModelProperty(value = "产品公司名称")
    private String productCompanyName;

    @ApiModelProperty(value = "子订单号")
    private String orderId;
    @ApiModelProperty(value = "主订单号")
    private String mainOrderId;

    @ApiModelProperty(value = "收益类型")
    private String incomeType;

}
