package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@ApiModel(description = "收益导出")
@Getter
@Setter
public class IncomeExportDTO {
    private Integer id;
    @ApiModelProperty(value = "主订单号")
    private Long mainOrderId;
    @ApiModelProperty(value = "子订单号")
    private Long orderId;
    @ApiModelProperty(value = "工单号")
    private String workOrderId;
    @ApiModelProperty(value = "订单来源")
    private String orderSource;
    @ApiModelProperty(value = "下单时间")
    private String createTime;
    @ApiModelProperty(value = "用户身份")
    private String userType;
    @ApiModelProperty(value = "用户id")
    private Integer userId;
    @ApiModelProperty(value = "用户名称")
    private String userName;
    @ApiModelProperty(value = "产品类型(三级名称)")
    private String productCategoryName;
    @ApiModelProperty(value = "产品类型(一级名称)")
    private String firstProductCategory;
    @ApiModelProperty(value = "型号范围")
    private String secondProductCategory;
    @ApiModelProperty(value = "产品型号")
    private String productCategory;
    @ApiModelProperty(value = "产品公司名称")
    private String productCompanyName;
    @ApiModelProperty(value = "计费方式")
    private String costName;
    @ApiModelProperty(value = "产品数量")
    private Integer productCount;
    @ApiModelProperty(value = "订单金额")
    private BigDecimal orderFee;
    @ApiModelProperty(value = "流水号")
    private String tradeNo;
    @ApiModelProperty(value = "支付时间")
    private String payTime;
    @ApiModelProperty(value = "支付方式")
    private String payType;
    @ApiModelProperty(value = "可分配金额")
    private BigDecimal settlementFee;
    @ApiModelProperty(value = "订单完成时间")
    private String orderCompleteTime;
    @ApiModelProperty(value = "结算月份")
    private String settlementMonth;
    @ApiModelProperty(value = "结算状态")
    private String settlementStatus;
    @ApiModelProperty(value = "安装工结算月份")
    private String engineerSettlementMonth;
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
    @ApiModelProperty(value = "经销商所在服务站公司名称")
    private String distributorStationName;
    @ApiModelProperty(value = "经销商所在服务站是否承包")
    private String distributorStationContract;
    @ApiModelProperty(value = "经销商所在服务站是否享受收益")
    private String distributorStationIncome;
    @ApiModelProperty(value = "是否拥有子账号")
    private String hasSubAccount;
    @ApiModelProperty(value = "子账号Id")
    private Integer subAccountId;
    @ApiModelProperty(value = "子账号")
    private String subAccount;
    @ApiModelProperty(value = "推荐人姓名")
    private String refereeName;
    @ApiModelProperty(value = "推荐人身份证号")
    private String refereeIdCard;
    @ApiModelProperty(value = "推荐人账号")
    private String refereeAccount;//
    @ApiModelProperty(value = "推荐人省")
    private String refereeProvince;
    @ApiModelProperty(value = "推荐人市")
    private String refereeCity;
    @ApiModelProperty(value = "推荐人区")
    private String refereeRegion;
    @ApiModelProperty(value = "推荐人所在服务站名称")
    private String refereeStationName;
    @ApiModelProperty(value = "会员用户Id")
    private Integer saleId;
    @ApiModelProperty(value = "会员用户身份证号码")
    private String saleIdCard;
    @ApiModelProperty(value = "会员手机号")
    private String saleMobile;
    @ApiModelProperty(value = "续费端")
    private String renewTerminal;

    @ApiModelProperty(value = "会员用户是否享受收益")
    private String userSaleFlag;
    @ApiModelProperty(value = "安装工姓名")
    private String engineerName;
    @ApiModelProperty(value = "安装工省")
    private String engineerProvince;
    @ApiModelProperty(value = "安装工市")
    private String engineerCity;
    @ApiModelProperty(value = "安装工区")
    private String engineerRegion;
    @ApiModelProperty(value = "安装工服务站名称")
    private String engineerStationName;
    @ApiModelProperty(value = "经销商收益")
    private BigDecimal distributorIncome;
    @ApiModelProperty(value = "经销商身份证号码")
    private String distributorIdCard;
    @ApiModelProperty(value = "分销商收益")
    private BigDecimal saleIncome;
    @ApiModelProperty(value = "推荐人收益")
    private BigDecimal refereeIncome;
    @ApiModelProperty(value = "区县级发起人收益")
    private BigDecimal regionSponsorIncome;
    @ApiModelProperty(value = "区县级发起人身份证号")
    private String regionSponsorIdCard;
    @ApiModelProperty(value = "服务站站长收益")
    private BigDecimal stationMasterIncome;
    @ApiModelProperty(value = "服务站站长身份证号")
    private String stationMasterIdCard;
    @ApiModelProperty(value = "区县级公司（经销商）收益")
    private BigDecimal regionDistributorIncome;
    @ApiModelProperty(value = "市级发起人收益")
    private BigDecimal citySponsorIncome;
    @ApiModelProperty(value = "市级合伙人收益")
    private BigDecimal cityPartnerIncome;
    @ApiModelProperty(value = "产品公司收益")
    private BigDecimal productCompanyIncome;
    @ApiModelProperty(value = "安装服务人员收益")
    private BigDecimal regionInstallerIncome;
    @ApiModelProperty(value = "安装服务人员身份证")
    private String regionInstallerIdCard;

    @ApiModelProperty(value = "承包人姓名")
    private String contractName;
    @ApiModelProperty(value = "承包人手机号")
    private String contractPhone;
    @ApiModelProperty(value = "承包人身份证")
    private String contractIdCard;

    @ApiModelProperty(value = "体检卡号")
    private String ticketNo;
    @ApiModelProperty(value = "使用时间")
    private String examDate;
    @ApiModelProperty(value = "用户手机号")
    private String userPhone;
    @ApiModelProperty(value = "体检人")
    private String medicalName;
    @ApiModelProperty(value = "体检人手机号")
    private String medicalPhone;
    @ApiModelProperty(value = "服务站名称")
    private String stationName;
    @ApiModelProperty(value = "服务站名称")
    private String stationCompanyName;
    @ApiModelProperty(value = "服务站省")
    private String stationProvince;
    @ApiModelProperty(value = "服务站市")
    private String stationCity;
    @ApiModelProperty(value = "服务站区")
    private String stationRegion;
    @ApiModelProperty(value = "服务站收益（服务）")
    private BigDecimal stationContractorService;
    @ApiModelProperty(value = "服务站收益（产品）")
    private BigDecimal stationContractorCompany;
    @ApiModelProperty(value = "服务站收益人身份证")
    private String stationContractorIdCard;
    @ApiModelProperty(value = "省级公司收益")
    private BigDecimal provincialCompanyIncome;
    @ApiModelProperty(value = "市级公司收益")
    private BigDecimal cityCompanyIncome;
    @ApiModelProperty(value = "区县级公司（安装工）收益")
    private BigDecimal engineerStationCompanyIncome;
    @ApiModelProperty(value = "续费单号")
    private String renewOrderId;
    @ApiModelProperty(value = "sn码")
    private String snCode;
    @ApiModelProperty(value = "续费时间")
    private String renewDate;
    @ApiModelProperty(value = "客户姓名")
    private String waterUserName;
    @ApiModelProperty(value = "客户联系方式")
    private String waterUserPhone;


    private List<ProductIncomeRecordPartDTO> recordPartList;
    private Integer incomeRuleId;
    private Integer stationId;
    private List<ServiceIncomeRecordPartDTO> serviceRecordPartList;


}
