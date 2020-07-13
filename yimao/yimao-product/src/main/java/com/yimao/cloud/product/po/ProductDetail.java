package com.yimao.cloud.product.po;

import com.yimao.cloud.pojo.dto.product.ProductDetailDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @auther: liu.lin
 * @date: 2018/11/29
 */
@Table(name = "product_detail")
@Getter
@Setter
public class ProductDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer productId;
    private String text;
    private Date createTime;
    private Date updateTime;

    public ProductDetail() {
    }

    /**
     * 用业务对象ProductDetailDTO初始化数据库对象ProductDetail。
     *
     * @param dto 业务对象
     */
    public ProductDetail(ProductDetailDTO dto) {
        this.productId = dto.getProductId();
        this.text = dto.getText();
        this.createTime = dto.getCreateTime();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象ProductDetailDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(ProductDetailDTO dto) {
        dto.setProductId(this.productId);
        dto.setText(this.text);
        dto.setCreateTime(this.createTime);
        dto.setUpdateTime(this.updateTime);
    }
}
