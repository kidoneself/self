package com.yimao.cloud.product.po;

import com.yimao.cloud.pojo.dto.product.ProductPropertyValueDTO;
import java.math.BigDecimal;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 产品的属性值
 *
 * @author Liu Yi
 * @date 2018/11/29.
 */
@Table(name = "product_property_value")
@Data
public class ProductPropertyValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;//属性id
    private Integer propertyId;//属性ID
    private String propertyValue;//属性值
    private String creator;//创建人
    private Date createTime;//创建时间
    private String updater;//更新人
    private Date updateTime;//更新时间


    public ProductPropertyValue() {
    }

    /**
     * 用业务对象ProductPropertyValueDTO初始化数据库对象ProductPropertyValue。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public ProductPropertyValue(ProductPropertyValueDTO dto) {
        this.id = dto.getId();
        this.propertyId = dto.getPropertyId();
        this.propertyValue = dto.getPropertyValue();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象ProductPropertyValueDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(ProductPropertyValueDTO dto) {
        dto.setId(this.id);
        dto.setPropertyId(this.propertyId);
        dto.setPropertyValue(this.propertyValue);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
    }
}