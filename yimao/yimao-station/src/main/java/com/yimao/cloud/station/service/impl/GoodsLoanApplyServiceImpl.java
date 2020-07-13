package com.yimao.cloud.station.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.GoodsCategoryEnum;
import com.yimao.cloud.base.enums.LoanApplyStatusEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.station.GoodsLoanApplyDTO;
import com.yimao.cloud.pojo.dto.station.StationAdminCacheDTO;
import com.yimao.cloud.pojo.dto.system.GoodsMaterialsDTO;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import com.yimao.cloud.pojo.dto.system.StationStockTransferDTO;
import com.yimao.cloud.station.feign.SystemFeign;
import com.yimao.cloud.station.mapper.StockLoanApplyMapper;
import com.yimao.cloud.station.mapper.StockLoanObjectMapper;
import com.yimao.cloud.station.po.StockLoanApply;
import com.yimao.cloud.station.po.StockLoanObject;
import com.yimao.cloud.station.service.GoodsLoanApplyService;
@Service
public class GoodsLoanApplyServiceImpl implements GoodsLoanApplyService {
	@Resource 
	private UserCache userCache;
	@Resource 
	private SystemFeign systemFeign;
	@Resource
	private StockLoanApplyMapper stockLoanApplyMapper;
	@Resource
	private StockLoanObjectMapper stockLoanObjectMapper;


