package com.yimao.cloud.engineer.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import com.yimao.cloud.pojo.dto.order.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.WorkOrderInstallNewStep;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.engineer.feign.OrderFeign;
import com.yimao.cloud.engineer.feign.ProductFeign;
import com.yimao.cloud.engineer.feign.UserFeign;
import com.yimao.cloud.engineer.service.WorkOrderBusinessService;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderReqDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderRsDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderStatDTO;
import com.yimao.cloud.pojo.dto.product.ProductCostDTO;
import com.yimao.cloud.pojo.dto.product.ProductDTO;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.vo.PageVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/****
 * 安装工app工单相关服务
 *
 * @author zhangbaobao
 *
 */
@Api(tags = "WorkOrderController")
@RestController
public class WorkOrderController {
	@Resource
	private OrderFeign orderFeign;
	@Resource
	private UserFeign userFeign;
	@Resource
	private UserCache userCache;
	@Resource
	private ProductFeign productFeign;
	@Resource
	private WorkOrderBusinessService workOrderBusinessService;

	/****
	 * 获取安装工单(待安装、待处理、挂单、已完成、退单)统计数据
	 *
	 * @return
	 */
	@ApiOperation(value = "获取安装工单(待安装、待处理、挂单、已完成、退单)数量")
	@GetMapping(value = "/workorder/install/stat/info")
	public Object getWorkOrderStatInfo() {
		Integer engineerId = userCache.getCurrentEngineerId();
		List<WorkOrderStatDTO> wosd = orderFeign.getWorkOrderStatInfo(engineerId);
		return ResponseEntity.ok(wosd);
	}

