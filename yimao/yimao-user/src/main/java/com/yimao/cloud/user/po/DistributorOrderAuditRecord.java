package com.yimao.cloud.user.po;

import com.yimao.cloud.pojo.dto.user.DistributorOrderAuditRecordDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 审核记录
 *
 * @author Lizhqiang
 * @date 2018/12/17
 */


@Table(name = "distributor_order_audit_record")
@Data
public class DistributorOrderAuditRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer distributorId;
    private Integer roleId; //经销商角色ID(注册经销商或原经销商)
    private String roleName;
    private Integer destRoleId; //经销商角色ID
    private String destRoleName;
    private Long distributorOrderId; //经销商订单ID
    private Integer status; //审核状态0-未审核、1-审核通过、2-审核不通过、3-无需审核
    private String cause; //审核不通过原因
    private String auditor; //审核人
    private Date auditTime; //审核时间
    private Integer auditType; //审核类型/0-财务、 1-企业


    public DistributorOrderAuditRecord() {
    }

    /**
     * 用业务对象DistributorOrderAuditRecordDTO初始化数据库对象DistributorOrderAuditRecord。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public DistributorOrderAuditRecord(DistributorOrderAuditRecordDTO dto) {
        this.id = dto.getId();
        this.distributorId = dto.getDistributorId();
        this.roleId = dto.getRoleId();
        this.roleName = dto.getRoleName();
        this.destRoleId = dto.getDestRoleId();
        this.destRoleName = dto.getDestRoleName();
        this.distributorOrderId = dto.getDistributorOrderId();
        this.status = dto.getStatus();
        this.cause = dto.getCause();
        this.auditor = dto.getAuditor();
        this.auditTime = dto.getAuditTime();
        this.auditType = dto.getAuditType();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象DistributorOrderAuditRecordDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(DistributorOrderAuditRecordDTO dto) {
        dto.setId(this.id);
        dto.setDistributorId(this.distributorId);
        dto.setRoleId(this.roleId);
        dto.setRoleName(this.roleName);
        dto.setDestRoleId(this.destRoleId);
        dto.setDestRoleName(this.destRoleName);
        dto.setDistributorOrderId(this.distributorOrderId);
        dto.setStatus(this.status);
        dto.setCause(this.cause);
        dto.setAuditor(this.auditor);
        dto.setAuditTime(this.auditTime);
        dto.setAuditType(this.auditType);
    }
}
