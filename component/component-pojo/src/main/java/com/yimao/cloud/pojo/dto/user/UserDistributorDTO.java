package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @author: yu chunlei
 * @create: 2019-01-15 15:12:58
 **/
@Data
public class UserDistributorDTO implements Serializable {

    private static final long serialVersionUID = -312767999222246149L;
    @ApiModelProperty(value = "头像")
    private String headImage;//头像
    @ApiModelProperty(value = "账户名")
    private String accountName;//账户名
    @ApiModelProperty(value = "姓名")
    private String realName;//姓名
    @ApiModelProperty(value = "性别")
    private Integer sex;//性别
    @ApiModelProperty(value = "联系方式")
    private String phone;//联系方式
    @ApiModelProperty(value = "地址")
    private String address;//地址
    @ApiModelProperty(value = "身份证号")
    private String idCard;//身份证号
    @ApiModelProperty(value = "邮箱")
    private String email;//邮箱

    @ApiModelProperty(value = "1-代理商,2-经销商, 3-经销商+代理商")
    private Integer type;
    @ApiModelProperty(value = "是否为站长0-否 1-是默认0")
    private Boolean stationMaster;
    @ApiModelProperty(value = "代理商级别 1-省代 2-市代  4-区代 3-省代+市代 5-省代+区代 6-市代+区代 7-省+市+区代")
    private Integer agentLevel;
    @ApiModelProperty(value = "用户注册意愿身份：0-体验版本经销商，1-微创版经销商，2-个人版经销商，3-企业版经销商")
    private Integer wishType;//用户注册意愿身份：0-体验版本经销商，1-微创版经销商，2-个人版经销商，3-企业版经销商
    @ApiModelProperty(value = "用户等级：0-经销商（体验版），1-经销商（微创版），2-经销商（个人版），3-分享用户（有健康大使），4-普通用户（无健康大使），5-企业版经销商（主账号），6-企业版经销商（子账号），7-分销商，8-特批经销商")
    private Integer userType;//用户等级：0-经销商（体验版），1-经销商（微创版），2-经销商（个人版），3-分享用户（有健康大使），4-普通用户（无健康大使），5-企业版经销商（主账号），6-企业版经销商（子账号），7-分销商
    @ApiModelProperty(value = "经销商角色等级 50体验版、350微创版、650个人版、950企业版 -50特批经销商")
    private Integer roleLevel;

    @ApiModelProperty(value = "企业信息")
    private UserCompanyDTO userCompany;

    @ApiModelProperty(value = "是否企业版经销商,0--否 1--是")
    private Boolean isCompanyInfo;//是否企业版经销商

    @ApiModelProperty(value = "是否创始人,0--否 1--是")
    private Boolean founder;

    private Integer id;


}