	/**
	 * 物资库存借调申请
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void goodsLoanApply(GoodsLoanApplyDTO dto) {
		//校验
		checkLoanApply(dto);
		//校验服务站门店
		Set<Integer> userStations=userCache.getStationUserAreas(0, null);
		
		if(! userStations.contains(dto.getApplyStationId())) {
			throw new BadRequestException("用户未绑定该调入门店");
		}
		
		//发起申请对象
		StationDTO applyStation = systemFeign.getStationById(dto.getApplyStationId());
		
		if(Objects.isNull(applyStation)) {
			throw new BadRequestException("申请调入门店不存在");
		}
		
		StationDTO loanStation = systemFeign.getStationById(dto.getLoanStationId());
		
		if(Objects.isNull(loanStation)) {
			throw new BadRequestException("申借对象门店申请调入门店不存在");
		}
		
		//校验申借对象门店物资是否存在
		GoodsMaterialsDTO  goodsMaterials = systemFeign.checkStationGoodsIsHaveStock(dto.getLoanStationId(), dto.getGoodsMaterialsId());
		
		if(Objects.isNull(goodsMaterials)) {
			throw new BadRequestException("申借对象门店不存在该物资");
		}
		
		//获取用户缓存信息
		StationAdminCacheDTO adminCache = userCache.getStationUserInfo();
		
		StockLoanApply apply=new StockLoanApply();
		apply.setStatus(LoanApplyStatusEnum.WAIT.value);
		apply.setFirstCategoryLevelName(goodsMaterials.getFirstLevelCategoryName());
		apply.setSecondCategoryLevelName(goodsMaterials.getSecondLevelCategoryName());
		apply.setGoodsMaterialsId(goodsMaterials.getId());
		apply.setGoodsMaterialsName(goodsMaterials.getName());
		apply.setApplyCount(dto.getApplyCount());
		apply.setRemark(dto.getRemark());
		apply.setApplyerId(adminCache.getId());
		apply.setApplyTime(new Date());
		
		int applyRes = stockLoanApplyMapper.insertSelective(apply);
		
		if(applyRes < 1) {
			throw new YimaoException("物资库存借调申请失败");
		}else {			
			List<StockLoanObject> list=new ArrayList<>();
			//借入对象
			StockLoanObject applyObj = new StockLoanObject();
			applyObj.setStockLoanApplyId(apply.getId());
			applyObj.setApplyType(false);
			applyObj.setCompanyId(adminCache.getStationCompanyId());
			applyObj.setCompanyName(adminCache.getStationCompanyName());
			applyObj.setStationId(dto.getApplyStationId());
			applyObj.setStationCode(applyStation.getCode());
			applyObj.setStationName(applyStation.getName());
			applyObj.setStationMasterName(applyStation.getMasterName());
			applyObj.setStationMasterPhone(applyStation.getMasterPhone());
			list.add(applyObj);
			//借出对象		
			StockLoanObject loanObj = new StockLoanObject();
			loanObj.setStockLoanApplyId(apply.getId());
			loanObj.setApplyType(true);
			loanObj.setCompanyId(dto.getLoanCompanyId());
			loanObj.setCompanyName(dto.getLoanCompanyName());
			loanObj.setStationId(dto.getLoanStationId());
			loanObj.setStationCode(loanStation.getCode());
			loanObj.setStationName(loanStation.getName());
			loanObj.setStationMasterName(loanStation.getMasterName());
			loanObj.setStationMasterPhone(loanStation.getMasterPhone());
			list.add(loanObj);
			
			int loanObject=stockLoanObjectMapper.batchInsert(list);
			
			if(loanObject != 2) {
				throw new YimaoException("物资库存借调申请失败,借出借入对象新增错误");
			}
		}
	}

	/**
	 * 校验申请参数
	 * @param dto
	 */
	private void checkLoanApply(GoodsLoanApplyDTO dto) {
		if(Objects.isNull(dto.getLoanCompanyId())) {
			throw new BadRequestException("申借对象公司id为空");
		}
		
		if(StringUtil.isEmpty(dto.getLoanCompanyName())) {
			throw new BadRequestException("申借对象公司名称为空");
		}
		
		if(Objects.isNull(dto.getLoanStationId())) {
			throw new BadRequestException("申借对象门店id为空");
		}
						
		if(Objects.isNull(dto.getApplyStationId())) {
			throw new BadRequestException("申请调入门店id为空");
		}
				
		if(Objects.isNull(dto.getGoodsMaterialsId())) {
			throw new BadRequestException("申请物资id为空");
		}
		
		if(Objects.isNull(dto.getApplyCount())) {
			throw new BadRequestException("申请物资数量为空");
		}
		
		if(dto.getApplyCount() <= 0) {
			throw new BadRequestException("申请物资数量错误");
		}

		
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void goodsLoanCheck(Integer id, Integer status) {		
		if(Objects.isNull(id)) {
			throw new BadRequestException("物资申请id为空");
		}
		
		if(Objects.isNull(LoanApplyStatusEnum.find(status))) {
			throw new BadRequestException("审核状态不存在");
		}
		
		//查询该申请被借调方
		GoodsLoanApplyDTO goodsLoanOut = stockLoanApplyMapper.selectStockLoanApply(id,true);
		//查询借调方
		GoodsLoanApplyDTO goodsLoanIn = stockLoanApplyMapper.selectStockLoanApply(id,false);
		
		if(Objects.isNull(goodsLoanOut)) {
			throw new YimaoException("物资申请不存在");
		}
		
		//校验该被借调方门店用户是否有权限
		Set<Integer> userStations=userCache.getStationUserAreas(0, null);
		
		if(! userStations.contains(goodsLoanOut.getStationId())) {
			throw new YimaoException("用户未绑定该门店");
		}
		
		StockLoanApply update=new StockLoanApply();
		update.setId(id);
		update.setStatus(status);
		update.setAuditTime(new Date());
		update.setAuditorId(userCache.getStationUserInfo().getId());
		if(status == LoanApplyStatusEnum.REFUSE.value) {//拒绝
			stockLoanApplyMapper.updateByPrimaryKeySelective(update);
		}else if(status == LoanApplyStatusEnum.AGREE.value) {
			//校验申借对象门店物资是否存在以及校验物资类型是否为水机设备
			GoodsMaterialsDTO goodsMaterials = systemFeign.checkStationGoodsIsHaveStock(goodsLoanOut.getStationId(), goodsLoanOut.getGoodsMaterialsId());
			
			if(Objects.isNull(goodsMaterials)) {
				throw new YimaoException("门店被申请物资不存在");
			}
			
			if(goodsMaterials.getStockCount() < goodsLoanOut.getApplyCount()) {
				throw new YimaoException("门店被申请物资库存不足");
			}
			
			//修改申请状态等信息
			stockLoanApplyMapper.updateByPrimaryKeySelective(update);
			
			//库存变更
			StationStockTransferDTO transfer = new StationStockTransferDTO();
			transfer.setGoodsMaterialsId(goodsMaterials.getId());
			transfer.setType(0);
			transfer.setTransferInStationId(goodsLoanIn.getStationId());
			transfer.setTransferOutStationId(goodsLoanOut.getStationId());
			transfer.setTransferStockCount(goodsLoanOut.getApplyCount());
			boolean res = systemFeign.transferStationStock(transfer);
			
			if(! res) {
				throw new YimaoException("门店库存借调操作失败");
			}
			
		}else {
			return;
		}
		
		
	}


}
