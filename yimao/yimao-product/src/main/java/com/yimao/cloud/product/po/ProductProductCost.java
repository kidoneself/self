package com.yimao.cloud.product.po;

import com.yimao.cloud.pojo.dto.product.ProductProductCostDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 产品-产品计费方式关联表
 *
 * @author Zhang Bo
 * @date 2018/11/7.
 */
@Table(name = "product__product_cost")
@Getter
@Setter
public class ProductProductCost {

    @Id
    private Integer productId;//产品id
    @Id
    private Integer costId;//价格模板id

    public ProductProductCost() {
    }

    /**
     * 用业务对象ProductCostDTO初始化数据库对象ProductCost。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public ProductProductCost(ProductProductCostDTO dto) {
        this.productId = dto.getProductId();
        this.costId = dto.getCostId();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象ProductCostDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(ProductProductCostDTO dto) {
        dto.setProductId(this.productId);
        dto.setCostId(this.costId);
    }
}