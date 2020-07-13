package com.yimao.cloud.order.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.MoveWaterDeviceOrderStatusEnum;
import com.yimao.cloud.base.enums.RepairOrderSourceType;
import com.yimao.cloud.base.enums.RepairOrderStatus;
import com.yimao.cloud.base.enums.RepairOrderStep;
import com.yimao.cloud.base.enums.ServiceEngineerChangeSourceEnum;
import com.yimao.cloud.base.enums.ServiceEngineerChangeWorkOrderTypeEnum;
import com.yimao.cloud.base.enums.WorkOrderNewStatusEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.UUIDUtil;
import com.yimao.cloud.order.mapper.MoveWaterDeviceOrderMapper;
import com.yimao.cloud.order.mapper.ServiceEngineerChangeRecordMapper;
import com.yimao.cloud.order.mapper.WorkOrderBackMapper;
import com.yimao.cloud.order.mapper.WorkOrderMapper;
import com.yimao.cloud.order.mapper.WorkRepairFaultMapper;
import com.yimao.cloud.order.mapper.WorkRepairOrderMapper;
import com.yimao.cloud.order.po.MoveWaterDeviceOrder;
import com.yimao.cloud.order.po.ServiceEngineerChangeRecord;
import com.yimao.cloud.order.po.WorkOrder;
import com.yimao.cloud.order.po.WorkOrderBack;
import com.yimao.cloud.order.po.WorkRepairFault;
import com.yimao.cloud.order.po.WorkRepairMaterialUseRecord;
import com.yimao.cloud.order.po.WorkRepairOrder;
import com.yimao.cloud.order.service.RepairOrderService;
import com.yimao.cloud.pojo.dto.order.WorkRepairFaultDTO;
import com.yimao.cloud.pojo.dto.order.WorkRepairOrderDTO;
import com.yimao.cloud.pojo.dto.product.ProductDTO;
import com.yimao.cloud.pojo.dto.system.GoodsMaterialsDTO;
import com.yimao.cloud.pojo.dto.system.StationMaterialStockDTO;
import com.yimao.cloud.pojo.dto.system.StationStoreHouseDTO;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.dto.user.EngineerServiceAreaDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDTO;
import com.yimao.cloud.pojo.query.order.WorkRepairOrderQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.station.WorkRepairOrderVO;

import tk.mybatis.mapper.entity.Example;

import com.yimao.cloud.order.feign.ProductFeign;
import com.yimao.cloud.order.feign.SystemFeign;
import com.yimao.cloud.order.feign.UserFeign;
import com.yimao.cloud.order.feign.WaterFeign;
/**
 * 3.0维修工单
 * @author yaoweijun
 *
 */
@Service
public class RepairOrderServiceImpl implements RepairOrderService {
	@Resource
	private UserCache userCache;
	@Resource
    private UserFeign userFeign;
	@Resource
	private WaterFeign waterFeign;
	@Resource
	private ProductFeign productFeign;
	@Resource
	private SystemFeign systemFeign;
	@Resource
	private WorkRepairOrderMapper workRepairOrderMapper;
	@Resource
	private WorkRepairFaultMapper workRepairFaultMapper;
	@Resource
	private WorkOrderBackMapper workOrderBackMapper;
	@Resource
	private WorkOrderMapper workOrderMapper;
    @Resource
    private MoveWaterDeviceOrderMapper moveWaterDeviceOrderMapper;
    @Resource
    private ServiceEngineerChangeRecordMapper serviceEngineerChangeRecordMapper;
	
