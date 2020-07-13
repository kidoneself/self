package com.yimao.cloud.pojo.dto.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author liulongjie
 * @date 2019/8/26
 */
@Data
@ApiModel(description = "经销商订单审核记录")
public class DistributorOrderAuditRecordExportDTO {
    @ApiModelProperty(value = "订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderId;
    @ApiModelProperty(value = "订单类型/0-注册、1-升级、2-续费")
    private String orderType;
    @ApiModelProperty(value = "姓名")
    private String name;
    @ApiModelProperty(value = "经销商账号")
    private String distributorAccount;
    @ApiModelProperty(value = "经销商类型")
    private String roleName;
    @ApiModelProperty(value = "升级经销商类型")
    private String destRoleName;
    @ApiModelProperty(value = "价格")
    private BigDecimal price;
    @ApiModelProperty(value = "支付方式")
    private String payType;
    @ApiModelProperty(value = "支付时间")
    private String payTime;
    @ApiModelProperty(value = "审核类型/0-财务、 1-企业")
    private String auditType;
    @ApiModelProperty(value = "审核状态0-未审核、1-审核通过、2-审核不通过、3-无需审核")
    private String status;
    @ApiModelProperty(value = "审核不通过原因")
    private String cause;
    @ApiModelProperty(value = "审核人")
    private String auditor;
    @ApiModelProperty(value = "审核时间")
    private String auditTime;

//    @ApiModelProperty(value = "订单号")
//    private Long orderId;
//
//    @ApiModelProperty(value = "地区")
//    private String address;
//
//    @ApiModelProperty(value = "订单类型")
//    private String orderTypeStr;
//
//    @ApiModelProperty(value = "经销商账户")
//    private String distributorAccount;
//
//    @ApiModelProperty(value = "经销商姓名")
//    private String realName;
//
//    @ApiModelProperty(value = "经销商类型")
//    private String distributorType;
//
//    @ApiModelProperty(value = "升级经销商类型")
//    private String destDistributorType;
//
//    @ApiModelProperty(value = "性别：1-男；2-女；")
//    private String sexStr;
//
//    @ApiModelProperty(value = "身份证号")
//    private String idCard;
//
//    @ApiModelProperty(value = "手机号")
//    private String phone;
//
//    @ApiModelProperty(value = "推荐人姓名")
//    private String recommendName;
//
//    @ApiModelProperty(value = "服务站公司名称")
//    private String stationCompanyName;
//
//    @ApiModelProperty(value = "支付方式/0-支付宝、1-微信、2-pos机、4-转账")
//    private String payTypeStr;
//
//    @ApiModelProperty(value = "支付状态")
//    private String payStateStr;
//
//    @ApiModelProperty(value = "支付时间")
//    private Date payTime;
//    private String payTimeStr;
//
//    @ApiModelProperty(value = "支付的金额")
//    private BigDecimal payMoney;
//
//    @ApiModelProperty(value = "订单状态/0-待审核、1-已完成、2-待付款")
//    private String orderStateStr;
//
//    @ApiModelProperty(value = "用户合同签署状态 0-未完成，1-已完成")
//    private String userSignStateStr;
//
//    @ApiModelProperty(value = "服务站签署状态")
//    private String stationSignStateStr;
//
//    @ApiModelProperty(value = "翼猫签署状态")
//    private String ymSignStateStr;
//
//    @ApiModelProperty(value = "财务审核状态")
//    private String financialStateStr;
//
//    @ApiModelProperty(value = "财务审核人")
//    private String financialAuditor;
//
//    @ApiModelProperty(value = "财务审核时间")
//    private Date financialAuditTime;
//    private String financialAuditTimeStr;
//
//    @ApiModelProperty(value = "合同是否创建/是 ，否")
//    private String protocolCreatedState;
//
//    @ApiModelProperty(value = "创建时间")
//    private Date protocolCreateTime;
//    private String protocolCreateTimeStr;
//
//    @ApiModelProperty("财务审核次数")
//    private Integer financialAuditCount;
//
//    @ApiModelProperty("有效剩余时间")
//    private Integer periodValidity;
//
//    @ApiModelProperty("流水号")
//    private String tradeNo;
//
//    @ApiModelProperty("订单来源")
//    private String orderSourceStr;
//
//    @ApiModelProperty("企业审核状态")
//    private String enterpriseStateStr;
//
//    @ApiModelProperty(value = "企业审核人")
//    private String companyAuditor;
//
//    @ApiModelProperty(value = "企业审核时间")
//    private Date companyAuditTime;
//    private String companyAuditTimeStr;
//
//    @ApiModelProperty(value = "企业名称")
//    private String companyName;
//
//    @ApiModelProperty(value = "完成时间")
//    private Date completionTime;
//    private String completionTimeStr;
//
//    //审核类型
//    private Integer auditType;
//
//    //经销商所在省
//    private String province;
//
//    //经销商所在市
//    private String city;
//
//    //经销商所在区
//    private String region;






}
