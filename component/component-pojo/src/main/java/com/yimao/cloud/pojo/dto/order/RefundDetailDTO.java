package com.yimao.cloud.pojo.dto.order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

/***
 * 退款记录详情
 * @author zhangbaobao
 *
 */
@Data
public class RefundDetailDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long afterSalesId;//售后单号
	private Long mainOrderId;//主订单号
	private String productCompanyName;//产品公司名称
	private Long orderId;//子订单号
	private BigDecimal refundFee;//应退金额
	private BigDecimal fee;//手续费
	private BigDecimal realRefundFee;//可退金额
	private String refundReason;//退款原因
	private Date refundTime;//退款申请时间//yyyy-MM-dd hh:mm:ss,指用户发起取消订单的时间
	private String refundTimeText;//退款申请时间//yyyy-MM-dd hh:mm:ss,指用户发起取消订单的时间
	private String orderSource;//订单来源转换
	private Integer terminal;//订单来源
	private String tradeNo;//交易流水号-退款流水号
	private String userTypeName;//用户身份
	private Long userId;//下单人编号
	private String userName;//下单人姓名
	private String userPhone;//下单人手机号
	private Date refundVerifyTime;//退款审核时间//yyyy-MM-dd hh:mm:ss
	private String refundVerifyTimeText;//退款审核时间//yyyy-MM-dd hh:mm:ss
}
