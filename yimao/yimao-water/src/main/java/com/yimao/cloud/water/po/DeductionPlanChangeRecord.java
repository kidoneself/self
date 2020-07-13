package com.yimao.cloud.water.po;

import com.yimao.cloud.pojo.dto.water.DeductionPlanChangeRecordDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：水机设备扣费计划修改记录
 *
 * @Author Zhang Bo
 * @Date 2019/12/04
 */
@Table(name = "water_device_deduction_plan_change_record")
@Getter
@Setter
public class DeductionPlanChangeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //设备ID
    private Integer deviceId;
    //扣费计划ID
    private Integer planId;
    //操作内容
    private String operation;
    //创建人
    private String creator;
    //创建时间
    private Date createTime;

    public DeductionPlanChangeRecord() {
    }

    /**
     * 用业务对象DeductionPlanChangeRecordDTO初始化数据库对象DeductionPlanChangeRecord。
     *
     * @param dto 业务对象
     */
    public DeductionPlanChangeRecord(DeductionPlanChangeRecordDTO dto) {
        this.id = dto.getId();
        this.deviceId = dto.getDeviceId();
        this.operation = dto.getOperation();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象DeductionPlanChangeRecordDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(DeductionPlanChangeRecordDTO dto) {
        dto.setId(this.id);
        dto.setDeviceId(this.deviceId);
        dto.setOperation(this.operation);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
    }
}
