package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "主订单DTO")
@Data
public class OrderStatusCountDTO {

    @ApiModelProperty(value = "订单状态")
    private Integer status;
    @ApiModelProperty(value = "订单数量")
    private Integer count;
}
