package com.yimao.cloud.pojo.vo.station;

import com.yimao.cloud.pojo.dto.user.UserCompanyDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Liu Long Jie
 * @date 2020-1-16
 */
@ApiModel(description = "经销商代理商列表VO（站务系统）")
@Getter
@Setter
@ToString
public class DistributorVO implements Serializable {

    private static final long serialVersionUID = 821614012348447917L;

    // -----------------------  查询列表需要字段 -------------------------------------
    @ApiModelProperty(value = "经销商ID")
    public Integer id;
    @ApiModelProperty(value = "经销商账号")
    private String userName;
    @ApiModelProperty(value = "经销商姓名")
    private String realName;
    @ApiModelProperty(value = "经销商手机号")
    private String phone;
    @ApiModelProperty(value = "经销代理身份： 1-代理商；2-经销商；3-经销商+代理商；")
    private Integer type;
    @ApiModelProperty(value = "代理商级别：1-省代；2-市代；4-区代；3-省代+市代；5-省代+区代；6-市代+区代；7-省代+市代+区代；")
    private Integer agentLevel;
    @ApiModelProperty(value = "经销商类型")
    private String roleName;
    @ApiModelProperty(value = "推荐人账号")
    private String recommendAccount;
    @ApiModelProperty(value = "上线端： 1-翼猫业务系统；2-经销商app;")
    private Integer terminal;
    @ApiModelProperty(value = "是否为发起人：0-不是；1-是")
    private Boolean sponsor;
    @ApiModelProperty(value = "剩余配额")
    private Integer remainingQuota;
    @ApiModelProperty(value = "创建时间", example = "2018-12-28 11:00:00")
    private Date createTime;
    @ApiModelProperty(value = "是否禁用：0-否，1-是")
    private String forbiddenStatus;

    // ---- 子账号查询字段 -----
    @ApiModelProperty(value = "e家号")
    private Integer userId;
    @ApiModelProperty(value = "主账号姓名")
    private String mainAccountName;
    @ApiModelProperty(value = "主账号")
    private String mainAccount;
    @ApiModelProperty(value = "经销商企业名称")
    private String companyName;
    @ApiModelProperty(value = "省")
    private String province;
    @ApiModelProperty(value = "市")
    private String city;
    @ApiModelProperty(value = "区")
    private String region;

    // -----------------------  详情需要字段（部分字段在上面） -------------------------------------
    @ApiModelProperty(value = "是否是福慧顺")
    private Boolean fuhuishun;
    @ApiModelProperty(value = "邮箱")
    private String email;
    @ApiModelProperty(value = "性别：1-男；2-女；")
    private Integer sex;
    @ApiModelProperty(value = "身份证")
    private String idCard;
    @ApiModelProperty(value = "是否为站长0-否 1-是默认0")
    private Boolean stationMaster;
    @ApiModelProperty(value = "地址")
    private String address;
    @ApiModelProperty(value = "经销商昵称")
    private String nickName;
    @ApiModelProperty(value = "成为该经销商身份时间", example = "2018-12-28 11:00:00")
    private Date completeTime;
    @ApiModelProperty(value = "总配额")
    private Integer quota;
    @ApiModelProperty(value = "已售水机数量")
    private Integer soldWaterDeviceCount;
    @ApiModelProperty(value = "分享用户数量")
    private Integer sharerUserCount;
    @ApiModelProperty(value = "分销用户数量")
    private Integer saleUserCount;
    @ApiModelProperty(value = "主账号的子账号数量")
    private Integer childAccountCount;
    @ApiModelProperty(value = "支付金额是否为0:0-否；1-是")
    private Boolean payIsZero;
    @ApiModelProperty(value = "成为省代时间", example = "2018-12-28 11:00:00")
    private Date provincialTime;
    @ApiModelProperty(value = "成为市代时间", example = "2018-12-28 11:00:00")
    private Date cityTime;
    @ApiModelProperty(value = "成为区代时间", example = "2018-12-28 11:00:00")
    private Date districtTime;
    @ApiModelProperty(value = "省代排名")
    private Integer provinceRanking;
    @ApiModelProperty(value = "市代排名")
    private Integer cityRanking;
    @ApiModelProperty(value = "区代排名")
    private Integer regionRanking;
    @ApiModelProperty(value = "区代发展的经销商数量")
    private Integer distributorCount;
    @ApiModelProperty(value = "推荐人Id（e家号）")
    private Integer recommendAccountId;
    @ApiModelProperty(value = "推荐人昵称")
    private String recommendNickName;
    @ApiModelProperty(value = "推荐人姓名")
    private String recommendName;
    @ApiModelProperty(value = "推荐人手机号")
    private String recommendPhone;
    @ApiModelProperty(value = "推荐人省")
    private String recommendProvince;
    @ApiModelProperty(value = "推荐人市")
    private String recommendCity;
    @ApiModelProperty(value = "推荐人区")
    private String recommendRegion;
    @ApiModelProperty(value = "经销商企业信息")
    private UserCompanyDTO userCompany;

    // ----- 子账号详情 --------
    @ApiModelProperty(value = "主账号昵称")
    private String mainNickName;
    @ApiModelProperty(value = "主账号手机号")
    private String mainPhone;
    @ApiModelProperty(value = "主账号身份证")
    private String mainIdCard;
    @ApiModelProperty(value = "主账号省")
    private String mainProvince;
    @ApiModelProperty(value = "主账号市")
    private String mainCity;
    @ApiModelProperty(value = "主账号区")
    private String mainRegion;

    // -----------------------  过度字段 -------------------------------------
    @ApiModelProperty(value = "推荐人ID")
    private Integer recommendId;

    // -----------------------  详情要返回areaId,用作接口安全校验 -------------------------------------
    @ApiModelProperty(value = "服务地区id")
    private Integer areaId;

}
