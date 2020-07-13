package com.yimao.cloud.system.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.system.StationBackStockRecordDTO;
import com.yimao.cloud.pojo.query.system.StationStoreHouseQuery;
import com.yimao.cloud.system.po.StationBackStockRecord;

import tk.mybatis.mapper.common.Mapper;

public interface StationBackStockRecordMapper extends Mapper<StationBackStockRecord>{

	/**
	 * 修改退机纪录为已转移库存，转移时间，转移人id
	 * @param transfer
	 * @return
	 */
	int changeTransferInfo(StationBackStockRecord transfer);

	Page<StationBackStockRecordDTO> pageStationCompanyStoreHouse(StationStoreHouseQuery query);

}