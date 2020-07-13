package com.yimao.cloud.user.po;

import com.yimao.cloud.pojo.dto.user.AdminLogDTO;
import lombok.Data;

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
@Table(name = "system_admin_log")
@Data
public class AdminLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String userName;
    private String realName;
    private Integer type;//管理员类型：1-系统管理员，2-服务站管理员
    private String ip;//登录ip
    private Boolean isSuccess;//0-登录失败；1-登录成功
    private String cause;//登录失败原因
    private Date time;

    public AdminLog() {
    }

    /**
     * 用业务对象AdminLogDTO初始化数据库对象AdminLog。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public AdminLog(AdminLogDTO dto) {
        this.id = dto.getId();
        this.userName = dto.getUserName();
        this.realName = dto.getRealName();
        this.type = dto.getType();
        this.ip = dto.getIp();
        this.isSuccess = dto.getIsSuccess();
        this.cause = dto.getCause();
        this.time = dto.getTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象AdminLogDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(AdminLogDTO dto) {
        dto.setId(this.id);
        dto.setUserName(this.userName);
        dto.setRealName(this.realName);
        dto.setType(this.type);
        dto.setIp(this.ip);
        dto.setIsSuccess(this.isSuccess);
        dto.setCause(this.cause);
        dto.setTime(this.time);
    }
}
