package com.yimao.cloud.engineer.feign;

import com.github.pagehelper.Page;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.order.*;
import com.yimao.cloud.pojo.query.order.WorkOrderBackQueryDTO;
import com.yimao.cloud.pojo.query.order.WorkRepairOrderQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.order.MoveWaterDeviceOrderVO;
import com.yimao.cloud.pojo.vo.station.WorkRepairOrderVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 描述：订单微服务的接口列表
 *
 * @author Zhang Bo
 * @date 2019/3/9.
 */
@FeignClient(name = Constant.MICROSERVICE_ORDER)
public interface OrderFeign {

	/***
	 * 获取安装工的给类型工单的个数
	 *
	 * @param engineerId
	 * @return
	 */
	@GetMapping(value = "/workorder/install/stat/info")
	List<WorkOrderStatDTO> getWorkOrderStatInfo(@RequestParam("engineerId") Integer engineerId);

	/***
	 * 根据条件搜索工单列表
	 *
	 * @param query
	 * @param pageSize
	 * @param pageNum
	 * @return
	 */
	@GetMapping(value = "/workorder/install/list/{pageNum}/{pageSize}")
	PageVO<WorkOrderRsDTO> getWorkOrderList(@RequestParam("engineerId") Integer engineerId,
	                                        @RequestParam("status") Integer status,
	                                        @RequestParam(value = "search", required = false) String search,
	                                        @RequestParam("sortType") Integer sortType,
	                                        @RequestParam("sortBy") Integer sortBy,
	                                        @RequestParam(value = "longitude", required = false) String longitude,
	                                        @RequestParam(value = "latitude", required = false) String latitude,
	                                        @PathVariable("pageNum") Integer pageNum,
	                                        @PathVariable("pageSize") Integer pageSize);

	/**
	 * 描述：根据工单id获取工单信息
	 *
	 * @param workOrderId 工单ID
	 **/
	@GetMapping(value = "/workorder/{workOrderId}")
	WorkOrderDTO getWorkOrderById(@PathVariable(value = "workOrderId") String workOrderId);

	/****
	 * 拒绝工单
	 *
	 * @param workOrderId
	 * @param reason
	 */
	@PatchMapping(value = "/workorder/refuse")
	void refuseWorkOrder(@RequestParam(value = "workOrderId") String workOrderId,
	                     @RequestParam(value = "engineerId") Integer engineerId,
	                     @RequestParam(value = "engineerName") String engineerName,
	                     @RequestParam(value = "reason") String reason);


	/****
	 * 接单
	 * @param workOrderId
	 * @param engineerId
	 * @param nextStep
	 */
	@PatchMapping(value = "/workorder/accept")
	void acceptWorkOrder(@RequestParam(value = "workOrderId") String workOrderId,
	                     @RequestParam(value = "engineerId") Integer engineerId,
	                     @RequestParam(value = "nextStep") Integer nextStep);

	/***
	 * 安装工退单
	 * @param workOrderId
	 * @param reason
	 * @param remark
	 */
	@PatchMapping(value = "/workorder/chargeback")
	void chargebackWorkOrder(@RequestParam(value = "workOrderId") String workOrderId,
	                         @RequestParam(value = "reason") String reason,
	                         @RequestParam(value = "remark") String remark);

	/***
	 * 退单
	 * @param req
	 */
	@PostMapping(value = "/workorder/chargeback/engineer", consumes = MediaType.APPLICATION_JSON_VALUE)
	void chargeback(@RequestBody WorkOrderReqDTO req);

	/**
	 * 修改安装工单
	 *
	 * @param workOrderDTO 工单对象
	 */
	@PutMapping(value = "/workorder", consumes = MediaType.APPLICATION_JSON_VALUE)
	WorkOrderDTO updateWorkOrder(@RequestBody WorkOrderDTO workOrderDTO);