	/**
	 * 安装工app创建维修工单
	 */
	public void createAppRepairOrder(WorkRepairOrderDTO dto) {
		basicCheck(dto);
		//校验安装工，返回安装工信息
		EngineerDTO engineerDTO = basicEngineerCheck(dto);
		
		//查询故障类别
		WorkRepairFault repairFault = workRepairFaultMapper.selectByPrimaryKey(dto.getFaultId());
		
		if(Objects.isNull(repairFault)) {
			throw new YimaoException("故障类型不存在");
		}
		
		//校验水机返回水机设备信息
		WaterDeviceDTO watderDevice = basicWaterDeviceCheck(dto);
		
		//判断水机所在区域是否为安装工服务区域
		boolean isServiceArea=false;
		for (EngineerServiceAreaDTO serviceArea : engineerDTO.getServiceAreaList()) {			
			String province=serviceArea.getProvince();
			String city=serviceArea.getCity();
			String region=serviceArea.getRegion();
			if(watderDevice.getProvince().equals(province) && watderDevice.getCity().equals(city) && watderDevice.getRegion().equals(region)) {
				isServiceArea=true;
				break;
			}
		}
		
		if(! isServiceArea) {
			throw new YimaoException("安装工服务区域非水机所在区域");
		}
		
		//校验是否存在未完成的相同故障维修，退机，移机工单
		unSolvedOrderCheck(dto);
		
		//维修工单信息添加
		WorkRepairOrder workRepairOrder= createWorkRepairOrder(dto,watderDevice,engineerDTO,repairFault);
		
		//新增维修单
		workRepairOrder.setSourceType(RepairOrderSourceType.ENGINEER_APP.value);
		int createRes =workRepairOrderMapper.createRepairOrder(workRepairOrder);
		
		if(createRes < 1) {
			throw new YimaoException("维修单创建失败");
		}
	}

	
	/**
	 * 业务系统后台创建工单
	 */
	public void createSystemRepairOrder(WorkRepairOrderDTO dto) {
		//前端传参校验
		basicCheck(dto);
		//校验安装工，返回安装工信息
		EngineerDTO engineerDTO = basicEngineerCheck(dto);
		
		//查询故障类别
		WorkRepairFault repairFault = workRepairFaultMapper.selectByPrimaryKey(dto.getFaultId());
		
		if(Objects.isNull(repairFault)) {
			throw new YimaoException("故障类型不存在");
		}
		
		//校验水机返回水机设备信息
		WaterDeviceDTO watderDevice = basicWaterDeviceCheck(dto);		
		
		//判断水机所在区域是否为安装工服务区域
		boolean isServiceArea=false;
		for (EngineerServiceAreaDTO serviceArea : engineerDTO.getServiceAreaList()) {			
			String province=serviceArea.getProvince();
			String city=serviceArea.getCity();
			String region=serviceArea.getRegion();
			if(watderDevice.getProvince().equals(province) && watderDevice.getCity().equals(city) && watderDevice.getRegion().equals(region)) {
				isServiceArea=true;
				break;
			}
		}
		
		if(! isServiceArea) {
			throw new YimaoException("安装工服务区域非水机所在区域");
		}
		
		//校验是否存在未完成的相同故障维修，退机，移机工单
		unSolvedOrderCheck(dto);
		
		//维修工单信息添加
		WorkRepairOrder workRepairOrder= createWorkRepairOrder(dto,watderDevice,engineerDTO,repairFault);
		
		//新增维修单
		workRepairOrder.setSourceType(RepairOrderSourceType.SYSTEM.value);
		workRepairOrder.setSystemCreator(userCache.getCurrentAdminRealName());
		int createRes =workRepairOrderMapper.createRepairOrder(workRepairOrder);
		
		if(createRes < 1) {
			throw new YimaoException("维修单创建失败");
		}
	}
	
	
	/**
	 * 水机推送创建维修工单
	 */
	public void createDevicePushRepairOrder(WorkRepairOrderDTO dto) {
		if(Objects.isNull(dto.getSourceType()) && dto.getSourceType() != RepairOrderSourceType.WATER_DEVICE_PUSH.value) {
			throw new BadRequestException("维修工单来源错误");
		}
		
		if(Objects.isNull(dto.getEngineerId())) {
			throw new BadRequestException("安装工id为空");
		}
		
		if(Objects.isNull(dto.getDeviceId())) {
			throw new BadRequestException("设备id为空");
		}
		
		if(StringUtil.isEmpty(dto.getFaultName())) {
			throw new BadRequestException("故障类型名称为空");
		}
		
		if(Objects.isNull(dto.getServiceStartTime())) {
			throw new BadRequestException("服务开始时间为空");
		}
		
		if(Objects.isNull(dto.getServiceEndTime())) {
			throw new BadRequestException("服务结束时间为空");
		}
		
		if(dto.getServiceStartTime().getTime() > dto.getServiceEndTime().getTime()) {
			throw new BadRequestException("服务时间错误");
		}
		//校验安装工，返回安装工信息
		EngineerDTO engineerDTO = basicEngineerCheck(dto);
		
		//校验水机返回水机设备信息
		WaterDeviceDTO watderDevice = basicWaterDeviceCheck(dto);		
		
		//判断水机所在区域是否为安装工服务区域
		boolean isServiceArea=false;
		for (EngineerServiceAreaDTO serviceArea : engineerDTO.getServiceAreaList()) {			
			String province=serviceArea.getProvince();
			String city=serviceArea.getCity();
			String region=serviceArea.getRegion();
			if(watderDevice.getProvince().equals(province) && watderDevice.getCity().equals(city) && watderDevice.getRegion().equals(region)) {
				isServiceArea=true;
				break;
			}
		}
		
		if(! isServiceArea) {
			throw new YimaoException("安装工服务区域非水机所在区域");
		}
		
		//获取故障类型信息
		WorkRepairFault faultQuery = new WorkRepairFault();
		faultQuery.setFaultName(dto.getFaultName());
		faultQuery.setType(0);		
		WorkRepairFault repairFault = workRepairFaultMapper.selectOne(faultQuery);
		
		if(Objects.isNull(repairFault)) {
			throw new YimaoException("水机推送故障类型不存在");
		}
		
		//校验是否存在未完成的相同故障维修，退机，移机工单
		unSolvedOrderCheck(dto);
		
		//维修工单信息添加
		WorkRepairOrder workRepairOrder= createWorkRepairOrder(dto,watderDevice,engineerDTO,repairFault);
		
		int createRes =workRepairOrderMapper.createRepairOrder(workRepairOrder);
		
		if(createRes < 1) {
			throw new YimaoException("维修单创建失败");
		}
		
	}
	
