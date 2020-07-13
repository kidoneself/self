package com.yimao.cloud.pojo.dto.user;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
* @description 经销商订单详情
* @author Liu Yi
* @date 9:39 2019/8/20
**/

@Data
public class DistributorOrderAllInfoDTO {
    @ApiModelProperty(value = "Id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @ApiModelProperty(value = "支付的金额")
    private BigDecimal price;
    @ApiModelProperty(value = "订单来源/0-H5页面、1-经销商app、2-翼猫业务系统")
    private Integer orderSouce;
    @ApiModelProperty(value = "经销商账号")
    private String distributorAccount;
    @ApiModelProperty(value = "支付状态/0-未支付、1-已支付、3-待审核")
    private Integer payState;
    @ApiModelProperty(value = "订单类型/0-注册、1-升级、2-续费")
    private Integer orderType;
    @ApiModelProperty(value = "支付方式/0-支付宝、1-微信、2-pos机、4-转账")
    private Integer payType;
    @ApiModelProperty(value = "订单状态/0-待审核、1-已完成、2待付款")
    private Integer orderState;
    @ApiModelProperty(value = "支付时间")
    private Date payTime;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    @ApiModelProperty(value = "完成时间")
    private Date completionTime;
    @ApiModelProperty(value = "支付凭证")
    private String payRecord;
    @ApiModelProperty(value = "支付流水号")
    private String payNumber;
    @ApiModelProperty(value = "经销商ID")
    private Integer distributorId;
    @ApiModelProperty(value = "财务审核状态/0-未审核、1-审核通过、2-审核不通过、3-无需审核")
    private Integer financialState;
    @ApiModelProperty(value = "企业审核状态/0-未审核、1-审核通过、2-审核不通过、3-无需审核")
    private Integer enterpriseState;
    @ApiModelProperty(value = "原经销商类型")
    private String roleName;
    @ApiModelProperty(value = "现经销商类型")
    private String destRoleName;
    @ApiModelProperty(value = "地区id")
    private Integer areaId;
    /**
     * 经销商信息
     */
    private DistributorDTO distributor;
    /**
     * 推荐人信息
     */
    private DistributorDTO referrerDistributor;
    /**
     * 合同信息
     */
    private DistributorProtocolDTO distributorProtocol;
    /**
     * 财务审核
     */
    private FinancialAuditDTO financialAudit;
    /**
     * 企业审核
     */
    private UserCompanyApplyDTO userCompany;
}
