package com.yimao.cloud.water.po;

import com.yimao.cloud.pojo.dto.water.WaterDeviceFilterChangeRecordDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：水机滤芯更换记录
 *
 * @Author Zhang Bo
 * @Date 2019/4/23
 */
@Table(name = "water_device_filter_change_record")
@Getter
@Setter
public class WaterDeviceFilterChangeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer deviceId;
    private String sn;
    private String filterName;
    private String province;
    private String city;
    private String region;
    private String address;
    private Date activatingTime;
    private Date createTime;
    //维护工单id
    private String maintenanceWorkOrderId;
    //来源：1-安装工提交 2-客户提交 3-自动生成
    private Integer source;
    //生效状态：0-否；1-是
    private Integer effective;

    public WaterDeviceFilterChangeRecord() {
    }

    /**
     * 用业务对象WaterDeviceFilterChangeRecordDTO初始化数据库对象WaterDeviceFilterChangeRecord。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public WaterDeviceFilterChangeRecord(WaterDeviceFilterChangeRecordDTO dto) {
        this.id = dto.getId();
        this.deviceId = dto.getDeviceId();
        this.sn = dto.getSn();
        this.filterName = dto.getFilterName();
        this.province = dto.getProvince();
        this.city = dto.getCity();
        this.region = dto.getRegion();
        this.address = dto.getAddress();
        this.activatingTime = dto.getActivatingTime();
        this.createTime = dto.getCreateTime();
        this.maintenanceWorkOrderId = dto.getMaintenanceWorkOrderId();
        this.source = dto.getSource();
        this.effective = dto.getEffective();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象WaterDeviceFilterChangeRecordDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(WaterDeviceFilterChangeRecordDTO dto) {
        dto.setId(this.id);
        dto.setSn(this.sn);
        dto.setDeviceId(this.deviceId);
        dto.setFilterName(this.filterName);
        dto.setProvince(this.province);
        dto.setCity(this.city);
        dto.setRegion(this.region);
        dto.setAddress(this.address);
        dto.setActivatingTime(this.activatingTime);
        dto.setCreateTime(this.createTime);
        dto.setMaintenanceWorkOrderId(this.maintenanceWorkOrderId);
        dto.setSource(this.source);
        dto.setEffective(this.effective);
    }
}
