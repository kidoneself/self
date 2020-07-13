package com.yimao.cloud.product.po;

import com.yimao.cloud.pojo.dto.product.ProductPropertyDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 产品属性
 *
 * @author Liu Yi
 * @date 2018/11/29.
 */
@Table(name = "product_property")
@Getter
@Setter
public class ProductProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;                   //属性id
    private String name;                  //属性名称
    private Integer typeId;               //产品类型
    private Integer sorts;                //排序

    private String creator;               //创建人
    private Date createTime;              //创建时间
    private String updater;               //更新人
    private Date updateTime;              //更新时间
    private Boolean deleted;              //删除标识：0-未删除；1-已删除

    public ProductProperty() {
    }

    /**
     * 用业务对象ProductPropertyDTO初始化数据库对象ProductProperty。
     *
     * @param dto 业务对象
     */
    public ProductProperty(ProductPropertyDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.typeId = dto.getTypeId();
        this.sorts = dto.getSorts();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
        this.deleted = dto.getDeleted();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象ProductPropertyDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(ProductPropertyDTO dto) {
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setTypeId(this.typeId);
        dto.setSorts(this.sorts);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
        dto.setDeleted(this.deleted);
    }
}