package com.yimao.cloud.water.po;

import com.yimao.cloud.pojo.dto.water.ConditionalAdvertisingDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：广告条件投放。
 *
 * @Author Zhang Bo
 * @Date 2019/1/29 17:41
 * @Version 1.0
 */
@Table(name = "conditional_advertising")
@Data
public class ConditionalAdvertising {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer platform;//广告平台：0-翼猫；1-百度；2-京东；3-科大讯飞
    private String ownAdslotId;//广告位ID
    private String adslotId;//第三方平台广告位ID，当为自有广告时，值为物料ID
    private String areas;//省市区：null-不限；1-多选，关联表记录
    private String models;//水机型号：null-不限；1-多选，关联表记录
    private String locationTab;//设备位置标签：null-不限
    private Integer connectionType;//网络连接类型：null-不限；1-WIFI；3-3G；
    private Integer afterConnectionType;//后续网络连接类型：null-不限；1-WIFI；3-3G；
    private Date effectiveBeginTime;//配置生效开始时间
    private Date effectiveEndTime;//配置生效结束时间
    private Boolean effective;//生效状态：0-未生效；1-已生效
    private Boolean forbidden;//下架状态：0-未下架；1-已下架
    private Boolean deleted;//删除状态：0-未删除；1-删除
    private Integer screenLocation;//1-大屏广告，2-小屏广告
    private Integer advertisingType;//1-条件投放，2-精准投放
    private Integer deviceCount;//投放设备数量
    private Integer deviceGroup;//设备组：null全部，1-用户组，2服务站组

    private String creator;//创建人
    private Date createTime;//创建时间
    private String updater;//更新人
    private Date updateTime;//更新时间


    public ConditionalAdvertising() {
    }

    /**
     * 用业务对象ConditionalAdvertisingDTO初始化数据库对象ConditionalAdvertising。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public ConditionalAdvertising(ConditionalAdvertisingDTO dto) {
        this.id = dto.getId();
        this.platform = dto.getPlatform();
        this.ownAdslotId = dto.getOwnAdslotId();
        this.adslotId = dto.getAdslotId();
        this.areas = dto.getAreas();
        this.models = dto.getModels();
        this.locationTab = dto.getLocationTab();
        this.connectionType = dto.getConnectionType();
        this.afterConnectionType = dto.getAfterConnectionType();
        this.effectiveBeginTime = dto.getEffectiveBeginTime();
        this.effectiveEndTime = dto.getEffectiveEndTime();
        this.effective = dto.getEffective();
        this.forbidden = dto.getForbidden();
        this.deleted = dto.getDeleted();
        this.screenLocation = dto.getScreenLocation();
        this.advertisingType = dto.getAdvertisingType();
        this.deviceCount = dto.getDeviceCount();
        this.deviceGroup = dto.getDeviceGroup();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象ConditionalAdvertisingDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(ConditionalAdvertisingDTO dto) {
        dto.setId(this.id);
        dto.setPlatform(this.platform);
        dto.setOwnAdslotId(this.ownAdslotId);
        dto.setAdslotId(this.adslotId);
        dto.setAreas(this.areas);
        dto.setModels(this.models);
        dto.setLocationTab(this.locationTab);
        dto.setConnectionType(this.connectionType);
        dto.setAfterConnectionType(this.afterConnectionType);
        dto.setEffectiveBeginTime(this.effectiveBeginTime);
        dto.setEffectiveEndTime(this.effectiveEndTime);
        dto.setEffective(this.effective);
        dto.setForbidden(this.forbidden);
        dto.setDeleted(this.deleted);
        dto.setScreenLocation(this.screenLocation);
        dto.setAdvertisingType(this.advertisingType);
        dto.setDeviceCount(this.deviceCount);
        dto.setDeviceGroup(this.deviceGroup);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
    }
}