	/**
	 * 分页查询维修单列表
	 */
	public PageVO<WorkRepairOrderVO> page(Integer pageNum, Integer pageSize, WorkRepairOrderQuery search) {
		PageHelper.startPage(pageNum, pageSize);
		//判别排序依据
		if(Objects.nonNull(search.getSortBy())) {
			switch(search.getSortBy()) {
				case 1:
					//距离
					if(Objects.isNull(search.getSortType())) {
						search.setSortTypeString("asc");
					}else {
						if(search.getSortType() == 1) {//正序
							search.setSortTypeString("asc");
						}
						
						if(search.getSortType() == 2) {//倒序
							search.setSortTypeString("desc");
						}
					}
				
					break;
				case 2:
					//发起时间
					if(Objects.isNull(search.getSortType())) {
						search.setSortTypeString("desc");
					}else {
						if(search.getSortType() == 1) {//正序
							search.setSortTypeString("asc");
						}
						
						if(search.getSortType() == 2) {//倒序
							search.setSortTypeString("desc");
						}
					}
					break;
				case 3:
					//完成时间
					if(Objects.isNull(search.getSortType())) {
						search.setSortTypeString("desc");
					}else {
						if(search.getSortType() == 1) {//正序
							search.setSortTypeString("asc");
						}
						
						if(search.getSortType() == 2) {//倒序
							search.setSortTypeString("desc");
						}
					}
			}
		}
		Page<WorkRepairOrderVO> pageResult = workRepairOrderMapper.listRepairOrder(search);
		return new PageVO<>(pageNum, pageResult);
	}
	
	
	
