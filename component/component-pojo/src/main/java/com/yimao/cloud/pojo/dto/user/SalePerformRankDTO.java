package com.yimao.cloud.pojo.dto.user;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

/***
 * 销售排行统计对象
 * 
 * @author zhangbaobao
 * @date 2020/4/26
 */
@Data
public class SalePerformRankDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer stationCompanyId;// 服务站公司id
	private Integer distributorId;// 经销商id
	private String distributorName;// 经销商姓名
	private Integer num;// 招商人数或者是订单数
	private BigDecimal salesAmount;// 销售额
	private String statMonth;// 统计日期
}
