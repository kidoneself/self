package com.yimao.cloud.water.po;


import com.yimao.cloud.base.enums.StatusEnum;
import com.yimao.cloud.base.enums.WorkOrderTypeEnum;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFailurePhenomenonDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;


@Table(name = "waterdevice_failure_phenomenon")
@Data
public class WaterDeviceFailurePhenomenon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String partsId;
    private String partsName;
    private String faultTypeId;//故障类型id
    private String faultTypeName;//故障类型名称
    private String workCode;//工单号
    private String workOrderIndex;
    private Date createTime;
    private Date updateTime;
    private String createUser;
    private String updateUser;


    private String delStatus;
    private Date deleteTime;
    private String idStatus;

    public WaterDeviceFailurePhenomenon() {
        this.workOrderIndex = WorkOrderTypeEnum.ORDER_TYPE_REPAIR.getType();

        this.delStatus = StatusEnum.FALSE.value();
        this.idStatus = StatusEnum.YES.value();
    }

    /**
     * 用业务对象WaterDeviceFailurePhenomenonDTO初始化数据库对象WaterDeviceFailurePhenomenon。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public WaterDeviceFailurePhenomenon(WaterDeviceFailurePhenomenonDTO dto) {
        this.id = dto.getId();
        this.partsId = dto.getPartsId();
        this.partsName = dto.getPartsName();
        this.faultTypeId = dto.getFaultTypeId();
        this.faultTypeName = dto.getFaultTypeName();
        this.workCode = dto.getWorkCode();
        this.workOrderIndex = dto.getWorkOrderIndex();
        this.createTime = dto.getCreateTime();
        this.updateTime = dto.getUpdateTime();
        this.createUser = dto.getCreateUser();
        this.updateUser = dto.getUpdateUser();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象WaterDeviceFailurePhenomenonDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(WaterDeviceFailurePhenomenonDTO dto) {
        dto.setId(this.id);
        dto.setPartsId(this.partsId);
        dto.setPartsName(this.partsName);
        dto.setFaultTypeId(this.faultTypeId);
        dto.setFaultTypeName(this.faultTypeName);
        dto.setWorkCode(this.workCode);
        dto.setWorkOrderIndex(this.workOrderIndex);
        dto.setCreateTime(this.createTime);
        dto.setUpdateTime(this.updateTime);
        dto.setCreateUser(this.createUser);
        dto.setUpdateUser(this.updateUser);
    }
}
