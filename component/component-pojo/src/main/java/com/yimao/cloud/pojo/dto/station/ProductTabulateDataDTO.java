package com.yimao.cloud.pojo.dto.station;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 站务系统-商品统计-数据汇总
 *
 * @author liu long jie
 */
@Data
@ApiModel(description = "商品统计-数据汇总")
public class ProductTabulateDataDTO {

    //---- 汇总数据 -----
    @ApiModelProperty(position = 1, required = true, value = "分类名称")
    private String categoryName;
    @ApiModelProperty(position = 2, required = true, value = "销售数量")
    private Integer salesQuantity;
    @ApiModelProperty(position = 3, required = true, value = "销售比例")
    private String salesProportion;
    @ApiModelProperty(position = 4, required = true, value = "销售额")
    private BigDecimal sale;
    @ApiModelProperty(position = 5, required = true, value = "销售金额比例")
    private String saleProportion;

}
