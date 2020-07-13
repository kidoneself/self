package com.yimao.cloud.order.po;

import com.yimao.cloud.pojo.dto.order.OrderAuditLogDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author zhilin.he
 * @description 订单审核日志记录
 * @date 2019/1/29 14:29
 **/
@Table(name = "order_audit_log")
@Data
public class OrderAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;//审核id
    private Long salesId;//售后单号
    private Long orderId;//子订单号
    private Integer type;//审核类型  1.取消订单退款（未收货），2.申请退货退款（已收货），3：提现
    private Integer subType;//子审核类型  1-业务部门审核，2-400客服审核（租赁商品），3-400客服提交物流，4-物资审核，5-经销商审核；
    private Boolean operationStatus;//操作状态:0、审核不通过，1、审核通过
    private String operation;//操作状态名
    private String menuName;//菜单名
    private String ip;//操作IP
    private String creator;//操作人
    private Date createTime;//操作时间
    private String auditReason;//审核不通过原因
    private String detailReason;//详情描述


    public OrderAuditLog() {
    }

    /**
     * 用业务对象OrderAuditLogDTO初始化数据库对象OrderAuditLog。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public OrderAuditLog(OrderAuditLogDTO dto) {
        this.id = dto.getId();
        this.salesId = dto.getSalesId();
        this.orderId = dto.getOrderId();
        this.type = dto.getType();
        this.subType = dto.getSubType();
        this.operationStatus = dto.getOperationStatus();
        this.operation = dto.getOperation();
        this.menuName = dto.getMenuName();
        this.ip = dto.getIp();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.auditReason = dto.getAuditReason();
        this.detailReason = dto.getDetailReason();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象OrderAuditLogDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(OrderAuditLogDTO dto) {
        dto.setId(this.id);
        dto.setSalesId(this.salesId);
        dto.setOrderId(this.orderId);
        dto.setType(this.type);
        dto.setSubType(this.subType);
        dto.setOperationStatus(this.operationStatus);
        dto.setOperation(this.operation);
        dto.setMenuName(this.menuName);
        dto.setIp(this.ip);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setAuditReason(this.auditReason);
        dto.setDetailReason(this.detailReason);
    }
}
