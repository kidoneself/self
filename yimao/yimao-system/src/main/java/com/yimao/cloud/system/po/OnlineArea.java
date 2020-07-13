package com.yimao.cloud.system.po;

import com.yimao.cloud.pojo.dto.system.OnlineAreaDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：上线地区
 *
 * @author Zhang Bo
 * @date 2019/3/9.
 */
@Table(name = "online_area")
@Getter
@Setter
public class OnlineArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * 省
     */
    private String province;
    /**
     * 市
     */
    private String city;
    /**
     * 区
     */
    private String region;
    /**
     * 区域等级：1-省；2-省市；3-省市区
     */
    private Integer level;
    /**
     * 删除状态:0-否 1-是
     */
    private Boolean deleted;

    /**
     * 上线状态:0-否 1-是
     */
    private Integer status;
    /**
     * 同步工单到售后系统状态:N-未同步；Y-已同步；FAILURE-同步失败
     */
    private String syncState;
    private String syncStateText;//同步状态描述 Y-已同步,N-未同步,FAILURE-同步失败

    private String creator;
    private Date createTime;
    private String updater;
    private Date updateTime;

    public OnlineArea() {
    }

    /**
     * 用业务对象OnlineAreaDTO初始化数据库对象OnlineArea。
     *
     * @param dto 业务对象
     */
    public OnlineArea(OnlineAreaDTO dto) {
        this.id = dto.getId();
        this.province = dto.getProvince();
        this.city = dto.getCity();
        this.region = dto.getRegion();
        this.level = dto.getLevel();
        this.syncState = dto.getSyncState();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象OnlineAreaDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(OnlineAreaDTO dto) {
        dto.setId(this.id);
        dto.setProvince(this.province);
        dto.setCity(this.city);
        dto.setRegion(this.region);
        dto.setLevel(this.level);
        dto.setSyncState(this.syncState);
    }
}
