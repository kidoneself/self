package com.yimao.cloud.user.service;

import com.yimao.cloud.pojo.dto.user.DistributorOrderAuditRecordDTO;
import com.yimao.cloud.pojo.dto.user.DistributorOrderAuditRecordExportDTO;
import com.yimao.cloud.pojo.query.user.DistributorOrderAuditRecordQuery;
import com.yimao.cloud.pojo.vo.PageVO;

import java.util.List;

/**
 * @author Lizhqiang
 * @date 2018/12/25
 */
public interface DistributorOrderAuditRecordService {


    PageVO<DistributorOrderAuditRecordDTO> page(Integer pageNum, Integer pageSize, DistributorOrderAuditRecordDTO query);

    List<DistributorOrderAuditRecordExportDTO> exportDistributorOrderAuditRecordAudit(DistributorOrderAuditRecordQuery query);
}
