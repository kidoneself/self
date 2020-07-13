package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.Date;

/**
 * @description: 用户导出信息
 * @author: yu chunlei
 * @create: 2019-05-31 09:56:30
 **/
@Getter
@Setter
@ApiModel(description = "导出用户信息DTO")
public class UserExportDTO implements Serializable {

    private static final long serialVersionUID = 3872257462771842047L;
    private Integer userId;//用户ID
    private String nickName;//昵称
    private String realName;//真实姓名
    private String sex;//性别（1：男，2：女）
    private Integer age;                    //年龄
    private String mobile;                  //手机号
    private String userType;               //用户等级：0-体验版经销商；1-微创版经销商；2-个人版经销商；3-分享用户；4-普通用户；5-企业版经销商（主）；6-企业版经销商（子）；7-分销用户；
    private String customerType;//客户类型
    private String originTerminal;         //来源端：1-健康e家公众号 2-经销商APP 3-净水设备
    private String origin;                 //用户来源方式：1-代言卡分享 2-宣传卡分享 3-资讯分享 4-视频分享 5-商品分享 6-自主关注公众号 7-经销商APP添加 8-水机屏推广二维码
    private String createTime;                //创建时间
    private String generalUserTime;                //普通用户时间
    private String beSalesTime;               //变为分销用户的时间
    private String province;                //省
    private String city;                    //市
    private String region;                  //区
    private String address;                  //详细地址

    private String companyName;             //公司名称
    private String companyIndustry;         //公司所属行业
    private Integer serviceNum;//服务人数
    private String sceneTag;//场景标签

    private Integer ambassadorId;           //健康大使ID
    private String ambassadorUserType;//健康大使身份
    private String ambassadorPhone;//健康大使手机号

    private Integer distributorId;//经销商ID
    private String distributorAccount;//经销商账号
    private String distributorName;//经销商姓名
    private String distributorType;//经销商类型
    private String distProvince;//经销商省
    private String distCity;//经销商市
    private String distRegion;//经销商区


    private String hasSubAccount;//是否有子账号
    private Integer subAccountId;//子账号ID
    private String subAccountUserName;//子账号用户名
    private String subAccountRealName;//子账号姓名



}
