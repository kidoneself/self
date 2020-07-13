package com.yimao.cloud.pojo.dto.order;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/****
 * @desc 订单退款请求对象
 * @author zhangbaobao
 *
 */
@Data
@ApiModel(description = "订单退款")
public class OrderRefundDTO  implements Serializable{

	private static final long serialVersionUID = 1L;
	
	//支付宝退款 参数:tradeNo和refundFee,微信退款需要所有参数
	private String tradeNo;//交易订单号
	private String refundFee;//订单退款金额
	private String outRefundNo;//商户退款单号
	private String totalFee;//订单总金额
	private String refundDesc;//退款描述信息
	private String orderId;//商户订单号
	
	public OrderRefundDTO(){
		
	}
	public OrderRefundDTO(String tradeNo, BigDecimal realRefundFee, Long id, BigDecimal payFee, String refundReason,Long orderId) {
		this.tradeNo=tradeNo;
		this.refundFee=String.valueOf(realRefundFee);
		this.outRefundNo=String.valueOf(id);
		this.totalFee=String.valueOf(payFee);
		this.refundDesc=refundReason;
		this.orderId=String.valueOf(orderId);
	}
	

}
