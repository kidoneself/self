package com.yimao.cloud.water.po;

import com.yimao.cloud.pojo.dto.water.AdslotDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDurationDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：水机亮灭屏时长
 *
 * @Author Zhang Bo
 * @Date 2019/1/29 17:41
 * @Version 1.0
 */
@Table(name = "water_device_duration")
@Data
public class WaterDeviceDuration {

    @Id
    private Integer id;
    private String deviceModel;//设备型号
    private Integer offDuration;//灭屏时长（单位：分钟）
    private Integer onDuration;//亮屏时长（单位：分钟）

    private String creator;//创建人
    private Date createTime;//创建时间
    private String updater;//更新人
    private Date updateTime;//更新时间


    public WaterDeviceDuration() {
    }

    /**
     * 用业务对象WaterDeviceDurationDTO初始化数据库对象WaterDeviceDuration。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public WaterDeviceDuration(WaterDeviceDurationDTO dto) {
        this.id = dto.getId();
        this.deviceModel = dto.getDeviceModel();
        this.offDuration = dto.getOffDuration();
        this.onDuration = dto.getOnDuration();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象WaterDeviceDurationDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(WaterDeviceDurationDTO dto) {
        dto.setId(this.id);
        dto.setDeviceModel(this.deviceModel);
        dto.setOffDuration(this.offDuration);
        dto.setOnDuration(this.onDuration);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
    }
}