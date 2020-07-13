package com.yimao.cloud.water.po;

import com.yimao.cloud.pojo.dto.water.WaterDeviceConsumableDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：水机耗材
 *
 * @Author Zhang Bo
 * @Date 2019/2/23 11:20
 * @Version 1.0
 */
@Table(name = "water_device_consumable")
@Getter
@Setter
public class WaterDeviceConsumable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //耗材名称
    private String name;
    //private Integer consumableTypeId;
    //耗材类型：1-滤芯 2-滤网
    private Integer type;
    //水机设备型号：1601T、1602T、1603T、1601L
    private String deviceModel;
    //滤芯可使用时长（单位：天），超过此值需更换
    private Integer time;
    //滤芯可使用流量（单位：升），超过此值需更换
    private Integer flow;

    private String creator;
    private Date createTime;
    private String updater;
    private Date updateTime;
    private String oldId;

    public WaterDeviceConsumable() {
    }

    /**
     * 用业务对象WaterDeviceConsumableDTO初始化数据库对象WaterDeviceConsumable。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public WaterDeviceConsumable(WaterDeviceConsumableDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.type = dto.getType();
        this.deviceModel = dto.getDeviceModel();
        this.time = dto.getTime();
        this.flow = dto.getFlow();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
        this.oldId = dto.getOldId();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象WaterDeviceConsumableDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(WaterDeviceConsumableDTO dto) {
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setType(this.type);
        dto.setDeviceModel(this.deviceModel);
        dto.setTime(this.time);
        dto.setFlow(this.flow);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
        dto.setOldId(this.oldId);
    }
}