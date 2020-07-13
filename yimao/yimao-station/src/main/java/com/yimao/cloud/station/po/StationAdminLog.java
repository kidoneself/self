package com.yimao.cloud.station.po;

import com.yimao.cloud.pojo.dto.station.StationAdminLogDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 服务站站务系统管理员操作记录表
 *
 * @author Liu long jie
 * @date 2019/12/31.
 */
@Table(name = "station_admin_log")
@Data
public class StationAdminLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer adminId; //管理员id
    private String userName;
    private String realName;
    private Integer roleId; //角色类型id
    private Integer osType;//操作类型：1-登陆
    private String ip;//登录ip
    private Date time; //操作时间


    public StationAdminLog() {
    }

    /**
     * 用业务对象StationAdminLogDTO初始化数据库对象StationAdminLog。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public StationAdminLog(StationAdminLogDTO dto) {
        this.id = dto.getId();
        this.adminId = dto.getAdminId();
        this.userName = dto.getUserName();
        this.realName = dto.getRealName();
        this.roleId = dto.getRoleId();
        this.osType = dto.getOsType();
        this.ip = dto.getIp();
        this.time = dto.getTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象StationAdminLogDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(StationAdminLogDTO dto) {
        dto.setId(this.id);
        dto.setAdminId(this.adminId);
        dto.setUserName(this.userName);
        dto.setRealName(this.realName);
        dto.setRoleId(this.roleId);
        dto.setOsType(this.osType);
        dto.setIp(this.ip);
        dto.setTime(this.time);
    }
}
