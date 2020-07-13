package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author hhf
 * @description DistributorDTO
 * @date 2018/12/18
 */
@ApiModel(description = "经销商DTO")
@Getter
@Setter
@ToString
public class DistributorDTO implements Serializable {

    private static final long serialVersionUID = 8216140961989447917L;

    @ApiModelProperty(value = "经销商ID")
    public Integer id;
    @ApiModelProperty(position = 1, value = "经销商账号创建方式：1-系统自动生成；2-自定义创建；")
    private Integer createAccountType;
    @ApiModelProperty(position = 1, value = "e家号")
    private Integer userId;
    @ApiModelProperty(position = 2, value = "经销商账号")
    private String userName;
    @ApiModelProperty(position = 3, value = "密码")
    private String password;
    @ApiModelProperty(position = 4, value = "经销商昵称")
    private String nickName;
    @ApiModelProperty(position = 5, value = "经销商姓名")
    private String realName;
    @ApiModelProperty(position = 6, value = "性别：1-男；2-女；")
    private Integer sex;
    @ApiModelProperty(position = 7, value = "经销商手机号")
    private String phone;
    @ApiModelProperty(position = 8, value = "省")
    private String province;
    @ApiModelProperty(position = 9, value = "市")
    private String city;
    @ApiModelProperty(position = 10, value = "区")
    private String region;
    @ApiModelProperty(position = 11, value = "地址")
    private String address;
    @ApiModelProperty(position = 12, value = "身份证")
    private String idCard;
    @ApiModelProperty(position = 13, value = "邮箱")
    private String email;
    @ApiModelProperty(position = 14, value = "支付金额")
    private BigDecimal money;
    @ApiModelProperty(position = 15, value = "头像")
    private String headImage;
    @ApiModelProperty(position = 16, value = "地域id")
    private Integer areaId;

    @ApiModelProperty(position = 20, value = "经销代理身份： 1-代理商；2-经销商；3-经销商+代理商；")
    private Integer type;
    @ApiModelProperty(position = 21, value = "是否为五大创始人：0-否；1-是；")
    private Boolean founder;
    @ApiModelProperty(position = 22, value = "是否为站长0-否 1-是默认0")
    private Boolean stationMaster;
    @ApiModelProperty(position = 23, value = "是否有推荐人：0-否；1-是")
    private Boolean hasRecommend;
    @ApiModelProperty(position = 23, value = "选择推荐人方式：1-系统自动分配；2-手动选择；3-设置自己为推荐人；")
    private Integer chooseRecommendType;
    @ApiModelProperty(position = 24, value = "推荐人ID")
    private Integer recommendId;
    @ApiModelProperty(position = 25, value = "推荐人姓名")
    private String recommendName;

    @ApiModelProperty(position = 30, value = "上线端： 1-翼猫业务系统；2-经销商app;")
    private Integer terminal;
    @ApiModelProperty(position = 31, value = "是否为发起人：0-不是；1-是")
    private Boolean sponsor;
    @ApiModelProperty(position = 32, value = "发起人级别：1-省级；2-市级；4-区级；3-省级+市级；5-省级+区级；6-市级+区级；7-省级+市级+区级；")
    private Integer sponsorLevel;
    @ApiModelProperty(position = 33, value = "代理商级别：1-省代；2-市代；4-区代；3-省代+市代；5-省代+区代；6-市代+区代；7-省代+市代+区代；")
    private Integer agentLevel;
    @ApiModelProperty(position = 34, value = "经销商角色ID")
    private Integer roleId;
    @ApiModelProperty(position = 35, value = "经销商角色等级：-50-折机版；50-体验版；350-微创版；650-个人版；950-企业版（主）；1000-企业版（子）")
    private Integer roleLevel;
    @ApiModelProperty(position = 36, value = "经销商角色名称")
    private String roleName;

    @ApiModelProperty(position = 40, value = "经销商企业ID")
    private Integer companyId;
    @ApiModelProperty(position = 40, value = "经销商企业名称")
    private String companyName;
    @ApiModelProperty(position = 40, value = "经销商企业所属行业")
    private String industry;
    @ApiModelProperty(position = 41, value = "企业版主账号id")
    private Integer pid;
    @ApiModelProperty(position = 42, value = "附件")
    private String attachment;
    @ApiModelProperty(position = 43, value = "备注")
    private String remark;
    @ApiModelProperty(position = 44, value = "配额")
    private Integer quota;
    @ApiModelProperty(position = 45, value = "剩余配额")
    private Integer remainingQuota;
    @ApiModelProperty(position = 46, value = "省代排名")
    private Integer provinceRanking;
    @ApiModelProperty(position = 47, value = "市代排名")
    private Integer cityRanking;
    @ApiModelProperty(position = 48, value = "区代排名")
    private Integer regionRanking;
    @ApiModelProperty(position = 49, value = "是否删除 0-否 1-是")
    private Boolean deleted;
    @ApiModelProperty(position = 50, value = "是否禁用：0-否，1-是")
    private Boolean forbidden;
    @ApiModelProperty(position = 51, value = "是否禁止下单：0-否，1-是")
    private Boolean forbiddenOrder;

