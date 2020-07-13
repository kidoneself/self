package com.yimao.cloud.station.po;

import java.util.Date;

import com.yimao.cloud.pojo.dto.station.StationRoleDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "station_role")
@Getter
@Setter
public class StationRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String roleName;

    private Integer stationCompanyId;

    private String discription;

    private Integer creator;

    private Date createTime;

    private Integer updater;

    private Date updateTime;


    public StationRole() {
    }

    /**
     * 用业务对象StationRoleDTO初始化数据库对象StationRole。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public StationRole(StationRoleDTO dto) {
        this.id = dto.getId();
        this.roleName = dto.getRoleName();
        this.stationCompanyId = dto.getStationCompanyId();
        this.discription = dto.getDiscription();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象StationRoleDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(StationRoleDTO dto) {
        dto.setId(this.id);
        dto.setRoleName(this.roleName);
        dto.setStationCompanyId(this.stationCompanyId);
        dto.setDiscription(this.discription);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
    }
}