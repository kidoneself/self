package com.yimao.cloud.water.po;

import com.yimao.cloud.pojo.dto.water.EffectStatisticsDTO;
import com.yimao.cloud.pojo.dto.water.EffectStatisticsForAppDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 描述：效果统计
 *
 */
@Table(name = "effect_statistics")
@Data
public class EffectStatistics {

    private Integer id;
    private String adslotId;//广告位ID
    private String materielName;//物料名称
    private String advertisers;//广告主
    private Integer screenLocation;//屏幕位置：1-大屏；2-小屏
    private Integer platform;//广告平台：0-翼猫；1-百度；2-京东；3-科大讯飞
    private Integer materielId;//物料ID
    private Integer advertisingId;//投放配置ID
    private String snCode;//设备编码
    private Integer deviceGroup;//设备组：1-用户组，2-服务站组
    private Integer clicks;//用户单日点击数
    private Integer playAmount;//广告单日播放次数
    private Date beginTime;//投放开始时间
    private Date endTime;//投放结束时间
    private Date playTime;//广告播放时间

    private Date createTime;//创建时间


    public EffectStatistics() {
    }

    /**
     * 用业务对象EffectStatisticsDTO初始化数据库对象EffectStatistics。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public EffectStatistics(EffectStatisticsDTO dto) {
        this.id = dto.getId();
        this.adslotId = dto.getAdslotId();
        this.materielName = dto.getMaterielName();
        this.advertisers = dto.getAdvertisers();
        this.screenLocation = dto.getScreenLocation();
        this.platform = dto.getPlatform();
        this.materielId = dto.getMaterielId();
        this.advertisingId = dto.getAdvertisingId();
        this.snCode = dto.getSnCode();
        this.deviceGroup = dto.getDeviceGroup();
        this.clicks = dto.getClicks();
        this.playAmount = dto.getPlayAmount();
        this.beginTime = dto.getBeginTime();
        this.endTime = dto.getEndTime();
        this.playTime = dto.getPlayTime();
        this.createTime = dto.getCreateTime();
    }


    public EffectStatistics(EffectStatisticsForAppDTO dto) {
        this.adslotId = dto.getAdslotId();
        this.materielName = dto.getMaterielName();
        this.advertisers = dto.getAdvertisers();
        this.screenLocation = dto.getScreenLocation();
        this.platform = dto.getPlatform();
        this.materielId = dto.getMaterielId();
        this.advertisingId = dto.getAdvertisingId();
        this.snCode = dto.getSnCode();
        this.beginTime = dto.getBeginTime();
        this.endTime = dto.getEndTime();
        this.deviceGroup=dto.getDeviceGroup();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象EffectStatisticsDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(EffectStatisticsDTO dto) {
        dto.setId(this.id);
        dto.setAdslotId(this.adslotId);
        dto.setMaterielName(this.materielName);
        dto.setAdvertisers(this.advertisers);
        dto.setScreenLocation(this.screenLocation);
        dto.setPlatform(this.platform);
        dto.setMaterielId(this.materielId);
        dto.setAdvertisingId(this.advertisingId);
        dto.setSnCode(this.snCode);
        dto.setDeviceGroup(this.deviceGroup);
        dto.setClicks(this.clicks);
        dto.setPlayAmount(this.playAmount);
        dto.setBeginTime(this.beginTime);
        dto.setEndTime(this.endTime);
        dto.setPlayTime(this.playTime);
        dto.setCreateTime(this.createTime);
    }
}