package com.yimao.cloud.user.po;

import com.yimao.cloud.pojo.dto.user.PermissionDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Zhang Bo
 * @date 2018/11/2.
 */
@Table(name = "system_permission")
@Getter
@Setter
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String code;
    private String method;
    private Integer menuId;
    private Integer sysType;//所属系统：2-翼猫业务管理系统；3-净水设备互动广告系统；

    private String creator;
    private Date createTime;
    private String updater;
    private Date updateTime;

    public Permission() {
    }

    /**
     * 用业务对象PermissionDTO初始化数据库对象Permission。
     *
     * @param dto 业务对象
     */
    public Permission(PermissionDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.code = dto.getCode();
        this.method = dto.getMethod();
        this.menuId = dto.getMenuId();
        this.sysType = dto.getSysType();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象PermissionDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(PermissionDTO dto) {
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setCode(this.code);
        dto.setMethod(this.method);
        dto.setMenuId(this.menuId);
        dto.setSysType(this.sysType);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
    }
}
