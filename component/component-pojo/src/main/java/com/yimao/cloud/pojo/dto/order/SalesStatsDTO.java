package com.yimao.cloud.pojo.dto.order;
import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;
@Data
public class SalesStatsDTO implements Serializable{
	private static final long serialVersionUID = 1L;
	private BigDecimal saleAmount;//销售金额
	private String completeTime; //订单日期
	private Integer increaseNum;//增长个数(经销商、产品订单、净水设备数量)
	private Integer distributorType;//经销商类型
	private String distributorTypeName;//经销商类型名称:体验版、微创版、个人版、企业版
	private Integer distributorNum;//各类经销商个数 
	private String renewStatusName;//续费状态名称
	private Integer renewNum;//续费数量
	private String renewProp;//续费数量所占比例
	private String firstCategoryName;//一级分类名称
	private String categoryName;//分类名称
	private Integer categoryNum;//各分类的数量
}
