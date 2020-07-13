package com.yimao.cloud.station.mapper;

import org.apache.ibatis.annotations.Param;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.station.GoodsLoanApplyDTO;
import com.yimao.cloud.pojo.query.station.GoodsLoanApplyQuery;
import com.yimao.cloud.station.po.StockLoanApply;

import tk.mybatis.mapper.common.Mapper;

public interface StockLoanApplyMapper extends Mapper<StockLoanApply>{

	Page<GoodsLoanApplyDTO> listStockLoanApply(GoodsLoanApplyQuery query);

	/**
	 * 物资申请审核校验被借调方或申请方门店申请信息
	 * @param id 物资申请id
	 * @param applyType 借调类型 0-借入 1-借出
	 * @return
	 */
	GoodsLoanApplyDTO selectStockLoanApply(@Param("id")Integer id, @Param("applyType")boolean applyType);

}