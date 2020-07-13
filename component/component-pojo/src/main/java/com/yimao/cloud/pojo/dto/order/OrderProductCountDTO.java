package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 客户订单-每个产品数量
 *
 * @author liuhao
 */
@ApiModel(description = "主订单DTO")
@Data
public class OrderProductCountDTO {

    @ApiModelProperty(value = "产品类型ID")
    private Integer productCategoryId;
    @ApiModelProperty(value = "产品类型名称")
    private String productCategoryName;
    @ApiModelProperty(value = "订单数量DTO")
    private List<OrderStatusCountDTO> orderList;    //订单数量


}
