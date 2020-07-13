package com.yimao.cloud.order.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.yimao.cloud.pojo.dto.order.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.yimao.cloud.base.enums.IncomeType;
import com.yimao.cloud.base.enums.WorkOrderOperationType;
import com.yimao.cloud.base.enums.WorkOrderStateEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.order.feign.SystemFeign;
import com.yimao.cloud.order.feign.UserFeign;
import com.yimao.cloud.order.service.OrderRenewService;
import com.yimao.cloud.order.service.OrderTransferService;
import com.yimao.cloud.order.service.WorkOrderService;
import com.yimao.cloud.pojo.dto.station.StationScheduleDTO;
import com.yimao.cloud.pojo.dto.system.TransferAreaInfoDTO;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.query.station.WorkOrderQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.order.EngineerWorkOrderVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * 描述：工单控制层
 *
 * @author Zhang Bo
 * @date 2019/3/9.
 */
@RestController
@Slf4j
@Api(tags = "WorkOrderController")
public class WorkOrderController {

	@Resource
	private WorkOrderService workOrderService;
	@Resource
	private UserFeign userFeign;
	@Resource
	private OrderRenewService orderRenewService;

	@Resource
	private OrderTransferService orderTransferService;

	@Resource
	private SystemFeign systemFeign;

	/**
	 * 描述：获取安装工某个状态工单的数量
	 *
	 * @param engineerId 安装工ID
	 * @param status     工单状态
	 * @Creator Zhang Bo
	 * @CreateTime 2019/3/9 12:03
	 **/
	@GetMapping(value = "/workorder/count/engineer/{engineerId}/status")
	@ApiOperation(value = "获取安装工某个状态工单的数量", notes = "获取安装工某个状态工单的数量")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "engineerId", value = "安装工ID", dataType = "Long", required = true, paramType = "path"),
			@ApiImplicitParam(name = "status", value = "工单状态", dataType = "Long", required = true, paramType = "query")})
	public ResponseEntity countWorkOrderByEngineerId(@PathVariable(value = "engineerId") Integer engineerId,
	                                                 @RequestParam(value = "status", required = false) Integer status) {
		return ResponseEntity.ok(workOrderService.countWorkOrderByEngineerId(engineerId, status));
	}

	/**
	 * 描述：获取安装工工单
	 *
	 * @param engineerId 安装工ID
	 **/
	@GetMapping(value = "/workorder/engineer/{engineerId}")
	public List<EngineerWorkOrderVO> listWorkOrderByEngineerId(@PathVariable Integer engineerId) {
		return workOrderService.listWorkOrderByEngineerId(engineerId);
	}

	/**
	 * 描述：获取安装工某段时间的工单数量
	 *
	 * @param engineerId 安装工ID
	 * @param startTime  开始时间
	 * @param endTime    结束时间
	 * @Creator Zhang Bo
	 * @CreateTime 2019/3/9 12:03
	 **/
	@GetMapping(value = "/workorder/count/engineer/{engineerId}/time")
	@ApiOperation(value = "获取安装工某段时间的工单数量", notes = "获取安装工某段时间的工单数量")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "engineerId", value = "安装工ID", dataType = "Long", required = true, paramType = "path"),
			@ApiImplicitParam(name = "startTime", value = "工单状态", dataType = "Date", required = true, paramType = "query", format = "yyyy-MM-dd HH:mm:ss"),
			@ApiImplicitParam(name = "endTime", value = "工单状态", dataType = "Date", required = true, paramType = "query", format = "yyyy-MM-dd HH:mm:ss")})
	public ResponseEntity countWorkOrderByEngineerId(@PathVariable(value = "engineerId") Integer engineerId,
	                                                 @RequestParam(value = "startTime") Date startTime, @RequestParam(value = "endTime") Date endTime) {
		return ResponseEntity.ok(workOrderService.countWorkOrderByEngineerId(engineerId, startTime, endTime));
	}

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
	@GetMapping(value = "/workorder/count/engineer/{engineerId}/time/product")
	@ApiOperation(value = "获取安装工某段时间某类产品的工单数量", notes = "获取安装工某段时间某类产品的工单数量")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "engineerId", value = "安装工ID", dataType = "Long", required = true, paramType = "path"),
			@ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "Date", required = true, paramType = "query", format = "yyyy-MM-dd HH:mm:ss"),
			@ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "Date", required = true, paramType = "query", format = "yyyy-MM-dd HH:mm:ss"),
			@ApiImplicitParam(name = "productIds", value = "产品ID集合", dataType = "List", required = true, paramType = "query")})
	public ResponseEntity countWorkOrderByEngineerId(@PathVariable(value = "engineerId") Integer engineerId,
	                                                 @RequestParam(value = "startTime") Date startTime, @RequestParam(value = "endTime") Date endTime,
	                                                 @RequestParam(value = "productIds") List<Integer> productIds) {
		return ResponseEntity
				.ok(workOrderService.countWorkOrderByEngineerId(engineerId, startTime, endTime, productIds));
	}

	/**
	 * 描述：获取安装工某段时间某个状态某类产品的工单数量
	 *
	 * @param engineerId 安装工ID
	 * @param status     工单状态
	 * @param startTime  开始时间
	 * @param endTime    结束时间
	 * @param productIds 产品ID集合
	 **/
	@GetMapping(value = "/workorder/count/engineer/{engineerId}/status/time/product")
	@ApiOperation(value = "获取安装工某段时间某类产品的工单数量", notes = "获取安装工某段时间某类产品的工单数量")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "engineerId", value = "安装工ID", dataType = "Long", required = true, paramType = "path"),
			@ApiImplicitParam(name = "status", value = "工单状态", dataType = "Long", required = true, paramType = "query"),
			@ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "Date", required = true, paramType = "query", format = "yyyy-MM-dd HH:mm:ss"),
			@ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "Date", required = true, paramType = "query", format = "yyyy-MM-dd HH:mm:ss"),
			@ApiImplicitParam(name = "productIds", value = "产品ID集合", dataType = "List", required = true, paramType = "query")})
	public ResponseEntity countWorkOrderByEngineerId(@PathVariable(value = "engineerId") Integer engineerId,
	                                                 @RequestParam(value = "status") Integer status, @RequestParam(value = "startTime") Date startTime,
	                                                 @RequestParam(value = "endTime") Date endTime,
	                                                 @RequestParam(value = "productIds") List<Integer> productIds) {
		return ResponseEntity
				.ok(workOrderService.countWorkOrderByEngineerId(engineerId, status, startTime, endTime, productIds));
	}

	/**
	 * 根据条件查询安装工单信息
	 */
	@GetMapping(value = "/workorder/{pageNum}/{pageSize}")
	@ApiOperation(value = "根据条件查询安装工单信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", required = true, paramType = "path"),
			@ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", required = true, paramType = "path"),
			@ApiImplicitParam(name = "query", value = "查询信息", dataType = "WorkOrderQueryDTO", paramType = "body")})
	public ResponseEntity<PageVO<WorkOrderDTO>> getWorkOrderList(@RequestBody WorkOrderQueryDTO query,
	                                                             @PathVariable(value = "pageNum") Integer pageNum, @PathVariable(value = "pageSize") Integer pageSize) {
		PageVO<WorkOrderDTO> orderList = workOrderService.getWorkOrderList(query, pageNum, pageSize);
		return ResponseEntity.ok(orderList);
	}

	/**
	 * 描述：根据工单id获取工单信息
	 *
	 * @param workOrderId 工单ID
	 **/
	@GetMapping(value = "/workorder/{workOrderId}")
	@ApiOperation(value = "根据工单id获取工单信息", notes = "根据工单id获取工单信息")
	@ApiImplicitParam(name = "workOrderId", value = "工单ID", dataType = "String", required = true, paramType = "path")
	public WorkOrderDTO getWorkOrderById(@PathVariable(value = "workOrderId") String workOrderId) {
		WorkOrderDTO dto = workOrderService.getWorkOrderById(workOrderId);
		return dto;
	}

	/**
	 * 工单评分内容修改
	 *
	 * @param workOrderId     工单ID
	 * @param appraiseContent 评分内容
	 **/
	@PutMapping(value = "/workorder/appraise")
	@ApiOperation(value = "工单评分内容修改", notes = "工单评分内容修改")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "workOrderId", value = "工单ID", dataType = "String", required = true, paramType = "query"),
			@ApiImplicitParam(name = "appraiseContent", value = "评分内容", dataType = "String", required = true, paramType = "query")})
	public ResponseEntity<WorkOrderDTO> updateWorkOrderAppraise(@RequestParam(value = "workOrderId") String workOrderId,
	                                                            @RequestParam(value = "appraiseContent") String appraiseContent) {
		return ResponseEntity.ok(workOrderService.updateWorkOrderAppraise(workOrderId, appraiseContent));
	}

	/**
	 * 工单评分等级修改
	 *
	 * @param workOrderId 工单ID
	 * @param levelId     评分等级
	 **/
	@PutMapping(value = "/workorder/appraise/distributor")
	@ApiOperation(value = "工单评分等级修改", notes = "工单评分等级修改")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "workOrderId", value = "工单ID", dataType = "String", required = true, paramType = "query"),
			@ApiImplicitParam(name = "levelId", value = "评分等级", dataType = "Long", required = true, paramType = "query")})
	public ResponseEntity<WorkOrderDTO> updateDistributorApprise(
			@RequestParam(value = "workOrderId") String workOrderId, @RequestParam(value = "levelId") Integer levelId) {
		return ResponseEntity.ok(workOrderService.updateDistributorApprise(workOrderId, levelId));
	}

	/**
	 * 微服务--拒单后重新指派安装工
	 *
	 * @param workOrderId 工单ID
	 * @param engineerId  安装工id
	 **/
	@PutMapping(value = "/workorder/engineer")
	@ApiOperation(value = "拒单后重新指派安装工", notes = "拒单后重新指派安装工")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "workOrderId", value = "工单ID", dataType = "String", required = true, paramType = "query"),
			@ApiImplicitParam(name = "engineerId", value = "安装工id", dataType = "Long", required = true, paramType = "query")})
	public ResponseEntity<WorkOrderDTO> updateWorkOrderEngineer(@RequestParam(value = "workOrderId") String workOrderId,
	                                                            @RequestParam(value = "engineerId") Integer engineerId) {
		return ResponseEntity.ok(workOrderService.updateWorkOrderEngineer(workOrderId, engineerId));
	}

	/**
	 * 修改安装工单
	 *
	 * @param workOrderDTO 工单对象
	 */
	@PutMapping(value = "/workorder")
	public ResponseEntity<WorkOrderDTO> updateWorkOrder(@RequestBody WorkOrderDTO workOrderDTO) {
		return ResponseEntity.ok(workOrderService.updateWorkOrder(workOrderDTO));
	}

	/**
	 * 修改安装工单局部字段
	 */
	@PatchMapping(value = "/workorder")
	public WorkOrderDTO updateWorkOrderPart(@RequestBody WorkOrderDTO dto) {
		return workOrderService.updateWorkOrderPart(dto);
	}

	/**
	 * 查询已完成的安装工单信息
	 */
	@GetMapping(value = "/workorder/complete")
	@ApiOperation(value = "已完成工单列表", notes = "已完成工单列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "engineerId", value = "安装工id", dataType = "Integer", required = true, paramType = "query"),
			@ApiImplicitParam(name = "consumerName", value = "用户名称", dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "consumerPhone", value = "用户手机号", dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "deviceSncode", value = "设备sn码", dataType = "String", paramType = "query")})
	public ResponseEntity<List<WorkOrderDTO>> getWorkOrderCompleteList(Integer engineerId, String consumerName,
	                                                                   String consumerPhone, String deviceSncode) {
		return ResponseEntity.ok(workOrderService.getWorkOrderCompleteList(engineerId, consumerName, consumerPhone,
				deviceSncode, WorkOrderStateEnum.WORKORDER_STATE_COMPLETE.state));
	}

	/**
	 * @description 更新安装工工单最后完成时间
	 */
	@PatchMapping(value = "/workorder/{hour}/finishTime")
	@ApiOperation(value = "更新安装工工单最后完成时间")
	@ApiImplicitParam(name = "hour", value = "小时", required = true, defaultValue = "1", dataType = "Long", paramType = "path")
	public Object updateLasteFinishTime(@PathVariable(value = "hour") Integer hour) {
		workOrderService.updateLasteFinishTime(hour);
		return ResponseEntity.noContent().build();
	}

	/**
	 * 描述：根据签约单号获取工单
	 *
	 * @param signOrderId 签约单号
	 **/
	@GetMapping(value = "/workorder/sign")
	@ApiOperation(value = "查询工单信息")
	@ApiImplicitParam(name = "signOrderId", value = "签约单号", dataType = "String", required = true, paramType = "query")
	public WorkOrderDTO getWorkOrderBySignOrderId(@RequestParam(value = "signOrderId") String signOrderId) {
		return workOrderService.getWorkOrderBySignOrderId(signOrderId);
	}

	@PostMapping(value = "/workorder/station/{pageNum}/{pageSize}")
	@ApiOperation(value = "站务系统查询工单列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", required = true, paramType = "path"),
			@ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", required = true, paramType = "path"),
			@ApiImplicitParam(name = "query", value = "查询信息", dataType = "WorkOrderQuery", paramType = "body")})
	public PageVO<WorkOrderResultDTO> getStationWorkOrderList(@PathVariable(value = "pageNum") Integer pageNum,
	                                                          @PathVariable(value = "pageSize") Integer pageSize, @RequestBody WorkOrderQuery query) {
		return workOrderService.getStationWorkOrderList(pageNum, pageSize, query);
	}

	/**
	 * -------------------------------云平台工单后台操作----start--------------------------------------------
	 **/

	/**
	 * 描述：云平台条件查询工单列表
	 **/
	@GetMapping(value = "/workorder/{operationType}/{pageNum}/{pageSize}")
	@ApiOperation(value = "云平台条件查询工单列表")
	public ResponseEntity<PageVO<WorkOrderResultDTO>> query(
			@PathVariable(value = "operationType") Integer operationType,
			@PathVariable(value = "pageNum") Integer pageNum, @PathVariable(value = "pageSize") Integer pageSize,
			@RequestParam(required = false) Integer payType, @RequestParam(required = false) String province,
			@RequestParam(required = false) String city, @RequestParam(required = false) String region,
			@RequestParam(required = false) Long orderId, @RequestParam(required = false) String workOrderId,
			@RequestParam(value = "startTime", required = false) Date startTime,
			@RequestParam(value = "endTime", required = false) Date endTime,
			@RequestParam(value = "cancelStartTime", required = false) Date cancelStartTime,
			@RequestParam(value = "cancelEndTime", required = false) Date cancelEndTime,
			@RequestParam(value = "deleteStartTime", required = false) Date deleteStartTime,
			@RequestParam(value = "deleteEndTime", required = false) Date deleteEndTime,
			@RequestParam(required = false) Integer backOrderStatus,
			@RequestParam(required = false) Integer backRefundStatus, @RequestParam(required = false) Integer roleLevel,
			@RequestParam(required = false) Date completeTime, @RequestParam(required = false) Date payTime,
			@RequestParam(required = false) String accountMonth,
			@RequestParam(required = false, defaultValue = "-1") Integer type,
			@RequestParam(required = false, defaultValue = "-3") Integer status,
			@RequestParam(value = "userId", required = false) Integer userId,
			@RequestParam(value = "payStartTime", required = false) Date payStartTime,
			@RequestParam(value = "payEndTime", required = false) Date payEndTime,
			@RequestParam(value = "finishStartTime", required = false) Date finishStartTime,
			@RequestParam(value = "finishEndTime", required = false) Date finishEndTime,
			@RequestParam(value = "finishMoneyStartTime", required = false) Date finishMoneyStartTime,
			@RequestParam(value = "finishMoneyEndTime", required = false) Date finishMoneyEndTime,
			@RequestParam(value = "terminal", required = false) Integer terminal,
			@RequestParam(value = "payTerminal", required = false) Integer payTerminal,
			@RequestParam(value = "chargeBackStatus", required = false) Integer chargeBackStatus,
			@RequestParam(value = "distributorId", required = false) Integer distributorId) {
		WorkOrderQueryDTO query = initWorkOrderQueryData(operationType, payType, province, city, region, orderId,
				workOrderId, startTime, endTime, roleLevel, completeTime, payTime, accountMonth, type, status,
				cancelStartTime, cancelEndTime, backOrderStatus, backRefundStatus, deleteStartTime, deleteEndTime,
				userId, finishStartTime, finishEndTime, finishMoneyStartTime, finishMoneyEndTime, payStartTime,
				payEndTime, terminal, payTerminal, chargeBackStatus, distributorId);
		return ResponseEntity.ok(workOrderService.selectWorkOrderList(query, pageNum, pageSize));
	}

	/****
	 * 组装工单查询数据
	 *
	 * @param chargeBackStatus
	 */
	private WorkOrderQueryDTO initWorkOrderQueryData(Integer operationType, Integer payType, String province,
	                                                 String city, String region, Long orderId, String workOrderId, Date startTime, Date endTime,
	                                                 Integer roleLevel, Date completeTime, Date payTime, String accountMonth, Integer type, Integer status,
	                                                 Date cancelStartTime, Date cancelEndTime, Integer backOrderStatus, Integer backRefundStatus,
	                                                 Date deleteStartTime, Date deleteEndTime, Integer userId, Date finishStartTime, Date finishEndTime,
	                                                 Date finishMoneyStartTime, Date finishMoneyEndTime, Date payStartTime, Date payEndTime, Integer terminal,
	                                                 Integer payTerminal, Integer chargeBackStatus, Integer distributorId) {
		WorkOrderQueryDTO query = new WorkOrderQueryDTO();
		query.setIncomeType(IncomeType.PRODUCT_INCOME.value);// 产品收益类型,查询工单列表和导出公用
		if (null != operationType) {
			query.setOperationType(operationType);
		} else {
			query.setOperationType(WorkOrderOperationType.WORK_ORDER.value);
		}
		query.setPayType(payType);
		query.setProvince(province);
		query.setCity(city);
		query.setRegion(region);
		query.setSubOrderId(orderId);
		query.setId(workOrderId);
		query.setStartTime(startTime);
		query.setEndTime(endTime);
		query.setDistributorRoleLevel(roleLevel);
		query.setCompleteTime(completeTime);
		query.setPayTime(payTime);
		query.setAccountMonth(accountMonth);
		query.setType(type);
		query.setStatus(status);
		query.setCancelStartTime(cancelStartTime);
		query.setCancelEndTime(cancelEndTime);
		query.setBackOrderStatus(backOrderStatus == null ? null : backOrderStatus.toString());
		query.setBackRefundStatus(backRefundStatus);
		query.setDeleteStartTime(deleteStartTime);
		query.setDeleteEndTime(deleteEndTime);
		query.setUserId(userId);
		query.setFinishStartTime(finishStartTime);
		query.setFinishEndTime(finishEndTime);
		query.setFinishMoneyStartTime(finishMoneyStartTime);
		query.setFinishMoneyEndTime(finishMoneyEndTime);
		query.setPayStartTime(payStartTime);
		query.setPayEndTime(payEndTime);
		query.setTerminal(terminal);
		query.setPayTerminal(payTerminal);
		query.setChargeBackStatus(chargeBackStatus);
		query.setDistributorId(distributorId);
		return query;
	}

	/**
	 * 描述：云平台导出工单
	 **/
