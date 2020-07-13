package com.yimao.cloud.order.po;

import java.math.BigDecimal;
import java.util.Date;

import com.yimao.cloud.pojo.dto.user.SalePerformRankDTO;

import lombok.Data;

/**
 * 销售业绩排行对象
 * 
 * @author zhangbaobao
 *
 */
@Data
public class OrderSalePerformRank {
	private Integer id;//主键
	private Integer stationCompanyId;// 服务站公司id
	private Integer distributorId;// 经销商id
	private String distributorName;// 经销商姓名
	private Integer num;// 招商人数/订单数/续费单数
	private BigDecimal salesAmount;// 销售额
	private String statMonth;// 排行日期
	private Integer type;//1:招商销售排行,2:产品销售排行,3.续费销售排行
	private Date createTime;//创建日期
	public OrderSalePerformRank() {}
	public  OrderSalePerformRank(SalePerformRankDTO sprd) {
		this.stationCompanyId=sprd.getStationCompanyId();
		this.distributorId=sprd.getDistributorId();
		this.distributorName=sprd.getDistributorName();
		this.num=sprd.getNum();
		this.salesAmount=sprd.getSalesAmount();
		this.statMonth=sprd.getStatMonth();
		this.createTime=new Date();
	}
}
