package com.yimao.cloud.user.po;

import com.yimao.cloud.pojo.dto.user.RoleDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 翼猫业务系统管理员角色表
 *
 * @author Zhang Bo
 * @date 2018/10/30.
 */
@Table(name = "system_role")
@Getter
@Setter
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;//角色名称
    private Integer sysType;//所属系统：2-翼猫业务管理系统；3-净水设备互动广告系统；

    private String creator;
    private Date createTime;
    private String updater;
    private Date updateTime;

    public Role() {
    }

    /**
     * 用业务对象RoleDTO初始化数据库对象Role。
     *
     * @param dto 业务对象
     */
    public Role(RoleDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.sysType = dto.getSysType();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象RoleDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(RoleDTO dto) {
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setSysType(this.sysType);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
    }
}
