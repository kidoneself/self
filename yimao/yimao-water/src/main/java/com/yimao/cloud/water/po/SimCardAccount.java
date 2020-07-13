package com.yimao.cloud.water.po;

import com.yimao.cloud.pojo.dto.water.SimCardAccountDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：SIM运营商分配的权限账号
 *
 * @Author Zhang Bo
 * @Date 2019/4/25
 */
@Table(name = "sim_card_account")
@Getter
@Setter
public class SimCardAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //账号
    private String username;
    //密码
    private String password;
    //权限密钥
    private String licenseKey;
    //运营商公司名称
    private String companyName;

    private String creator;
    private Date createTime;
    private String updater;
    private Date updateTime;

    public SimCardAccount() {
    }

    /**
     * 用业务对象SimCardAccountDTO初始化数据库对象SimCardAccount。
     *
     * @param dto 业务对象
     */
    public SimCardAccount(SimCardAccountDTO dto) {
        this.id = dto.getId();
        this.username = dto.getUsername();
        this.password = dto.getPassword();
        this.licenseKey = dto.getLicenseKey();
        this.companyName = dto.getCompanyName();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象SimCardAccountDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(SimCardAccountDTO dto) {
        dto.setId(this.id);
        dto.setUsername(this.username);
        dto.setPassword(this.password);
        dto.setLicenseKey(this.licenseKey);
        dto.setCompanyName(this.companyName);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
    }
}
