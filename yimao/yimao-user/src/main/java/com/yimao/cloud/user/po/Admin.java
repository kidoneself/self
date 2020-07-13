package com.yimao.cloud.user.po;

import com.yimao.cloud.pojo.dto.user.AdminDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 翼猫业务系统管理员表
 *
 * @author Zhang Bo
 * @date 2018/10/30.
 */
@Table(name = "system_admin")
@Getter
@Setter
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;//管理员ID
    private String userName;//用户名
    private String password;//密码
    private String realName;//姓名
    private Integer sex;//性别：1-男，2-女
    private String phone;//电话
    private String email;//邮箱
    private Integer stationId;//服务站id
    private Integer deptId;//部门ID
    private String deptName;//部门名称
    private String remark;//备注
    private Boolean forbidden;//是否禁用：1-是，0-否
    private Integer sysType;//所属系统：2-翼猫业务管理系统；3-净水设备互动广告系统；
    private String creator;//创建人
    private Date createTime;//创建时间
    private String updater;//更新人
    private Date updateTime;//更新时间

    public Admin() {
    }

    /**
     * 用业务对象AdminDTO初始化数据库对象Admin。
     *
     * @param dto 业务对象
     */
    public Admin(AdminDTO dto) {
        this.id = dto.getId();
        this.userName = dto.getUserName();
        this.password = dto.getPassword();
        this.realName = dto.getRealName();
        this.sex = dto.getSex();
        this.phone = dto.getPhone();
        this.email = dto.getEmail();
        this.stationId = dto.getStationId();
        this.deptId = dto.getDeptId();
        this.deptName = dto.getDeptName();
        this.remark = dto.getRemark();
        this.forbidden = dto.getForbidden();
        this.sysType = dto.getSysType();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象AdminDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(AdminDTO dto) {
        dto.setId(this.id);
        dto.setUserName(this.userName);
        dto.setPassword(this.password);
        dto.setRealName(this.realName);
        dto.setSex(this.sex);
        dto.setPhone(this.phone);
        dto.setEmail(this.email);
        dto.setStationId(this.stationId);
        dto.setDeptId(this.deptId);
        dto.setDeptName(this.deptName);
        dto.setRemark(this.remark);
        dto.setForbidden(this.forbidden);
        dto.setSysType(this.sysType);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
    }
}
