package com.yimao.cloud.system.po;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.yimao.cloud.pojo.dto.system.TransferOperationLogDTO;

import java.util.Date;

/**
 * 服务区域承包转让操作记录
 *
 * @author Liu Long Jie
 * @Date 2020-5-21 08:58:38
 */
@Data
@Table(name = "transfer_operation_log")
public class TransferOperationLog {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer transferorId;//转让方
    private Integer receiverId;//接收方
    private Integer operateType;//操作类型:1.售后承包转让,2.安装工转让
    private String description;//描述信息:用来存储具体转让的详细信息
    private String operator;//操作人
    private Date createTime;//创建时间

    public TransferOperationLog() {
    }

    /**
     * 用业务对象TransferOperationLogDTO初始化数据库对象TransferOperationLog。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public TransferOperationLog(TransferOperationLogDTO dto) {
        this.id = dto.getId();
        this.transferorId=dto.getTransferorId();
        this.receiverId=dto.getReceiverId();
        this.operateType=dto.getOperateType();
        this.description=dto.getDescription();
        this.operator = dto.getOperator();
        this.createTime = dto.getCreateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象ServiceAreaMakeOverOperationLogDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(TransferOperationLogDTO dto) {
        dto.setId(this.id);
        dto.setTransferorId(this.transferorId);
        dto.setReceiverId(this.receiverId);
        dto.setOperateType(this.operateType);
        dto.setDescription(this.description);
        dto.setOperator(this.operator);
        dto.setCreateTime(this.createTime);
        
    }
}