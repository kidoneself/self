package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户概况
 *
 * @author hhf
 * @date 2019/3/19
 */
@Data
public class UserOverviewDTO implements Serializable {

    private static final long serialVersionUID = 1892833751010245968L;

    @ApiModelProperty(value = "经销商总数")
    private Integer distributorTotal;

    @ApiModelProperty(value = "代理商总数")
    private Integer agentTotal;

    @ApiModelProperty(value = "用户总数")
    private Integer userTotal;

    @ApiModelProperty(value = "经销商总数(7天)")
    private Integer distributorTotalOfWeek;

    @ApiModelProperty(value = "代理商总数(7天)")
    private Integer agentTotalOfWeek;

    @ApiModelProperty(value = "用户总数(7天)")
    private Integer userTotalOfWeek;

    @ApiModelProperty(value = "经销商总数(1个月)")
    private Integer distributorTotalOfMonth;

    @ApiModelProperty(value = "代理商总数(1个月)")
    private Integer agentTotalOfMonth;

    @ApiModelProperty(value = "用户总数(1个月)")
    private Integer userTotalOfMonth;

    @ApiModelProperty(value = "经销商总数(3个月)")
    private Integer distributorTotalOf3Month;

    @ApiModelProperty(value = "代理商总数(3个月)")
    private Integer agentTotalOf3Month;

    @ApiModelProperty(value = "用户总数(3个月)")
    private Integer userTotalOf3Month;

    @ApiModelProperty(value = "财务审核订单总数")
    private Integer financialCount;

    @ApiModelProperty(value = "留商审核订单总数")
    private Integer enterpriseCount;

    @ApiModelProperty(value = "法务审核订单总数")
    private Integer protocolCount;
}
