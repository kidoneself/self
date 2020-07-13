package com.yimao.cloud.product.po;

import com.yimao.cloud.pojo.dto.product.ProductProductFrontCategoryDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 产品-产品前台类目关联关系
 *
 * @author Zhang Bo
 * @date 2018/11/7.
 */
@Table(name = "product__product_front_category")
@Getter
@Setter
public class ProductProductFrontCategory {

    @Id
    private Integer productId;
    @Id
    private Integer frontCategoryId;

    public ProductProductFrontCategory() {
    }

    /**
     * 用业务对象ProductProductFrontCategoryDTO初始化数据库对象ProductProductFrontCategory。
     *
     * @param dto 业务对象
     */
    public ProductProductFrontCategory(ProductProductFrontCategoryDTO dto) {
        this.productId = dto.getProductId();
        this.frontCategoryId = dto.getFrontCategoryId();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象ProductProductFrontCategoryDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(ProductProductFrontCategoryDTO dto) {
        dto.setProductId(this.productId);
        dto.setFrontCategoryId(this.frontCategoryId);
    }
}
