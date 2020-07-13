package com.yimao.cloud.user.po;

import com.yimao.cloud.pojo.dto.user.FinancialAuditDTO;
import lombok.Data;

import java.util.Date;

/**
 * 财务审核
 *
 * @author Lizhqiang
 * @date 2018/12/21
 */


@Data
public class FinancialAudit {


    private Integer id;
    /**
     * 经销商订单号
     */
    private Long distributorOrderId;
    /**
     * 财务审核状态
     * 0-未审核、1-审核通过、2-审核不通过、3-无需审核
     */
    private Integer financialState;

    /**
     * 审核不通过原因
     */
    private String cause;

    /**
     * 财务审核人
     */
    private String financialAuditor;

    /**
     * 财务审核时间
     */
    private Date auditTime;

    /**
     * 支付凭证
     */
    private String payRecord;

    /**
     * 支付时间
     */
    private Date payTime;


    public FinancialAudit() {
    }

    /**
     * 用业务对象FinancialAuditDTO初始化数据库对象FinancialAudit。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public FinancialAudit(FinancialAuditDTO dto) {
        this.id = dto.getId();
        this.distributorOrderId = dto.getDistributorOrderId();
        this.financialState = dto.getFinancialState();
        this.cause = dto.getCause();
        this.financialAuditor = dto.getFinancialAuditor();
        this.auditTime = dto.getAuditTime();
        this.payRecord = dto.getPayRecord();
        this.payTime = dto.getPayTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象FinancialAuditDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(FinancialAuditDTO dto) {
        dto.setId(this.id);
        dto.setDistributorOrderId(this.distributorOrderId);
        dto.setFinancialState(this.financialState);
        dto.setCause(this.cause);
        dto.setFinancialAuditor(this.financialAuditor);
        dto.setAuditTime(this.auditTime);
        dto.setPayRecord(this.payRecord);
        dto.setPayTime(this.payTime);
    }
}
