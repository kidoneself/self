package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(description = "订单提现订单实体类")
public class OrderWithdrawDTO {

    @ApiModelProperty(value = "主订单号")
    private Long mainOrderId;

    @ApiModelProperty(value = "子订单号")
    private String subOrderId;

    @ApiModelProperty(value = "产品类目")
    private String productCategory;

    @ApiModelProperty(value = "产品Id")
    private String productId;

    @ApiModelProperty(value = "产品数量")
    private Integer productNum;

    @ApiModelProperty(value = "订单金额")
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "支付方式：1-微信；2-支付宝；3-POS机；4-转账")
    private  String payType;

    @ApiModelProperty(value = "订单完成时间")
    private Date orderTime;

    @ApiModelProperty(value = "可提现金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "用户身份")
    private String userType;

    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    @ApiModelProperty(value = "用户姓名")
    private String name;

    @ApiModelProperty(value = "用户手机号")
    private String phone;

    @ApiModelProperty(value = "用户Id")
    private String userId;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "收益类型")
    private String incomeType;

    @ApiModelProperty(value = "提现审核状态")
    private String auditStatus;

    /**
     * 这个其实是订单完成状态[在这里当做提现状态]
     */
    @ApiModelProperty(value = "提现状态:0-未完成 1-(已完成)可结算 2-已结算 3-已失效(退单) 4-暂停结算，默认0 ")
    private Integer status;


}