	/**
	 * 安装工操作更换设备型号
	 */
	@PatchMapping(value = "/workorder/changeProductAndFee")
	void changeWorkOrderProductAndCostByEngineer(@RequestParam(name = "workOrderId") String workOrderId,
	                                             @RequestParam(name = "productId") Integer productId,
	                                             @RequestParam(name = "costId") Integer costId,
	                                             @RequestParam(value = "logisticsCode", required = false) String logisticsCode,
	                                             @RequestParam(value = "snCode", required = false) String snCode,
	                                             @RequestParam(value = "simCard", required = false) String simCard,
	                                             @RequestParam(name = "type") Integer type);

	/****
	 * 安装工完成工单
	 * @param workOrderId
	 */
	@PatchMapping(value = "/workorder/complete")
	void completeWorkOrder(@RequestParam(value = "workOrderId") String workOrderId);

	//微信支付相关服务
	@PostMapping(value = "/wxpay/unified/order", consumes = MediaType.APPLICATION_JSON_VALUE)
	String wechatScanCodePay(@RequestBody WechatPayRequest payRequest);

	@PostMapping(value = "/wxpay/unified/order", consumes = MediaType.APPLICATION_JSON_VALUE)
	Object unifiedorder(@RequestBody WechatPayRequest payRequest);

	//微信支付订单查询
	@PostMapping(value = "/wxpay/query/order", consumes = MediaType.APPLICATION_JSON_VALUE)
	Object orderQuery(@RequestBody WechatPayRequest payRequest);

	//支付宝支付相关服务
	@PostMapping(value = "/alipay/tradeprecreate", consumes = MediaType.APPLICATION_JSON_VALUE)
	String aliScanCodePay(@RequestBody AliPayRequest payRequest);

	@PostMapping(value = "/alipay/tradeapp", consumes = MediaType.APPLICATION_JSON_VALUE)
	Object tradeapp(@RequestBody AliPayRequest payRequest);

	//支付宝支付订单查询
	@PostMapping(value = "/alipay/tradequery/order", consumes = MediaType.APPLICATION_JSON_VALUE)
	Object tradequery(@RequestBody AliPayRequest payRequest);

	/**
	 * 获取支付账号
	 *
	 * @param companyId
	 * @param platform
	 * @param clientType
	 * @param receiveType
	 * @return
	 */
	@PostMapping(value = "/payaccount")
	PayAccountDetail getPayAccount(@RequestParam(value = "companyId") Integer companyId, @RequestParam(value = "platform") Integer platform, @RequestParam(value = "clientType") Integer clientType, @RequestParam(value = "receiveType") Integer receiveType);

	/***
	 * 其他支付
	 * @param wo
	 */
	@PostMapping(value = "/otherpay/submit", consumes = MediaType.APPLICATION_JSON_VALUE)
	void otherPay(@RequestBody WorkOrderDTO wo);

	/**
	 * 描述： 根据签约单号获取工单
	 *
	 * @param signOrderId 签约单号
	 */
	@GetMapping(value = "/workorder/sign")
	WorkOrderDTO getWorkOrderBySignOrderId(@RequestParam(value = "signOrderId") String signOrderId);

