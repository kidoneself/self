package com.yimao.cloud.engineer.controller;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.DeviceFaultState;
import com.yimao.cloud.base.enums.DeviceFaultType;
import com.yimao.cloud.base.enums.RepairOrderSourceType;
import com.yimao.cloud.base.enums.RepairOrderStatus;
import com.yimao.cloud.base.enums.RepairOrderStep;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.engineer.feign.OrderFeign;
import com.yimao.cloud.engineer.feign.SystemFeign;
import com.yimao.cloud.engineer.feign.UserFeign;
import com.yimao.cloud.engineer.feign.WaterFeign;
import com.yimao.cloud.pojo.dto.order.WorkRepairOrderDTO;
import com.yimao.cloud.pojo.dto.system.GoodsMaterialsDTO;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFaultDTO;
import com.yimao.cloud.pojo.query.order.WorkRepairOrderQuery;
import com.yimao.cloud.pojo.query.water.WaterDeviceQuery;
import com.yimao.cloud.pojo.vo.station.WorkRepairOrderVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
/**
 *维修工单
 * @author yaoweijun
 *
 */

@RestController
@Api(tags = "RepairWorkOrderController")
@Slf4j
public class RepairWorkOrderController {
	
	@Resource
	private OrderFeign orderFeign;
	@Resource
	private SystemFeign systemFeign;
	@Resource
	private UserFeign userFeign;
	@Resource
	private UserCache userCache;	
	@Resource
	private WaterFeign waterFeign;
    @Resource
    private AmqpTemplate rabbitTemplate;
	
	@GetMapping("/repairWorkOrder/faultList")
	@ApiOperation(value = "获取故障类型信息列表")
	public Object faultList() {
		Integer engineerId = userCache.getCurrentEngineerId();
		
		if (engineerId == null) {
			 throw new YimaoException("安装工登录失效");
	    }
		
		return orderFeign.pageRepairFault(0, 0, null);
	
	}
	
	/**
	 * 创建维修工单获取水机用户
	 * @return
	 */
	@PostMapping("/repairWorkOrder/create/getWaterDeviceList")
	@ApiOperation(value = "创建维修工单获取水机用户")
	public List<WaterDeviceDTO> getWaterUserList(@RequestBody WaterDeviceQuery query) {
		Integer engineerId = userCache.getCurrentEngineerId();
		
		if (engineerId == null) {
			 throw new YimaoException("安装工登录失效");
	    }
		 // 根据姓名获取安装工信息
		EngineerDTO engineer = userFeign.getEngineerById(engineerId);
		
		if(Objects.isNull(engineer)) {
			throw new YimaoException("安装工信息不存在");
		}
		
		if(CollectionUtil.isEmpty(engineer.getServiceAreaList())) {
			throw new YimaoException("安装工未绑定服务区域");
		}
		
		query.setServiceAreaList(engineer.getServiceAreaList());
		
		return waterFeign.getWaterDeviceList(query);
	
	}
	
	@GetMapping("/repairWorkOrder/{pageNum}/{pageSize}")
	@ApiOperation(value = "维修工单各状态列表")
	public Object repairOrderList(@PathVariable("pageNum") Integer pageNum,
						          @PathVariable("pageSize") Integer pageSize,
						          @RequestParam(value="status",required=true) Integer status,
						          @RequestParam(value="keywords",required=false) String keywords,
						          @RequestParam(value="sortBy",required=false) Integer sortBy,
						          @RequestParam(value="sortType",required=false) Integer sortType,
						          @RequestParam(value="latitude",required=false) String latitude,
						          @RequestParam(value="longitude",required=false) String longitude) {
		
		Integer engineerId = userCache.getCurrentEngineerId();
		
		if (engineerId == null) {
			 throw new YimaoException("安装工登录失效");
	    }
		
		if(Objects.isNull(status)) {
			throw new BadRequestException("工单状态为空");
		}
		
		if(Objects.isNull(RepairOrderStatus.find(status))) {
			throw new BadRequestException("工单状态不存在");
		}
		
		if(Objects.nonNull(sortBy) && sortBy == 1) {
			if(StringUtil.isEmpty(latitude)) {
				throw new BadRequestException("根据距离查询纬度为空");
			}
			
			if(StringUtil.isEmpty(longitude)) {
				throw new BadRequestException("根据距离查询经度为空");
			}
			
		}
		
		WorkRepairOrderQuery search=new WorkRepairOrderQuery();
		search.setStatus(status);
		search.setKeywords(keywords);
		search.setSortBy(sortBy);
		search.setSortType(sortType);
		search.setEngineerId(engineerId);
		search.setLatitude(latitude);
		search.setLongitude(longitude);
		return orderFeign.pageRepairOrders(search,pageNum,pageSize);
	}
	
