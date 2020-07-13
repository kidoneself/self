package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 财务审核导出信息
 *
 * @author liulongjie
 * @date 2019/8/26
 */
@Data
public class FinancialAuditExportDTO {

    @ApiModelProperty(value = "订单号")
    private Long orderId;

    @ApiModelProperty(value = "地区")
    private String address;

    @ApiModelProperty(value = "订单类型")
    private String orderTypeStr;

    @ApiModelProperty(value = "经销商账户")
    private String distributorAccount;

    @ApiModelProperty(value = "经销商姓名")
    private String realName;

    @ApiModelProperty(value = "经销商类型")
    private String distributorType;

    @ApiModelProperty(value = "升级经销商类型")
    private String destDistributorType;

    @ApiModelProperty(value = "性别：1-男；2-女；")
    private String sexStr;

    @ApiModelProperty(value = "身份证号")
    private String idCard;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "推荐人姓名")
    private String recommendName;

    @ApiModelProperty(value = "服务站公司名称")
    private String stationCompanyName;

    @ApiModelProperty(value = "支付方式/1-微信、2-支付宝、3-pos机、4-转账")
    private String payTypeStr;

    @ApiModelProperty(value = "支付状态")
    private String payStateStr;

    @ApiModelProperty(value = "支付时间")
    private String payTimeStr;

    @ApiModelProperty(value = "支付的金额")
    private BigDecimal payMoney;

    @ApiModelProperty(value = "订单状态/0-待审核、1-已完成、2-待付款")
    private String orderStateStr;

    @ApiModelProperty(value = "财务审核状态/0-未审核、1-审核通过、2-审核不通过、3-无需审核")
    private String financialStateStr;


    //经销商所在省
    private String province;

    //经销商所在市
    private String city;

    //经销商所在区
    private String region;


}
