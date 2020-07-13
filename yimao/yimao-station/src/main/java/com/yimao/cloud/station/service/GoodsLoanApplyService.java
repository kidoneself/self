package com.yimao.cloud.station.service;

import com.yimao.cloud.pojo.dto.station.GoodsLoanApplyDTO;

public interface GoodsLoanApplyService {

	void goodsLoanApply(GoodsLoanApplyDTO dto);

	void goodsLoanCheck(Integer id, Integer status);

}
