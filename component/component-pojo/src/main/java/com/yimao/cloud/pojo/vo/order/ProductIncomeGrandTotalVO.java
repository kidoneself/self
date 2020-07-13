package com.yimao.cloud.pojo.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 产品收益记录
 *
 * @author Liu Yi
 * @date 2019-1-29
 */
@Data
@ApiModel(description = "产品收益汇总（经销商app）")
public class ProductIncomeGrandTotalVO implements Serializable {
    private static final long serialVersionUID = -6047391967687534960L;
    @ApiModelProperty(value = "收益汇总")
    private BigDecimal total;
    @ApiModelProperty(value = "未完成订单收益汇总")
    private BigDecimal unfinishedTotal;
    @ApiModelProperty(value = "已完成订单收益汇总")
    private BigDecimal finishTotal;
    @ApiModelProperty(value = "退单单收益汇总")
    private BigDecimal returnTotal;
    @ApiModelProperty(value = "昨日订单收益汇总")
    private BigDecimal yesterdayTotal;
    @ApiModelProperty(value = "本月订单收益汇总")
    private BigDecimal currentMonthTotal;
    @ApiModelProperty(value = "今日预估收益汇总")
    private BigDecimal todayTotal;
}