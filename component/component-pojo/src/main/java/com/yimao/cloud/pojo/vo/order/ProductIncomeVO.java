package com.yimao.cloud.pojo.vo.order;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 产品收益记录
 *
 * @author Liu Yi
 * @date 2019-1-29
 */
@Data
@ApiModel(description = "产品（产品、续费、服务）收益记录")
public class ProductIncomeVO  implements Serializable {
    private static final long serialVersionUID = -6047391967687534960L;
    @ApiModelProperty(value = "主键ID")
    @JsonSerialize(using=ToStringSerializer.class)
    private Integer id;
    @ApiModelProperty(value = "主订单id")
    @JsonSerialize(using=ToStringSerializer.class)
    private Long mainOrderId;
    @ApiModelProperty(value = "子订单id")
    @JsonSerialize(using=ToStringSerializer.class)
    private Long orderId;
    @ApiModelProperty(value = "续费单号")
    private String renewOrderId;
    @ApiModelProperty(value = "用户id")
    private Integer userId;
    @ApiModelProperty(value = "用户名称")
    private String userName;
    @ApiModelProperty(value = "用户类型：1-经销商（微创版），2-经销商（个人版），3-分享用户（有健康大使），4-普通用户（无健康大使），5-企业版经销商（主账号），6-企业版经销商（子账号），7-会员用户")
    private Integer userType;
    @ApiModelProperty(value = "产品id")
    private Integer productId;
    @ApiModelProperty(value = "产品公司id")
    private Integer productCompanyId;
    @ApiModelProperty(value = "产品分类(类目)")
    private String productCategoryName;
    @ApiModelProperty(value = "经销商id")
    private Integer distributorId;
    @ApiModelProperty(value = "经销商名称")
    private String distributorName;
    @ApiModelProperty(value = "经销商账户")
    private String distributorAccount;
    @ApiModelProperty(value = "经销商省")
    private String subjectProvince;
    @ApiModelProperty(value = "经销商市")
    private String subjectCity;
    @ApiModelProperty(value = "经销商县")
    private String subjectRegion;
    @ApiModelProperty(value = "产品价格")
    private BigDecimal productPrice;
    @ApiModelProperty(value = "产品名称")
    private String productName;
    @ApiModelProperty(value = "收益类型：1-产品收益，2-续费收益，3-服务收益，4-招商收益")
    private Integer incomeType;
   /* @ApiModelProperty(value = "订单完成状态：0-未完成 1-(已完成)可结算 2-已结算 3-已失效")
    private Integer orderCompleteStatus;*/
    @ApiModelProperty(value = "订单完成时间", example = "2018-12-28 11:00:00")
    private Date orderCompleteTime;

    @ApiModelProperty(value = "安装工收益结算月份", example = "2018-12")
    private String engineerSettlementTime;

    @ApiModelProperty(value = "订单结算月份", example = "2018-12")
    private String orderSettlementTime;

    @ApiModelProperty(value = "可结算金额")
    private BigDecimal allocationMoney;
    @ApiModelProperty(value = "产品可结算数量")
    private Integer settlementAmount;
    @ApiModelProperty(value = "经销商省")
    private String distributorProvince;
    @ApiModelProperty(value = "经销商市")
    private String distributorCity;
    @ApiModelProperty(value = "经销商区县")
    private String distributorRegion;

    @ApiModelProperty(value = "总费用金额")
    private BigDecimal amountFee;
    @ApiModelProperty(value = "开户费")
    private BigDecimal openAccountFee;
    @ApiModelProperty(value = "运费")
    private BigDecimal logisticsFee;
    @ApiModelProperty("订单支付时间")
    private String payTime;

    @ApiModelProperty(value = "体检卡号")
    private String ticketNo;

    @ApiModelProperty(value = "sn码")
    private String snCode;

    @ApiModelProperty(value = "收益结算状态:0-未完成 1-(已完成)可结算 2-已结算 3-退单 4-暂停结算")
    private Integer status;
    


}