	//创建维修工单基础参数校验
	public void basicCheck(WorkRepairOrderDTO dto) {
		//安装工校验
		if(Objects.isNull(dto.getEngineerId())) {
			throw new BadRequestException("安装工id为空");
		}
		
		if(Objects.isNull(dto.getFaultId())) {
			throw new BadRequestException("故障类型id为空");
		}
		
		if(Objects.isNull(dto.getDeviceId())) {
			throw new BadRequestException("设备id为空");
		}
		
		if(Objects.isNull(dto.getServiceStartTime())) {
			throw new BadRequestException("服务开始时间为空");
		}
		
		if(Objects.isNull(dto.getServiceEndTime())) {
			throw new BadRequestException("服务结束时间为空");
		}
		
		if(dto.getServiceStartTime().getTime() > dto.getServiceEndTime().getTime()) {
			throw new BadRequestException("服务时间错误");
		}
	}
	
	//创建维修工单校验安装工
	public EngineerDTO basicEngineerCheck(WorkRepairOrderDTO dto) {
		//查询安装工信息
		EngineerDTO engineerDTO = userFeign.getEngineerById(dto.getEngineerId());
		
		if(Objects.isNull(engineerDTO)) {
			throw new YimaoException("安装工不存在");
		}
		
		if(engineerDTO.getForbidden()) {
			throw new YimaoException("安装工已禁用");
		}
		
		if(CollectionUtil.isEmpty(engineerDTO.getServiceAreaList())) {
			throw new YimaoException("安装工未绑定服务区域");
		}
		
		return engineerDTO;
	}
	
	
	//创建维修工单校验设备
	public WaterDeviceDTO basicWaterDeviceCheck(WorkRepairOrderDTO dto) {
		//查询水机设备信息
		WaterDeviceDTO watderDevice = waterFeign.getWaterDeviceById(dto.getDeviceId());
		
		if(Objects.isNull(watderDevice)) {
			throw new YimaoException("水机设备不存在");
		}
		
		if(StringUtil.isEmpty(watderDevice.getProvince()) || StringUtil.isEmpty(watderDevice.getCity()) || StringUtil.isEmpty(watderDevice.getRegion())) {
			throw new YimaoException("水机设备所在省市区信息不完整");
		}
		
		return watderDevice;
	}
	

