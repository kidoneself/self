package com.yimao.cloud.system.service;

import com.yimao.cloud.pojo.dto.system.StoreHouseOperationLogDTO;
import com.yimao.cloud.pojo.query.system.StoreHouseOperationLogQuery;
import com.yimao.cloud.pojo.vo.PageVO;

public interface StoreHouseOperationLogService {

    void save(StoreHouseOperationLogDTO dto);

    PageVO<StoreHouseOperationLogDTO> page(StoreHouseOperationLogQuery query, Integer pageNum, Integer pageSize);
}
