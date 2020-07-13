package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 描述：申请退款请求参数
 *
 * @Author Zhang Bo
 * @Date 2019/10/12
 */
@ApiModel(description = "申请退款请求参数")
@Getter
@Setter
public class RefundRequest {

    @ApiModelProperty(position = 1, value = "商户订单号")
    private String outTradeNo;
    @ApiModelProperty(position = 2, value = "商户退款单号")
    private String outRefundNo;
    @ApiModelProperty(position = 3, value = "订单金额")
    private BigDecimal totalFee;
    @ApiModelProperty(position = 4, value = "退款金额")
    private BigDecimal refundFee;
    @ApiModelProperty(position = 5, value = "退款原因")
    private String refundReason;

    @ApiModelProperty(position = 9, value = "支付流水号")
    private String payTradeNo;
}