	@GetMapping("/repairWorkOrder/detail/{workOrderNo}")
	@ApiOperation(value = "维修工单详情")
	public Object repairOrderDetail(@PathVariable("workOrderNo") String workOrderNo) {
		Integer engineerId = userCache.getCurrentEngineerId();
		
		if (engineerId == null) {
			 throw new YimaoException("安装工登录失效");
	    }
		
		if(StringUtil.isEmpty(workOrderNo)) {
			throw new BadRequestException("维修工单号为空");
		}
		
		WorkRepairOrderVO vo = orderFeign.getRepairOrderByWorkOrderNo(workOrderNo);
		
		if(Objects.isNull(vo)) {
			throw new YimaoException("维修工单不存在");
		}
		
		if(! engineerId.equals(vo.getEngineerId())) {
			throw new YimaoException("维修工单所属安装工错误");
		}
		
		return vo;
	}
	
	@PostMapping("/repairWorkOrder/create")
	@ApiOperation(value = "创建维修工单")
	public Object create(@RequestBody WorkRepairOrderDTO dto) {
		Integer engineerId = userCache.getCurrentEngineerId();
		
		if (engineerId == null) {
			 throw new YimaoException("安装工登录失效");
	    }
		
		if(! engineerId.equals(dto.getEngineerId())) {
			throw new YimaoException("安装工不匹配");
		}
		//设置来源
		dto.setSourceType(RepairOrderSourceType.ENGINEER_APP.value);
		return orderFeign.createRepairOrder(dto);
	}
	
	@PatchMapping("/repairWorkOrder/renegotiation")
	@ApiOperation(value = "改约挂单")
	public void renegotiation(@RequestBody WorkRepairOrderDTO dto) {
		Integer engineerId = userCache.getCurrentEngineerId();
		
		if (engineerId == null) {
			throw new YimaoException("安装工登录失效");
	    }
		
		if(StringUtil.isEmpty(dto.getWorkOrderNo())) {
			throw new BadRequestException("维修工单号为空");
		}
		
		if(StringUtil.isEmpty(dto.getHangRemark())) {
			throw new BadRequestException("改约原因为空");
		}
		
		if(Objects.isNull(dto.getRevisionStartTime())){
			throw new BadRequestException("改约服务开始时间为空");
		}
		
		if(Objects.isNull(dto.getRevisionEndTime())){
			throw new BadRequestException("改约服务结束时间为空");
		}
		
		if(dto.getRevisionEndTime().getTime() < dto.getRevisionStartTime().getTime()) {
			throw new BadRequestException("改约时间错误");
		}
		
		WorkRepairOrderVO workRepairOrder = orderFeign.getRepairOrderByWorkOrderNo(dto.getWorkOrderNo());
		
		if(Objects.isNull(workRepairOrder)) {
			throw new YimaoException("维修工单不存在");
		}
		
		if(! workRepairOrder.getEngineerId().equals(engineerId)) {
			throw new YimaoException("维修工单安装工不匹配");
		}
		//判断维修工单是否为待维修
		if(RepairOrderStatus.PENDING.value != workRepairOrder.getStatus()) {
			throw new YimaoException("维修工单非待维修");
		}
		
		dto.setId(workRepairOrder.getId());
		orderFeign.renegotiationRepairOrderUpdate(dto);
		
	}
	
