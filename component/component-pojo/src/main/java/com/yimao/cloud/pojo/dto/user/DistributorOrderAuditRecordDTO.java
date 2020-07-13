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
 * @date 2018/12/17
 */
@Data
@ApiModel(description = "经销商订单审核记录")
public class DistributorOrderAuditRecordDTO implements Serializable {

    private static final long serialVersionUID = 2430339047489701780L;

    @ApiModelProperty(value = "Id")
    private Integer id;
    @ApiModelProperty(value = "经销商Id")
    private Integer distributorId;
    @ApiModelProperty(value = "经销商角色ID(注册经销商或原经销商)")
    private Integer roleId;
    @ApiModelProperty(value = "经销商角色名称")
    private String roleName;
    @ApiModelProperty(value = "升级经销商角色id")
    private Integer destRoleId;
    @ApiModelProperty(value = "升级经销商角色名称")
    private String destRoleName;
    @ApiModelProperty(value = "经销商订单ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long distributorOrderId;
    @ApiModelProperty(value = "审核状态0-未审核、1-审核通过、2-审核不通过、3-无需审核")
    private Integer status;
    @ApiModelProperty(value = "审核不通过原因")
    private String cause;
    @ApiModelProperty(value = "审核人")
    private String auditor;
    @ApiModelProperty(value = "审核类型/0-财务、 1-企业")
    private Integer auditType;
    @ApiModelProperty(value = "姓名")
    private String name;
    @ApiModelProperty(value = "订单号")
    private Integer orderId;
    @ApiModelProperty(value = "订单类型/0-注册、1-升级、2-续费")
    private Integer orderType;
    @ApiModelProperty(value = "支付方式/0-支付宝、1-微信、2-pos机、4-转账")
    private Integer payType;
    @ApiModelProperty(value = "支付区间开始时间")
    private Date payStartTime;
    @ApiModelProperty(value = "支付区间结束时间")
    private Date payEndTime;
    @ApiModelProperty(value = "经销商支付金额")
    private BigDecimal price;
    @ApiModelProperty(value = "经销商支付时间")
    private Date payTime;
    @ApiModelProperty(value = "支付流水号")
    private String payRecord;
    @ApiModelProperty(value = "经销商账号")
    private String distributorAccount;
    @ApiModelProperty(value = "审核时间")
    private Date auditTime;
}
