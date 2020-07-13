package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@ApiModel(description = "经销商导出DTO")
@Getter
@Setter
public class DistributorExportDTO {

    @ApiModelProperty(value = "经销商ID")
    public Integer id;
    @ApiModelProperty(value = "e家号")
    private Integer userId;
    @ApiModelProperty(value = "经销商账号")
    private String userName;
    @ApiModelProperty(value = "经销商姓名")
    private String realName;
    @ApiModelProperty(value = "性别：1-男；2-女；")
    private String sex;
    @ApiModelProperty(value = "经销商昵称")
    private String nickName;
    @ApiModelProperty(value = "身份证")
    private String idCard;
    @ApiModelProperty(value = "经销商手机号")
    private String phone;
    @ApiModelProperty(value = "经销代理身份：1-代理商；2-经销商；3-经销商+代理商；")
    private String type;
    @ApiModelProperty(value = "代理商级别：1-省代；2-市代；4-区代；3-省代+市代；5-省代+区代；6-市代+区代；7-省代+市代+区代；")
    private String agentLevel;
    @ApiModelProperty(value = "经销商角色名称")
    private String roleName;
    @ApiModelProperty(value = "经销商等级")
    private Integer roleLevel;
    @ApiModelProperty(value = "省")
    private String province;
    @ApiModelProperty(value = "市")
    private String city;
    @ApiModelProperty(value = "区")
    private String region;
    @ApiModelProperty(value = "经销商企业名称")
    private String companyName;
    @ApiModelProperty(value = "子账号个数")
    private Integer count;
    @ApiModelProperty(value = "是否为站长0-否 1-是默认0")
    private String stationMaster;
    @ApiModelProperty(value = "是否为发起人：0-不是；1-是")
    private String sponsor;
    @ApiModelProperty(value = "是否为是主账号")
    private String mainAccount;
    @ApiModelProperty(value = "上线端： 1-翼猫业务系统；2-经销商app;")
    private String terminal;
    @ApiModelProperty(value = "来源方式：1-翼猫企业二维码;2-翼猫后台上线; 3:经销商app创建账号; 4-资讯分享 ; 5-视频分享; 6-权益卡分享 ; 7-发展经销商二维码；")
    private String sourceType;
    @ApiModelProperty(value = "配额")
    private BigDecimal quota;
    @ApiModelProperty(value = "剩余配额")
    private BigDecimal remainingQuota;
    @ApiModelProperty(value = "置换金额")
    private BigDecimal replacementAmount;
    @ApiModelProperty(value = "剩余置换金额")
    private BigDecimal remainingReplacementAmount;
    @ApiModelProperty(value = "支付金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "智慧助理")
    private String userAssistant;
    @ApiModelProperty(value = "智慧助理省市区")
    private String userAssistantArea;

    @ApiModelProperty(value = "推荐人姓名")
    private String recommendName;
    @ApiModelProperty(value = "推荐人账号")
    private String recommendAccount;
    @ApiModelProperty(value = "推荐人身份证号")
    private String recommendIdCard;
    @ApiModelProperty( value = "推荐人省市区")
    private String recommendArea;
    @ApiModelProperty( value = "推荐人省")
    private String recommendProvince;
    @ApiModelProperty(value = "推荐人")
    private String recommendCity;
    @ApiModelProperty(value = "推荐人")
    private String recommendRegion;

    @ApiModelProperty(value = "推荐人智慧助理")
    private String recommendAssistant;
    @ApiModelProperty(value = "推荐人智慧助理")
    private String recommendAssistantArea;

    @ApiModelProperty(value = "是否禁用：0-否，1-是")
    private String forbidden;
    @ApiModelProperty(value = "是否禁止下单：0-否，1-是")
    private String forbiddenOrder;
    @ApiModelProperty( value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "免费体验首次升级时间")
    private String firstUpdateTime;
    @ApiModelProperty(value = "升级支付时间(微创)")
    private String payTimeforMin;
    @ApiModelProperty(value = "升级支付时间(个人)")
    private String payTimeforPerson;
    @ApiModelProperty(value = "升级支付时间(企业)")
    private String payTimeforEnterprise;
    @ApiModelProperty(value = "升级支付金额(微创)")
    private BigDecimal payAmountforMin;
    @ApiModelProperty(value = "升级支付金额(个人)")
    private BigDecimal payAmountforPerson;
    @ApiModelProperty(value = "升级支付金额(企业)")
    private BigDecimal payAmountforEnterprise;
    @ApiModelProperty(value = "续费次数")
    private Integer renewalCount;

    @ApiModelProperty(value = "是否溢价")
    private Integer premium;

    @ApiModelProperty(value = "省代时间")
    private String provinceTime;
    @ApiModelProperty(value = "市代时间")
    private String cityTime;
    @ApiModelProperty(value = "区代时间")
    private String regionTime;
    @ApiModelProperty(value = "是否是省发起人")
    private String isProvinceSponsor;
    @ApiModelProperty(value = "是否是市发起人")
    private String isCitySponsor;
    @ApiModelProperty(value = "是否是区发起人")
    private String isRegionSponsor;
    @ApiModelProperty(value = "主账号姓名")
    private String mainName;
    @ApiModelProperty(value = "主账号")
    private String mainUserName;
    @ApiModelProperty(value = "成为该经销商身份时间")
    private String completeTime;

    private List<DistributorOrderDTO> orderList;

}
