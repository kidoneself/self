package com.yimao.cloud.product.po;

import com.yimao.cloud.pojo.dto.product.ProductDistributorDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 产品-经销商关联表（目前只有折机会关联经销商）
 *
 * @author Liu Yi
 * @date 2019/8/5.
 */
@Table(name = "product__distributor")
@Getter
@Setter
public class ProductDistributor {

    
    private Integer productId;//产品id
   
    private Integer distributorId;//经销商id

    public ProductDistributor() {}

    /**
     * 用业务对象ProductDistributorDTO初始化数据库对象ProductDistributor。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public ProductDistributor(ProductDistributorDTO dto) {
        this.productId = dto.getProductId();
        this.distributorId = dto.getDistributorId();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象ProductDistributorDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(ProductDistributorDTO dto) {
        dto.setProductId(this.productId);
        dto.setDistributorId(this.distributorId);
    }
}