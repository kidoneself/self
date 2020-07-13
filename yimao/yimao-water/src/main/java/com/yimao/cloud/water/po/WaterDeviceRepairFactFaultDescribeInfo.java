package com.yimao.cloud.water.po;

import com.yimao.cloud.pojo.dto.water.WaterDeviceRepairFactFaultDescribeInfoDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 解决措施
 */
@Table(name = "waterdevice_repair_fact_fault_describe")
@Data
public class WaterDeviceRepairFactFaultDescribeInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Date createTime;
    private Date updateTime;
    private String createUser;
    private String updateUser;
    private String delStatus;
    private Date deleteTime;
    private String idStatus;

    private String workCode;
    private String workOrderIndex;
    private Integer deviceId;
    private String deviceSncode;
    private String factFaultDescribeId;
    private String factFaultDescribe;
    private String factFaultReasonId;
    private String factFaultReason;
    private String solveMeasureId;
    private String solveMeasure;
    private String oldDeviceId;//老系统设备id


    public WaterDeviceRepairFactFaultDescribeInfo() {
    }

    /**
     * 用业务对象WaterDeviceRepairFactFaultDescribeInfoDTO初始化数据库对象WaterDeviceRepairFactFaultDescribeInfo。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public WaterDeviceRepairFactFaultDescribeInfo(WaterDeviceRepairFactFaultDescribeInfoDTO dto) {
        this.id = dto.getId();
        this.createTime = dto.getCreateTime();
        this.updateTime = dto.getUpdateTime();
        this.createUser = dto.getCreateUser();
        this.updateUser = dto.getUpdateUser();
        this.delStatus = dto.getDelStatus();
        this.deleteTime = dto.getDeleteTime();
        this.idStatus = dto.getIdStatus();
        this.workCode = dto.getWorkCode();
        this.workOrderIndex = dto.getWorkOrderIndex();
        this.deviceId = dto.getDeviceId();
        this.deviceSncode = dto.getDeviceSncode();
        this.factFaultDescribeId = dto.getFactFaultDescribeId();
        this.factFaultDescribe = dto.getFactFaultDescribe();
        this.factFaultReasonId = dto.getFactFaultReasonId();
        this.factFaultReason = dto.getFactFaultReason();
        this.solveMeasureId = dto.getSolveMeasureId();
        this.solveMeasure = dto.getSolveMeasure();
        this.oldDeviceId = dto.getOldDeviceId();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象WaterDeviceRepairFactFaultDescribeInfoDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(WaterDeviceRepairFactFaultDescribeInfoDTO dto) {
        dto.setId(this.id);
        dto.setCreateTime(this.createTime);
        dto.setUpdateTime(this.updateTime);
        dto.setCreateUser(this.createUser);
        dto.setUpdateUser(this.updateUser);
        dto.setDelStatus(this.delStatus);
        dto.setDeleteTime(this.deleteTime);
        dto.setIdStatus(this.idStatus);
        dto.setWorkCode(this.workCode);
        dto.setWorkOrderIndex(this.workOrderIndex);
        dto.setDeviceId(this.deviceId);
        dto.setDeviceSncode(this.deviceSncode);
        dto.setFactFaultDescribeId(this.factFaultDescribeId);
        dto.setFactFaultDescribe(this.factFaultDescribe);
        dto.setFactFaultReasonId(this.factFaultReasonId);
        dto.setFactFaultReason(this.factFaultReason);
        dto.setSolveMeasureId(this.solveMeasureId);
        dto.setSolveMeasure(this.solveMeasure);
        dto.setOldDeviceId(this.oldDeviceId);
    }
}
