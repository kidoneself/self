
package com.yimao.cloud.pojo.dto.product;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;

/**
 * 产品-经销商关联表（目前只有折机会关联经销商）
 *
 * @author Liu Yi
 * @date 2019/8/5.
 */
@Getter
@Setter
public class ProductDistributorDTO {
    private Integer productId;//产品id
    private Integer distributorId;//经销商id
    private String distributorUserName;//经销商姓名
}
