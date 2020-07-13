package com.yimao.cloud.order.mapper;

import java.util.List;

import com.yimao.cloud.order.po.OrderSalePerformRank;
import com.yimao.cloud.pojo.dto.user.SalePerformRankDTO;
import com.yimao.cloud.pojo.dto.user.SalePerformRankQuery;

import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/***
 * @desc 销售排行mapper
 * @author zhangbaobao
 *
 */
public interface SalePerformRankMapper extends Mapper<OrderSalePerformRank> {

	List<SalePerformRankDTO> getSalePerformRankData(SalePerformRankQuery query);
	
	void batchInsert(@Param("list")List<OrderSalePerformRank> list);

}
