package com.yimao.cloud.product.po;

import com.yimao.cloud.pojo.dto.product.ProductRenewDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 商品续费
 *
 * @author Liu Yi
 * @date 2018/11/29.
 */
@Table(name = "product_renew")
@Data
public class ProductRenew {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;                     //续费商品id
    private String name;                    //续费商品名称
    private Integer categoryId;             //产品分类id
    private Integer costId;                 //计费方式模版id
    private Integer sorts;                  //排序
    private Boolean deleted;                //删除标识

    private String creator;                 //创建人
    private Date createTime;                //创建时间
    private String updater;                 //更新人
    private Date updateTime;                //更新时间

    public ProductRenew() {
    }

    /**
     * 用业务对象ProductRenewDTO初始化数据库对象ProductRenew。
     *
     * @param dto 业务对象
     */
    public ProductRenew(ProductRenewDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.categoryId = dto.getCategoryId();
        this.costId = dto.getCostId();
        this.sorts = dto.getSorts();
        this.deleted = dto.getDeleted();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象ProductRenewDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(ProductRenewDTO dto) {
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setCategoryId(this.categoryId);
        dto.setCostId(this.costId);
        dto.setSorts(this.sorts);
        dto.setDeleted(this.deleted);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
    }
}