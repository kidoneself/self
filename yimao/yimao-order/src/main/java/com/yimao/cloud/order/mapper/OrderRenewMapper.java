package com.yimao.cloud.order.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.order.po.OrderRenew;
import com.yimao.cloud.pojo.dto.order.AgentSalesOverviewDTO;
import com.yimao.cloud.pojo.dto.order.OrderRenewDTO;
import com.yimao.cloud.pojo.dto.order.RenewDTO;
import com.yimao.cloud.pojo.dto.order.SalesStatsDTO;
import com.yimao.cloud.pojo.dto.order.SalesStatsQueryDTO;
import com.yimao.cloud.pojo.dto.station.RenewStatisticsDTO;
import com.yimao.cloud.pojo.export.order.OrderRenewExport;
import com.yimao.cloud.pojo.query.order.RenewOrderQuery;
import com.yimao.cloud.pojo.query.station.StationWaterDeviceQuery;
import com.yimao.cloud.pojo.vo.order.OrderRenewVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author Zhang Bo
 * @date 2018/11/8.
 */
public interface OrderRenewMapper extends Mapper<OrderRenew> {

    Page<OrderRenewVO> orderRenewFinanceList(RenewOrderQuery query);

    OrderRenewVO selectOrderRenewDetail(@Param(value = "id") String id);

    Long countRenewOrder4Audit();

    Page<OrderRenewVO> getOrderRenewList(RenewOrderQuery query);

    Page<OrderRenewExport> orderRenewExport(RenewOrderQuery query);

    OrderRenewVO selectOrderRenewFinanceDetail(@Param(value = "id") String id);

    Page<OrderRenewVO> orderRenewPayCheckList(RenewOrderQuery query);

    List<OrderRenewVO> selectRenewOrderRecordBySnCodeForApp(@Param(value = "snCode") String snCode, @Param(value = "distributorId") Integer distributorId);

    OrderRenew selectPayInfoById(@Param(value = "id") String id);

    int countRenewTimes(@Param(value = "deviceId") Integer deviceId);

    List<RenewDTO> getRenewOrderListById(@Param(value = "distributorId") Integer distributorId);

    Page<OrderRenewVO> orderRenewPayCheckListExport(RenewOrderQuery query);

    Integer getRenewOrderCount(@Param(value = "distributorId") Integer distributorId);

    void updateRenewOrderSn(@Param(value = "deviceId") Integer deviceId, @Param(value = "oldSn") String oldSn, @Param(value = "newSn") String newSn);

    List<RenewStatisticsDTO> getIsRenewPicData(StationWaterDeviceQuery waterDeviceQuery);

    int getStationRenewNum(@Param("engineerIds") List<Integer> engineerIds);

    int getStationYesterdayRenewNum(@Param("engineerIds") List<Integer> engineerIds);

    Integer getSalesReturnOrderNum(@Param("engineerIds") List<Integer> engineerIds);

    List<SalesStatsDTO> getRenewSaleStats(SalesStatsQueryDTO query);

    AgentSalesOverviewDTO getOrderSalesHomeReport(SalesStatsQueryDTO query);

    List<RenewDTO> getCostTypeName();

    /***
     * 安装工转让,更新续费单上的安装工信息
     * @param orderRenewDTO
     * @return
     */
    int updateRenewOrderForEngineer(OrderRenewDTO orderRenew);

    Integer getSalesReturnOrderNumByDistributorIds(@Param("distributorIds") List<Integer> distributorIds);
}
