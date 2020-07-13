package com.yimao.cloud.pojo.export.order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class OrderCheckExport implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = -3042076755919989619L;
	
	private String id;
	private String productCompanyName;	
	private Integer count;
	private BigDecimal orderAmountFee;
	private Integer payStatus;
	private Integer payType;
	@JsonFormat(pattern="yyyy-MM-dd hh:mm:ss")
	private String payCredentialSubmitTime;
	private Integer payTerminal;
	private Long subOrderId;
	private Integer status;
	private BigDecimal fee;
	@JsonFormat(pattern="yyyy-MM-dd hh:mm:ss")
	private String payTime;
	private String productCategoryName;
	private String amountFee;
	private String statusName;
	private String payTypeName;
	private Integer orderType;
	@JsonFormat(pattern="yyyy-MM-dd hh:mm:ss")
	private String createTime;
	private String creator;
	private String payCheckStatusName;
	private String reason;
	private String orderId;
	
	public String getOrderType() {
		if(Objects.isNull(orderType)) {
			return "";
		}
		if(orderType == 1) {
			return "产品订单";
		}else if(orderType == 2) {
			return "水机续费单";
		}else {
			return "";
		}
	}
	
	public String getPayStatus() {
		String res="";
		if(Objects.isNull(payStatus)) {
			return res;
		}
		
		switch (payStatus) {
		case 1:
			res="未支付";
			break;
		case 2:
			res="待审核";
			break;
		case 3:
			res="支付成功";
			break;
		case 4:
			res="支付失败";
			break;
		default:
			break;
		}
		return res;
	}
	
	public String getPayType() {
		String res="";
		if(Objects.isNull(payType)) {
			return res;
		}
		switch (payType) {
		case 1:
			res="微信";
			break;
		case 2:
			res="支付宝";
			break;
		case 3:
			res="POS机";
			break;
		case 4:
			res="转账";
			break;
		default:
			break;
		}
		return res;
	}
	
	public String getPayTerminal() {
		if(Objects.isNull(payTerminal)) {
			return "";
		}
		if(payTerminal == 1) {
			return "立即支付";
		}else if(payTerminal == 2) {
			return "货到付款";
		}else {
			return "";
		}
	}
	
	public String getStatus() {
		
		return "审核中";
	}
	

}
