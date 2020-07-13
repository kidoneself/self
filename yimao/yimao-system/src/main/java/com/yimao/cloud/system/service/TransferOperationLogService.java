package com.yimao.cloud.system.service;

import com.yimao.cloud.pojo.dto.system.TransferOperationLogDTO;
import com.yimao.cloud.pojo.query.system.TransferOperationLogQuery;
import com.yimao.cloud.pojo.vo.PageVO;

/**
 * @author Liu Long Jie
 * @Date 2020-5-21 09:41:11
 */
public interface TransferOperationLogService {
    void save(TransferOperationLogDTO dto);

    PageVO<TransferOperationLogDTO> page(Integer pageNum, Integer pageSize, TransferOperationLogQuery query);
}
