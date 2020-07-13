package com.yimao.cloud.pojo.dto.station;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 站务系统-商品统计-商品销售情况
 *
 * @author liu long jie
 */
@Data
@ApiModel(description = "商品统计-商品销售情况")
public class ProductSalesStatusDTO {

    //---- 商品销售情况 -----
    @ApiModelProperty(position = 1, required = true, value = "商品名称")
    private String productName;
    @ApiModelProperty(position = 2, required = true, value = "销售数量")
    private Integer salesQuantity;
    @ApiModelProperty(position = 3, required = true, value = "销售额")
    private BigDecimal sale;

}