	/**
	 * 根据条件查询退机工单信息
	 */
	@GetMapping(value = "/workorderBack/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
	PageVO<WorkOrderBackDTO> getWorkOrderBackList(@RequestBody WorkOrderBackQueryDTO query, @PathVariable(value = "pageNum") Integer pageNum, @PathVariable(value = "pageSize") Integer pageSize);

	/**
	 * 根据安装工查询退机工单数量
	 */
	@GetMapping(value = "/workorderBack/engineer/count")
	List<Map<String, Object>> getWorkOrderBackCountByEngineerId();

	/**
	 * 描述：根据工单id获取退机工单信息
	 *
	 * @param id 退机工单ID
	 **/
	@GetMapping(value = "/workorderBack/{id}")
	WorkOrderBackDTO getWorkOrderBackById(@PathVariable(value = "id") Integer id);

	/**
	 * 创建退机工单
	 *
	 * @param workOrderBackDTO 退机工单
	 */
	@PostMapping(value = "/workorderBack")
	void createWorkOrderBack(@RequestBody WorkOrderBackDTO workOrderBackDTO);

	/**
	 * 修改退机工单
	 *
	 * @param workOrderBackDTO 工单
	 */
	@PutMapping(value = "/workorderBack")
	void updateWorkOrderBack(@RequestBody WorkOrderBackDTO workOrderBackDTO);

	/**
	 * 描述：确认提交
	 **/
	@PutMapping(value = "/workorderBack/{id}/finish")
	void finishWorkOrderBack(@PathVariable(value = "id") Integer id, @RequestParam(value = "snCode") String snCode);

	/**
	 * 新安装工app - 移机工单 - 待处理移机工单列表
	 */
	@GetMapping(value = "/move/water/device/order/waitDispose/list")
	List<MoveWaterDeviceOrderVO> getWaitDisposeList(@RequestParam(value = "engineerId") Integer engineerId,
	                                                @RequestParam(value = "sort", required = false) Boolean sort,
	                                                @RequestParam(value = "serviceType") Integer serviceType,
	                                                @RequestParam(value = "longitude") Double longitude,
	                                                @RequestParam(value = "latitude") Double latitude);

	/**
	 * 新安装工app - 移机工单 - 处理中移机工单列表
	 */
	@GetMapping(value = "/move/water/device/order/dispose/list")
	List<MoveWaterDeviceOrderVO> getDisposeList(@RequestParam(value = "engineerId") Integer engineerId,
	                                            @RequestParam(value = "sort", required = false) Boolean sort,
	                                            @RequestParam(value = "longitude") Double longitude,
	                                            @RequestParam(value = "latitude") Double latitude);

	/**
	 * 新安装工app - 移机工单 - 挂单列表
	 */
	@GetMapping(value = "/move/water/device/order/pending/list")
	List<MoveWaterDeviceOrderVO> getPendingList(@RequestParam(value = "engineerId") Integer engineerId,
	                                            @RequestParam(value = "sort", required = false) Boolean sort,
	                                            @RequestParam(value = "longitude") Double longitude,
	                                            @RequestParam(value = "latitude") Double latitude);

	/**
	 * 新安装工app - 移机工单 - 已完成移机工单列表
	 */
	@GetMapping(value = "/move/water/device/order/complete/list/{pageNum}/{pageSize}")
	Page<MoveWaterDeviceOrderVO> getCompleteList(@PathVariable(value = "pageNum") Integer pageNum,
	                                             @PathVariable(value = "pageSize") Integer pageSize,
	                                             @RequestParam(value = "engineerId") Integer engineerId,
	                                             @RequestParam(value = "sort", required = false) Boolean sort);

	/**
	 * 新安装工app - 移机工单 - 待处理 - 拆机服务开始
	 *
	 * @param id 移机工单id
	 */
	@PatchMapping(value = "/move/water/device/order/dismantle/{id}")
	MoveWaterDeviceOrderVO dismantle(@PathVariable(value = "id") String id);

	/**
	 * 新安装工app - 移机工单 - 处理中 - 继续拆机服务
	 *
	 * @param id 移机工单id
	 */
	@PatchMapping(value = "/move/water/device/order/continueDismantle/{id}")
	MoveWaterDeviceOrderVO continueDismantle(@PathVariable(value = "id") String id);

	/**
	 * 新安装工app - 移机工单 - 处理中 - 点击“待移入地处理”回显拆机安装工信息
	 *
	 * @param id 移机工单id
	 */
	@PatchMapping(value = "/move/water/device/order/waitDismantle/{id}")
	MoveWaterDeviceOrderVO waitDismantle(@PathVariable(value = "id") String id);

	/**
	 * 完成拆机
	 *
	 * @param id         移机工单id
	 * @param engineerId 拆机安装工id
	 */
	@PatchMapping(value = "/move/water/device/order/completeDismantle/{id}")
	Map<String, Integer> completeDismantle(@PathVariable(value = "id") String id,
	                                       @RequestParam(value = "engineerId") Integer engineerId);

	/**
	 * 新安装工app - 移机工单 - 待处理 - 移入安装服务开始
	 *
	 * @param id 移机工单id
	 */
	@PatchMapping(value = "/move/water/device/order/install/{id}")
	MoveWaterDeviceOrderVO install(@PathVariable(value = "id") String id);

	/**
	 * 新安装工app - 移机工单 - 处理中 - 继续移入安装服务
	 *
	 * @param id 移机工单id
	 */
	@PatchMapping(value = "/move/water/device/order/continueInstall/{id}")
	MoveWaterDeviceOrderVO continueInstall(@PathVariable(value = "id") String id);

	/**
	 * 点击“等待拆机完成”回显装机安装工信息
	 *
	 * @param id 移机工单id
	 */
	@PatchMapping(value = "/move/water/device/order/waitInstall/{id}")
	MoveWaterDeviceOrderVO waitInstall(@PathVariable(value = "id") String id);

	/**
	 * 完成移机工单
	 *
	 * @param id 移机工单id
	 */
	@PatchMapping(value = "/move/water/device/order/complete/{id}")
	Void complete(@PathVariable(value = "id") String id);

	/**
	 * 新安装工app 移机工单完成列表获取工单详情
	 *
	 * @param id 移机工单id
	 */
	@GetMapping(value = "/move/water/device/order/app/details/{id}")
	MoveWaterDeviceOrderVO appGetMoveWaterDeviceDetails(@PathVariable(value = "id") String id);

	/**
	 * 新安装工app 移机工单挂单操作
	 *
	 * @param id         移机工单id
	 * @param type       1-拆机挂单 2-移入挂单
	 * @param cause      改约原因
	 * @param startTime  改约后服务时间（开始）
	 * @param endTime    改约后服务时间（结束）
	 * @param engineerId 操作安装工id
	 */
	@GetMapping(value = "/move/water/device/order/hangUp/{id}")
	Void hangUp(@PathVariable(value = "id") String id,
	            @RequestParam(value = "type") Integer type,
	            @RequestParam(value = "cause") String cause,
	            @RequestParam(value = "startTime") Date startTime,
	            @RequestParam(value = "endTime") Date endTime,
	            @RequestParam(value = "engineerId") Integer engineerId);


	/**
	 * 维修工单列表
	 *
	 * @param search
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@PostMapping(value = "/repair/workorder/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
	PageVO<WorkRepairOrderVO> pageRepairOrders(@RequestBody WorkRepairOrderQuery search,
	                                           @PathVariable("pageNum") Integer pageNum,
	                                           @PathVariable("pageSize") Integer pageSize);

	/**
	 * 创建维修工单
	 *
	 * @param dto
	 * @return
	 */
	@PostMapping(value = "/repair/workorder", consumes = MediaType.APPLICATION_JSON_VALUE)
	Object createRepairOrder(@RequestBody WorkRepairOrderDTO dto);

	/**
	 * 改约维修工单修改
	 *
	 * @param dto
	 */
	@PutMapping(value = "/repair/workorder/renegotiation", consumes = MediaType.APPLICATION_JSON_VALUE)
	void renegotiationRepairOrderUpdate(@RequestBody WorkRepairOrderDTO dto);

	/**
	 * 维修工单处理中步骤流转
	 *
	 * @param dto
	 * @return
	 */
	@PutMapping(value = "/repair/workorder/processStepChange", consumes = MediaType.APPLICATION_JSON_VALUE)
	WorkRepairOrderVO processRepairOrderChange(@RequestBody WorkRepairOrderDTO dto);

	/**
	 * 根据维修单号查询维修单
	 *
	 * @param workOrderNo
	 * @return
	 */
	@GetMapping(value = "/repair/workorder/getRepairOrderByWorkOrderNo")
	WorkRepairOrderVO getRepairOrderByWorkOrderNo(@RequestParam("workOrderNo") String workOrderNo);


	/**
	 * 维修工单变更完成状态
	 *
	 * @param id
	 */
	@PutMapping(value = "/repair/workorder/submit/{id}")
	void submitRepairOrder(@PathVariable("id") Integer id);

	/**
	 * 获取故障类型列表
	 *
	 * @param pageNum
	 * @param pageSize
	 * @param type
	 * @return
	 */
	@PostMapping(value = "/repair/fault/{pageNum}/{pageSize}")
	Object pageRepairFault(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize, @RequestParam(value = "type", required = false) Integer type);


	/**
	 * 维修工单继续服务返回展示信息
	 *
	 * @param workOrderNo
	 * @return
	 */
	@GetMapping(value = "/repair/workorder/continueRepairServiveInfo/{id}/{step}")
	WorkRepairOrderVO continueRepairServiveInfo(@PathVariable("id") Integer workOrderNo, @PathVariable("step") Integer step);


	/**
	 * 移机模块工单数量
	 *
	 * @param engineerId
	 * @return
	 */
	@GetMapping(value = "/move/water/device/statistics/count")
	Map<String, Integer> getMoveWaterDeviceCount(@RequestParam(value = "engineerId") Integer engineerId);

	/**
	 * 服务统计-工单服务统计-安装统计
	 *
	 * @param completeTime
	 * @param engineerId
	 * @return
	 */
	@GetMapping(value = "/workorder/install/complete/statistics")
	List<RenewDTO> statisticsInstallWorderOrder(@RequestParam(value = "completeTime") String completeTime,
	                                            @RequestParam(value = "engineerId") Integer engineerId,
	                                            @RequestParam(value = "timeType") Integer timeType);


	/**
	 * 服务统计-工单服务-维修统计
	 *
	 * @param completeTime
	 * @param engineerId
	 * @return
	 */
	@GetMapping(value = "/order/repairWorkOrder/statistics")
	List<RenewDTO> statisticsRepairWorkOrder(@RequestParam(value = "completeTime") String completeTime,
	                                         @RequestParam(value = "engineerId") Integer engineerId,
	                                         @RequestParam(value = "timeType") Integer timeType);


	/**
	 * 服务统计-工单服务-维护统计
	 *
	 * @param completeTime
	 * @param engineerId
	 * @return
	 */
	@GetMapping(value = "/order/maintenanceWorkOrder/statistics")
	List<RenewDTO> getMaintenanceWorkOrderList(@RequestParam(value = "completeTime") String completeTime,
	                                           @RequestParam(value = "engineerId") Integer engineerId,
	                                           @RequestParam(value = "timeType") Integer timeType);


	/**
	 * 服务统计-工单服务-退机统计
	 *
	 * @param completeTime
	 * @param engineerId
	 * @return
	 */
	@GetMapping(value = "/workorderBack/statistics/list")
	List<RenewDTO> statisticsWorkOrderBack(@RequestParam(value = "completeTime") String completeTime,
	                                       @RequestParam(value = "engineerId") Integer engineerId,
	                                       @RequestParam(value = "timeType") Integer timeType);


	/**
	 * 服务统计-工单服务-移机统计
	 *
	 * @param completeTime
	 * @param engineerId
	 * @return
	 */
	@GetMapping(value = "/move/water/device/list")
	List<RenewDTO> getMoveWaterDeviceList(@RequestParam(value = "completeTime") String completeTime,
	                                      @RequestParam(value = "engineerId") Integer engineerId,
	                                      @RequestParam(value = "timeType") Integer timeType);


	//======================================================安装工app地图工单=========================================//
	@GetMapping(value = "/workerorder/map/{engineerId}")
	Map<String, List<MapOrderDTO>> getMapOrder(@PathVariable(value = "engineerId") Integer engineerId);


	/**
	 * 市场服务-安装模块工单统计
	 *
	 * @param engineerId
	 * @return
	 */
	@GetMapping(value = "/workorder/install/device/statistics/count")
	Map<String, Integer> getInstallWaterDeviceCount(@RequestParam(value = "engineerId") Integer engineerId);


	/**
	 * 市场服务-维修模块工单统计
	 *
	 * @param engineerId
	 * @return
	 */
	@GetMapping(value = "/repair/workorder/count/census")
	Map<String, Integer> getRepairOrderCount(@RequestParam(value = "engineerId") Integer engineerId);

	/**
	 * 市场服务-维护模块工单统计
	 *
	 * @param engineerId
	 * @return
	 */
	@GetMapping(value = "/order/maintenance/workorder/statistics/count")
	Map<String, Integer> getMaintenanceWorkOrderCount(@RequestParam(value = "engineerId") Integer engineerId);


	/**
	 * 市场服务-退机模块工单统计
	 *
	 * @param engineerId
	 * @return
	 */
	@GetMapping(value = "/workorder/back/statistics/count")
	Map<String, Integer> getWorkOrderBackCount(@RequestParam(value = "engineerId") Integer engineerId);

	/**
	 * 查询维护工单列表
	 */
	@GetMapping(value = "/order/maintenanceWorkOrder/app/{pageNum}/{pageSize}")
	Object listMaintenanceWorkOrderForClient(@RequestParam(value = "distributorId", required = false) String distributorId,
	                                         @RequestParam(value = "engineerId", required = false) Integer engineerId,
	                                         @RequestParam(value = "state", required = false) Integer state,
	                                         @RequestParam(value = "search", required = false) String search,
	                                         @RequestParam(value = "longitude", required = false) Double longitude,
	                                         @RequestParam(value = "latitude", required = false) Double latitude,
	                                         @PathVariable("pageNum") Integer pageNum,
	                                         @PathVariable("pageSize") Integer pageSize);


	/**
	 * 功能描述：根据状态查询维护工单数量
	 *
	 * @param engineerId 安装工id
	 */
	@GetMapping(value = "/order/maintenanceWorkOrder/count")
	Object getWorkOrderMaintenanceCount(@RequestParam(value = "engineerId", required = false) Integer engineerId);


	/**
	 * 功能描述：修改维护工单信息
	 * 场景：开始维护/改约/确认维护完成
	 */
	@PutMapping(value = "/order/maintenanceWorkOrder/hang", consumes = MediaType.APPLICATION_JSON_VALUE)
	Object hangMaintenanceWorkOrder(@RequestBody MaintenanceDTO maintenanceDTO);


	/**
	 * 描述：已完成维护工单
	 *
	 * @param engineerId 安装工ID
	 * @return
	 * @description 已完成维护工单
	 */
	@GetMapping(value = "/order/maintenanceWorkOrder/app/complete/{pageNum}/{pageSize}")
	Object maintenanceWorkOrderCompleteList(@RequestParam(value = "engineerId", required = false) Integer engineerId,
	                                        @RequestParam(value = "sortType", required = false) Integer sortType,
	                                        @PathVariable("pageNum") Integer pageNum,
	                                        @PathVariable("pageSize") Integer pageSize);

	/**
	 * 描述：维护工单记录详情
	 *
	 * @param engineerId   安装工ID
	 * @param deviceSncode SN码
	 * @return
	 * @description 维护记录列表
	 */
	@GetMapping(value = "/order/maintenanceWorkOrder/app/record/detail")
	Object maintenanceWorkOrderRecordDetail(@RequestParam(value = "engineerId") Integer engineerId,
	                                        @RequestParam(value = "deviceSncode") String deviceSncode);


	/**
	 * 安装模块总工单数量
	 * @param engineerId
	 * @return
	 */
	@GetMapping(value = "/workorder/install/device/total/number")
	Integer getInstallWaterDeviceTotalNum(@RequestParam(value = "engineerId") Integer engineerId);

	/**
	 * 维修模块总工单数量
	 * @param engineerId
	 * @return
	 */
	@GetMapping(value = "/repair/model/workorder/count/total")
	public Integer getRepairModelTotalCount(@RequestParam(value = "engineerId") Integer engineerId);


	/**
	 * 维护模块总工单数量
	 * @param engineerId
	 * @return
	 */
	@GetMapping(value = "/order/maintenance/workorder/count/total")
	Integer getMaintenanceModelWorkOrderTotalCount(@RequestParam(value = "engineerId") Integer engineerId);


	/**
	 * 移机模块总工单数量
	 * @param engineerId
	 * @return
	 */
	@GetMapping(value = "/move/model/water/device/total")
	Integer getMoveModelTotalCount(@RequestParam(value = "engineerId") Integer engineerId);


	/**
	 * 退机模块总工单数量
	 * @param engineerId
	 * @return
	 */
	@GetMapping(value = "/back/model/water/order/total")
	Integer getBackModelTotalCount(@RequestParam(value = "engineerId") Integer engineerId);
}
