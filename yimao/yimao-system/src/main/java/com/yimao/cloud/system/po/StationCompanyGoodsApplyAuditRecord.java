package com.yimao.cloud.system.po;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 物资申请审核记录
 *
 * @author Liu Long Jie
 * @date 2020-6-18
 */


@Table(name = "station_company_goods_apply_audit_record")
@Data
public class StationCompanyGoodsApplyAuditRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String applyOrderId; //申请单号id
    private Integer type; //审核类型 1-初审 2-复审
    private Integer status; //审核状态 1-通过 0-不通过
    private String cause; //审核不通过原因
    private String auditor; //审核人
    private Date auditTime; //审核时间

}
