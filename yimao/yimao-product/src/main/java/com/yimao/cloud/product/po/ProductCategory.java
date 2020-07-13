package com.yimao.cloud.product.po;

import com.yimao.cloud.pojo.dto.product.ProductCategoryDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 产品类目。
 *
 * @author Zhang Bo
 * @date 2018/11/7.
 */
@Table(name = "product_category")
@Getter
@Setter
public class ProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;                    //产品类目名称
    private String code;                    //产品类目code
    private String icon;                    //产品类目图标
    private Integer type;                   //前台类目还是后台类目：1-后台类目；2-前台类目；
    private Integer terminal;               //终端：1-健康e家公众号；3-翼猫APP
    private Integer pid;                    //父类目id，当id=0时说明是根节点，一级类目
    private Integer level;                  //产品类目等级：1-一级；2-二级；3-三级；
    private Integer companyId;              //产品公司ID
    private String companyName;             //产品公司名称
    private Integer minMoq;                 //最小起订量
    private Integer maxMoq;                 //最大限订量
    private Integer storeGoodsId;           //对应库存表中物品id
    private String description;             //描述
    private Integer sorts;                  //排序
    private Boolean deleted;                //是否删除：0-未删除；1-已删除

    private String creator;
    private Date createTime;
    private String updater;
    private Date updateTime;
    private String oldId;                       //老的产品类目id

    public ProductCategory() {
    }

    /**
     * 用业务对象ProductCategoryDTO初始化数据库对象ProductCategory。
     *
     * @param dto 业务对象
     */
    public ProductCategory(ProductCategoryDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.code = dto.getCode();
        this.icon = dto.getIcon();
        this.type = dto.getType();
        this.terminal = dto.getTerminal();
        this.pid = dto.getPid();
        this.level = dto.getLevel();
        this.companyId = dto.getCompanyId();
        this.companyName = dto.getCompanyName();
        this.minMoq = dto.getMinMoq();
        this.maxMoq = dto.getMaxMoq();
        this.description = dto.getDescription();
        this.sorts = dto.getSorts();
        this.deleted = dto.getDeleted();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
        this.oldId = dto.getOldId();
        this.storeGoodsId = dto.getStoreGoodsId();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象ProductCategoryDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(ProductCategoryDTO dto) {
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setCode(this.code);
        dto.setIcon(this.icon);
        dto.setType(this.type);
        dto.setTerminal(this.terminal);
        dto.setPid(this.pid);
        dto.setLevel(this.level);
        dto.setCompanyId(this.companyId);
        dto.setCompanyName(this.companyName);
        dto.setMinMoq(this.minMoq);
        dto.setMaxMoq(this.maxMoq);
        dto.setDescription(this.description);
        dto.setSorts(this.sorts);
        dto.setDeleted(this.deleted);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
        dto.setOldId(this.oldId);
        dto.setStoreGoodsId(this.storeGoodsId);
    }
}