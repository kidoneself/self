package com.yimao.cloud.order.service;

import com.github.pagehelper.Page;
import com.yimao.cloud.order.po.OrderSub;
import com.yimao.cloud.order.po.PayRecord;
import com.yimao.cloud.order.po.SubOrderDetail;
import com.yimao.cloud.order.po.WorkOrder;
import com.yimao.cloud.pojo.dto.order.*;
import com.yimao.cloud.pojo.dto.station.StationScheduleDTO;
import com.yimao.cloud.pojo.dto.system.TransferAreaInfoDTO;
import com.yimao.cloud.pojo.query.station.WorkOrderQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.order.EngineerWorkOrderVO;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface WorkOrderService {

	/**
	 * 描述：获取安装工某个状态工单的数量
	 *
	 * @param engineerId 安装工ID
	 * @param status     工单状态
	 * @Creator Zhang Bo
	 * @CreateTime 2019/3/9 12:03
	 **/
	Integer countWorkOrderByEngineerId(Integer engineerId, Integer status);

	/**
	 * 描述：获取安装工某段时间的工单数量
	 *
	 * @param engineerId 安装工ID
	 * @param startTime  开始时间
	 * @param endTime    结束时间
	 * @Creator Zhang Bo
	 * @CreateTime 2019/3/9 12:03
	 **/
	Integer countWorkOrderByEngineerId(Integer engineerId, Date startTime, Date endTime);

	/**
	 * 描述：获取安装工某段时间某类产品的工单数量
	 *
	 * @param engineerId 安装工ID
	 * @param startTime  开始时间
	 * @param endTime    结束时间
	 * @param productIds 产品ID集合
	 * @Creator Zhang Bo
	 * @CreateTime 2019/3/9 12:03
	 **/
	Integer countWorkOrderByEngineerId(Integer engineerId, Date startTime, Date endTime, List<Integer> productIds);

	/**
	 * 描述：获取安装工某段时间某个状态某类产品的工单数量
	 *
	 * @param engineerId 安装工ID
	 * @param status     工单状态
	 * @param startTime  开始时间
	 * @param endTime    结束时间
	 * @param productIds 产品ID集合
	 **/
	Integer countWorkOrderByEngineerId(Integer engineerId, Integer status, Date startTime, Date endTime, List<Integer> productIds);

	/**
	 * 创建安装工单
	 *
	 * @param workOrderDTO 工单对象
	 */
	void createWorkOrder(WorkOrder workOrder);

	/**
	 * 根据条件查询安装工单信息
	 */
	PageVO<WorkOrderDTO> getWorkOrderList(WorkOrderQueryDTO workOrderQueryDTO, Integer pageNum, Integer pageSize);

	/**
	 * 描述：根据工单id获取工单信息
	 *
	 * @param workOrderId 工单ID
	 **/
	WorkOrderDTO getWorkOrderById(String workOrderId);

	/**
	 * 工单评分内容修改
	 *
	 * @param workOrderId     工单ID
	 * @param appraiseContent 评分内容
	 **/
	WorkOrderDTO updateWorkOrderAppraise(String workOrderId, String appraiseContent);

	/**
	 * 工单评分等级修改
	 *
	 * @param workOrderId 工单ID
	 * @param levelId     评分等级
	 **/
	WorkOrderDTO updateDistributorApprise(String workOrderId, Integer levelId);

	/**
	 * 拒单后重新指派安装工
	 *
	 * @param workOrderId 工单ID
	 * @param engineerId  安装工id
	 **/
	WorkOrderDTO updateWorkOrderEngineer(String workOrderId, Integer engineerId);

	/**
	 * 修改安装工单
	 */
	WorkOrderDTO updateWorkOrder(WorkOrderDTO workOrderDTO);

	/**
	 * 修改安装工单局部字段
	 */
	WorkOrderDTO updateWorkOrderPart(WorkOrderDTO workOrderDTO);

	/**
	 * 云平台工单列表
	 **/
	PageVO<WorkOrderResultDTO> selectWorkOrderList(WorkOrderQueryDTO workOrderQueryDTO, Integer pageNum, Integer pageSize);

	/**
	 * 描述：云平台导出工单
	 **/
	Page<WorkOrderExportDTO> exportWorkOrderList(WorkOrderQueryDTO workOrderQueryDTO, Integer pageNum, Integer pageSize);

	/**
	 * 云平台删除工单
	 **/
	void deleteWorkOrder(String workOrderId);

	/**
	 * 云平台分配安装工
	 *
	 * @param workOrderId 工单ID
	 * @param engineerId  安装工ID
	 **/
	void allotEngineer(String workOrderId, Integer engineerId);

	/**
	 * 云平台工单评价
	 **/
	void updateWorkOrderAppraiseScore(String workOrderId, Integer score, String appraiseContent);

	/**
	 * 描述：云平台工单概况
	 **/
	List<WorkOrderCountDTO> countWorkOrderByStatus();

	/**
	 * 描述：云平台工单趋势
	 **/
	List<WorkOrderCountDTO> countWorkOrderByCreateTime(String days, Date startTime, Date endTime);

	/**
	 * 描述：云平台根据条件查询安装工单支付信息
	 */
	PageVO<PayRecordDTO> getWorkOrderPayList(WorkOrderQueryDTO workOrderQueryDTO, Integer pageNum, Integer pageSize);

	/**
	 * 描述：云平台--工单退单
	 *
	 * @param id 工单ID
	 **/
	void backOrderUpdate(String id);

	/**
	 * 根据条件查询工单发票列表信息
	 */
	PageVO<WorkOrderDTO> getWorkOrderInvoiceList(WorkOrderQueryDTO workOrderQueryDTO, Integer pageNum, Integer pageSize);

	/**
	 * 云平台导出工单发票信息
	 */
	Page<WorkOrderInvoiceExportDTO> exportWorkOrderInvoiceList(WorkOrderQueryDTO workOrderQueryDTO, Integer pageNum, Integer pageSize);

	/**
	 * @description 根据工单id查询工单操作记录列表
	 */
	PageVO<WorkOrderOperationDTO> getWorkOrderOperationList(String id, Integer pageNum, Integer pageSize);

	/**
	 * 根据状态查询安装工单信息
	 */
	List<WorkOrderDTO> getWorkOrderCompleteList(Integer engineerId, String consumerName, String consumerPhone, String deviceSncode, Integer Status);

	List<EngineerWorkOrderVO> listWorkOrderByEngineerId(Integer engineerId);

	void updateLasteFinishTime(Integer hour);

	/**
	 * 描述：根据签约单号获取工单
	 *
	 * @param signOrderId 签约单号
	 **/
	WorkOrderDTO getWorkOrderBySignOrderId(String signOrderId);

	/**
	 * 根据用户手机号查询正常完成的工单
	 *
	 * @param mobile 手机号
	 */
	WorkOrderDTO getCompletedWorkOrderByUserPhone(String mobile);

	Boolean checkWorkOrder180ComplatePay(String completeOutTradeNo);

	/***
	 * 根据安装工支付状态工单状态查询工单信息不分页
	 * @param engineerId
	 * @param status
	 * @param payStatus
	 * @param completeType
	 * @return
	 */
	List<WorkOrderDTO> getWorkOrderListByquery(Integer engineerId, Integer status, Integer payStatus, Integer completeType);


	/***
	 * 创建工单并且同步售后
	 * @param subOrderDetail
	 * @param subOrder
	 * @return
	 */
	Map<String, Object> generateWorkOrderAndSyncaAfterSale(OrderSub subOrder, SubOrderDetail subOrderDetail);

	/**
	 * 安装工拒单
	 */
	void refuseWorkOrder(String workOrderId, Integer engineerId, String engineerName, String reason);

	/**
	 * 安装工接单
	 */
	void acceptWorkOrder(String workOrderId, Integer engineerId, Integer nextStep);

	/**
	 * 安装工中止工单
	 */
	void discontinueWorkOrder(String workOrderId, String reason, String remark, Integer reasonNum);

	/**
	 * 安装工更换设备
	 */
	void changedeviceWorkOrder(String workOrderId);

	/**
	 * 安装工继续服务
	 */
	void continueWorkOrder(String workOrderId);

	/**
	 * 安装工退单
	 */
	void chargebackWorkOrder(String workOrderId, String reason, String remark, Integer reasonNum);

	/**
	 * 安装工完成工单
	 */
	void completeWorkOrder(String workOrderId);

	Boolean existsWithLogisticsCode(String logisticsCode);

	/**
	 * 安装工APP工单支付回调
	 */
	void payCallback(PayRecord record);

	void changeProductAndCost(String workOrderId, Integer productId, Integer costId, String logisticsCode, String snCode, String simCard, Integer type);

	PageVO<WorkOrderResultDTO> getStationWorkOrderList(Integer pageNum, Integer pageSize, WorkOrderQuery query);

	StationScheduleDTO getStationWorkerOrderNum(List<Integer> engineerIds);

	List<WorkOrderUnfinishedRsDTO> queryWorkOrderForUnfinished(TransferAreaInfoDTO transferAreaInfoDTO);

	/**
	 * 给指定经销商下的工单“经销商第一次升级时间”字段赋值
	 *
	 * @param info
	 */
	void setFirstUpgradeTime(Map<String, Object> info);

	/***
	 * 安装工新app-获取安装工各种状态的工单数量
	 * @param engineerId
	 * @return
	 */
	List<WorkOrderStatDTO> getWorkOrderStatInfo(Integer engineerId);

	/****
	 * 安装工新app-查询工单列表
	 * @param query
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	PageVO<WorkOrderRsDTO> getWorkOrderListForEngineer(WorkOrderReqDTO query, Integer pageNum, Integer pageSize);

	/***
	 * 安装公工退单
	 * @param req
	 */
	void chargeback(WorkOrderReqDTO req);

	/**
	 * 查询已完成安装工单
	 * @param completeTime
	 * @param engineerId
	 * @return
	 */
	List<RenewDTO> getInstallWorderOrderList(String completeTime, Integer engineerId, Integer timeType);

	HashMap<String, List<MapOrderDTO>> getInstallWorkOrder(Integer engineerId);

	/**
	 * 市场服务-安装模块下工单数量统计
	 * @param engineerId
	 * @return
	 */
	Map<String, Integer> getInstallWaterDeviceCount(Integer engineerId);

	Integer getInstallWaterDeviceTotalNum(Integer engineerId);
}
