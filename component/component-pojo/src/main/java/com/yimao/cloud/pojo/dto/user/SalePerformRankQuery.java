package com.yimao.cloud.pojo.dto.user;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
@Data
public class SalePerformRankQuery implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer stationCompanyId;// 服务站公司id
	private List<Integer> ids;// 经销商id
	private String statMonth;// 统计日期
	private Integer type;//1:招商销售排行,2:产品销售排行,3.续费销售排行
	private Integer queryType;//1:个人,2公司
}
