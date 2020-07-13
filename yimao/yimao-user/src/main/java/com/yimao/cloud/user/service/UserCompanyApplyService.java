package com.yimao.cloud.user.service;

import com.yimao.cloud.pojo.dto.user.CompanyAuditExportDTO;
import com.yimao.cloud.pojo.dto.user.UserCompanyApplyDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.user.po.UserCompanyApply;

import java.util.List;
/**
 * @description   公司申请信息
 * @author Liu Yi
 * @date 2019/9/2 10:41
 */
public interface UserCompanyApplyService {
    /**
     * @description   新增公司信息
     * @author Liu Yi
     * @date 2019/8/21 17:42
     * @param
     * @return void
     */
    void insert(UserCompanyApply userCompanyApply);

    /**
     * @description   更新公司信息
     * @author Liu Yi
     * @date 2019/8/21 17:42
     * @param
     * @return void
     */
    void update(UserCompanyApplyDTO dto);
    /**
     * 根据经销商ID查询经企业审核详情
     *
     * @return
     * @author hhf
     * @date 2018/12/20
     */
    UserCompanyApplyDTO getCompanyByDistributorId(Integer id);

    /**
     * @description   根据ID查询经企业详情
     * @author Liu Yi
     * @date 2019/8/23 14:26
     * @param
     * @return com.yimao.cloud.pojo.dto.user.UserCompany
     */
    UserCompanyApply getCompanyById(Integer id);

    /**
     * @description   根据订单ID查询经企业详情
     * @author Liu Yi
     * @date 2019/8/23 14:26
     * @param
     * @return com.yimao.cloud.pojo.dto.user.UserCompany
     */
    UserCompanyApplyDTO getCompanyByOrderId(Long orderId);

    /**
     * 分页查询经销商信息
     *
     * @return
     * @author hhf
     * @date 2018/12/21
     */
    PageVO<UserCompanyApplyDTO> pageQuery(Integer pageNum, Integer pageSize, Long orderId, Integer orderType, String companyName, String account, Integer roleLevel);

    /**
     * 企业审核
     *
     * @param
     * @param updater
     * @param cause
     * @return
     * @author hhf
     * @date 2018/12/24
     */
    void audit(String updater, Long id, Integer enterpriseState, String cause);

    /**
     * 批量企业审核
     *
     * @return
     * @author hhf
     * @date 2018/12/25
     */

    void auditBatch(String updater, List<Long> ids, Integer enterpriseState, String cause);

    /**
     * 导出企业审核信息
     * @param orderId
     * @param orderType
     * @param companyName
     * @param account
     * @param roleLevel
     * @return
     */
    List<CompanyAuditExportDTO> exportUserCompanyApplyAudit(Long orderId, Integer orderType, String companyName, String account, Integer roleLevel);
}
