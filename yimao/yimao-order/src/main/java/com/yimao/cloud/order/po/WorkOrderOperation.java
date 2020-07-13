package com.yimao.cloud.order.po;

import com.yimao.cloud.pojo.dto.order.WorkOrderOperationDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author zhilin.he
 * @description 工单操作对象
 * @date 2019/5/10 16:08
 **/
@Table(name = "workorder_operation")
@Getter
@Setter
public class WorkOrderOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;//工单操作id
    private String admin;//管理员
    private String workOrderId;//工单id
    private String renewOrderId;//续费工单id
    private String operation;//操作
    private String snCode;//sn码
    private String simCard;//sim码
    private String batchCode;//批次码
    private String remark;//备注
    private String reason;//原因
    private Date createTime;//创建时间


    public WorkOrderOperation() {
    }

    /**
     * 用业务对象WorkOrderOperationDTO初始化数据库对象WorkOrderOperation。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public WorkOrderOperation(WorkOrderOperationDTO dto) {
        this.id = dto.getId();
        this.admin = dto.getAdmin();
        this.workOrderId = dto.getWorkOrderId();
        this.renewOrderId = dto.getRenewOrderId();
        this.operation = dto.getOperation();
        this.snCode = dto.getSnCode();
        this.simCard = dto.getSimCard();
        this.batchCode = dto.getBatchCode();
        this.remark = dto.getRemark();
        this.reason = dto.getReason();
        this.createTime = dto.getCreateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象WorkOrderOperationDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(WorkOrderOperationDTO dto) {
        dto.setId(this.id);
        dto.setAdmin(this.admin);
        dto.setWorkOrderId(this.workOrderId);
        dto.setRenewOrderId(this.renewOrderId);
        dto.setOperation(this.operation);
        dto.setSnCode(this.snCode);
        dto.setSimCard(this.simCard);
        dto.setBatchCode(this.batchCode);
        dto.setRemark(this.remark);
        dto.setReason(this.reason);
        dto.setCreateTime(this.createTime);
    }
}
