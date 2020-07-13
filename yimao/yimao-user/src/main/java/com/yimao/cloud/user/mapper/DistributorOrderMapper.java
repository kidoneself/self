package com.yimao.cloud.user.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.order.AgentSalesOverviewDTO;
import com.yimao.cloud.pojo.dto.order.RenewDTO;
import com.yimao.cloud.pojo.dto.order.SalesStatsDTO;
import com.yimao.cloud.pojo.dto.order.SalesStatsQueryDTO;
import com.yimao.cloud.pojo.dto.station.FlowStatisticsDTO;
import com.yimao.cloud.pojo.dto.user.DistributorOrderAllInfoDTO;
import com.yimao.cloud.pojo.dto.user.DistributorOrderDTO;
import com.yimao.cloud.pojo.dto.user.DistributorOrderExportDTO;
import com.yimao.cloud.pojo.dto.user.SalePerformRankDTO;
import com.yimao.cloud.pojo.query.station.StatisticsQuery;
import com.yimao.cloud.pojo.query.user.DistributorOrderQueryDTO;
import com.yimao.cloud.pojo.vo.station.DistributorOrderVO;
import com.yimao.cloud.user.po.DistributorOrder;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author Lizhqiang
 * @date 2018/12/18
 */
public interface DistributorOrderMapper extends Mapper<DistributorOrder> {

    Page<DistributorOrderDTO> pageQueryDistributorOrder(DistributorOrderQueryDTO query);

    Page<DistributorOrderExportDTO> listOrderExport(DistributorOrderQueryDTO query);


    DistributorOrderAllInfoDTO getDistributorOrderById(Long orderId);

    List<DistributorOrder> listDistributorOrder(@Param("distributorId") Integer distributorId, @Param("orderType") Integer orderType, @Param("roleLevel") Integer roleLevel);

    Integer selectCount4Status(@Param("sign")Integer sign);

	List<FlowStatisticsDTO> getDistributorOrderData(StatisticsQuery query);

	List<FlowStatisticsDTO> getDistributorOrderPicData(StatisticsQuery query);
    /**
     * 根据条件查询经销商订单列表（站务系统调用）
     *
     * @param query
     * @return
     */
    Page<DistributorOrderVO> pageQueryDistributorOrderToStation(DistributorOrderQueryDTO query);

	List<FlowStatisticsDTO> getTotalDistributorOrderPicData(StatisticsQuery query);

	AgentSalesOverviewDTO getOrderSalesHomeReport(SalesStatsQueryDTO query);

	/***
	 * 根据日期(年、月、日)统计招商销售额
	 * @param query
	 * @return
	 */
	List<SalesStatsDTO> getDistributorOrderSalesData(SalesStatsQueryDTO query);
	
	/***
	 * 获取经销商增长个数
	 * @param query
	 * @return
	 */
	List<SalesStatsDTO> getDistributorIncreaseNumData(SalesStatsQueryDTO query);
	
	/****
	 * 获取经销商发展的所有经销商各类型比例
	 * @param query
	 * @return
	 */
	List<SalesStatsDTO> getDistributorNumProp(SalesStatsQueryDTO query);
	
	/***
	 * 统计上个月排行榜信息
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	List<SalePerformRankDTO> getDistributorPerformRank(@Param("startTime")String startTime, @Param("endTime")String endTime);


	/**
	 * 查询续费订单类型
	 * @return
	 */
	List<RenewDTO> getOrderTypeName();
}
