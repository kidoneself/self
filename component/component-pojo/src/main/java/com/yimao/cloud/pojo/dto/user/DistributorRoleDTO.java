package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Lizhqiang
 * @date 2018/12/17
 */
@ApiModel(description = "经销商角色配置DTO")
@Getter
@Setter
public class DistributorRoleDTO {

    @ApiModelProperty(value = "经销商角色配置ID")
    private Integer id;

    @ApiModelProperty(position = 1, value = "经销商角色名称")
    private String name;
    @ApiModelProperty(position = 2, value = "经销商角色等级：-50-折机版；50-体验版；350-微创版；650-个人版；950-企业版（主）；1000-企业版（子）")
    private Integer level;
    @ApiModelProperty(position = 3, value = "经销商价格")
    private BigDecimal price;
    @ApiModelProperty(position = 4, value = "经销商水机配额数量限制：0-不限制；1-限制；")
    private Boolean waterDeviceQuotaLimit;
    @ApiModelProperty(position = 5, value = "经销商水机配额数量")
    private Integer waterDeviceQuota;
    @ApiModelProperty(position = 6, value = "经销商水机配额数量阈值（最小值，到此值时提醒经销商续费）")
    private Integer waterDeviceQuotaThreshold;
    @ApiModelProperty(position = 7, value = "是否有子账户：0-没有；1-有；")
    private Boolean hasSubAccount;
    @ApiModelProperty(position = 8, value = "是否限制子账户数量：0-不限制；1-限制；")
    private Boolean subAccountAmountLimit;
    @ApiModelProperty(position = 9, value = "子账户数量")
    private Integer subAccountAmount;
    @ApiModelProperty(position = 10, value = "是否可以补差价升级：0-否；1-是；")
    private Boolean upgrade;
    @ApiModelProperty(position = 11, value = "补差价有效期限制（再此限制时间内才支持补差价）")
    private Integer upgradeLimitDays;
    @ApiModelProperty(position = 12, value = "是否限制续费：0-不限制；1-限制；")
    private Boolean renewLimit;
    @ApiModelProperty(position = 13, value = "可以续费次数")
    private Integer renewLimitTimes;

    @ApiModelProperty(position = 14, value = "招商收益分配规则ID")
    private Integer incomeRuleId;
    @ApiModelProperty(position = 15, value = "是否禁用：0-否；1-是；")
    private Boolean forbidden;

    @ApiModelProperty(position = 100, value = "创建人")
    private String creator;
    @ApiModelProperty(position = 101, value = "创建时间")
    private Date createTime;
    @ApiModelProperty(position = 102, value = "更新人")
    private String updater;
    @ApiModelProperty(position = 103, value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(position = 104, value = "补差价有效期：无或者自定义")
    private Boolean isLimitedDays;


}
