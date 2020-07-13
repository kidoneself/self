package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 退款查询信息
 *
 * @author hhf
 * @date 2019/3/12
 */
@Data
@ApiModel(description = "退款查询信息")
public class AfterSalesOrderQueryDTO implements Serializable {

    private static final long serialVersionUID = -6441542092877816304L;

    @ApiModelProperty(value = "售后单号")
    private Long id;

    @ApiModelProperty(value = "订单号")
    private Long orderId;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "用户手机号")
    private Long phone;

    @ApiModelProperty(value = "产品公司")
    private Long productCompany;

    @ApiModelProperty(value = "申请开始时间")
    private Long applyBeginTime;

    @ApiModelProperty(value = "申请结束时间")
    private Long applyEndTime;

    @ApiModelProperty(value = "订单来源")
    private Long orderResource;

    @ApiModelProperty(value = "支付方式")
    private Long payType;

    @ApiModelProperty(value = "1-线上 2-线下")
    private Integer terminal;
}
