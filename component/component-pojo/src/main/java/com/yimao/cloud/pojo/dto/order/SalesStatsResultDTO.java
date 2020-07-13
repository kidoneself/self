package com.yimao.cloud.pojo.dto.order;
/***
 * 销售额统计结果对象
 * @author zhangbaobao
 *
 */
import java.io.Serializable;
import java.util.List;

import com.yimao.cloud.pojo.dto.user.SalePerformRankDTO;

import lombok.Data;
@Data
public class SalesStatsResultDTO implements Serializable{
	private static final long serialVersionUID = 1L;
	//招商销售
	private List<SalesStatsDTO> investSalesList;//招商收益柱状图数据(根据日(7天)、月(12个月)、年统计(所有))
	private List<SalesStatsDTO> distributorIncreaseList;//各类经销商增长趋势数据
	private List<SalesStatsDTO> distributorPropList;//各类经销商占比数据
	private List<SalePerformRankDTO> salePerformRankList;//销售排行榜数据
	
	//续费销售
	private  List<SalesStatsDTO> renewSaleList;//续费销售统计(根据日(7天)、月(12个月)、年统计(所有)
	private  List<SalesStatsDTO> deviceRenewPropList;//设备续费率-统计新安装、待续费、已续费所占比例
	
	//产品销售
	private List<SalesStatsDTO> prodSalesList;//产品销售额
	private List<SalesStatsDTO> tradeOrderList;//交易成功订单数
	private List<SalesStatsDTO> waterModelPropList;//水机型号占比
}
