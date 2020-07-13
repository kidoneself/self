package com.yimao.cloud.order.po;

import com.yimao.cloud.pojo.dto.order.ServiceEngineerChangeRecordDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 安装工单、维修工单、维护工单、移机工单、退机工单 服务人员变更记录
 *
 */
@Data
@Table(name = "service_engineer_change_record")
public class ServiceEngineerChangeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //工单号
    private String workOrderNo;
    //工单类型 1-安装工单、2-维修工单、3-维护工单、4-退机工单、5-移机工单（拆机） 6-移机工单（装机）
    private Integer workOrderType;
    //来源端 1-站务系统
    private Integer source;
    //原安装工id
    private Integer origEngineerId;
    //原安装工姓名
    private String origEngineerName;
    //更变后安装工id
    private Integer destEngineerId;
    //更变后安装工姓名
    private String destEngineerName;
    //操作人
    private String operator;
    //操作时间
    private Date time;

    public ServiceEngineerChangeRecord() {
    }

    /**
     * 用业务对象EngineerChangeRecordDTO初始化数据库对象EngineerChangeRecord。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public ServiceEngineerChangeRecord(ServiceEngineerChangeRecordDTO dto) {
        this.id = dto.getId();
        this.workOrderNo = dto.getWorkOrderNo();
        this.workOrderType = dto.getWorkOrderType();
        this.source = dto.getSource();
        this.origEngineerId = dto.getOrigEngineerId();
        this.origEngineerName = dto.getOrigEngineerName();
        this.destEngineerId = dto.getDestEngineerId();
        this.destEngineerName = dto.getDestEngineerName();
        this.operator = dto.getOperator();
        this.time = dto.getTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象EngineerChangeRecordDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(ServiceEngineerChangeRecordDTO dto) {
        dto.setId(this.id);
        dto.setWorkOrderNo(this.workOrderNo);
        dto.setWorkOrderType(this.workOrderType);
        dto.setSource(this.source);
        dto.setOrigEngineerId(this.origEngineerId);
        dto.setOrigEngineerName(this.origEngineerName);
        dto.setDestEngineerId(this.destEngineerId);
        dto.setDestEngineerName(this.destEngineerName);
        dto.setOperator(this.operator);
        dto.setTime(this.time);
    }
}
