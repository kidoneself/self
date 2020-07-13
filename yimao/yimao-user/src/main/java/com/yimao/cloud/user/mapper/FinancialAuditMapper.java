package com.yimao.cloud.user.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.user.FinancialAuditDTO;
import com.yimao.cloud.pojo.dto.user.FinancialAuditExportDTO;
import com.yimao.cloud.user.po.FinancialAudit;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author Lizhqiang
 * @date 2018/12/21
 */
public interface FinancialAuditMapper extends Mapper<FinancialAudit> {

    Page<FinancialAuditDTO> financialAuditPage(FinancialAuditDTO query);

    List<FinancialAuditDTO> getFinancialAuditById(Long orderId);

    Page<FinancialAuditExportDTO> listFinancialAudit(@Param("distributorOrderId") Long distributorOrderId, @Param("orderType") Integer orderType, @Param("name") String name, @Param("distributorAccount") String distributorAccount, @Param("roleId") Integer roleId, @Param("destRoleId") Integer destRoleId, @Param("payType") Integer payType, @Param("payStartTime") String payStartTime, @Param("payEndTime") String payEndTime);
}
