package com.yimao.cloud.pojo.vo.station;

import com.yimao.cloud.pojo.dto.station.ProductSalesStatusDTO;
import com.yimao.cloud.pojo.dto.station.ProductTabulateDataDTO;
import com.yimao.cloud.pojo.dto.station.ProductTwoCategoryPicResDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 站务系统-商品统计-商品销售情况 以及二级分类图标 返回类
 *
 * @author liu long jie
 */
@Data
@ApiModel(description = "商品统计-商品销售情况 以及二级分类图标 返回类")
public class ProductSalesStatusAndTwoCategoryPicResVO {

    //---- 商品销售情况 -----
    @ApiModelProperty(position = 1, required = true, value = "商品销售情况")
    List<ProductSalesStatusDTO> productSalesStatus;
    //---- 二级分类图表 -----
    @ApiModelProperty(position = 2, required = true, value = "二级分类图表")
    List<ProductTwoCategoryPicResDTO> productTwoCategoryPicRes;


}
