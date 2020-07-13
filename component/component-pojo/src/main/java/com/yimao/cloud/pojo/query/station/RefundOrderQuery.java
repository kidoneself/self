package com.yimao.cloud.pojo.query.station;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Liu long jie
 * @description 退款订单查询条件
 * @date 2019-12-26
 **/
@Getter
@Setter
public class RefundOrderQuery extends BaseQuery {

	@ApiModelProperty(value = "售后单号")
    private String salesId;

    @ApiModelProperty(value = "子订单号")
    private String orderId;
    
    @ApiModelProperty(value = "订单来源")
    private String terminal;
    
    @ApiModelProperty(value = "售后状态")
    private String status;
    
    @ApiModelProperty(value = "售后申请开始时间")
    private String startTime;

    @ApiModelProperty(value = "售后申请结束时间")
    private String endTime;
    
    @ApiModelProperty(value = "商品类型")
    private Integer productType;

}