//    @GetMapping("/workorder/export")
//    @ApiOperation(value = "云平台导出工单")
//    public ResponseEntity<List<WorkOrderExportDTO>> exportWorkOrderList(@RequestParam(value = "operationType",required = false) Integer operationType,
//													            @RequestParam(required = false) Integer payType,
//													            @RequestParam(required = false) String province,
//													            @RequestParam(required = false) String city,
//													            @RequestParam(required = false) String region,
//													            @RequestParam(required = false) Long orderId,
//													            @RequestParam(required = false) String workOrderId,
//													            @RequestParam(value = "startTime", required = false) Date startTime,
//													            @RequestParam(value = "endTime", required = false) Date endTime,
//													            @RequestParam(value = "cancelStartTime", required = false) Date cancelStartTime,
//													            @RequestParam(value = "cancelEndTime", required = false) Date cancelEndTime,
//													            @RequestParam(value = "deleteStartTime", required = false) Date deleteStartTime,
//													            @RequestParam(value = "deleteEndTime", required = false) Date deleteEndTime,
//													            @RequestParam(required = false) Integer backOrderStatus,
//													            @RequestParam(required = false) Integer backRefundStatus,
//													            @RequestParam(required = false) Integer roleLevel,
//													            @RequestParam(required = false) Date completeTime,
//													            @RequestParam(required = false) Date payTime,
//													            @RequestParam(required = false) String accountMonth,
//													            @RequestParam(required = false, defaultValue = "-1") Integer type,
//													            @RequestParam(required = false, defaultValue = "-3") Integer status,
//													            @RequestParam(value = "userId", required = false) Integer userId,
//													            @RequestParam(value = "payStartTime", required = false) Date payStartTime,
//													            @RequestParam(value = "payEndTime", required = false) Date payEndTime,
//													            @RequestParam(value = "finishStartTime", required = false) Date finishStartTime,
//													            @RequestParam(value = "finishEndTime", required = false) Date finishEndTime,
//													            @RequestParam(value = "finishMoneyStartTime", required = false) Date finishMoneyStartTime,
//													            @RequestParam(value = "finishMoneyEndTime", required = false) Date finishMoneyEndTime,
//													            @RequestParam(value = "terminal", required = false) Integer terminal,
//			                                                    @RequestParam(value = "payTerminal", required = false) Integer payTerminal) {
//
//    	WorkOrderQueryDTO query =initWorkOrderQueryData(operationType,payType,province,city,region,orderId,
//        		workOrderId,startTime,endTime,roleLevel,completeTime,payTime,accountMonth,type,status,cancelStartTime,
//        		cancelEndTime,backOrderStatus,backRefundStatus,deleteStartTime,deleteEndTime,userId,finishStartTime,finishEndTime,
//        		finishMoneyStartTime,finishMoneyEndTime,payStartTime,payEndTime,terminal,payTerminal);
//        return ResponseEntity.ok(workOrderService.exportWorkOrderList(query));
//    }

	/**
	 * 描述：云平台删除工单
	 *
	 * @param workOrderId 工单ID
	 **/
	@DeleteMapping(value = "/workorder/{workOrderId}")
	@ApiOperation(value = "云平台删除工单", notes = "云平台删除工单")
	@ApiImplicitParam(name = "workOrderId", value = "工单ID", dataType = "String", required = true, paramType = "path")
	public ResponseEntity deleteWorkOrder(@PathVariable(value = "workOrderId") String workOrderId) {
		workOrderService.deleteWorkOrder(workOrderId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * 描述：云平台分配客服--查询该工单所在地区下的安装工列表
	 *
	 * @param id 工单ID
	 **/
	@GetMapping(value = "/workorder/allot/engineer/{id}")
	@ApiOperation(value = "云平台分配客服")
	@ApiImplicitParam(name = "id", value = "工单ID", dataType = "String", required = true, paramType = "path")
	public ResponseEntity<List<EngineerDTO>> getAllotEngineerList(@PathVariable(value = "id") String id) {
		WorkOrderDTO workOrder = workOrderService.getWorkOrderById(id);
		if (workOrder == null) {
			throw new BadRequestException("该工单号不存在！");
		}
		String province = workOrder.getProvince();
		String city = workOrder.getCity();
		String region = workOrder.getRegion();
		Integer areaId = systemFeign.getRegionIdByPCR(province, city, region);
		List<EngineerDTO> list = userFeign.listEngineerByRegion(areaId);
		return ResponseEntity.ok(list);
	}

	/**
	 * 云平台分配客服--即分配安装工
	 *
	 * @param workOrderId 工单ID
	 * @param engineerId  安装工id
	 **/
	@PutMapping(value = "/workorder/allot/engineer")
	@ApiOperation(value = "云平台分配客服", notes = "云平台分配客服")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "workOrderId", value = "工单ID", dataType = "String", required = true, paramType = "query"),
			@ApiImplicitParam(name = "engineerId", value = "安装工id", dataType = "Long", required = true, paramType = "query")})
	public ResponseEntity allotEngineer(@RequestParam(value = "workOrderId") String workOrderId,
	                                    @RequestParam(value = "engineerId") Integer engineerId) {
		workOrderService.allotEngineer(workOrderId, engineerId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * 云平台工单评价
	 **/
	@PutMapping(value = "/workorder/appraise/score")
	@ApiOperation(value = "工单评价", notes = "工单评价")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "workOrderId", value = "工单ID", dataType = "String", required = true, paramType = "query"),
			@ApiImplicitParam(name = "score", value = "评分内容", dataType = "Integer", required = true, paramType = "query"),
			@ApiImplicitParam(name = "appraiseContent", value = "评分内容", dataType = "String", required = true, paramType = "query")})
	public void updateWorkOrderAppraiseScore(@RequestParam(value = "workOrderId") String workOrderId,
	                                         @RequestParam(value = "score") Integer score,
	                                         @RequestParam(value = "appraiseContent") String appraiseContent) {
		workOrderService.updateWorkOrderAppraiseScore(workOrderId, score, appraiseContent);
	}

	/**
	 * 描述：云平台工单概况--获取某个状态工单的数量
	 **/
	@GetMapping("/workorder/count/status")
	@ApiOperation(value = "云平台工单概况")
	public ResponseEntity<List<WorkOrderCountDTO>> countWorkOrderByStatus() {
		return ResponseEntity.ok(workOrderService.countWorkOrderByStatus());
	}

	/**
	 * 描述：云平台工单趋势--获取两个日期间的工单数量
	 **/
	@GetMapping(value = "/workorder/count/time")
	@ApiOperation(value = "云平台工单趋势")
	@ApiImplicitParams({@ApiImplicitParam(name = "days", value = "日期天数", dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "Date", paramType = "query", format = "yyyy-MM-dd"),
			@ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "Date", paramType = "query", format = "yyyy-MM-dd")})
	public ResponseEntity<List<WorkOrderCountDTO>> countWorkOrderByCreateTime(
			@RequestParam(value = "days", required = false) String days,
			@RequestParam(value = "startTime", required = false) Date startTime,
			@RequestParam(value = "endTime", required = false) Date endTime) {
		return ResponseEntity.ok(workOrderService.countWorkOrderByCreateTime(days, startTime, endTime));
	}

	/**
	 * 描述：云平台根据条件查询安装工单支付信息
	 */
	@GetMapping(value = "/workorder/pay/{pageNum}/{pageSize}")
	@ApiOperation(value = "云平台根据条件查询安装工单支付信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "dto", value = "工单条件查询对象", dataType = "WorkOrderQueryDTO", required = true, paramType = "body"),
			@ApiImplicitParam(name = "pageNum", value = "页码", required = true, defaultValue = "1", dataType = "Long", paramType = "path"),
			@ApiImplicitParam(name = "pageSize", value = "页数", required = true, defaultValue = "10", dataType = "Long", paramType = "path")})
	public ResponseEntity<PageVO<PayRecordDTO>> getWorkOrderPayList(@RequestBody WorkOrderQueryDTO dto,
	                                                                @PathVariable(value = "pageNum") Integer pageNum, @PathVariable(value = "pageSize") Integer pageSize) {
		return ResponseEntity.ok(workOrderService.getWorkOrderPayList(dto, pageNum, pageSize));
	}

	/**
	 * 描述：云平台--工单退单
	 *
	 * @param id 工单ID
	 **/
	@PutMapping(value = "/workorder/chargeback/{id}")
	@ApiOperation(value = "云平台退单")
	@ApiImplicitParam(name = "id", value = "工单ID", dataType = "String", required = true, paramType = "path")
	public void backOrderUpdate(@PathVariable(value = "id") String id) {
		workOrderService.backOrderUpdate(id);
	}

	/**
	 * 根据条件查询工单发票列表信息
	 */
	@GetMapping(value = "/workorder/invoice/{pageNum}/{pageSize}")
	@ApiOperation(value = "根据条件查询工单发票列表信息", notes = "根据条件查询工单发票列表信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "dto", value = "工单发票条件对象", dataType = "WorkOrderQueryDTO", required = true, paramType = "body"),
			@ApiImplicitParam(name = "pageNum", value = "页码", required = true, defaultValue = "1", dataType = "Long", paramType = "path"),
			@ApiImplicitParam(name = "pageSize", value = "页数", required = true, defaultValue = "10", dataType = "Long", paramType = "path")})
	public ResponseEntity<PageVO<WorkOrderDTO>> getWorkOrderInvoiceList(@RequestBody WorkOrderQueryDTO dto,
	                                                                    @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
		return ResponseEntity.ok(workOrderService.getWorkOrderInvoiceList(dto, pageNum, pageSize));
	}