	@PatchMapping("/repairWorkOrder/startService")
	@ApiOperation(value = "开始服务")
	public WorkRepairOrderVO startService(@RequestParam("workOrderNo")String workOrderNo) {
		Integer engineerId = userCache.getCurrentEngineerId();

		if (engineerId == null) {
			 throw new YimaoException("安装工登录失效");
	    }
		
		if(StringUtil.isEmpty(workOrderNo)) {
			throw new BadRequestException("维修工单号为空");
		}
		
		WorkRepairOrderVO workRepairOrder = orderFeign.getRepairOrderByWorkOrderNo(workOrderNo);
	
		if(Objects.isNull(workRepairOrder)) {
			throw new YimaoException("维修工单不存在");
		}
		
		if(! workRepairOrder.getEngineerId().equals(engineerId)) {
			throw new YimaoException("维修工单安装工不匹配");
		}
		
		//判断维修工单是否为待维修或者挂单
		if(RepairOrderStatus.PENDING.value != workRepairOrder.getStatus() && RepairOrderStatus.HANG_UP.value != workRepairOrder.getStatus()) {
			throw new YimaoException("维修工单非待维修或挂单状态");
		}
		
		//修改维修工单开始服务内容
		WorkRepairOrderDTO dto=new WorkRepairOrderDTO();
		dto.setId(workRepairOrder.getId());
		dto.setWorkOrderNo(workOrderNo);
		dto.setStep(RepairOrderStep.CONFIRM.value);
		WorkRepairOrderVO vo= orderFeign.processRepairOrderChange(dto);
		 
		//根据设备id查询水机信息
		WaterDeviceDTO waterDevice = waterFeign.getWaterDeviceById(workRepairOrder.getDeviceId());
		
		vo.setWaterDevice(waterDeviceInfoFilter(waterDevice));
		
		return vo;
	}
	
	@PatchMapping("/repairWorkOrder/confirmFault")
	@ApiOperation(value = "故障确认提交")
	public Object confirmFault(@RequestBody WorkRepairOrderDTO dto) {
		Integer engineerId = userCache.getCurrentEngineerId();
		
		if (engineerId == null) {
			 throw new YimaoException("安装工登录失效");
	    }
		
		if(StringUtil.isEmpty(dto.getWorkOrderNo())) {
			throw new BadRequestException("维修工单号为空");
		}
		
		if(StringUtil.isEmpty(dto.getFrontImage())) {
			throw new BadRequestException("设备正面照片未上传");
		}
		
		if(StringUtil.isEmpty(dto.getBackImage())) {
			throw new BadRequestException("设备后面照片未上传");
		}

		if(StringUtil.isEmpty(dto.getLeftImage())) {
			throw new BadRequestException("设备左侧照片未上传");
		}

		if(StringUtil.isEmpty(dto.getRightImage())) {
			throw new BadRequestException("设备右侧照片未上传");
		}
		
		WorkRepairOrderVO workRepairOrder = orderFeign.getRepairOrderByWorkOrderNo(dto.getWorkOrderNo());
	
		if(Objects.isNull(workRepairOrder)) {
			throw new YimaoException("维修工单不存在");
		}
		
		if(! workRepairOrder.getEngineerId().equals(engineerId)) {
			throw new YimaoException("维修工单安装工不匹配");
		}
		
		//判断维修工单是否为处理中
		if(RepairOrderStatus.PROCESSIONG.value != workRepairOrder.getStatus()) {
			throw new YimaoException("维修工单非处理中");
		}
		
		if(Objects.isNull(workRepairOrder.getStep()) || RepairOrderStep.CONFIRM.value != workRepairOrder.getStep()) {
			throw new YimaoException("维修工单处理中步骤错误");
		}
		
		//修改维修工单故障确认内容
		dto.setId(workRepairOrder.getId());
		dto.setStep(RepairOrderStep.REPAIR.value);
		WorkRepairOrderVO vo= orderFeign.processRepairOrderChange(dto);
		
		//根据设备id查询水机信息
		WaterDeviceDTO waterDevice = waterFeign.getWaterDeviceById(workRepairOrder.getDeviceId());
		vo.setWaterDevice(waterDeviceInfoFilter(waterDevice));		
		vo.setDeviceModel(workRepairOrder.getDeviceModel());
		vo.setWorkOrderNo(workRepairOrder.getWorkOrderNo());
		
		//根据型号查询耗材
		List<GoodsMaterialsDTO> materialList= systemFeign.getMaterialListByCategoryId(workRepairOrder.getProductCategoryId());
		
		vo.setMaterialList(materialList);
		
		return vo;
	
	}
	
