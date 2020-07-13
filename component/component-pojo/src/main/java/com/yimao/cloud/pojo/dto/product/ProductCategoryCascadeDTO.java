package com.yimao.cloud.pojo.dto.product;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhilin.he
 * @description 产品类目级联信息
 * @date 2019/5/24 14:07
 **/
@Getter
@Setter
public class ProductCategoryCascadeDTO {

    private Integer productCategoryFirstId;       //产品一级类目id
    private Integer productCategoryTwoId;         //产品二级类目id
    private Integer productCategoryId;            //产品三级类目id
    private String productCategoryFirstName;      //产品一级类目名称
    private String productCategoryTwoName;        //产品二级类目名称
    private String productCategoryName;           //产品三级类目名称
    private String oldProductCategoryFirstId;     //老的产品一级类目id
    private String oldProductCategoryTwoId;       //老的产品二级类目id
    private String oldProductCategoryId;          //老的产品三级类目id

}
