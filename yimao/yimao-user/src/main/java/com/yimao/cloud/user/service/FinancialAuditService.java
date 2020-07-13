package com.yimao.cloud.user.service;

import com.yimao.cloud.pojo.dto.user.FinancialAuditDTO;
import com.yimao.cloud.pojo.dto.user.FinancialAuditExportDTO;
import com.yimao.cloud.pojo.vo.PageVO;

import java.util.Date;
import java.util.List;

/**
 * @author Lizhqiang
 * @date 2018/12/21
 */
public interface FinancialAuditService {

    /**
     * 财务审核分页显示
     *
     * @param pageNum
     * @param pageSize
     * @param query
     * @return
     */
    PageVO<FinancialAuditDTO> page(Integer pageNum, Integer pageSize, FinancialAuditDTO query);

    /**
     * 财务审核
     *
     * @param orderId
     * @param financialState
     * @param cause
     */
    void audit(Long orderId, Integer financialState, String cause);


    void auditBatch(List<Long> orderIds, Integer activityStatus, String cause);

    /**
     * 财务审核导出
     * @param distributorOrderId
     * @param orderType
     * @param name
     * @param distributorAccount
     * @param roleId
     * @param destRoleId
     * @param payType
     * @param payStartTime
     * @param payEndTime
     * @return
     */
    List<FinancialAuditExportDTO> exportFinancialAudit(Long distributorOrderId, Integer orderType, String name, String distributorAccount, Integer roleId, Integer destRoleId, Integer payType, String payStartTime, String payEndTime);
}
