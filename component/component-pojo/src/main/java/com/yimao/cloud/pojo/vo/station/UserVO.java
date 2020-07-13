package com.yimao.cloud.pojo.vo.station;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Liu Long Jie
 * @date 2020-1-15
 */
@Getter
@Setter
@ApiModel(description = "用户信息 (服务站站务系统)")
public class UserVO implements Serializable {

    private static final long serialVersionUID = 1234534369568533399L;

    // -----------------------  查询列表需要字段 -------------------------------------
    @ApiModelProperty(value = "e家号")
    private Integer id;
    @ApiModelProperty(value = "昵称")
    private String nickName;
    @ApiModelProperty(value = "用户姓名")
    private String realName;
    @ApiModelProperty(value = "用户手机号")
    private String mobile;
    @ApiModelProperty(value = "用户身份：体验版经销商；微创版经销商；个人版经销商；企业版经销商；普通用户；会员用户；特批经销商")
    private String userTypeName;
    @ApiModelProperty(value = "来源端：1-健康e家公众号 2-经销商APP 3-净水设备")
    private Integer originTerminal;
    @ApiModelProperty(value = "用户来源方式：1-代言卡分享 2-宣传卡分享 3-资讯分享 4-视频分享 5-商品分享 6-自主关注公众号 7-经销商APP添加 8-水机屏推广二维码")
    private String sourceMode;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    @ApiModelProperty(value = "升级当前身份时间")
    private Date upCurrentIdentityDate;
    @ApiModelProperty(value = "健康大使ID")
    private Integer ambassadorId;
    @ApiModelProperty(value = "健康大使手机号")
    private String ambassadorPhone;
    @ApiModelProperty(value = "经销商e家号")
    private Integer distributorNumber;
    @ApiModelProperty(value = "经销商姓名")
    private String distributorName;
    @ApiModelProperty(value = "经销商账号")
    private String distributorAccount;
    @ApiModelProperty(value = "经销商省")
    private String distProvince;
    @ApiModelProperty(value = "经销商市")
    private String distCity;
    @ApiModelProperty(value = "经销商区")
    private String distRegion;

    // ----------------------- 列表展示,详情过度字段 -------------------------------------
    @ApiModelProperty(value = "用户来源方式：1-代言卡分享 2-宣传卡分享 3-资讯分享 4-视频分享 5-商品分享 6-自主关注公众号 7-经销商APP添加 8-水机屏推广二维码")
    private Integer origin;
    @ApiModelProperty(value = "用户等级：0-体验版经销商；1-微创版经销商；2-个人版经销商；3-分享用户；4-普通用户；5-企业版经销商（主）；6-企业版经销商（子）；7-分销用户；8-特批经销商")
    private Integer userType;
    @ApiModelProperty(value = "用户绑定经销商ID")
    private Integer mid;
    @ApiModelProperty(value = "经销商ID")
    private Integer distributorId;
    @ApiModelProperty(value = "变为分享用户的时间")
    private Date beSharerTime;
    @ApiModelProperty(value = "变为经销商的时间")
    private Date beDistributorTime;
    @ApiModelProperty(value = "变为分销用户的时间")
    private Date beSalesTime;
    @ApiModelProperty(value = "用户绑定上级健康大使的时间")
    private Date bindAmbassadorTime;
    @ApiModelProperty(value = "健康大使身份")
    private Integer ambassadorUserType;

    // -----------------------  详情需要字段（部分字段在上面） -------------------------------------
    @ApiModelProperty(value = "头像")
    private String headImg;
    @ApiModelProperty(value = "性别（1：男，2：女）")
    private Integer sex;
    @ApiModelProperty(value = "身份证号码")
    private String idCard;
    @ApiModelProperty(value = "用户绑定手机号的时间")
    private Date bindPhoneTime;
    @ApiModelProperty(value = "绑定上级关系时间")
    private Date bindingSuperiorsDate;
    @ApiModelProperty(value = "用户绑定上级经销商的时间")
    private Date bindDistributorTime;
    @ApiModelProperty(value = "省")
    private String province;
    @ApiModelProperty(value = "市")
    private String city;
    @ApiModelProperty(value = "区")
    private String region;

    @ApiModelProperty(value = "健康大使e家号")
    private Integer ambassadorNumber;
    @ApiModelProperty(value = "健康大使名字")
    private String ambassadorName;
    @ApiModelProperty(value = "健康大使昵称")
    private String ambassadorNickName;
    @ApiModelProperty(value = "健康大使身份名称")
    private String ambassadorUserTypeName;

    @ApiModelProperty(value = "经销商类型名称")
    private String distributorTypeName;
    @ApiModelProperty(value = "经销商手机号")
    private String distributorPhone;
    @ApiModelProperty(value = "经销商身份证号")
    private String distributorIdCard;

    // -----------------------  详情要返回areaId,用作接口安全校验 -------------------------------------
    @ApiModelProperty(value = "服务地区id")
    private Integer areaId;

}
