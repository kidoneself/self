package com.yimao.cloud.user.po;

import com.yimao.cloud.pojo.dto.user.DepartmentDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Zhang Bo
 * @date 2018/10/30.
 */
@Table(name = "system_department")
@Data
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;//部门名称
    private String remark;//部门描述
    private Integer sysType;//所属系统：2-翼猫业务管理系统；3-净水设备互动广告系统；

    private String creator;
    private Date createTime;
    private String updater;
    private Date updateTime;


    public Department() {
    }

    /**
     * 用业务对象DepartmentDTO初始化数据库对象Department。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public Department(DepartmentDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.remark = dto.getRemark();
        this.sysType = dto.getSysType();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象DepartmentDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(DepartmentDTO dto) {
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setRemark(this.remark);
        dto.setSysType(this.sysType);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
    }
}