//    /**
//     * 云平台导出工单发票信息
//     */
//    @GetMapping(value = "/workorder/invoice/export")
//    @ApiOperation(value = "云平台导出工单发票信息", notes = "云平台导出工单发票信息")
//    @ApiImplicitParam(name = "dto", value = "工单发票条件对象", dataType = "WorkOrderQueryDTO", required = true, paramType = "body")
//    public ResponseEntity<List<WorkOrderInvoiceExportDTO>> exportWorkOrderInvoiceList(@RequestBody WorkOrderQueryDTO dto) {
//        return ResponseEntity.ok(workOrderService.exportWorkOrderInvoiceList(dto));
//    }

	/**
	 * @description 根据工单id查询工单操作记录列表
	 */
	@GetMapping(value = "/workorder/operation/{id}/{pageNum}/{pageSize}")
	@ApiOperation(value = "根据工单id查询工单操作记录列表", notes = "根据工单id查询工单操作记录列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "工单id", dataType = "String", required = true, paramType = "path"),
			@ApiImplicitParam(name = "pageNum", value = "页码", required = true, defaultValue = "1", dataType = "Long", paramType = "path"),
			@ApiImplicitParam(name = "pageSize", value = "页数", required = true, defaultValue = "10", dataType = "Long", paramType = "path")})
	public ResponseEntity<PageVO<WorkOrderOperationDTO>> getWorkOrderOperationList(@PathVariable("id") String id,
	                                                                               @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
		return ResponseEntity.ok(workOrderService.getWorkOrderOperationList(id, pageNum, pageSize));
	}

	/**
	 * -------------------------------云平台工单后台操作----end--------------------------------------------
	 **/

	/**
	 * 根据用户手机号查询正常完成的工单
	 *
	 * @param mobile 手机号
	 */
	@GetMapping(value = "/order/getcompletedworkorder/{mobile}")
	public WorkOrderDTO getCompletedWorkOrderByMobile(@PathVariable String mobile) {
		return workOrderService.getCompletedWorkOrderByUserPhone(mobile);
	}

	@GetMapping(value = "/workorder/checkcompletepay")
	public Boolean checkWorkOrder180ComplatePay(@RequestParam(value = "completeOutTradeNo") String completeOutTradeNo) {
		return workOrderService.checkWorkOrder180ComplatePay(completeOutTradeNo);
	}

	/**
	 * 根据条件查询安装工单信息
	 */
	@GetMapping(value = "/workorder/list")
	@ApiOperation(value = "根据条件查询安装工单信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "engineerId", value = "安装工id", dataType = "Long", paramType = "query"),
			@ApiImplicitParam(name = "status", value = "工单状态", dataType = "Long", paramType = "query"),
			@ApiImplicitParam(name = "payStatus", value = "支付状态", dataType = "Long", paramType = "query"),
			@ApiImplicitParam(name = "completeType", value = "完成类型", dataType = "Long", paramType = "query")})
	public Object getWorkOrderInfo(@RequestParam Integer engineerId, @RequestParam(required = false) Integer status,
	                               @RequestParam(required = false) Integer payStatus, @RequestParam(required = false) Integer completeType) {
		return workOrderService.getWorkOrderListByquery(engineerId, status, payStatus, completeType);
	}

	/**
	 * 安装工拒单
	 */
	@PatchMapping(value = "/workorder/refuse")
	public void refuseWorkOrder(@RequestParam String workOrderId, @RequestParam Integer engineerId,
	                            @RequestParam String engineerName, @RequestParam String reason) {
		workOrderService.refuseWorkOrder(workOrderId, engineerId, engineerName, reason);
	}

	/**
	 * 安装工接单
	 */
	@PatchMapping(value = "/workorder/accept")
	public void acceptWorkOrder(@RequestParam String workOrderId, @RequestParam Integer engineerId,
	                            @RequestParam(required = false) Integer nextStep) {
		workOrderService.acceptWorkOrder(workOrderId, engineerId, nextStep);
	}

	/**
	 * 安装工中止工单
	 */
	@PatchMapping(value = "/workorder/discontinue")
	public void discontinueWorkOrder(@RequestParam String workOrderId, @RequestParam String reason,
	                                 @RequestParam String remark, @RequestParam Integer reasonNum) {
		workOrderService.discontinueWorkOrder(workOrderId, reason, remark, reasonNum);
	}

	/**
	 * 安装工更换设备
	 */
	@PatchMapping(value = "/workorder/changedevice")
	public void changedeviceWorkOrder(@RequestParam String workOrderId) {
		workOrderService.changedeviceWorkOrder(workOrderId);
	}

	/**
	 * 安装工继续服务
	 */
	@PatchMapping(value = "/workorder/continue")
	public void continueWorkOrder(@RequestParam String workOrderId) {
		workOrderService.continueWorkOrder(workOrderId);
	}

	/**
	 * 安装工退单
	 */
	@PatchMapping(value = "/workorder/chargeback")
	public void chargebackWorkOrder(@RequestParam String workOrderId, @RequestParam String reason, @RequestParam String remark, @RequestParam Integer reasonNum) {
		workOrderService.chargebackWorkOrder(workOrderId, reason, remark, reasonNum);
	}


	/**
	 * 安装工完成工单
	 */
	@PatchMapping(value = "/workorder/complete")
	public void completeWorkOrder(@RequestParam String workOrderId) {
		workOrderService.completeWorkOrder(workOrderId);
	}

	/**
	 * 根据物流编码查询工单是否存在
	 */
	@GetMapping(value = "/workorder/exists/logisticsCode")
	public Boolean existsWithLogisticsCode(@RequestParam String logisticsCode) {
		return workOrderService.existsWithLogisticsCode(logisticsCode);
	}

	/**
	 * 安装工更换设备型号
	 */
	@PatchMapping(value = "/workorder/changeProductAndFee")
	public void changeProductAndCostByEngineer(@RequestParam(name = "workOrderId") String workOrderId,
	                                           @RequestParam(name = "productId") Integer productId,
	                                           @RequestParam(name = "costId") Integer costId,
	                                           @RequestParam(value = "logisticsCode", required = false) String logisticsCode,
	                                           @RequestParam(value = "snCode", required = false) String snCode,
	                                           @RequestParam(value = "simCard", required = false) String simCard,
	                                           @RequestParam(name = "type") Integer type) {
		workOrderService.changeProductAndCost(workOrderId, productId, costId, logisticsCode, snCode, simCard, type);
	}

	/**
	 * 站务系统- 控制台-待办事项(工单数与续费数）
	 *
	 * @param query
	 * @return
	 */
	@PostMapping(value = "/workorder/station/workerOrderAndRenewNum")
	public StationScheduleDTO getStationWorkerOrderAndRenewNum(@RequestBody WorkOrderQuery query) {
		// 工单数
		StationScheduleDTO workorder = workOrderService.getStationWorkerOrderNum(query.getEngineerIds());
		// 续费数
		StationScheduleDTO reneworder = orderRenewService.getStationRenewOrderNum(query.getEngineerIds());

		workorder.setRenewNum(reneworder.getRenewNum());
		workorder.setYesterdayRenewNum(reneworder.getYesterdayRenewNum());

		return workorder;
	}

	/***
	 * 获取未完成和退单中的工单数量
	 *
	 * @param transferAreaInfoDTO
	 * @return
	 */
	@PostMapping(value = "/workorder/unfinished", consumes = MediaType.APPLICATION_JSON_VALUE)
	public List<WorkOrderUnfinishedRsDTO> getWorkOrderForUnfinished(
			@RequestBody TransferAreaInfoDTO transferAreaInfoDTO) {
		return workOrderService.queryWorkOrderForUnfinished(transferAreaInfoDTO);

	}

	/***
	 * 务站公司服务区域承包转让 未完成的、退单中的工单和订单
	 *
	 * @param transferAreaInfoDTOS
	 */
	@PostMapping(value = "/workorder/transfer", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateOrderForEngineerStation(@RequestBody List<TransferAreaInfoDTO> transferAreaInfoDTOS) {
		orderTransferService.updateOrderDeviceForEngineerStation(transferAreaInfoDTOS);
	}

	/***
	 * 安装工app新版本--获取安装工的给类型工单的个数
	 *
	 * @param engineerId
	 * @return
	 */
	@GetMapping(value = "/workorder/install/stat/info")
	public List<WorkOrderStatDTO> getWorkOrderStatInfo(@RequestParam("engineerId") Integer engineerId) {
		return workOrderService.getWorkOrderStatInfo(engineerId);

	}

	/****
	 * 安装工app新版本--获取某个状态的工单列表数据
	 *
	 * @param query
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@GetMapping(value = "/workorder/install/list/{pageNum}/{pageSize}")
	public PageVO<WorkOrderRsDTO> getWorkOrderList(@RequestParam("engineerId") Integer engineerId,
	                                               @RequestParam("status") Integer status,
	                                               @RequestParam(value = "search", required = false) String search,
	                                               @RequestParam(value = "sortType") Integer sortType,
	                                               @RequestParam(value = "sortBy") Integer sortBy,
	                                               @RequestParam(value = "longitude", required = false) String longitude,
	                                               @RequestParam(value = "latitude", required = false) String latitude,
	                                               @PathVariable("pageNum") Integer pageNum,
	                                               @PathVariable("pageSize") Integer pageSize) {
		WorkOrderReqDTO query = new WorkOrderReqDTO();
		query.setEngineerId(engineerId);
		query.setStatus(status);
		query.setSearch(search);
		query.setSortType(sortType);
		query.setLongitude(longitude);
		query.setLatitude(latitude);
		query.setSortBy(sortBy);
		return workOrderService.getWorkOrderListForEngineer(query, pageNum, pageSize);
	}

	/****
	 * 安装工app新版本---退单
	 *
	 * @param req
	 */
	@PostMapping(value = "/workorder/chargeback/engineer")
	public void chargeback(@RequestBody WorkOrderReqDTO req) {
		workOrderService.chargeback(req);
	}


	/**
	 * 服务统计-工单服务统计-安装统计
	 *
	 * @param completeTime
	 * @return
	 */
	@GetMapping(value = "/workorder/install/complete/statistics")
	public List<RenewDTO> statisticsInstallWorderOrder(@RequestParam(value = "completeTime") String completeTime,
	                                                   @RequestParam(value = "engineerId") Integer engineerId,
	                                                   @RequestParam(value = "timeType") Integer timeType) {
		return workOrderService.getInstallWorderOrderList(completeTime, engineerId, timeType);
	}


	//======================================================安装工app地图工单=========================================//

	@GetMapping(value = "/workerorder/map/{engineerId}")
	@ApiOperation(value = "根据安装工id获取该安装工的安装工单")
	@ApiImplicitParam(name = "engineerId", value = "安装工id", dataType = "Long", paramType = "path", required = true)
	public Map<String, List<MapOrderDTO>> getInstallWorkOrder(@PathVariable Integer engineerId) {
		return workOrderService.getInstallWorkOrder(engineerId);
	}


	/**
	 * 安装模块下工单数量统计
	 * @param engineerId
	 * @return
	 */
	@GetMapping(value = "/workorder/install/device/statistics/count")
	public Map<String, Integer> getInstallWaterDeviceCount(@RequestParam(value = "engineerId") Integer engineerId) {
		return workOrderService.getInstallWaterDeviceCount(engineerId);
	}


	/**
	 * 安装模块总工单数量
	 * @param engineerId
	 * @return
	 */
	@GetMapping(value = "/workorder/install/device/total/number")
	public Integer getInstallWaterDeviceTotalNum(@RequestParam(value = "engineerId") Integer engineerId) {
		return workOrderService.getInstallWaterDeviceTotalNum(engineerId);
	}

}
