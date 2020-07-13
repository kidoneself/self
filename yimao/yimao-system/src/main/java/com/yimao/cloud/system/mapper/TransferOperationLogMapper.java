package com.yimao.cloud.system.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.system.TransferOperationLogDTO;
import com.yimao.cloud.pojo.dto.system.TransferOperationLogExportDTO;
import com.yimao.cloud.pojo.query.system.TransferOperationLogQuery;
import com.yimao.cloud.system.po.TransferOperationLog;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author Liu Long Jie
 * @Date 2020-5-21 09:45:03
 */
public interface TransferOperationLogMapper extends Mapper<TransferOperationLog> {

    Page<TransferOperationLogDTO> page(TransferOperationLogQuery query);

    List<TransferOperationLogExportDTO> exportTransferOperationLog(TransferOperationLogQuery transferOperationLogQuery);
}
