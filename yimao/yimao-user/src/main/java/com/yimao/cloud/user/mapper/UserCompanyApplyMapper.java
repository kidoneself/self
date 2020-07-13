package com.yimao.cloud.user.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.user.CompanyAuditExportDTO;
import com.yimao.cloud.pojo.dto.user.UserCompanyApplyDTO;
import com.yimao.cloud.user.po.UserCompanyApply;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * @description   企业信息申请信息
 * @author Liu Yi
 * @date 2019/9/2 9:42
 */
public interface UserCompanyApplyMapper extends Mapper<UserCompanyApply> {

    Page<UserCompanyApplyDTO> pageQuery(@Param("orderId") Long orderId, @Param("orderType") Integer orderType, @Param("companyName") String companyName, @Param("account") String account, @Param("roleLevel") Integer roleLevel);

    Page<CompanyAuditExportDTO> listCompanyAudit(@Param("orderId") Long orderId, @Param("orderType") Integer orderType, @Param("companyName") String companyName, @Param("account") String account, @Param("roleLevel") Integer roleLevel);

    String getCompanyNameByOrderId(@Param("orderId") Long orderId);
}
