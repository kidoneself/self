package com.yimao.cloud.pojo.dto.order;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @description: 产品销售实体类
 * @author: yu chunlei
 * @create: 2019-08-29 11:53:34
 **/
@Data
public class SalesProductDTO {

    private Integer productOneCategoryId;
    private String productOneCategoryName;
    private BigDecimal totalAmount;

}
