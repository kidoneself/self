package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@ApiModel(description = "提现导出")
public class WithdrawExportDTO {

    @ApiModelProperty(value = "子提现订单")
    private Long id;
    @ApiModelProperty(value = "分配主表ID")
    private Integer recordId;
    @ApiModelProperty(value = "主提现单号")
    private Long mainPartnerTradeNo;
    @ApiModelProperty(value = "子订单号")
    private String orderId;
    @ApiModelProperty(value = "主订单号")
    private Long mainOrderId;
    @ApiModelProperty(value = "工单号")
    private String workOrderId;
    @ApiModelProperty(value = "提现金额")
    private BigDecimal withdrawFee;
    @ApiModelProperty(value = "申请提现时间")
    private String applyTime;
    @ApiModelProperty(value = "提现流水号")
    private String paymentNo;
    @ApiModelProperty(value = "产品数量")
    private Integer productNum;
    @ApiModelProperty(value = "状态: 1-审核通过 2-审核不通过 3-待审核")
    private String status;
    @ApiModelProperty(value = "提现方式")
    private String withdrawType;
    @ApiModelProperty(value = "商品类型（大类）:1实物商品，2电子卡券，3租赁商品")
    private Integer productType;
    @ApiModelProperty(value = "产品类型(三级名称)")
    private String productCategoryName;
    @ApiModelProperty(value = "产品类型(一级)")
    private String firstProductCategory;
    @ApiModelProperty(value = "产品范围")
    private String secondProductCategory;
    @ApiModelProperty(value = "产品型号")
    private String productCategory;
    @ApiModelProperty(value = "产品公司名称")
    private String productCompanyName;
    @ApiModelProperty(value = "订单金额")
    private BigDecimal orderFee;
    @ApiModelProperty(value = "支付时间")
    private String payTime;
    @ApiModelProperty(value = "订单完成时间")
    private String orderCompleteTime;
    @ApiModelProperty(value = "支付方式：1-微信；2-支付宝")
    private String payType;
    @ApiModelProperty(value = "提现到账时间")
    private String paymentTime;

    @ApiModelProperty(value = "用户身份")
    private String userType;
    @ApiModelProperty(value = "用户id")
    private Integer userId;
    @ApiModelProperty(value = "用户名称")
    private String userName;
    @ApiModelProperty(value = "用户昵称") // ?
    private String nickName;
    @ApiModelProperty(value = "用户手机号")
    private String phone;
    @ApiModelProperty(value = "经销商身份")
    private String distributorType;
    @ApiModelProperty(value = "经销商Id")
    private Integer distributorId;
    @ApiModelProperty(value = "经销商名称")
    private String distributorName;
    @ApiModelProperty(value = "经销商账户")
    private String distributorAccount;
    @ApiModelProperty(value = "经销商省")
    private String distributorProvince;
    @ApiModelProperty(value = "经销商市")
    private String distributorCity;
    @ApiModelProperty(value = "经销商区")
    private String distributorRegion;
    @ApiModelProperty(value = "是否拥有子账号")
    private String hasSubAccount;
    @ApiModelProperty(value = "子账号Id")
    private Integer subAccountId;
    @ApiModelProperty(value = "子账号")
    private String subAccount;
    @ApiModelProperty(value = "推荐人姓名")
    private String refereeName;
    @ApiModelProperty(value = "推荐人账号")
    private String refereeAccount;//
    @ApiModelProperty(value = "推荐人省")
    private String refereeProvince;
    @ApiModelProperty(value = "推荐人市")
    private String refereeCity;
    @ApiModelProperty(value = "推荐人区")
    private String refereeRegion;
    @ApiModelProperty(value = "审核时间")
    private String auditTime;
    @ApiModelProperty(value = "审核人")
    private String updater;
    @ApiModelProperty(value = "结算主体省市区")
    private String subjectArea;
    @ApiModelProperty(value = "结算主体公司名称")
    private String subjectCompany;
    @ApiModelProperty(value = "付款说明")
    private String payInstructions;

    @ApiModelProperty(value = "收益类型")
    private String incomeType;
}