	//校验创建维修工单是否存在其他未完成工单
	public void unSolvedOrderCheck(WorkRepairOrderDTO dto) {
		//判断是否存在未完成的安装工单
		Example installOrderExample = new Example(WorkOrder.class);
		Example.Criteria installOrderCriteria = installOrderExample.createCriteria();
		installOrderCriteria.andEqualTo("deviceId", dto.getDeviceId());
		installOrderCriteria.andNotEqualTo("status", WorkOrderNewStatusEnum.COMPLETED.value);
		int installOrderCount = workOrderMapper.selectCountByExample(installOrderExample);
		
		if(installOrderCount > 0) {
			throw new YimaoException("设备存在未完成的安装工单");
		}
		
		//判断同一水机是否存在未完成的相同维修故障类型工单
		Example repairOrderExample = new Example(WorkRepairOrder.class);
        Example.Criteria repairOrderCriteria = repairOrderExample.createCriteria();
        repairOrderCriteria.andEqualTo("faultId", dto.getFaultId());
        repairOrderCriteria.andEqualTo("deviceId", dto.getDeviceId());
        repairOrderCriteria.andNotEqualTo("status", RepairOrderStatus.SOLVED.value);
		int sameRepairOrderCount = workRepairOrderMapper.selectCountByExample(repairOrderExample);
		
		if(sameRepairOrderCount > 0) {
			throw new YimaoException("已存在相同故障未完成的维修单");
		}
		
		//校验设备是否有未完成的移机工单
		Example moveWaterOrderExample = new Example(MoveWaterDeviceOrder.class);
        Example.Criteria moveWaterCriteria = moveWaterOrderExample.createCriteria();
        moveWaterCriteria.andEqualTo("deviceId", dto.getDeviceId());
        moveWaterCriteria.andNotEqualTo("status", MoveWaterDeviceOrderStatusEnum.COMPLETED.value);
		int moveWaterOrderCount = moveWaterDeviceOrderMapper.selectCountByExample(moveWaterOrderExample);
		
		if(moveWaterOrderCount > 0) {
			throw new YimaoException("设备存在未完成的移机单");
		}
		
		//校验设备是否有未完成的退机工单
		Example workOrderBackExample = new Example(WorkOrderBack.class);
        Example.Criteria workOrderBackCriteria = workOrderBackExample.createCriteria();
        workOrderBackCriteria.andEqualTo("sn", dto.getSn());
        //TODO 存在枚举类换成枚举
        workOrderBackCriteria.andNotEqualTo("status", 4);
		int workOrderBackCount = workOrderBackMapper.selectCountByExample(workOrderBackExample);
		
		if(workOrderBackCount > 0) {
			throw new YimaoException("设备存在未完成的退机单");
		}
	
	}
		
		
	//维修工单创建信息添加
	private WorkRepairOrder createWorkRepairOrder(WorkRepairOrderDTO dto, WaterDeviceDTO watderDevice, EngineerDTO engineerDTO,
			WorkRepairFault repairFault) {
		
		WorkRepairOrder workRepairOrder = new WorkRepairOrder();
		workRepairOrder.setWorkOrderNo(UUIDUtil.getWorkRepairOrderNoToStr());
		workRepairOrder.setStatus(RepairOrderStatus.PENDING.value);
		workRepairOrder.setRemark(dto.getRemark());
		workRepairOrder.setSourceType(dto.getSourceType());
		workRepairOrder.setLaunchTime(new Date());
		workRepairOrder.setServiceStartTime(dto.getServiceStartTime());
		workRepairOrder.setServiceEndTime(dto.getServiceEndTime());
		//水机信息
		WorkOrder workorder = workOrderMapper.selectByPrimaryKey(watderDevice.getWorkOrderId());
		if(Objects.isNull(workorder)) {
			throw new YimaoException("安装工单信息不存在");
		}
		
		//获取产品型号
		ProductDTO product = productFeign.getProductById(workorder.getProductId());
		
		if(Objects.isNull(product)) {
			throw new YimaoException("工单对应产品不存在，获取型号失败");
		}
		
		workRepairOrder.setDeviceId(watderDevice.getId());
		workRepairOrder.setDeviceModel(watderDevice.getDeviceModel());
		workRepairOrder.setProductCategoryId(product.getCategoryId());
		workRepairOrder.setSn(watderDevice.getSn());
		workRepairOrder.setProvince(watderDevice.getProvince());
		workRepairOrder.setCity(watderDevice.getCity());
		workRepairOrder.setRegion(watderDevice.getRegion());
		workRepairOrder.setDeviceUserId(watderDevice.getDeviceUserId());
		workRepairOrder.setDeviceUserName(watderDevice.getDeviceUserName());
		workRepairOrder.setDeviceUserPhone(watderDevice.getDeviceUserPhone());
		workRepairOrder.setAddress(watderDevice.getAddress());
		workRepairOrder.setLongitude(watderDevice.getLongitude());
		workRepairOrder.setLatitude(watderDevice.getLatitude());	
		workRepairOrder.setDistributorId(watderDevice.getDistributorId());
		workRepairOrder.setDistributorName(watderDevice.getDistributorName());
		workRepairOrder.setDistributorPhone(watderDevice.getDistributorPhone());
		//故障信息
		workRepairOrder.setFaultId(repairFault.getId());
		workRepairOrder.setFaultName(repairFault.getFaultName());
		//安装工
		workRepairOrder.setEngineerId(engineerDTO.getId());
		workRepairOrder.setEngineerName(engineerDTO.getRealName());
		workRepairOrder.setStationId(engineerDTO.getStationId());
		workRepairOrder.setStationName(engineerDTO.getStationName());
		
		return workRepairOrder;
	}


