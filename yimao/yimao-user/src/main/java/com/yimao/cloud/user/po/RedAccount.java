package com.yimao.cloud.user.po;

import com.yimao.cloud.pojo.dto.user.RedAccountDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;


@Table(name = "assembly_red_account")
@Getter
@Setter
public class RedAccount  {
    private static final long serialVersionUID = 8424210036922497445L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String oldId;

    private String yimaoOldSystemId;
    private String idStatus;
    private String isMasterAccount;
    private String userName;
    private String roleIds;
    private String roleNames;
    private Integer accountId;
    private String oldAccountId;
    private String accountFatherId;
    private int currentMoney;
    private int lockedMoney;
    private int totalMoney;
    private String allowCollectMoney;
    private String allowPayMoney;
    private String bindAlipayAccount;
    private String bindAlipayRealName;
    private String addrProvinceId;
    private String addrProvinceName;
    private String addrCityId;
    private String addrCityName;
    private String addrRegionId;
    private String addrRegionName;

    private Date createTime;
    private Date updateTime;
    private String createUser;
    private String updateUser;
    private String delStatus;
    private Date deleteTime;

  /*  public RedAccount() {
        this.isMasterAccount = StatusEnum.NO.value();
        this.currentMoney = 0;
        this.lockedMoney = 0;
        this.totalMoney = 0;
        this.allowCollectMoney = StatusEnum.FAILURE.value();
        this.allowPayMoney = StatusEnum.FAILURE.value();
    }*/

    public RedAccount() {
    }

    /**
     * 用业务对象RedAccountDTO初始化数据库对象RedAccount。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public RedAccount(RedAccountDTO dto) {
        this.id = dto.getId();
        this.oldId = dto.getOldId();
        this.createTime = dto.getCreateTime();
        this.updateTime = dto.getUpdateTime();
        this.createUser = dto.getCreateUser();
        this.updateUser = dto.getUpdateUser();
        this.delStatus = dto.getDelStatus();
        this.deleteTime = dto.getDeleteTime();
        this.yimaoOldSystemId = dto.getYimaoOldSystemId();
        this.idStatus = dto.getIdStatus();
        this.isMasterAccount = dto.getIsMasterAccount();
        this.userName = dto.getUserName();
        this.roleIds = dto.getRoleIds();
        this.roleNames = dto.getRoleNames();
        this.accountId = dto.getAccountId();
        this.accountFatherId = dto.getAccountFatherId();
        this.currentMoney = dto.getCurrentMoney();
        this.lockedMoney = dto.getLockedMoney();
        this.totalMoney = dto.getTotalMoney();
        this.allowCollectMoney = dto.getAllowCollectMoney();
        this.allowPayMoney = dto.getAllowPayMoney();
        this.bindAlipayAccount = dto.getBindAlipayAccount();
        this.bindAlipayRealName = dto.getBindAlipayRealName();
        this.addrProvinceId = dto.getAddrProvinceId();
        this.addrProvinceName = dto.getAddrProvinceName();
        this.addrCityId = dto.getAddrCityId();
        this.addrCityName = dto.getAddrCityName();
        this.addrRegionId = dto.getAddrRegionId();
        this.addrRegionName = dto.getAddrRegionName();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象RedAccountDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(RedAccountDTO dto) {
        dto.setId(this.id);
        dto.setOldId(this.oldId);
        dto.setCreateTime(this.createTime);
        dto.setUpdateTime(this.updateTime);
        dto.setCreateUser(this.createUser);
        dto.setUpdateUser(this.updateUser);
        dto.setDelStatus(this.delStatus);
        dto.setDeleteTime(this.deleteTime);
        dto.setYimaoOldSystemId(this.yimaoOldSystemId);
        dto.setIdStatus(this.idStatus);
        dto.setIsMasterAccount(this.isMasterAccount);
        dto.setUserName(this.userName);
        dto.setRoleIds(this.roleIds);
        dto.setRoleNames(this.roleNames);
        dto.setAccountId(this.accountId);
        dto.setAccountFatherId(this.accountFatherId);
        dto.setCurrentMoney(this.currentMoney);
        dto.setLockedMoney(this.lockedMoney);
        dto.setTotalMoney(this.totalMoney);
        dto.setAllowCollectMoney(this.allowCollectMoney);
        dto.setAllowPayMoney(this.allowPayMoney);
        dto.setBindAlipayAccount(this.bindAlipayAccount);
        dto.setBindAlipayRealName(this.bindAlipayRealName);
        dto.setAddrProvinceId(this.addrProvinceId);
        dto.setAddrProvinceName(this.addrProvinceName);
        dto.setAddrCityId(this.addrCityId);
        dto.setAddrCityName(this.addrCityName);
        dto.setAddrRegionId(this.addrRegionId);
        dto.setAddrRegionName(this.addrRegionName);
    }
}
