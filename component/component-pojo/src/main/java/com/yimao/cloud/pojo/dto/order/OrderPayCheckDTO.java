package com.yimao.cloud.pojo.dto.order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "支付审核记录")
public class OrderPayCheckDTO implements Serializable{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1678654602066323803L;
	@ApiModelProperty( value = "id")
	private Integer id;
    //订单类型：1-产品订单；2-水机续费单；
	@ApiModelProperty( value = "订单类型：1-产品订单；2-水机续费单")
    private Integer orderType;
	@ApiModelProperty( value = "单号")
    private String orderId;

	@ApiModelProperty( value = "审核状态：1-通过；2-不通过")
    private Integer status;
	@ApiModelProperty( value = "审核状态名")
    private String statusName;
    //审核不通过原因
	@ApiModelProperty( value = "原因")
    private String reason;
	@ApiModelProperty( value = "操作人")
    private String creator;
	@ApiModelProperty( value = "操作时间")
    private Date createTime;
	@ApiModelProperty( value = "审核状态名")
    private String payCheckStatusName;
	@ApiModelProperty( value = "操作开始时间")
    private Date startCreateTime;
	@ApiModelProperty( value = "操作结束时间时间")
    private Date endCreateTime;
	@ApiModelProperty( value = "支付金额")
	private BigDecimal payMoney;
	@ApiModelProperty( value = "是否支付 0-否；1-是")
	private Integer isPay;
	@ApiModelProperty( value = "支付方式：1-微信；2-支付宝；3-POS机；4-转账")
	private Integer payType;
	@ApiModelProperty( value = "支付凭证")
	private String payCredential;
	@ApiModelProperty( value = "提交凭证时间")
	private Date payCredentialSubmitTime;

}
