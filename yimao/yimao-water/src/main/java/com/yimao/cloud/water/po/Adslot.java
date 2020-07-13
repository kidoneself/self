package com.yimao.cloud.water.po;

import com.yimao.cloud.pojo.dto.water.AdslotDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：平台广告位
 *
 * @Author Zhang Bo
 * @Date 2019/1/29 17:41
 * @Version 1.0
 */
@Table(name = "ad_adslot")
@Data
public class Adslot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String adslotId;//广告位ID
    private String name;//广告位名称
    private Integer position;//屏幕位置：1-大屏；2-小屏
    private Integer duration;//广告时长（单位：秒）
    private Boolean forbidden;//禁用状态：0-启用；1-禁用
    private Boolean deleted;//删除状态：0-未删除；1-删除

    private String creator;//创建人
    private Date createTime;//创建时间
    private String updater;//更新人
    private Date updateTime;//更新时间


    public Adslot() {
    }

    /**
     * 用业务对象AdslotDTO初始化数据库对象Adslot。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public Adslot(AdslotDTO dto) {
        this.id = dto.getId();
        this.adslotId = dto.getAdslotId();
        this.name = dto.getName();
        this.position = dto.getPosition();
        this.duration = dto.getDuration();
        this.forbidden = dto.getForbidden();
        this.deleted = dto.getDeleted();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象AdslotDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(AdslotDTO dto) {
        dto.setId(this.id);
        dto.setAdslotId(this.adslotId);
        dto.setName(this.name);
        dto.setPosition(this.position);
        dto.setDuration(this.duration);
        dto.setForbidden(this.forbidden);
        dto.setDeleted(this.deleted);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
    }
}