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
@ApiModel(description = "经销商申请DTO")
@Getter
@Setter
@ToString
public class UsreDistributorApplyDTO implements Serializable {
    private static final long serialVersionUID = 625725524845839672L;

    @ApiModelProperty(value = "经销商ID")
    public Integer id;
    @ApiModelProperty(value = "经销商订单ID")
    public Long orderId;
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

    @ApiModelProperty(position = 24, value = "推荐人ID")
    private Integer recommendId;
    @ApiModelProperty(position = 25, value = "推荐人姓名")
    private String recommendName;

    @ApiModelProperty(position = 30, value = "上线端： 1-翼猫业务系统；2-经销商app;")
    private Integer terminal;
    @ApiModelProperty(position = 34, value = "经销商角色ID")
    private Integer roleId;
    @ApiModelProperty(position = 35, value = "经销商角色等级：-50-折机版；50-体验版；350-微创版；650-个人版；950-企业版（主）；1000-企业版（子）")
    private Integer roleLevel;
    @ApiModelProperty(position = 36, value = "经销商角色名称")
    private String roleName;

   /* @ApiModelProperty(position = 40, value = "经销商企业ID")
    private Integer companyId;
    @ApiModelProperty(position = 40, value = "经销商企业名称")
    private String companyName;*/
    @ApiModelProperty(position = 42, value = "附件")
    private String attachment;
    @ApiModelProperty(position = 43, value = "备注")
    private String remark;
    @ApiModelProperty(position = 44, value = "配额")
    private Integer quota;

    @ApiModelProperty(position = 69, value = "推荐人Id（e家号）")
    private Integer recommendAccountId;
    @ApiModelProperty(position = 70, value = "推荐人账号")
    private String recommendAccount;

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

    @ApiModelProperty(position = 106, value = "来源方式：1-翼猫企业二维码;2-翼猫后台上线; 3:经销商app创建账号; 4-资讯分享 ; 5-视频分享; 6-权益卡分享 ; 7-发展经销商二维码；")
    private Integer sourceType;
    @ApiModelProperty(position = 107, value = "老的推荐人Id")
    private String oldRecommendId;

}
