package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 服务收益记录
 *
 * @author Liu Yi
 * @date 2019-1-17
 */
@ApiModel(description = "服务收益记录")
@Getter
@Setter
public class ServiceIncomeRecordDTO {

    @ApiModelProperty(value = "主键ID")
    private Integer id;
    @ApiModelProperty(position = 1, value = "主订单id")
    private Long mainOrderId;
    @ApiModelProperty(position = 2, value = "订单id")
    private Long orderId;
    @ApiModelProperty(position = 3, value = "订单金额")
    private BigDecimal orderFee;
    @ApiModelProperty(position = 4, value = "体检卡号")
    private String ticketNo;
    @ApiModelProperty(position = 5, value = "HRA设备ID")
    private String deviceId;
    @ApiModelProperty(position = 6, value = "服务时间（评估卡使用时间）")
    private Date serviceTime;

    @ApiModelProperty(position = 11, value = "产品ID")
    private Integer productId;
    @ApiModelProperty(position = 12, value = "产品名称")
    private String productName;
    @ApiModelProperty(position = 13, value = "产品公司ID")
    private Integer productCompanyId;
    @ApiModelProperty(position = 14, value = "产品公司名称")
    private String productCompanyName;
    @ApiModelProperty(position = 15, value = "产品三级类目ID")
    private Integer productCategoryId;
    @ApiModelProperty(position = 16, value = "产品三级类目名称")
    private String productCategoryName;
    @ApiModelProperty(position = 17, value = "产品价格")
    private BigDecimal productPrice;

    @ApiModelProperty(position = 21, value = "用户id")
    private Integer userId;
    @ApiModelProperty(position = 22, value = "用户身份：用户等级：1-经销商（微创版），2-经销商（个人版），3-分享用户（有健康大使），4-普通用户（无健康大使），5-企业版经销商（主账号），6-企业版经销商（子账号），7-分销商")
    private Integer userType;
    @ApiModelProperty(position = 23, value = "用户类型名称")
    private String userTypeName;
    @ApiModelProperty(position = 24, value = "用户名称")
    private String userName;
    @ApiModelProperty(position = 25, value = "用户手机号")
    private String userPhone;

    @ApiModelProperty(position = 31, value = "经销商ID")
    private Integer distributorId;
    @ApiModelProperty(position = 32, value = "经销商姓名")
    private String distributorName;
    @ApiModelProperty(position = 33, value = "经销商类型名称")
    private String distributorTypeName;
    @ApiModelProperty(position = 34, value = "经销商账号")
    private String distributorAccount;
    @ApiModelProperty(position = 35, value = "经销商省")
    private String distributorProvince;
    @ApiModelProperty(position = 36, value = "经销商市")
    private String distributorCity;
    @ApiModelProperty(position = 37, value = "经销商区县")
    private String distributorRegion;

    @ApiModelProperty(position = 41, value = "服务站id")
    private Integer stationId;
    @ApiModelProperty(position = 42, value = "服务站名称")
    private String stationName;
    @ApiModelProperty(position = 43, value = "服务站省")
    private String stationProvince;
    @ApiModelProperty(position = 44, value = "服务站市")
    private String stationCity;
    @ApiModelProperty(position = 45, value = "服务站区")
    private String stationRegion;

    @ApiModelProperty(position = 51, value = "收益规则id")
    private Integer incomeRuleId;
    @ApiModelProperty(position = 52, value = "收益类型：1-产品收益，2-续费收益，3-服务收益，4-招商收益")
    private Integer incomeType;
    @ApiModelProperty(position = 53, value = "分配规则：1-按比例分配 2-按金额分配")
    private Integer allotType;
    @ApiModelProperty(position = 54, value = "结算金额")
    private BigDecimal settlementFee;

    @ApiModelProperty(position = 100, value = "创建时间")
    private Date createTime;
    @ApiModelProperty(position = 101, value = "更新时间")
    private Date updateTime;

    private List<ServiceIncomeRecordPartDTO> serviceIncomeRecordPartList;
}