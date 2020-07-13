package com.yimao.cloud.system.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.system.StoreHouseOperationLogDTO;
import com.yimao.cloud.pojo.query.system.StoreHouseOperationLogQuery;
import com.yimao.cloud.system.po.StoreHouseOperationLog;
import tk.mybatis.mapper.common.Mapper;

public interface StoreHouseOperationLogMapper extends Mapper<StoreHouseOperationLog> {

    Page<StoreHouseOperationLogDTO> page(StoreHouseOperationLogQuery query);
}
