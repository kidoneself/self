package com.yimao.cloud.pojo.export.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Liulongjie
 * @description 招商收益导出
 * @date 2019/8/30
 */
@Data
@ApiModel(description = "招商收益导出")
public class InvestmentIncomeExportDTO implements Serializable {
    private static final long serialVersionUID = -5459312852936119632L;

    @ApiModelProperty(value = "招商收益记录主表id")
    private Integer incomeRecordId;
    @ApiModelProperty(value = "订单号")
    private Long orderId;
    @ApiModelProperty(value = "订单类型：0-注册、1-升级、2-续费")
    private String distributorOrderTypeStr;
    @ApiModelProperty(value = "经销商类型：1-代理商,2-经销商, 3-经销商+代理商")
    private String distributorTypeStr;
    @ApiModelProperty(value = "实付款")
    private BigDecimal realPayment;
    @ApiModelProperty(value = "应收款")
    private BigDecimal receivableMoney;
    @ApiModelProperty(value = "多收款")
    private BigDecimal moreMoney;
    @ApiModelProperty(value = "付款主体")
    private String paySubject;
    @ApiModelProperty(value = "支付方式")
    private String payTypeStr;
    @ApiModelProperty(value = "支付时间")
    private String payTime;
    @ApiModelProperty(value = "流水号")
    private String trade;
    @ApiModelProperty(value = "订单完成时间")
    private Date orderCompletionTime;
    private String orderCompletionTimeStr;
    @ApiModelProperty(value = "结算时间")
    private Date settlementTime;
    //结算月份
    private String settlementMonth;
    @ApiModelProperty(value = "升级后经销商类型")
    private String destDistributorTypeStr;
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
    @ApiModelProperty(value = "推荐人归属区域")
    private String refereeDistrict;
    @ApiModelProperty(value = "推荐人服务站公司")
    private String refereeStationCompany;
    @ApiModelProperty(value = "推荐人收益")
    private String refereeIncome;
    @ApiModelProperty(value = "智慧助理收益")
    private String wisdomAssistantIncome;
    @ApiModelProperty(value = "区县级公司（推荐人）收益")
    private String refereeStationCompanyIncome;
    @ApiModelProperty(value = "翼猫总部收益")
    private String yiMaoHQIncome;
    //经销商id
    private Integer distributorId;

}