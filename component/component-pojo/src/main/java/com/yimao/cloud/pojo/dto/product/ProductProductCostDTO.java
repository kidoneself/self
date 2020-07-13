
package com.yimao.cloud.pojo.dto.product;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 产品-产品计费方式关联关系
 *
 * @auther: liu.lin
 * @date: 2018/11/29
 */
@Getter
@Setter
public class ProductProductCostDTO {

    private Integer productId;   //产品ID
    private Integer costId;      //计费方式ID

}
