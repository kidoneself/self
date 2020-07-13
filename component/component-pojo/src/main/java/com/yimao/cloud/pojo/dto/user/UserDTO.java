package com.yimao.cloud.pojo.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Zhang Bo
 * @date 2018/8/10.
 */
@Getter
@Setter
@ApiModel(description = "用户信息")
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 293683436956833399L;
    @ApiModelProperty(position = 1, value = "e家号")
    private Integer id;
    @ApiModelProperty(position = 2, value = "用户手机号")
    private String mobile;
    @ApiModelProperty(position = 3, value = "用户名")
    private String userName;
    @ApiModelProperty(position = 4, value = "密码")
    private String password;
    @ApiModelProperty(position = 5, value = "昵称")
    private String nickName;
    @ApiModelProperty(position = 6, value = "用户姓名")
    private String realName;
    @ApiModelProperty(position = 7, value = "用户等级：0-体验版经销商；1-微创版经销商；2-个人版经销商；3-分享用户；4-普通用户；5-企业版经销商（主）；6-企业版经销商（子）；7-分销用户；8-特批经销商")
    private Integer userType;
    @ApiModelProperty(position = 7, value = "用户身份：体验版经销商；微创版经销商；个人版经销商；企业版经销商；普通用户；会员用户；特批经销商")
    private String userTypeName;

    @ApiModelProperty(position = 8, value = "用户绑定手机号的时间")
    private Date bindPhoneTime;
    @ApiModelProperty(position = 9, value = "用户绑定上级经销商的时间")
    private Date bindDistributorTime;
    @ApiModelProperty(position = 10, value = "用户绑定上级健康大使的时间")
    private Date bindAmbassadorTime;
    @ApiModelProperty(position = 11, value = "经销商ID")
    private Integer distributorId;
    @ApiModelProperty(position = 12, value = "健康大使ID")
    private Integer ambassadorId;

    @ApiModelProperty(position = 13, value = "变为经销商的时间")
    private Date beDistributorTime;
    @ApiModelProperty(position = 14, value = "变为分销用户的时间")
    private Date beSalesTime;
    @ApiModelProperty(position = 15, value = "变为分享用户的时间")
    private Date beSharerTime;

    @ApiModelProperty(position = 16, value = "产品分销收益权限：例：1,2,3,4,5以逗号分隔 数字为产品类型")
    private String incomePermission;
    @ApiModelProperty(position = 17, value = "是否关注公众号")
    private Boolean subscribe;
    @ApiModelProperty(position = 18, value = "用户来源方式：1-代言卡分享 2-宣传卡分享 3-资讯分享 4-视频分享 5-商品分享 6-自主关注公众号 7-经销商APP添加 8-水机屏推广二维码")
    private Integer origin;
    @ApiModelProperty(position = 19, value = "来源端：1-健康e家公众号 2-经销商APP 3-净水设备")
    private Integer originTerminal;

    private String sourceMode;

    @ApiModelProperty(position = 20, value = "个人专属二维码（公众号）")
    private String qrcode;
    @ApiModelProperty(position = 21, value = "个人专属二维码（小程序）")
    private String wxacode;
    @ApiModelProperty(position = 22, value = "是否在线：0-否，1-是")
    private Boolean online;
    @ApiModelProperty(position = 23, value = "是否可用：0-否，1-是")
    private Boolean available;
    @ApiModelProperty(position = 24, value = "头像")
    private String headImg;
    @ApiModelProperty(position = 25, value = "年龄")
    private Integer age;
    @ApiModelProperty(position = 26, value = "出生日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthday;
    @ApiModelProperty(position = 27, value = "邮箱")
    private String email;
    @ApiModelProperty(position = 28, value = "性别（1：男，2：女）")
    private Integer sex;
    @ApiModelProperty(position = 29, value = "身高（单位cm）")
    private Integer height;
    @ApiModelProperty(position = 30, value = "体重（单位kg）")
    private Integer weight;
    @ApiModelProperty(position = 31, value = "身份证号码")
    private String idCard;
    @ApiModelProperty(position = 32, value = "最后登陆时间")
    private Date lastLoginTime;
    @ApiModelProperty(position = 33, value = "省")
    private String province;
    @ApiModelProperty(position = 34, value = "市")
    private String city;
    @ApiModelProperty(position = 35, value = "区")
    private String region;
    @ApiModelProperty(position = 36, value = "详细地址")
    private String address;

    @ApiModelProperty(position = 37, value = "创建时间")
    private Date createTime;
    @ApiModelProperty(position = 38, value = "更新时间")
    private Date updateTime;


    @ApiModelProperty(position = 39, value = "健康大使手机号")
    private String ambassadorPhone;
    @ApiModelProperty(position = 40, value = "健康大使名字")
    private String ambassadorName;
    @ApiModelProperty(position = 41, value = "健康大使昵称")
    private String ambassadorNickName;
    @ApiModelProperty(position = 42, value = "健康大使身份")
    private Integer ambassadorUserType;
    @ApiModelProperty(position = 43, value = "健康大使身份名称")
    private String ambassadorUserTypeName;

    @ApiModelProperty(position = 44, value = "经销商姓名")
    private String distributorName;
    @ApiModelProperty(position = 44, value = "经销商昵称")
    private String distributorNickName;
    @ApiModelProperty(position = 45, value = "经销商账号")
    private String distributorAccount;
    @ApiModelProperty(position = 46, value = "经销商手机号")
    private String distributorPhone;
    @ApiModelProperty(position = 47, value = "经销商类型")
    private Integer distributorType;
    @ApiModelProperty(position = 48, value = "经销商类型名称")
    private String distributorTypeName;
    @ApiModelProperty(position = 49, value = "经销商身份证号")
    private String distributorIdCard;
    @ApiModelProperty(position = 49, value = "经销商唯一Id(对应Mongodb)")
    private String oldDistributorId;
    @ApiModelProperty(position = 50, value = "经销商省")
    private String distProvince;
    @ApiModelProperty(position = 51, value = "经销商市")
    private String distCity;
    @ApiModelProperty(position = 52, value = "经销商区")
    private String distRegion;


    @ApiModelProperty(position = 53, value = "用户认证jwt信息")
    private String token;// 用户认证jwt信息

    private String openid;
    private String healthyOpenid;
    private String catOpenid;

    private String unionid;


    @ApiModelProperty(position = 55, value = "企业版经销商公司名称")
    private String companyName;

    @ApiModelProperty(position = 57, value = "用户绑定经销商ID")
    private Integer mid;

    @ApiModelProperty(position = 58, value = "客户ID")
    private Integer clientId;

    @ApiModelProperty(position = 59, value = "客户别名")
    private String clientName;

    @ApiModelProperty(position = 60, value = "几星")
    private Integer starNum;
    @ApiModelProperty(position = 61, value = "服务站名称")
    private String stationName;

    @ApiModelProperty(position = 62, value = "经销商e家号")
    private Integer distributorNumber;

    @ApiModelProperty(position = 63, value = "健康大使e家号")
    private Integer ambassadorNumber;

    @ApiModelProperty(position = 64, value = "升级当前身份时间")
    private Date upCurrentIdentityDate;

    @ApiModelProperty(position = 65, value = "绑定上级关系时间")
    private Date bindingSuperiorsDate;

    @ApiModelProperty(position = 67, value = "是否解绑")
    private Boolean isBind;

    @ApiModelProperty(position = 68, value = "解绑微信")
    private Boolean unBindWeChat;

     @ApiModelProperty(position = 69, value = "客户类型 1-个人客户 2-企业客户")
     private Integer customerType;
    @ApiModelProperty(position = 70, value = "经销商企业信息")
    private UserCompanyDTO userCompany;

    @ApiModelProperty(position = 80, value = "用户多重身份最高身份")
    private String identity;
    @ApiModelProperty(position = 81, value = "用户多重身份集合")
    private List<String> identityList;

    @ApiModelProperty(position = 90, value = "代理商级别：1-省代；2-市代；4-区代；3-省代+市代；5-省代+区代；6-市代+区代；7-省代+市代+区代；默认为空（非代理商）")
    private Integer agentLevel;
    @ApiModelProperty(position = 93, value = "是否为创始人：0-否；1-是；默认为0")
    private Boolean founder = false;

    // @ApiModelProperty(position = 93, value = "老的经销商id(对应Mongodb)")
    // private String oldMid;

}
