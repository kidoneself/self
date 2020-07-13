package com.yimao.cloud.water.po;

import com.yimao.cloud.pojo.dto.water.WaterDeviceFaultDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：水机设备故障记录
 *
 * @Author Zhang Bo
 * @Date 2019/2/23 15:19
 * @Version 1.0
 */
@Table(name = "water_device_fault")
@Getter
@Setter
public class WaterDeviceFault {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //设备ID
    private Integer deviceId;
    //SN码
    private String sn;
    //故障类型：1-余额不足；2-制水故障；3-TDS异常；4-滤芯报警；5-阀值提醒；6-续费超期；
    private Integer type;
    //故障状态：1-故障；2-故障已解除；
    private Integer state;
    //滤芯类型：PP、CTO、UDF、T33
    private String filterType;
    //故障描述
    private String fault;
    //同类故障发生的次数
    private Integer amount;
    //同类故障发生的最短时间间隔（单位：分钟）
    private Integer minTimeInterval;
    //同类故障发生的最长时间间隔（单位：分钟）
    private Integer maxTimeInterval;
    //创建时间（同类故障第一次发生时间）
    private Date createTime;
    //更新时间（同类故障最近一次发生时间）
    private Date updateTime;


    private String oldDeviceId;


    public WaterDeviceFault() {
    }

    /**
     * 用业务对象WaterDeviceFaultDTO初始化数据库对象WaterDeviceFault。
     *
     * @param dto 业务对象
     */
    public WaterDeviceFault(WaterDeviceFaultDTO dto) {
        this.id = dto.getId();
        this.deviceId = dto.getDeviceId();
        this.sn = dto.getSn();
        this.type = dto.getType();
        this.state = dto.getState();
        this.filterType = dto.getFilterType();
        this.fault = dto.getFault();
        this.amount = dto.getAmount();
        this.minTimeInterval = dto.getMinTimeInterval();
        this.maxTimeInterval = dto.getMaxTimeInterval();
        this.createTime = dto.getCreateTime();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象WaterDeviceFaultDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(WaterDeviceFaultDTO dto) {
        dto.setId(this.id);
        dto.setDeviceId(this.deviceId);
        dto.setSn(this.sn);
        dto.setType(this.type);
        dto.setState(this.state);
        dto.setFilterType(this.filterType);
        dto.setFault(this.fault);
        dto.setAmount(this.amount);
        dto.setMinTimeInterval(this.minTimeInterval);
        dto.setMaxTimeInterval(this.maxTimeInterval);
        dto.setCreateTime(this.createTime);
        dto.setUpdateTime(this.updateTime);
    }
}
