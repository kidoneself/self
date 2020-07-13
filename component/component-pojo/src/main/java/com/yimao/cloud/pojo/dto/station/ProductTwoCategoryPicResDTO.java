package com.yimao.cloud.pojo.dto.station;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 站务系统-商品统计-二级分类图标
 *
 * @author liu long jie
 */
@Data
@ApiModel(description = "商品统计-二级分类图表")
public class ProductTwoCategoryPicResDTO {

    //---- 二级分类图表 -----
    @ApiModelProperty(position = 1, required = true, value = "分类名称")
    private String categoryName;
    @ApiModelProperty(position = 2, required = true, value = "销售额")
    private BigDecimal sale;

}
