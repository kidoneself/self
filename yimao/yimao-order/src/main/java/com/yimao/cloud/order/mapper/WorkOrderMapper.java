package com.yimao.cloud.order.mapper;

import java.util.List;

import com.yimao.cloud.pojo.dto.order.*;
import com.yimao.cloud.pojo.dto.water.WaterDeviceCompleteDTO;
import org.apache.ibatis.annotations.Param;

import com.github.pagehelper.Page;
import com.yimao.cloud.order.po.WorkOrder;
import com.yimao.cloud.pojo.dto.station.StationScheduleDTO;
import com.yimao.cloud.pojo.query.station.WorkOrderQuery;
import com.yimao.cloud.pojo.vo.order.EngineerWorkOrderVO;

import tk.mybatis.mapper.common.Mapper;

public interface WorkOrderMapper extends Mapper<WorkOrder> {

    /**
     * 根据条件查询安装工单信息
     */
    Page<WorkOrderDTO> getWorkOrderList(WorkOrderQueryDTO workOrderQueryDTO);

    /**
     * 描述：云平台工单概况
     **/
    List<WorkOrderCountDTO> countWorkOrderByStatus();

    /**
     * 描述：云平台工单趋势
     **/
    List<WorkOrderCountDTO> countWorkOrderByCreateTime(WorkOrderQueryDTO workOrderQueryDTO);

    /**
     * 根据条件查询安装工单支付信息
     */
    Page<PayRecordDTO> getWorkOrderPayList(WorkOrderQueryDTO workOrderQueryDTO);

    /**
     * 根据条件查询工单发票列表信息
     */
    Page<WorkOrderDTO> getWorkOrderInvoiceList(WorkOrderQueryDTO workOrderQueryDTO);


    List<EngineerWorkOrderVO> selectWorkOrderByEngineerId(@Param("engineerId") Integer engineerId);

    void updateLasteFinishTime(Integer hour);

    List<WorkOrderDTO> selectCompletedWorkOrderByUserPhone(@Param("userPhone") String userPhone);

    WorkOrder selectWorkOrderByDeviceIdForRenewOrder(@Param("deviceId") Integer deviceId);

    Page<WorkOrderDTO> selectDeliveryPayCheckList(WorkOrderQueryDTO query);

    Page<WorkOrderDTO> selectDeliveryPayCheckListExport(WorkOrderQueryDTO query);

    /**
     * 根据子订单号，获取工单信息
     */
    WorkOrder findWorkOrderByOrderId(@Param("subOrderId") Long subOrderId);

    Boolean checkWorkOrder180ComplatePay(@Param("completeOutTradeNo") String completeOutTradeNo);

    WorkOrder selectBasicInfoById(@Param("id") String id);

    Boolean existsWithLogisticsCode(@Param("logisticsCode") String logisticsCode);

    void updateForChangeDevice(WorkOrder workOrder);

    void updateForContinue(WorkOrder workOrder);

    void updateForChargeback(WorkOrder workOrder);

    int updateCheckWorkOrderStatus(WorkOrder workOrder);

    WorkOrder selectSignInfoBySignOrderId(@Param("signOrderId") String signOrderId);

    int selectDeliveryPayCheckCount();

    void updateOutStockStepToStartStep(@Param("province") String province,
                                       @Param("city") String city,
                                       @Param("region") String region,
                                       @Param("productId") Integer productId,
                                       @Param("oldStep") Integer oldStep,
                                       @Param("newStep") Integer newStep);

    /**
     * 业务系统查询工单列表
     */
    Page<WorkOrderResultDTO> getWorkOrderListBypage(WorkOrderQueryDTO workOrderQueryDTO);

    /***
     * 导出工单
     * @param workOrderQueryDTO
     * @return
     */
    Page<WorkOrderExportDTO> getWorkOrderListExport(WorkOrderQueryDTO workOrderQueryDTO);

    /**
     * 更新工单信息(部分更新)
     *
     * @param workOrder
     */
    void updateWorkOrder(WorkOrder workOrder);

    /**
     * 总部审核：更新工单信息[部分更新]
     *
     * @param workOrder 工单对象
     */
    void updateWorkOrderRefundAudit(WorkOrder workOrder);

    Page<WorkOrderInvoiceExportDTO> exportWorkOrderInvoiceList(WorkOrderQueryDTO workOrderQueryDTO);
    /**
     * 站务系统工单列表查看
     * @param query
     * @return
     */
	Page<WorkOrderResultDTO> getStationWorkOrderList(WorkOrderQuery query);
	/**
	 *站务系统
     * 根据区域查询未受理，已受理，处理中，正常完成工单数
     */
    StationScheduleDTO getStationWorkOrderNum(@Param("engineerIds") List<Integer> engineerIds);

    /***
     * 更新工单上的安装工信息
     * @param workOrderDTO
     * @return
     */
    int updateWorkorderForEngineer(WorkOrderDTO workOrder);

    /***
     * 根据省市区获取未完成和退单中的工单
     * @param province
     * @param city
     * @param region
     * @return
     */
    List<WorkOrderUnfinishedRsDTO> queryWorkOrderForUnfinished(@Param("province") String province, @Param("city") String city, @Param("region") String region);

    /***
     * 根据省市区获取未完成和退单中的工单数量
     * @param province
     * @param city
     * @param region
     * @return
     */
    List<Integer> queryWorkOrderForEngineerIdsByPcr(@Param("province") String province, @Param("city") String city, @Param("region") String region);

    /***
     * 查询这种状态的工单数量
     * @param engineerId
     * @return
     */
    List<WorkOrderStatDTO> getWorkOrderStatInfo(@Param("engineerId") Integer engineerId);

    /***
     * 安装工app新版本-查询工单列表
     * @param query
     * @return
     */
    Page<WorkOrderRsDTO> getWorkOrderListForEngineer(WorkOrderReqDTO query);

    /**
     * 统计已完成安装工单
     *
     * @param completeTime
     * @param engineerId
     * @return
     */
    List<RenewDTO> getInstallWorderOrderList(@Param("completeTime") String completeTime,
											 @Param("engineerId") Integer engineerId,
											 @Param("timeType") Integer timeType);

	/**
	 * 安装模块下工单数量统计
	 * @param engineerId
	 * @param status
	 * @return
	 */
	Integer getWorkOrderCount(@Param("engineerId") Integer engineerId,@Param("status") Integer status);


    List<MapOrderDTO> getInstallWorkOrder(Integer engineerId);

    List<MapOrderDTO> getRepairWorkOrder(Integer engineerId);

    List<MapOrderDTO> getMaintenanceWorkOrder(Integer engineerId);

    List<MapOrderDTO> getMoveWaterDeviceOrder(Integer engineerId);

    MapOrderDTO mergeOrder(@Param("sn") String sn, @Param("state") Integer state, @Param("engineerId") Integer engineerId);

    List<MapOrderDTO> getBackWaterDeviceOrder(Integer engineerId);

    Integer getInstallWaterDeviceTotalCount(@Param("engineerId") Integer engineerId);
}