	/****
	 * 获取某个状态的工单列表
	 * @return
	 */
	@ApiOperation(value = "获取安装工单(待安装、待处理、挂单、已完成、退单)工单列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
			@ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path"),
			@ApiImplicitParam(name = "status", value = "工单类型描述:1.待安装,2.处理中,3.挂单,4.已完成,5.退单", dataType = "String", required = true, paramType = "query"),
			@ApiImplicitParam(name = "search", value = "搜索条件", dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "longitude", value = "经度", dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "latitude", value = "纬度", dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "sortBy", value = "排序规则：1-创建时间 2-距离 3-完成时间 4-退单时间", dataType = "Long", defaultValue = "2", required = true, paramType = "query"),
			@ApiImplicitParam(name = "sortType", value = "排序类型:1-升序 2-降序", dataType = "Long", defaultValue = "1", required = true, paramType = "query")
	})
	@GetMapping(value = "/workorder/install/list/{pageNum}/{pageSize}")
	public Object getWorkOrderList(@PathVariable(value = "pageNum") Integer pageNum,
	                               @PathVariable(value = "pageSize") Integer pageSize,
	                               @RequestParam(value = "status") Integer status,
	                               @RequestParam(value = "sortType") Integer sortType,
	                               @RequestParam(value = "sortBy") Integer sortBy,
	                               @RequestParam(value = "longitude", required = false) String longitude,
	                               @RequestParam(value = "latitude", required = false) String latitude,
	                               @RequestParam(value = "search", required = false) String search) {
		Integer engineerId = userCache.getCurrentEngineerId();
		PageVO<WorkOrderRsDTO> wosdList = orderFeign.getWorkOrderList(engineerId, status, search, sortType, sortBy, longitude, latitude, pageNum, pageSize);
		return ResponseEntity.ok(wosdList);
	}

	/****
	 * 获取安装工单详情
	 * @return
	 */
	@ApiOperation(value = "获取安装工单详情")
	@ApiImplicitParam(name = "workOrderId", value = "安装工单id", required = true, dataType = "String", paramType = "path")
	@GetMapping(value = "/workorder/install/{workOrderId}")
	public Object getWorkOrderById(@PathVariable(value = "workOrderId") String workOrderId) {
		WorkOrderDTO workorder = orderFeign.getWorkOrderById(workOrderId);
		return ResponseEntity.ok(workorder);
	}

	/****
	 * 接单
	 * @return
	 */
	@ApiOperation(value = "接单")
	@ApiImplicitParam(name = "workOrderId", value = "工单号", dataType = "String", required = true, paramType = "query")
	@PatchMapping(value = "/workorder/accept")
	public void accept(@RequestParam("workOrderId") String workOrderId) {
		Integer engineerId = userCache.getCurrentEngineerId();
		orderFeign.acceptWorkOrder(workOrderId, engineerId, WorkOrderInstallNewStep.COLLECT_WATER.value);
	}

	/****
	 * 拒绝工单
	 *
	 * @param workOrderId
	 * @param reason
	 * @return
	 */
	@ApiOperation(value = "拒绝工单")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "reason", value = "拒绝原因", dataType = "String", required = true, paramType = "query"),
			@ApiImplicitParam(name = "workOrderId", value = "工单号", dataType = "String", required = true, paramType = "query")
	})
	@PatchMapping(value = "/workorder/refuse")
	public void refuse(@RequestParam("workOrderId") String workOrderId, @RequestParam("reason") String reason) {
		Integer engineerId = userCache.getCurrentEngineerId();
		EngineerDTO engineer = userFeign.getEngineerById(engineerId);
		orderFeign.refuseWorkOrder(workOrderId, engineerId, engineer.getRealName(), reason);
	}

	/****
	 * 安装工改预约时间
	 *
	 * @return
	 */
	@ApiOperation(value = "安装工改预约时间")
	@ApiImplicitParam(name = "req", value = "请求参数", required = true, dataType = "WorkOrderReqDTO", paramType = "body")
	@PatchMapping(value = "/workorder/change/appoint", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void changeAppoint(@RequestBody WorkOrderReqDTO req) {
		workOrderBusinessService.changeAppoint(req);
	}

	/*****
	 * 开始服务-采集水源
	 *
	 * @param req
	 * @return
	 */
	@ApiOperation(value = "采集水源")
	@ApiImplicitParam(name = "req", value = "请求参数", required = true, dataType = "WorkOrderReqDTO", paramType = "body")
	@PatchMapping(value = "/workorder/collect/water", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void collectWater(@RequestBody WorkOrderReqDTO req) {
		workOrderBusinessService.collectWater(req);
	}

	/***
	 * 扫描sn码、批次码、sim卡
	 * @param req
	 */
	@ApiOperation(value = "扫描sn码、批次码、sim卡")
	@ApiImplicitParam(name = "req", value = "请求参数", required = true, dataType = "WorkOrderReqDTO", paramType = "body")
	@PatchMapping(value = "/workorder/scanCode", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void scanCode(@RequestBody WorkOrderReqDTO req) {
		workOrderBusinessService.scanCode(req);
	}

	/****
	 * 更换设备型号/计费方式
	 * @param req
	 */
	@ApiOperation(value = "更换设备型号/计费方式")
	@ApiImplicitParam(name = "req", value = "请求参数", required = true, dataType = "WorkOrderReqDTO", paramType = "body")
	@PatchMapping(value = "/workorder/changeTypeAndModel", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void changeTypeAndModel(@RequestBody WorkOrderReqDTO req) {
		workOrderBusinessService.changeTypeAndModel(req);
	}

	/****
	 * 签约
	 * @param req
	 */
	@ApiOperation(value = "签约")
	@ApiImplicitParam(name = "req", value = "请求参数", required = true, dataType = "WorkOrderReqDTO", paramType = "body")
	@PatchMapping(value = "/workorder/sign/contract")
	public void signContract(@RequestBody WorkOrderReqDTO req) {
		workOrderBusinessService.signContract(req);
	}

	/**
	 * 安装工退单
	 */
	@ApiOperation(value = "安装工退单")
	@ApiImplicitParam(name = "req", value = "请求参数", required = true, dataType = "WorkOrderReqDTO", paramType = "body")
	@PatchMapping(value = "/workorder/chargeback")
	public void chargebackWorkOrder(@RequestBody WorkOrderReqDTO req) {
		workOrderBusinessService.chargebackWorkOrder(req);
	}

	/**
	 * 安装工完成工单
	 */
	@ApiOperation(value = "安装工完成工单")
	@ApiImplicitParam(name = "req", value = "请求参数", required = true, dataType = "WorkOrderReqDTO", paramType = "body")
	@PatchMapping(value = "/workorder/complete")
	public void completeWorkOrder(@RequestParam("workOrderId") String workOrderId) {
		orderFeign.completeWorkOrder(workOrderId);
	}

	/**
	 * 工单未支付:获取已上架产品的所有型号,工单已支付,获取同价位产品的型号
	 */
	@ApiOperation(value = "获取设备型号列表")
	@ApiImplicitParam(name = "workOrderId", value = "工单id", dataType = "String", required = true, paramType = "query")
	@GetMapping(value = "/workorder/deviceModel")
	public Object deviceModel(@RequestParam("workOrderId") String workOrderId) {
		WorkOrderDTO wo = orderFeign.getWorkOrderById(workOrderId);
		if (wo == null) {
			throw new YimaoException("工单信息不存在");
		}

		List<ProductDTO> data = productFeign.getOnlinePoruductCategory(null);
		if (wo.getPay() != null && wo.getPay()) {
			//已支付 工单已支付,获取同价位产品的型号
			BigDecimal fee = wo.getFee();
			if (null == fee) {
				throw new YimaoException("工单价格为null，无法获取同价位的产品");
			}
			data = productFeign.getOnlinePoruductCategory(fee);
		}
		return ResponseEntity.ok(data);
	}

	/**
	 * 工单未支付:获取已上架产品的所有计费方式,工单已支付:获取同价位产品的计费方式,如果选择了产品型号,则获取该型号的计费方式
	 */
	@ApiOperation(value = "获取计费方式列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "workOrderId", value = "工单id", dataType = "String", required = true, paramType = "query"),
			@ApiImplicitParam(name = "productId", value = "产品id", dataType = "Long", required = false, paramType = "query")
	})
	@GetMapping(value = "/workorder/cost/list")
	public Object listCost(@RequestParam("workOrderId") String workOrderId, @RequestParam(value = "productId", required = false) Integer productId) {
		WorkOrderDTO wo = orderFeign.getWorkOrderById(workOrderId);
		if (wo == null) {
			throw new YimaoException("工单信息不存在");
		}
		List<ProductCostDTO> costList = new ArrayList<ProductCostDTO>();
		if (productId != null) {
			//选择了某个型号,则获取某个型号的计费方式
			costList = productFeign.productCostList(productId);
		} else {
			costList = productFeign.getOnlinePoruductCost(null);
			if (wo.getPay() != null && wo.getPay()) {
				//已支付,需要获取同等价格的产品计费方式
				BigDecimal fee = wo.getFee();
				if (null == fee) {
					throw new YimaoException("工单价格为null，无法获取同价位的产品计费方式");
				}
				costList = productFeign.getOnlinePoruductCost(fee);
			}
		}
		return ResponseEntity.ok(costList);
	}


	//======================================================安装工app地图工单=========================================//
	@GetMapping(value = "/workerorder/map/{engineerId}")
	@ApiOperation(value = "根据安装工id获取该安装工的安装工单")
	@ApiImplicitParam(name = "engineerId", value = "安装工id", dataType = "Long", paramType = "path", required = true)
	public Map<String, List<MapOrderDTO>> getMapOrder(@PathVariable Integer engineerId) {
		return orderFeign.getMapOrder(engineerId);
	}

	/**
	 * 安装工app--更换设备
	 */
	@PutMapping(value = "/workorder/install/{workOrderId}/changeWaterDevce")
	@ApiOperation(value = "安装工app--更换设备")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "workOrderId", value = "工单号", dataType = "String", required = true, paramType = "path"),
			@ApiImplicitParam(name = "logisticsCode", value = "物流编码", dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "snCode", value = "SN", dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "simCard", value = "SIM卡", dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "productId", value = "产品id", dataType = "Long", paramType = "query"),
			@ApiImplicitParam(name = "costId", value = "计费方式id", dataType = "Long", paramType = "query")
	})
	public void changeWaterDevice(@PathVariable(value = "workOrderId") String workOrderId,
	                              @RequestParam(value = "logisticsCode", required = false) String logisticsCode,
	                              @RequestParam(value = "snCode", required = false) String snCode,
	                              @RequestParam(value = "simCard", required = false) String simCard,
	                              @RequestParam(value = "productId", required = false) Integer productId,
	                              @RequestParam(value = "costId", required = false) Integer costId) {

		workOrderBusinessService.changeWaterDevice(workOrderId, logisticsCode, productId, snCode, simCard, costId);
	}

}