	/**
	 * 维修工单处理中流程变更
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
	public WorkRepairOrderVO processRepairOrderChange(WorkRepairOrderDTO dto) {
		
		if(Objects.isNull(dto.getId())) {
			throw new BadRequestException("id为空");
		}
		
		if(Objects.isNull(dto.getStep())) {
			throw new BadRequestException("处理中step为空");
		}

		if(StringUtil.isEmpty(dto.getWorkOrderNo())) {
			throw new BadRequestException("维修工单号为空");
		}
		
		WorkRepairOrderVO vo=new WorkRepairOrderVO();
		
		if(RepairOrderStep.CONFIRM.value == dto.getStep()) {//工单开始服务,步骤变更为故障确认
			WorkRepairOrder update =new WorkRepairOrder();
			//将工单状态变为处理中
			update.setStatus(RepairOrderStatus.PROCESSIONG.value);
			update.setEngineerServiceTime(new Date());
			update.setStep(dto.getStep());
			Example example = new Example(WorkRepairOrder.class);
	        Example.Criteria criteria = example.createCriteria();
	        criteria.andEqualTo("id", dto.getId());
	        criteria.andEqualTo("workOrderNo", dto.getWorkOrderNo());
	        List<Integer> statusList=new ArrayList<>();
	        statusList.add(RepairOrderStatus.PENDING.value);
	        statusList.add(RepairOrderStatus.HANG_UP.value);
	        criteria.andIn("status", statusList);
			int res = workRepairOrderMapper.updateByExampleSelective(update, example);
			
			if(res < 1) {
				throw new YimaoException("开始服务变更失败");
			}
			
			vo= workRepairOrderMapper.selectConfirmInfo(dto.getId());
			
		}else if(RepairOrderStep.REPAIR.value == dto.getStep()){//步骤变更为维修故障
			WorkRepairOrder update =new WorkRepairOrder();
			update.setFrontImage(dto.getFrontImage());
			update.setBackImage(dto.getBackImage());
			update.setLeftImage(dto.getLeftImage());
			update.setRightImage(dto.getRightImage());
			update.setStep(dto.getStep());
			Example example = new Example(WorkRepairOrder.class);
	        Example.Criteria criteria = example.createCriteria();
	        criteria.andEqualTo("id", dto.getId());
	        criteria.andEqualTo("workOrderNo", dto.getWorkOrderNo());
	        criteria.andEqualTo("status", RepairOrderStatus.PROCESSIONG.value);
	        criteria.andEqualTo("step", RepairOrderStep.CONFIRM.value);
			int res = workRepairOrderMapper.updateByExampleSelective(update, example);
			
			if(res < 1) {
				throw new YimaoException("故障维修变更失败");
			}
			
			//查询故障类型列表
			List<WorkRepairFault> workRepairFaultList = workRepairFaultMapper.listWorkRepairFault(null);
			
			if(CollectionUtil.isNotEmpty(workRepairFaultList)) {
				List<WorkRepairFaultDTO> faultList=new ArrayList<>();
				BeanUtils.copyProperties(workRepairFaultList, faultList, WorkRepairFaultDTO.class);
				vo.setWorkRepairFaultList(faultList);
			}
			
			
		}else if(RepairOrderStep.SUBMIT.value == dto.getStep()) {//步骤变更为确认提交
			
			//判断故障是否存在
			WorkRepairFault repairFault = workRepairFaultMapper.selectByPrimaryKey(dto.getConfirmFaultId());
			
			if(Objects.isNull(repairFault)) {
				throw new YimaoException("故障类型不存在");
			}
			
			if(dto.getIsChangeMaterial()) {
				List<WorkRepairMaterialUseRecord> useRecord=new ArrayList();
				for (GoodsMaterialsDTO material : dto.getMaterials()) {
					WorkRepairMaterialUseRecord record=new WorkRepairMaterialUseRecord();
					if(Objects.isNull(material.getId())) {
						throw new BadRequestException("更换耗材id为空");
					}
					
					if(StringUtil.isEmpty(material.getName())) {
						throw new BadRequestException("更换耗材名称为空");
					}
					
					if(Objects.isNull(material.getMaterialCount()) || material.getMaterialCount() <= 0) {
						throw new BadRequestException("更换耗材数量错误");
					}
					record.setMaterialId(material.getId());
					record.setMaterialName(material.getName());
					record.setMaterialCount(material.getMaterialCount());
					record.setFirstCategoryName(material.getFirstLevelCategoryName());
					record.setSecondCategoryName(material.getSecondLevelCategoryName());
					record.setWorkRepairOrderId(dto.getId());
					useRecord.add(record);
				}
								
				//保存工单使用耗材记录
				workRepairOrderMapper.batchInsertMaterialUseRecord(useRecord);
			}
						
			//修改工单状态
			WorkRepairOrder update =new WorkRepairOrder();
			update.setStep(dto.getStep());
			update.setConfirmFaultId(repairFault.getId());
			update.setConfirmFaultName(repairFault.getFaultName());
			update.setIsChangeMaterial(dto.getIsChangeMaterial());
			Example example = new Example(WorkRepairOrder.class);
	        Example.Criteria criteria = example.createCriteria();
	        criteria.andEqualTo("id", dto.getId());
	        criteria.andEqualTo("status", RepairOrderStatus.PROCESSIONG.value);
	        criteria.andEqualTo("step", RepairOrderStep.REPAIR.value);
			int res = workRepairOrderMapper.updateByExampleSelective(update, example);
			
			if(res < 1) {
				throw new YimaoException("故障维修变更失败");
			}
			//
			vo = workRepairOrderMapper.selectSubmitRepairInfo(dto.getId());	
			
			if(dto.getIsChangeMaterial()) {
				
				//扣减库存库存（事务一致性，返回false全部回滚）
				StationMaterialStockDTO reduce =new StationMaterialStockDTO();
				reduce.setStationId(vo.getStationId());
				reduce.setProductCategoryId(vo.getProductCategoryId());
				reduce.setMaterials(dto.getMaterials());
				boolean reduceStockRes = systemFeign.reduceStationMaterialStock(reduce);
			
				if(!reduceStockRes) {
					throw new YimaoException("安装工门店耗材库存不足");
				}
			}
			//设置返回信息
			
		}else {
			throw new BadRequestException("处理中step错误");
		}
		
		return vo;
		
	}


	/**
	 * 维修工单继续服务展示信息
	 */
	public WorkRepairOrderVO continueRepairServiveInfo(Integer id, Integer step) {
		if(RepairOrderStep.CONFIRM.value == step) {
			return workRepairOrderMapper.selectConfirmInfo(id);
		}else if(RepairOrderStep.REPAIR.value == step) {
			WorkRepairOrder workRepairOrder = workRepairOrderMapper.selectByPrimaryKey(id);
			
			if(Objects.isNull(workRepairOrder)) {
				return null;
			}			
			
			WorkRepairOrderVO vo =new WorkRepairOrderVO();
			//查询故障类型列表
			List<WorkRepairFault> workRepairFaultList = workRepairFaultMapper.listWorkRepairFault(null);
			
			if(CollectionUtil.isNotEmpty(workRepairFaultList)) {
				List<WorkRepairFaultDTO> faultList=new ArrayList<>();
				BeanUtils.copyProperties(workRepairFaultList, faultList, WorkRepairFaultDTO.class);
				vo.setWorkRepairFaultList(faultList);
			}
			
			List<GoodsMaterialsDTO> materialList = systemFeign.getMaterialListByCategoryId(workRepairOrder.getProductCategoryId());
			
			vo.setMaterialList(materialList);
			
			return vo;
			
		}else if(RepairOrderStep.SUBMIT.value == step) {
			return workRepairOrderMapper.selectSubmitRepairInfo(id);
		}else {
			return null;
		}
	}

