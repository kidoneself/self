package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 收益记录详情（返回结果通用）
 *
 * @author hhf
 * @date 2019/2/26
 */
@Data
@ApiModel(description = "收益记录详情（返回结果）")
public class IncomeRecordResultDTO {

    @ApiModelProperty(value = "主订单号")
    private String mainOrderId;

    @ApiModelProperty(value = "子订单号")
    private String orderId;

    @ApiModelProperty(value = "续费单号")
    private String renewOrderId;

    @ApiModelProperty(value = "商品合计")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "运费")
    private BigDecimal logisticsFee;

    @ApiModelProperty(value = "活动优惠")
    private BigDecimal activityFee;

    @ApiModelProperty(value = "实付款金额")
    private BigDecimal realFee;

    @ApiModelProperty(value = "可分配金额")
    private BigDecimal allocationMoney;

    @ApiModelProperty(value = "服务站公司名称")
    private String stationName;

    @ApiModelProperty(value = "计费模板")
    private String costName;

    @ApiModelProperty(value = "收益类型：1-产品收益，2-续费收益，3-服务收益，4-招商收益")
    private Integer incomeType;

    @ApiModelProperty(value = "收益分配")
    List<IncomeRecordPartResultDTO> incomePartResults;
}
