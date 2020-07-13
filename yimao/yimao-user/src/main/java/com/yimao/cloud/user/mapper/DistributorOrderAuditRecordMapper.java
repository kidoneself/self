package com.yimao.cloud.user.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.user.DistributorOrderAuditRecordDTO;
import com.yimao.cloud.pojo.dto.user.DistributorOrderAuditRecordExportDTO;
import com.yimao.cloud.pojo.query.user.DistributorOrderAuditRecordQuery;
import com.yimao.cloud.user.po.DistributorOrderAuditRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


/**
 * @author Lizhqiang
 * @date 2018/12/20
 */
public interface DistributorOrderAuditRecordMapper extends Mapper<DistributorOrderAuditRecord> {

    /**
     * 审核记录分页
     *
     * @param query
     * @return
     */
    Page<DistributorOrderAuditRecordDTO> pageAudit(DistributorOrderAuditRecordDTO query);

    /**
     * 审核记录导出
     *
     * @param query
     * @return
     */
    Page<DistributorOrderAuditRecordExportDTO> distributorOrderAuditRecordExport(DistributorOrderAuditRecordQuery query);

    List<DistributorOrderAuditRecordDTO> getDistributorOrderAuditRecordByOrderId(@Param("orderId") Long orderId);

    Integer queryFinancialAuditCount(@Param("orderId") Long orderId);
}
