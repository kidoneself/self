package com.yimao.cloud.user.po;

import com.yimao.cloud.pojo.dto.user.DistributorRoleDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 经销商角色配置
 *
 * @author Lizhqiang
 * @date 2018/12/17
 */
@Table(name = "distributor_role")
@Getter
@Setter
public class DistributorRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;                        //经销商角色名称
    private Integer level;                      //经销商角色等级：-50-折机版；50-体验版；350-微创版；650-个人版；950-企业版（主）；1000-企业版（子）
    private BigDecimal price;                   //经销商价格
    private Boolean waterDeviceQuotaLimit;      //经销商水机配额数量限制：0-不限制；1-限制；
    private Integer waterDeviceQuota;           //经销商水机配额数量
    private Integer waterDeviceQuotaThreshold;  //经销商水机配额数量阈值（最小值，到此值时提醒经销商续费）
    private Boolean hasSubAccount;              //是否有子账户：0-没有；1-有；
    private Boolean subAccountAmountLimit;      //是否限制子账户数量：0-不限制；1-限制；
    private Integer subAccountAmount;           //子账户数量
    private Boolean upgrade;                    //是否可以补差价升级：0-否；1-是；
    private Integer upgradeLimitDays;           //补差价有效期限制（单位：天。再此限制时间内才支持补差价）
    private Boolean renewLimit;                 //是否限制续费：0-不限制；1-限制；
    private Integer renewLimitTimes;            //限制的续费次数

    private Integer incomeRuleId;               //招商收益分配规则ID
    private Boolean forbidden;                  //是否禁用：0-否；1-是；

    private String creator;
    private Date createTime;
    private String updater;
    private Date updateTime;

    public DistributorRole() {
    }

    /**
     * 用业务对象DistributorConfigurationDTO初始化数据库对象DistributorConfiguration。
     *
     * @param dto 业务对象
     */
    public DistributorRole(DistributorRoleDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.level = dto.getLevel();
        this.price = dto.getPrice();
        this.waterDeviceQuotaLimit = dto.getWaterDeviceQuotaLimit();
        this.waterDeviceQuota = dto.getWaterDeviceQuota();
        this.waterDeviceQuotaThreshold = dto.getWaterDeviceQuotaThreshold();
        this.hasSubAccount = dto.getHasSubAccount();
        this.subAccountAmountLimit = dto.getSubAccountAmountLimit();
        this.subAccountAmount = dto.getSubAccountAmount();
        this.upgrade = dto.getUpgrade();
        this.upgradeLimitDays = dto.getUpgradeLimitDays();
        this.renewLimit = dto.getRenewLimit();
        this.renewLimitTimes = dto.getRenewLimitTimes();
        this.incomeRuleId = dto.getIncomeRuleId();
        this.forbidden = dto.getForbidden();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象DistributorConfigurationDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(DistributorRoleDTO dto) {
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setLevel(this.level);
        dto.setPrice(this.price);
        dto.setWaterDeviceQuotaLimit(this.waterDeviceQuotaLimit);
        dto.setWaterDeviceQuota(this.waterDeviceQuota);
        dto.setWaterDeviceQuotaThreshold(this.waterDeviceQuotaThreshold);
        dto.setHasSubAccount(this.hasSubAccount);
        dto.setSubAccountAmountLimit(this.subAccountAmountLimit);
        dto.setSubAccountAmount(this.subAccountAmount);
        dto.setUpgrade(this.upgrade);
        dto.setUpgradeLimitDays(this.upgradeLimitDays);
        dto.setRenewLimit(this.renewLimit);
        dto.setRenewLimitTimes(this.renewLimitTimes);
        dto.setIncomeRuleId(this.incomeRuleId);
        dto.setForbidden(this.forbidden);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
    }
}
