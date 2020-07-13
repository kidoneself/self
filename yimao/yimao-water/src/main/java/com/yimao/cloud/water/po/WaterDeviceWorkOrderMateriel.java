package com.yimao.cloud.water.po;


import com.yimao.cloud.pojo.dto.water.WaterDeviceWorkOrderMaterielDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;


/**
 * 工单消耗的耗材(滤芯)
 *
 * @author Liu Yi
 * @date 2019-3-20
 */
@Data
@Table(name = "workorder_materiel_filter")
public class WaterDeviceWorkOrderMateriel{
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
    private String materielId;
    private String materielName;
    private String materielTypeId;
    private String materielTypeName;
    private String materielBatchCode;
    private String materielIndex;
    private Date scanCodeTime;


    public WaterDeviceWorkOrderMateriel() { }

    /**
     * 用业务对象WaterDeviceWorkOrderMaterielDTO初始化数据库对象WaterDeviceWorkOrderMateriel。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public WaterDeviceWorkOrderMateriel(WaterDeviceWorkOrderMaterielDTO dto) {
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
        this.materielId = dto.getMaterielId();
        this.materielName = dto.getMaterielName();
        this.materielTypeId = dto.getMaterielTypeId();
        this.materielTypeName = dto.getMaterielTypeName();
        this.materielBatchCode = dto.getMaterielBatchCode();
        this.materielIndex = dto.getMaterielIndex();
        this.scanCodeTime = dto.getScanCodeTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象WaterDeviceWorkOrderMaterielDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(WaterDeviceWorkOrderMaterielDTO dto) {
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
        dto.setMaterielId(this.materielId);
        dto.setMaterielName(this.materielName);
        dto.setMaterielTypeId(this.materielTypeId);
        dto.setMaterielTypeName(this.materielTypeName);
        dto.setMaterielBatchCode(this.materielBatchCode);
        dto.setMaterielIndex(this.materielIndex);
        dto.setScanCodeTime(this.scanCodeTime);
    }
}
