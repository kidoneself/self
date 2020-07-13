package com.yimao.cloud.pojo.dto.product;

import java.io.Serializable;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 产品-产品前台类目关联关系
 *
 * @auther: liu.lin
 * @date: 2019/1/4
 */
@Getter
@Setter
public class ProductProductFrontCategoryDTO {

    private Integer productId;
    private Integer frontCategoryId;

}
