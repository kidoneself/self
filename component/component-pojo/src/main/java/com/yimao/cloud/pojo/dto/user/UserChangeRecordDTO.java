package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;


/**
 * @description: 信息变更记录
 * @author: yu chunlei
 * @create: 2018-12-19 09:38:12
 **/
@Data
@ApiModel(description = "信息变更记录")
public class UserChangeRecordDTO implements Serializable {

    private static final long serialVersionUID = 625725524845839672L;
    private Integer id;

    @ApiModelProperty(position = 1, value = "原用户e家号")
    private Integer origUserId;

    @ApiModelProperty(position = 2, value = "原经销商Id")
    private Integer origDistributorId;

    @ApiModelProperty(position = 3, value = "原经销商账号")
    private String origAccount;

    @ApiModelProperty(position = 4, value = "原手机号")
    private String origPhone;

    @ApiModelProperty(position = 5, value = "原用户类型")
    private Integer origUserType;

    @ApiModelProperty(position = 6, value = "原经销商类型")
    private Integer origDistributorType;

    @ApiModelProperty(position = 7, value = "更改后Id")
    private Integer destUserId;

    @ApiModelProperty(position = 8, value = "更改后经销商Id")
    private Integer destDistributorId;

    @ApiModelProperty(position = 9, value = "原经销商账号")
    private String destAccount;

    @ApiModelProperty(position = 10, value = "更改后手机号")
    private String destPhone;

    @ApiModelProperty(position = 11, value = "更改后用户类型,1-经销商（微创版），2-经销商（个人版），3-分享用户（有健康大使），4-普通用户（无健康大使），5-企业版经销商（主账号），6-企业版经销商（子账号），7-分销商")
    private Integer destUserType;

    @ApiModelProperty(position = 12, value = "原经销商类型")
    private Integer destDistributorType;

    @ApiModelProperty(position = 13, value = "变化类型（事件）1-创建账号 2-升级分享 3-升级分销用户 4-注册 5-续费 6-升级 7-首次关注公众号 8-首次登陆小程序 9-取消关注公众号 10-转让 11-编辑 12-变更健康大使")
    private Integer type;

    @ApiModelProperty(position = 14, value = "发生时间")
    private Date time;

    @ApiModelProperty(position = 15, value = "变更端 1-H5页面；2-经销商app; 3:翼猫业务系统")
    private Integer terminal;

    @ApiModelProperty(position = 16, value = "金额（升级或者续费）")
    private BigDecimal amount;

    @ApiModelProperty(position = 17, value = "订单号")
    @JsonSerialize(using=ToStringSerializer.class)
    private Long orderId;

    @ApiModelProperty(position = 18, value = "来源 1-自主关注公众号 2-优惠卡赠送 2-绑定手机号")
    private Integer source;

    @ApiModelProperty(position = 19, value = "分享者ID")
    private Integer shareId;

    @ApiModelProperty(position = 20, value = "备注")
    private String remark;

    @ApiModelProperty(position = 21, value = "创建人")
    private String creator;
    
    /**
     * 经销商代理商编辑前数据json
     */
    @ApiModelProperty(position = 22, value = " 经销商代理商编辑前数据json")
    private String origDistributorData;
    
    /**
     * 经销商代理商编辑后数据json
     */
    @ApiModelProperty(position = 23, value = "经销商代理商编辑后数据json")
    private String destDistributorData;
    
    @ApiModelProperty(position = 24, value = "经销商代理商编辑前数据")
    private DistributorDTO originDistributor;
    @ApiModelProperty(position = 25, value = "经销商代理商编辑后数据")
    private DistributorDTO destDistributor;
    @ApiModelProperty(position = 26, value = "用户编辑前数据json")
    private String origUserData;
    @ApiModelProperty(position = 27, value = "用户编辑后数据json")
    private String destUserData;
    @ApiModelProperty(position = 28, value = "用户编辑前数据")
    private UserDTO originUser;
    @ApiModelProperty(position = 29, value = "用户编辑后数据")
    private UserDTO destUser;
    @ApiModelProperty(position = 30, value = "转让人姓名")
    private String origRealName;
    @ApiModelProperty(position = 31, value = "被转让人姓名")
    private String destRealName;
    
    public UserChangeRecordDTO() {
    }

    public UserChangeRecordDTO(Integer origUserId, Integer type, Integer origUserType, String origPhone, String remark) {
        this.origUserId = origUserId;
        this.origPhone = origPhone;
        this.origUserType = origUserType;
        this.type = type;
        this.remark = remark;
    }
}
