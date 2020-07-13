package com.yimao.cloud.order.po;



import com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderOperateLogDTO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/***
 * 功能描述:维护工单记录实体类
 *
 * @auther: liu yi
 * @date: 2019/4/1 9:58
 */
@Data
@Table(name = "maintenance_work_order_operate_log")
@ApiModel(description = "维护工单操作日志")
public class MaintenanceWorkOrderOperateLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;//ID
    private String maintenanceWorkOrderId;//维护工单id
    private String operateDescription;//操作描述
    private Date createTime;//创建时间
    private String creator;//创建用户

    public MaintenanceWorkOrderOperateLog() {
    }
    /**
     * 用业务对象MaintenanceWorkOrderOperateLogDTO初始化数据库对象MaintenanceWorkOrderOperateLog。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public MaintenanceWorkOrderOperateLog(MaintenanceWorkOrderOperateLogDTO dto) {
        this.id = dto.getId();
        this.maintenanceWorkOrderId = dto.getMaintenanceWorkOrderId();
        this.operateDescription = dto.getOperateDescription();
        this.createTime = dto.getCreateTime();
        this.creator = dto.getCreator();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象MaintenanceWorkOrderOperateLogDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(MaintenanceWorkOrderOperateLogDTO dto) {
        dto.setId(this.id);
        dto.setMaintenanceWorkOrderId(this.maintenanceWorkOrderId);
        dto.setOperateDescription(this.operateDescription);
        dto.setCreateTime(this.createTime);
        dto.setCreator(this.creator);
    }
}
