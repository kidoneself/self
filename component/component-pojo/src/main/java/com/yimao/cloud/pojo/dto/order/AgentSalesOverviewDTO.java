package com.yimao.cloud.pojo.dto.order;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
/**
 * @description 代理商首页销售概况
 * @author Liu Yi
 * @date 2020/4/30 10:05
 */
public class AgentSalesOverviewDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private BigDecimal saleTotalFee;//销售金额
	private BigDecimal investmentTotalSaleFee;//招商销售金额
	private BigDecimal productTotalSaleFee;   //产品销售金额
	private BigDecimal renewTotalSaleFee;     //续费销售金额

	//招商累计销售相关
	private BigDecimal investmentRegistSaleFee;    //招商注册销售金额
	private BigDecimal investmentUpgradeSaleFee;   //招商升级销售金额
	private BigDecimal investmentRenewSaleFee;     //招商续费销售金额
	private List<String> orderTypeNames;//续费订单类型

	//产品累计销售相关
	private BigDecimal jsfwSaleFee;              //净水服务销售金额
	private BigDecimal jkspSaleFee;              //健康食品销售金额
	private BigDecimal swkjSaleFee;              //生物科技销售金额
	private BigDecimal jkpgSaleFee;              //健康评估销售金额
	private List<String> categoryNames;//一级类目


	//设备续费相关
	private BigDecimal syllSaleFee;     //商用流量类型销售金额
	private BigDecimal jyllSaleFee;     //家用流量类型销售金额
	private BigDecimal sybnSaleFee;     //商用包年类型销售金额
	private BigDecimal jybnSaleFee;     //家用包年类型销售金额
	private List<String> costTypeNames;//计费类型

}