	@PatchMapping("/repairWorkOrder/repairFault")
	@ApiOperation(value = "维修故障提交")
	public Object repairFault(@RequestBody WorkRepairOrderDTO dto) {
		Integer engineerId = userCache.getCurrentEngineerId();
		
		if (engineerId == null) {
			 throw new YimaoException("安装工登录失效");
	    }
		
		if(StringUtil.isEmpty(dto.getWorkOrderNo())) {
			throw new BadRequestException("维修工单号为空");
		}
		
		if(Objects.isNull(dto.getIsChangeMaterial())) {
			throw new BadRequestException("未选择是否使用耗材");
		}
		
		if(dto.getIsChangeMaterial() || CollectionUtil.isEmpty(dto.getMaterials())) {
			throw new BadRequestException("未选择耗材");
		}
		
		if(Objects.isNull(dto.getConfirmFaultId())) {
			throw new BadRequestException("确认故障id为空");
		}
		
		WorkRepairOrderVO workRepairOrder = orderFeign.getRepairOrderByWorkOrderNo(dto.getWorkOrderNo());
		
		if(Objects.isNull(workRepairOrder)) {
			throw new YimaoException("维修工单不存在");
		}
		
		if(! workRepairOrder.getEngineerId().equals(engineerId)) {
			throw new YimaoException("维修工单安装工不匹配");
		}
		
		//判断维修工单是否为处理中
		if(RepairOrderStatus.PROCESSIONG.value != workRepairOrder.getStatus()) {
			throw new YimaoException("维修工单非处理中");
		}
		
		if(Objects.isNull(workRepairOrder.getStep()) || RepairOrderStep.REPAIR.value != workRepairOrder.getStep()) {
			throw new YimaoException("维修工单处理中步骤错误");
		}
		
		//修改维修工单故障确认内容
		dto.setId(workRepairOrder.getId());
		dto.setStep(RepairOrderStep.SUBMIT.value);
		WorkRepairOrderVO vo= orderFeign.processRepairOrderChange(dto);
		//根据设备id查询水机信息
		WaterDeviceDTO waterDevice = waterFeign.getWaterDeviceById(workRepairOrder.getDeviceId());
		vo.setWaterDevice(waterDeviceInfoFilter(waterDevice));
		return vo;
	}
	