    @ApiModelProperty(position = 60, value = "区代发展的经销商数量")
    private Integer distributorCount;
    @ApiModelProperty(position = 61, value = "分享用户数量")
    private Integer sharerUserCount;
    @ApiModelProperty(position = 62, value = "分销用户数量")
    private Integer saleUserCount;
    @ApiModelProperty(position = 63, value = "主账号的子账号数量")
    private Integer childAccountCount;
    @ApiModelProperty(position = 64, value = "主账号")
    private String mainAccount;
    @ApiModelProperty(position = 65, value = "主账号姓名")
    private String mainAccountName;

    @ApiModelProperty(position = 69, value = "推荐人Id（e家号）")
    private Integer recommendAccountId;
    @ApiModelProperty(position = 70, value = "推荐人账号")
    private String recommendAccount;
    @ApiModelProperty(position = 72, value = "推荐人昵称")
    private String recommendNickName;
    @ApiModelProperty(position = 73, value = "推荐人手机号")
    private String recommendPhone;
    @ApiModelProperty(position = 74, value = "推荐人身份证号")
    private String recommendIdCard;
    @ApiModelProperty(position = 75, value = "推荐人省")
    private String recommendProvince;
    @ApiModelProperty(position = 76, value = "推荐人市")
    private String recommendCity;
    @ApiModelProperty(position = 77, value = "推荐人区")
    private String recommendRegion;

    @ApiModelProperty(position = 80, value = "置换金额")
    private BigDecimal replacementAmount;
    @ApiModelProperty(position = 81, value = "剩余置换金额")
    private BigDecimal remainingReplacementAmount;
    @ApiModelProperty(position = 82, value = "是否是福慧顺")
    private Boolean fuhuishun;
    @ApiModelProperty(position = 83, value = "成为该经销商身份时间", example = "2018-12-28 11:00:00")
    private Date completeTime;

    @ApiModelProperty(position = 90, value = "用户认证jwt信息")
    private String token;
    @ApiModelProperty(position = 95, value = "用户信息")
    private UserDTO user;
    @ApiModelProperty(position = 96, value = "经销商企业信息")
    private UserCompanyDTO userCompany;
    @ApiModelProperty(position = 100, value = "创建人")
    private String creator;
    @ApiModelProperty(position = 101, value = "创建时间", example = "2018-12-28 11:00:00")
    private Date createTime;
    @ApiModelProperty(position = 102, value = "更新人")
    private String updater;
    @ApiModelProperty(position = 103, value = "更新时间", example = "2018-12-28 11:00:00")
    private Date updateTime;
    @ApiModelProperty(position = 104, value = "mongo库中的主键")
    private String oldId;
    @ApiModelProperty(position = 105, value = "是否是子账号")
    private Boolean subAccount;
    @ApiModelProperty(position = 106, value = "来源方式：1-翼猫企业二维码;2-翼猫后台上线; 3:经销商app创建账号; 4-资讯分享 ; 5-视频分享; 6-权益卡分享 ; 7-发展经销商二维码；")
    private Integer sourceType;


    @ApiModelProperty(position = 106, value = "终端类型：1-Android；2-ios")
    private Integer appType;
    @ApiModelProperty(position = 107, value = "app版本")
    private String version;

    @ApiModelProperty(position = 108, value = "主账号Id(e家好)")
    private Integer mainAccountId;
    @ApiModelProperty(position = 109, value = "主账号昵称")
    private String mainNickName;
    @ApiModelProperty(position = 110, value = "主账号手机号")
    private String mainPhone;
    @ApiModelProperty(position = 111, value = "主账号身份证")
    private String mainIdCard;
    @ApiModelProperty(position = 112, value = "主账号省")
    private String mainProvince;
    @ApiModelProperty(position = 113, value = "主账号市")
    private String mainCity;
    @ApiModelProperty(position = 114, value = "主账号区")
    private String mainRegion;

    @ApiModelProperty(position = 115, value = "经销商所属服务站")
    private String station;
    @ApiModelProperty(position = 116, value = "支付金额是否为0:0-否；1-是")
    private Boolean payIsZero;
    @ApiModelProperty(position = 117, value = "成为省代时间", example = "2018-12-28 11:00:00")
    private Date provincialTime; 
    @ApiModelProperty(position = 118, value = "成为市代时间", example = "2018-12-28 11:00:00")
    private Date cityTime;	
    @ApiModelProperty(position = 119, value = "成为区代时间", example = "2018-12-28 11:00:00")
    private Date districtTime;
    @ApiModelProperty(position = 120, value = "mongo库中的推荐人ID主键")
    private String oldRecommendId;          //mongo库中的推荐人ID主键

}
