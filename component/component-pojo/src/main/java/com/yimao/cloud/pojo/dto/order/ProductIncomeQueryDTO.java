package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 产品、续费、服务收益查询内部DTO
 *
 * @author Liu Yi
 * @date 2018/12/19
 */
@Data
public class ProductIncomeQueryDTO implements Serializable {

    private static final long serialVersionUID = -6047391967687534960L;

    @ApiModelProperty(value = "产品公司ID")
    private Integer productCompanyId;
    @ApiModelProperty(value = "订单号")
    private String orderId;
    @ApiModelProperty(value = "产品类目id")
    private Integer productCategoryId;
    @ApiModelProperty(value = "用户id")
    private Integer userId;
    @ApiModelProperty(value = "收益类型：1-产品收益，2-续费收益，3-服务收益")
    private Integer incomeType;
    @ApiModelProperty(value = "用户类型1-经销商（微创版），2-经销商（个人版），3-分享用户（有健康大使），4-普通用户（无健康大使），5-企业版经销商（主账号），6-企业版经销商（子账号），7-分销商")
    private Integer userType;
    @ApiModelProperty(value = "经销商账户")
    private String distributorAccount;
    @ApiModelProperty(value = "经销商名称")
    private String distributorName;
    @ApiModelProperty(value = "经销商省")
    private String distributorProvince;
    @ApiModelProperty(value = "经销商市")
    private String distributorCity;
    @ApiModelProperty(value = "经销商区县")
    private String distributorRegion;
    @ApiModelProperty(value = "体检卡号（体检收益查询专用）")
    private String ticketNo;
    @ApiModelProperty(value = "结算时间")
    private String settlementTime;
    @ApiModelProperty(value = "结算状态 0-不可结算（订单未完成）；1-可结算（订单已完成）；2-已结算；3-已失效(退单) 4-暂停结算")
    private Integer settlementStatus;
    @ApiModelProperty(value = "安装工结算时间")
    private String engineerSettlementTime;
    @ApiModelProperty("订单完成开始时间")
    private String startTime;
    @ApiModelProperty("订单完成结束时间")
    private String endTime;
    @ApiModelProperty("支付完成开始时间")
    private String payStartTime;
    @ApiModelProperty("支付完成结束时间")
    private String payEndTime;
    @ApiModelProperty(value = "产品公司名称")
    private Integer productCompanyName;
    @ApiModelProperty(value = "产品模式：1-实物；2-虚拟；3-租赁；")
    private Integer productMode;
}