	/**
	 * 将工单状态变更为已完成
	 * @param workOrderNo
	 * @return
	 */
	@PatchMapping("/repairWorkOrder/submit")
	@ApiOperation(value = "完成提交")
	public void submit(String workOrderNo) {
		Integer engineerId = userCache.getCurrentEngineerId();
		
		if (engineerId == null) {
			 throw new YimaoException("安装工登录失效");
	    }
		
		if(StringUtil.isEmpty(workOrderNo)) {
			throw new BadRequestException("维修工单号为空");
		}
		
		WorkRepairOrderVO workRepairOrder = orderFeign.getRepairOrderByWorkOrderNo(workOrderNo);
		
		if(Objects.isNull(workRepairOrder)) {
			throw new YimaoException("维修工单不存在");
		}
		
		if(! workRepairOrder.getEngineerId().equals(engineerId)) {
			throw new YimaoException("维修工单安装工不匹配");
		}
		
		//判断维修工单是否为处理中
		if(RepairOrderStatus.PROCESSIONG.value != workRepairOrder.getStatus()) {
			throw new YimaoException("维修工单非处理中");
		}
		
		if(Objects.isNull(workRepairOrder.getStep()) || RepairOrderStep.SUBMIT.value != workRepairOrder.getStep()) {
			throw new YimaoException("维修工单处理中步骤错误");
		}
		
		orderFeign.submitRepairOrder(workRepairOrder.getId());
		
		if(workRepairOrder.getSourceType() == RepairOrderSourceType.WATER_DEVICE_PUSH.value) {
			//如果解决的订单为系统推送，将推送设备故障纪录变为已解决
			WaterDeviceFaultDTO deviceFault = new WaterDeviceFaultDTO();
	        deviceFault.setDeviceId(workRepairOrder.getDeviceId());
	        deviceFault.setSn(workRepairOrder.getSn());
	        if(workRepairOrder.getFaultName().equals(DeviceFaultType.TDS.name)) {
	        	deviceFault.setType(DeviceFaultType.TDS.value);
	        }else if(workRepairOrder.getFaultName().equals(DeviceFaultType.WATER.name)) {
	        	deviceFault.setType(DeviceFaultType.WATER.value);
	        }else {
	        	log.error("系统推送故障纪录变更失败,repairOrderId={}",workRepairOrder.getId());
	        	return;
	        }
	        
	        deviceFault.setState(DeviceFaultState.RESOLVE.value);
	        rabbitTemplate.convertAndSend(RabbitConstant.DEVICE_FAULT, deviceFault);
		}
	}
	
	/**
	 * 维修工单继续服务展示信息
	 * 
	 * @param dto
	 * @return
	 */
	@GetMapping("/repairWorkOrder/continueServive")
	@ApiOperation(value = "继续服务返回信息")
	public WorkRepairOrderVO continueServive(String workOrderNo) {
		Integer engineerId = userCache.getCurrentEngineerId();
		
		if (engineerId == null) {
			 throw new YimaoException("安装工登录失效");
	    }
		
		if(StringUtil.isEmpty(workOrderNo)) {
			throw new BadRequestException("维修工单号为空");
		}
		
		WorkRepairOrderVO workRepairOrder = orderFeign.getRepairOrderByWorkOrderNo(workOrderNo);
		
		if(Objects.isNull(workRepairOrder)) {
			throw new YimaoException("维修工单不存在");
		}
		
		if(! workRepairOrder.getEngineerId().equals(engineerId)) {
			throw new YimaoException("维修工单安装工不匹配");
		}
		
		//判断维修工单是否为处理中
		if(RepairOrderStatus.PROCESSIONG.value != workRepairOrder.getStatus()) {
			throw new YimaoException("维修工单非处理中");
		}
		
		WorkRepairOrderVO vo = orderFeign.continueRepairServiveInfo(workRepairOrder.getId(),workRepairOrder.getStep());
		
		//根据id查询水机信息
		WaterDeviceDTO waterDevice = waterFeign.getWaterDeviceById(workRepairOrder.getDeviceId());
		vo.setWaterDevice(waterDeviceInfoFilter(waterDevice));
		
		return vo;
	}
	
	//返回水机信息过滤字段
	public WaterDeviceDTO waterDeviceInfoFilter(WaterDeviceDTO dto) {
		if(Objects.isNull(dto)) {
			return null;
		}
		
		WaterDeviceDTO res =new WaterDeviceDTO();
		res.setDeviceUserName(dto.getDeviceUserName());
		res.setSn(dto.getSn());
		res.setLogisticsCode(dto.getLogisticsCode());
		res.setIccid(dto.getIccid());
		res.setDeviceModel(dto.getDeviceModel());
		res.setCurrentCostName(dto.getCurrentCostName());
		res.setMoney(dto.getMoney());
		res.setCreateTime(dto.getCreateTime());
		res.setProvince(dto.getProvince());
		res.setCity(dto.getCity());
		res.setRegion(dto.getRegion());
		res.setAddress(dto.getAddress());
		res.setDistributorName(dto.getDistributorName());
		res.setDistributorPhone(dto.getDistributorPhone());
		
		return res;
		
	}

}
