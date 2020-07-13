package com.yimao.cloud.station.mapper;

import java.util.List;

import com.yimao.cloud.station.po.StockLoanObject;

import tk.mybatis.mapper.common.Mapper;

public interface StockLoanObjectMapper extends Mapper<StockLoanObject>{

	int batchInsert(List<StockLoanObject> list);

}