	@Override
	public Map<String, Integer> getRepairOrderCount(Integer engineerId) {
		Map<String, Integer> repairMap = new HashMap<>();
		repairMap.put("pendingOrderCount",workRepairOrderMapper.getRepairOrderCount(engineerId,RepairOrderStatus.PENDING.value));//待维修
		repairMap.put("processingOrderCount",workRepairOrderMapper.getRepairOrderCount(engineerId,RepairOrderStatus.PROCESSIONG.value));//维修中
		repairMap.put("handUpOrderCount",workRepairOrderMapper.getRepairOrderCount(engineerId,RepairOrderStatus.HANG_UP.value));//挂单
		repairMap.put("completeOrderCount",workRepairOrderMapper.getRepairOrderCount(engineerId,RepairOrderStatus.SOLVED.value));//已完成
		return repairMap;
	}


	/**
	 * 更换维修安装工
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
	public void replaceRepairEngineer(String workOrderNo, Integer engineerId,Integer sourceType,String operator) {
		if(StringUtil.isEmpty(workOrderNo)) {
			throw new BadRequestException("维修工单号为空");
		}
		
		if(StringUtil.isEmpty(operator)) {
			throw new BadRequestException("操作人为空");
		}
		
		if(Objects.isNull(sourceType)) {
			throw new YimaoException("系统来源为空");
		}   
    	
    	if(Objects.isNull(engineerId)) {
			throw new YimaoException("更换安装工id为空");
		}    	
		
		WorkRepairOrder query = new WorkRepairOrder();
		query.setWorkOrderNo(workOrderNo);
		WorkRepairOrder workRepairOrder =workRepairOrderMapper.selectOne(query);
    	
    	if(Objects.isNull(workRepairOrder)) {
    		throw new YimaoException("维修工单不存在");
    	}
    	
    	if(RepairOrderStatus.PENDING.value != workRepairOrder.getStatus()) {
    		throw new YimaoException("维修工单非待维修状态");
    	}
    	
    	//查询安装工
    	EngineerDTO engineer = userFeign.getEngineerById(engineerId);
    	
    	if(Objects.isNull(engineer)) {
    		throw new YimaoException("更换安装工不存在");
    	}
    	
    	if(engineer.getForbidden()) {
    		throw new YimaoException("更换安装工已禁用");
    	}
    	
    	if(CollectionUtil.isEmpty(engineer.getServiceAreaList())) {
    		throw new YimaoException("更换安装工未绑定服务区域");
    	}
    	
    	if(Objects.isNull(engineer.getStationId()) || !engineer.getStationId().equals(workRepairOrder.getStationId())) {
    		throw new YimaoException("更换安装工所属门店错误");
    	}
    	
    	//判断水机所在区域是否为安装工服务区域
		boolean isServiceArea=false;
		for (EngineerServiceAreaDTO serviceArea : engineer.getServiceAreaList()) {			
			String province=serviceArea.getProvince();
			String city=serviceArea.getCity();
			String region=serviceArea.getRegion();
			if(workRepairOrder.getProvince().equals(province) && workRepairOrder.getCity().equals(city) && workRepairOrder.getRegion().equals(region)) {
				isServiceArea=true;
				break;
			}
		}
		
		if(! isServiceArea) {
			throw new YimaoException("更换安装工服务区域不匹配");
		}
		
		int res = workRepairOrderMapper.replaceRepairEngineer(workOrderNo,engineerId,engineer.getRealName());
		
		if(res < 1) {
			throw new YimaoException("更换安装工失败");
		}
		
		 // 服务人员更换记录
		ServiceEngineerChangeRecord serviceEngineerChangeRecord = new ServiceEngineerChangeRecord();
        serviceEngineerChangeRecord.setWorkOrderNo(workOrderNo);
		serviceEngineerChangeRecord.setWorkOrderType(ServiceEngineerChangeWorkOrderTypeEnum.REPAIR_WORK_ORDER.value);
        serviceEngineerChangeRecord.setOrigEngineerId(workRepairOrder.getEngineerId());
        serviceEngineerChangeRecord.setOrigEngineerName(workRepairOrder.getEngineerName());
        serviceEngineerChangeRecord.setDestEngineerId(engineer.getId());
        serviceEngineerChangeRecord.setDestEngineerName(engineer.getRealName());
        serviceEngineerChangeRecord.setSource(sourceType);
        serviceEngineerChangeRecord.setOperator(operator);
        serviceEngineerChangeRecord.setTime(new Date());
        serviceEngineerChangeRecordMapper.insertSelective(serviceEngineerChangeRecord);
		
	}


	@Override
	public Integer getRepairModelTotalCount(Integer engineerId) {
		return workRepairOrderMapper.getRepairModelTotalCount(engineerId);
	}
}


