package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 产品收益记录
 *
 * @author Liu Yi
 * @date 2019-1-17
 */
@ApiModel(description = "产品收益记录")
@Getter
@Setter
public class ProductIncomeRecordDTO {

    @ApiModelProperty(value = "主键ID")
    private Integer id;
    @ApiModelProperty(position = 1, value = "主订单id")
    private Long mainOrderId;
    @ApiModelProperty(position = 2, value = "子订单id")
    private String orderId;
    @ApiModelProperty(position = 3, value = "订单金额")
    private BigDecimal orderFee;

    @ApiModelProperty(position = 5, value = "产品ID")
    private Integer productId;
    @ApiModelProperty(position = 6, value = "产品名称")
    private String productName;
    @ApiModelProperty(position = 7, value = "产品公司ID")
    private Integer productCompanyId;
    @ApiModelProperty(position = 8, value = "产品公司名称")
    private String productCompanyName;
    @ApiModelProperty(position = 9, value = "产品三级类目ID")
    private Integer productCategoryId;
    @ApiModelProperty(position = 10, value = "产品三级类目名称")
    private String productCategoryName;
    @ApiModelProperty(position = 11, value = "产品一级类目ID")
    private Integer productOneCategoryId;
    @ApiModelProperty(position = 12, value = "产品一级类目名称")
    private String productOneCategoryName;
    @ApiModelProperty(position = 13, value = "产品二级类目ID")
    private Integer productTwoCategoryId;
    @ApiModelProperty(position = 14, value = "产品二级类目名称")
    private String productTwoCategoryName;

    @ApiModelProperty(position = 15, value = "用户id")
    private Integer userId;
    @ApiModelProperty(position = 16, value = "用户类型")
    private Integer userType;
    @ApiModelProperty(position = 17, value = "用户类型名称")
    private String userTypeName;
    @ApiModelProperty(position = 18, value = "用户名称")
    private String userName;
    @ApiModelProperty(position = 19, value = "用户手机号")
    private String userPhone;

    @ApiModelProperty(position = 21, value = "经销商id")
    private Integer distributorId;
    @ApiModelProperty(position = 22, value = "经销商姓名")
    private String distributorName;
    @ApiModelProperty(position = 23, value = "经销商类型名称")
    private String distributorTypeName;
    @ApiModelProperty(position = 24, value = "经销商账号")
    private String distributorAccount;
    @ApiModelProperty(position = 25, value = "经销商省")
    private String distributorProvince;
    @ApiModelProperty(position = 26, value = "经销商市")
    private String distributorCity;
    @ApiModelProperty(position = 27, value = "经销商区县")
    private String distributorRegion;

    @ApiModelProperty(position = 31, value = "收益规则id")
    private Integer incomeRuleId;
    @ApiModelProperty(position = 32, value = "收益类型：1-产品收益，2-续费收益，3-服务收益，4-招商收益")
    private Integer incomeType;
    @ApiModelProperty(position = 33, value = "分配规则：1-按比例分配 2-按金额分配")
    private Integer allotType;
    @ApiModelProperty(position = 34, value = "结算金额")
    private BigDecimal settlementFee;
    @ApiModelProperty(position = 35, value = "产品可结算数量")
    private Integer settlementAmount;
    @ApiModelProperty(position = 36, value = "收益结算状态：0-不可结算（订单未完成）；1-可结算（订单已完成）；2-已结算；3-已失效；")
    private Integer status;
    @ApiModelProperty(position = 37, value = "订单完成时间")
    private Date orderCompleteTime;

    @ApiModelProperty(position = 100, value = "创建时间")
    private Date createTime;
    @ApiModelProperty(position = 101, value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(position = 102, value = "结算月份")
    private String settlementMonth;
    @ApiModelProperty(position = 103, value = "安装工收益结算月份")
    private String engineerSettlementMonth;

    private List<ProductIncomeRecordPartDTO> productIncomeRecordPartList;

}