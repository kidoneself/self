package com.yimao.cloud.pojo.query.user;

import com.yimao.cloud.pojo.query.station.BaseQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Lizhqiang
 * @date 2018/12/17
 */


@Getter
@Setter
@ApiModel(description = "经销商订单查询条件")
public class DistributorOrderQueryDTO extends BaseQuery implements Serializable {

    private static final long serialVersionUID = 2430339047489701753L;

    @ApiModelProperty(value = "服务站门店id")
    private Integer stationId;
    @ApiModelProperty(value = "服务区域id")
    private Integer areaId;
    @ApiModelProperty(value = "订单Id")
    private Long id;
    @ApiModelProperty(value = "经销商ID")
    private Integer distributorId;
    @ApiModelProperty(value = "订单来源/0-H5页面、1-经销商app、2-翼猫业务系统")
    private Integer orderSouce;
    @ApiModelProperty(value = "订单类型/0-注册、1-升级、2-续费")
    private Integer orderType;
    @ApiModelProperty(value = "经销商账号")
    private String distributorAccount;
    @ApiModelProperty(value = "支付状态/0-未支付、1-已支付、3-待审核")
    private Integer payState;
    @ApiModelProperty(value = "支付方式/0-支付宝、1-微信、2-pos机、4-转账")
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
    @ApiModelProperty(value = "推荐人经销商账号")
    private String referee;
    @ApiModelProperty(value = "经销商类型等级 50体验版、350微创版、650个人版、950企业版")
    private Integer roleLevel;
    @ApiModelProperty(value = "升级经销商类型等级 50体验版、350微创版、650个人版、950企业版")
    private Integer destRoleLevel;

    @ApiModelProperty(value = "升级剩余有效期（最小时间）")
    private Integer minPeriodValidity;
    @ApiModelProperty(value = "升级剩余有效期（最大时间）")
    private Integer maxPeriodValidity;
}
