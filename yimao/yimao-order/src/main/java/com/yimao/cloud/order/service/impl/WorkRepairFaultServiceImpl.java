package com.yimao.cloud.order.service.impl;

import java.util.Date;
import java.util.Objects;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.order.mapper.WorkRepairFaultMapper;
import com.yimao.cloud.order.mapper.WorkRepairFaultOperationMapper;
import com.yimao.cloud.order.po.WorkRepairFault;
import com.yimao.cloud.order.po.WorkRepairFaultOperation;
import com.yimao.cloud.order.service.WorkRepairFaultService;
import com.yimao.cloud.pojo.vo.PageVO;

import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
public class WorkRepairFaultServiceImpl implements WorkRepairFaultService {
	@Resource
	private WorkRepairFaultOperationMapper workRepairFaultOperationMapper;
	@Resource
	private WorkRepairFaultMapper workRepairFaultMapper;
	@Resource
	private UserCache userCache;

	/**
	 * 新增故障类型
	 */
	public void create(WorkRepairFault fault) {
		
		if(StringUtil.isEmpty(fault.getFaultName())) {
			throw new BadRequestException("故障类型名称为空");
		}
		//校验名称是否重复
		int count = workRepairFaultMapper.faultRenameCount(fault.getFaultName(),null);
		
		if(count>0) {
			throw new YimaoException("故障类型名称重复");
		}
		//后台创建
		fault.setType(1);
		fault.setCreateTime(new Date());
		fault.setCreator(userCache.getCurrentAdminRealName());
		
		workRepairFaultMapper.insertSelective(fault);
	}
	
	/**
	 * 故障类型列表
	 */
	public PageVO<WorkRepairFault> page(Integer pageNum, Integer pageSize, Integer type) {
	
		PageHelper.startPage(pageNum, pageSize);
		
		Page<WorkRepairFault> pageResult = workRepairFaultMapper.listWorkRepairFault(type);
				
		return new PageVO<>(pageNum, pageResult);

	}

	/**
	 * 故障类型编辑（只存在编辑名称，排序）
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
	public void update(WorkRepairFault fault) {
		
		if(Objects.isNull(fault.getId())) {
			throw new BadRequestException("故障类型id为空");
		}
		
		WorkRepairFault origin = workRepairFaultMapper.selectByPrimaryKey(fault.getId());
		
		if(Objects.isNull(origin)) {
			throw new YimaoException("故障类型不存在");
		}
		
		boolean changeName=false;
		boolean changeSorts=false;
		
		WorkRepairFault update =new WorkRepairFault();
		update.setId(fault.getId());
		
		//名称编辑
		if(Objects.nonNull(fault.getFaultName()) && ! fault.getFaultName().equals(origin.getFaultName())) {
			
			//校验名称是否重复
			int count = workRepairFaultMapper.faultRenameCount(fault.getFaultName(),null);
			
			if(count>0) {
				throw new YimaoException("故障类型名称重复");
			}
			
			if(origin.getType() == 0) {
				throw new YimaoException("水机推送故障类别无法编辑名称");
			}
			
			update.setFaultName(fault.getFaultName());
			changeName=true;
		}
		
		//排序编辑
		if(Objects.nonNull(fault.getSorts()) && ! fault.getSorts().equals(origin.getSorts())) {
			update.setSorts(fault.getSorts());
			changeSorts=true;
		}
		
		if(changeName || changeSorts) {
			
			int res = workRepairFaultMapper.updateByPrimaryKeySelective(update);
			
			if(res < 1) {
				throw new YimaoException("故障类别编辑失败");
			}
			
			//存在编辑姓名纪录操作日志
			if(changeName) {
				WorkRepairFaultOperation operation = new WorkRepairFaultOperation();
				operation.setFaultId(origin.getId());
				operation.setBeforeFaultName(origin.getFaultName());
				operation.setAfterFaultName(update.getFaultName());
				operation.setOperateType(1);
				operation.setOperateTime(new Date());
				operation.setOperator(userCache.getCurrentAdminRealName());
				workRepairFaultOperationMapper.insertSelective(operation);
			}
		}
		
	}

	/**
	 * 删除故障类型
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
	public void delete(Integer id) {
		if(Objects.isNull(id)) {
			throw new BadRequestException("故障类型id为空");
		}
		
		WorkRepairFault origin = workRepairFaultMapper.selectByPrimaryKey(id);
		
		if(Objects.isNull(origin)) {
			throw new YimaoException("故障类型不存在");
		}
		
		if(origin.getType() == 0) {
			throw new YimaoException("水机推送故障类别无法删除");
		}
		
		int res = workRepairFaultMapper.deleteByPrimaryKey(id);
		
		if(res < 1) {
			throw new YimaoException("故障类别删除失败");
		}
		
		WorkRepairFaultOperation operation = new WorkRepairFaultOperation();
		operation.setFaultId(origin.getId());
		operation.setBeforeFaultName(origin.getFaultName());
		operation.setOperateType(0);
		operation.setOperateTime(new Date());
		operation.setOperator(userCache.getCurrentAdminRealName());
		workRepairFaultOperationMapper.insertSelective(operation);
	}
	
	
}
