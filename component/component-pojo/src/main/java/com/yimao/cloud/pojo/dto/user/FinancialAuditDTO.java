package com.yimao.cloud.pojo.dto.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Lizhqiang
 * @date 2018/12/21
 */
@Data
@ApiModel(description = "财务审核记录")
public class FinancialAuditDTO implements Serializable {

    private static final long serialVersionUID = 2430339047489701760L;

    private Integer id;

    @ApiModelProperty(value = "经销商订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long distributorOrderId;

    @ApiModelProperty(value = "财务审核状态")
    private Integer financialState;

    @ApiModelProperty(value = "审核不通过原因")
    private String cause;

    @ApiModelProperty(value = "财务审核人")
    private String financialAuditor;

    @ApiModelProperty(value = "财务审核时间")
    private Date auditTime;

    @ApiModelProperty(value = "支付凭证")
    private String payRecord;

    @ApiModelProperty(value = "支付时间")
    private Date payTime;

    @ApiModelProperty(value = "订单类型/0-注册、1-升级、2-续费")
    private Integer orderType;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "经销商账号")
    private String distributorAccount;

    @ApiModelProperty(value = "经销商升级类型ID")
    private Integer destRoleId;

    @ApiModelProperty(value = "经销商升级类型")
    private String destRoleName;

    @ApiModelProperty(value = "支付金额")
    private BigDecimal payMoney;

    @ApiModelProperty(value = "支付状态")
    private Integer payState;

    @ApiModelProperty(value = "原经销商类型Id")
    private Integer roleId;

    @ApiModelProperty(value = "原经销商类型")
    private String roleName;

    @ApiModelProperty(value = "支付方式")
    private Integer payType;

    @ApiModelProperty(value = "支付开始时间")
    private String payStartTime;

    @ApiModelProperty(value = "支付结束时间")
    private String payEndTime;

    @ApiModelProperty(value = "市")
    private String city;

    @ApiModelProperty(value = "经销商等级")
    private Integer level;


}
