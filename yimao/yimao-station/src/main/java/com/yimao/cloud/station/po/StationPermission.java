package com.yimao.cloud.station.po;

import java.util.Date;

import com.yimao.cloud.pojo.dto.station.StationPermissionDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "station_permission")
@Data
public class StationPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String method;

    private String url;

    private Integer menuId;

    private Integer creator;

    private Date createTime;

    private Integer updater;

    private Date updateTime;

    private Integer type;
    
    public StationPermission() {
    }

    /**
     * 用业务对象StationPermissionDTO初始化数据库对象StationPermission。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public StationPermission(StationPermissionDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.method = dto.getMethod();
        this.url = dto.getUrl();
        this.menuId = dto.getMenuId();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
        this.type = dto.getType();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象StationPermissionDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(StationPermissionDTO dto) {
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setMethod(this.method);
        dto.setUrl(this.url);
        dto.setMenuId(this.menuId);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
        dto.setType(this.type);
    }
}