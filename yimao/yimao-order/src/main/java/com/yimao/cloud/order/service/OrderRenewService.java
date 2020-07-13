package com.yimao.cloud.order.service;

import java.util.List;

import com.yimao.cloud.order.po.OrderRenew;
import com.yimao.cloud.pojo.dto.order.AgentSalesOverviewDTO;
import com.yimao.cloud.pojo.dto.order.SalesStatsDTO;
import com.yimao.cloud.pojo.dto.order.SalesStatsQueryDTO;
import com.yimao.cloud.pojo.dto.station.StationScheduleDTO;
import com.yimao.cloud.pojo.query.order.RenewOrderQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.order.OrderRenewVO;

/**
 * @author Zhang Bo
 * @date 2018/11/12.
 */
public interface OrderRenewService {

    void save(OrderRenew renew);

    PageVO<OrderRenewVO> orderRenewFinanceList(RenewOrderQuery query, Integer pageNum, Integer pageSize);

    OrderRenewVO getOrderRenewDetail(String id);

    OrderRenew getById(String id);

    void resubmit(String id, Integer payType, String payCredential);

    PageVO<OrderRenewVO> getOrderRenewList(RenewOrderQuery query, Integer pageNum, Integer pageSize);

    void orderRenewPayCheckSingle(String id, Boolean pass, String reason, String adminName);

    OrderRenewVO getOrderRenewFinanceDetail(String id);

    PageVO<OrderRenewVO> orderRenewPayCheckList(RenewOrderQuery query, Integer pageNum, Integer pageSize);

    void payCallback(String renewOrderId);

    OrderRenew getPayInfoById(String id);

    void allotRenewIncome(OrderRenew renewOrder);

	StationScheduleDTO getStationRenewOrderNum(List<Integer> engineerIds);

	List<SalesStatsDTO> getRenewSaleStats(SalesStatsQueryDTO query);

    List<SalesStatsDTO> getWaterDeviceRenewSale(SalesStatsQueryDTO query);

    AgentSalesOverviewDTO getOrderSalesHomeReport(SalesStatsQueryDTO query);

    List<String> getRenewOrderTypeNames();
}
