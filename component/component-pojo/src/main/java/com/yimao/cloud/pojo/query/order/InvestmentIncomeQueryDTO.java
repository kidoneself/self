package com.yimao.cloud.pojo.query.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 * 招商收益查询DTO
 * @author Liu Yi
 * @date 2018/12/19
 */
@Data
public class InvestmentIncomeQueryDTO implements Serializable {

    private static final long serialVersionUID = -6047391967687534960L;

    @ApiModelProperty(value = "主键ID", hidden = true)
    private Integer id;
    @ApiModelProperty(value = "订单id")
    private Long orderId;
    @ApiModelProperty(value = "用户e家号(经销商id)")
    private Integer userId;
    @ApiModelProperty(value = "用户名称", hidden = true)
    private String userName;
    @ApiModelProperty(value = "用户手机号", hidden = true)
    private String userPhone;
    @ApiModelProperty(value = "经销商id")
    private Integer distributorId;
    @ApiModelProperty(value = "经销商名称")
    private String distributorName;
    @ApiModelProperty(value = "经销商类型（50-体验版、350-微创版、650-个人版、950-企业版）")
    private Integer distributorLevel;
    @ApiModelProperty(value = "经销商账户")
    private String  distributorAccount;
    @ApiModelProperty(value = "经销商名称(推荐人)")
    private String refereeName;
    @ApiModelProperty(value = "经销商账户(推荐人)")
    private String  refereeAccount;
    @ApiModelProperty(value = "经销商订单类型：0-注册、1-升级、2-续费")
    private Integer distributorOrderType;
    @ApiModelProperty(value = "收益规则id", hidden = true)
    private Integer incomeRuleId;
    @ApiModelProperty(value = "收益规则code", hidden = true)
    private String incomeSubjectCode;
    @ApiModelProperty(value = "分配规则：1-按比例分配 2-按金额分配", hidden = true)
    private Integer allotType;
    @ApiModelProperty(value = "订单完成状态：0-未完成 1-(已完成)可结算 2-已结算 3-已失效", hidden = true)
    private Integer orderCompleteStatus;

    @ApiModelProperty(value = "省")
    private String province;
    @ApiModelProperty(value = "市")
    private String city;
    @ApiModelProperty(value = "区")
    private String region;

    @ApiModelProperty(value = "订单完成时间",example = "2018-12-28 11:00:00")
    private String orderCompleteTime;
    @ApiModelProperty("订单完成开始时间")
    private String startTime;
    @ApiModelProperty("订单完成结束时间")
    private String endTime;
    @ApiModelProperty("结算月份")
    private String settlementMonth;

    @ApiModelProperty(value = "经销商ID集合",hidden = true)
    private List<Integer> distributorIds;
}