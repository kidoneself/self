package com.yimao.cloud.pojo.dto.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Lizhqiang
 * @date 2018/12/17
 */


@Getter
@Setter
@ApiModel(description = "经销商订单")
public class DistributorOrderDTO implements Serializable {

    private static final long serialVersionUID = 2430339047489701754L;

    @ApiModelProperty(value = "订单Id")

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @ApiModelProperty(value = "经销商ID")
    private Integer distributorId;
    @ApiModelProperty(value = "订单来源/0-H5页面、1-经销商app、2-翼猫业务系统")
    private Integer orderSouce;
    @ApiModelProperty(value = "订单类型/0-注册、1-升级、2-续费")
    private Integer orderType;
    @ApiModelProperty(value = "经销商账号")
    private String distributorAccount;
    @ApiModelProperty(value = "支付状态:1-未支付，2-已完成(支付成功)， 3-支付失败 4-待审核")
    private Integer payState;
    @ApiModelProperty(value = "支付方式/支付类型：1-微信；2-支付宝；3-POS机；4-转账；")
    private Integer payType;
    @ApiModelProperty(value = "订单状态/0-待审核、1-已完成、2-待付款")
    private Integer orderState;
    @ApiModelProperty(value = "财务审核状态/0-未审核、1-审核通过、2-审核不通过、3-无需审核")
    private Integer financialState;
    @ApiModelProperty(value = "企业审核状态/0-未审核、1-审核通过、2-审核不通过、3-无需审核")
    private Integer enterpriseState;
    @ApiModelProperty(value = "升级剩余有效期")
    private Integer periodValidity;
    @ApiModelProperty(value = "姓名")
    private String name;
    @ApiModelProperty(value = "手机号")
    private String mobile;
    @ApiModelProperty(value = "省")
    private String province;
    @ApiModelProperty(value = "市")
    private String city;
    @ApiModelProperty(value = "区")
    private String region;
    @ApiModelProperty(value = "支付区间开始时间")
    private Date payStartTime;
    @ApiModelProperty(value = "支付区间结束时间")
    private Date payEndTime;
    @ApiModelProperty(value = "完成区间开始时间")
    private Date completionStartTime;
    @ApiModelProperty(value = "完成区间结束时间")
    private Date completionEndTime;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
    @ApiModelProperty(value = "推荐人经销商账号")
    private String referee;
   /* @ApiModelProperty(value = "支付的金额")
    private BigDecimal payMoney;*/
    @ApiModelProperty(value = "支付时间")
    private Date payTime;
    @ApiModelProperty(value = "完成时间")
    private Date completionTime;
    @ApiModelProperty(value = "配额")
    private Integer quotaNumber;
    @ApiModelProperty(value = "经销商角色id")
    private Integer roleId;
    @ApiModelProperty(value = "经销商类型等级 50体验版、350微创版、650个人版、950企业版")
    private Integer roleLevel;
    @ApiModelProperty(value = "经销商角色名称")
    private String roleName;
    @ApiModelProperty(value = "升级经销商类型或者注册经销商类型等级ID 50体验版、350微创版、650个人版、950企业版")
    private Integer destRoleId;
    @ApiModelProperty(value = "升级经销商类型等级 50体验版、350微创版、650个人版、950企业版")
    private Integer destRoleLevel;
    @ApiModelProperty(value = "升级经销商角色名称")
    private String destRoleName;
    @ApiModelProperty(value = "价格")
    private BigDecimal price;
    @ApiModelProperty(value = "支付凭证/pos机付款的凭证")
    private String payRecord;
    @ApiModelProperty(value = "支付流水号/线上付款的流水号")
    private String tradeNo;
    @ApiModelProperty(value = "创建人id（app创建新账号人的id）")
    private Integer creator;
    @ApiModelProperty(value = "服务站公司id")
    private Integer stationCompanyId;
    @ApiModelProperty(value = "服务站公司名称")
    private String stationCompanyName;
    @ApiModelProperty(value = "地区id")
    private Integer areaId;

    @ApiModelProperty(value = "企业信息")
    private UserCompanyApplyDTO userCompanyApply;

    @ApiModelProperty(value = "经销商信息")
    private DistributorDTO distributor;
    @ApiModelProperty(value = "经销商推荐人信息")
    private DistributorDTO recommendDistributor;
    @ApiModelProperty(value = "用户信息")
    private UserDTO user;

    @ApiModelProperty(value = "用户合同签署状态")
    private Integer userSignState;//用户签署状态

    @ApiModelProperty(value = "财务审核人")
    private String financialAuditor;
    @ApiModelProperty(value = "财务审核时间")
    private Date financialAuditTime;
    @ApiModelProperty(value = "企业审核人")
    private String enterpriseAuditor;
    @ApiModelProperty(value = "企业审核时间")
    private Date enterpriseAuditTime;
    @ApiModelProperty(value = "推荐人id")
    private Integer recommendId;
    @ApiModelProperty(value = "推荐人名称")
    private String recommendName;
    @ApiModelProperty(value = "经销商身份证号")
    private String distributorIdCard;
    @ApiModelProperty(value = "地址")
    private String address;
    @ApiModelProperty(value = "真实姓名")
    private String realName;
    @ApiModelProperty(value = "老系统orderId")
    private String oldOrderId;
}
