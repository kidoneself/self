package com.yimao.cloud.product.po;

import com.yimao.cloud.pojo.dto.product.ProductDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Zhang Bo
 * @date 2018/11/7.
 */
@Table(name = "product")
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;                         //产品id
    private String name;                        //产品名称
    private String code;                        //产品CODE值
    private Integer mode;                       //产品模式：1-实物；2-虚拟；3-租赁；
    private Integer categoryId;                 //产品后台三级类目ID
    private String categoryName;                //产品后台三级类目名称
    private Integer companyId;                  //产品公司ID
    private String companyName;                 //产品公司名称
    private String supplyCode;                  //产品供应类型：PJXCP-经销产品；PZZZG-站长专供；PTPSJ-特批水机；PTGCP-特供产品；
    private Boolean newProduct;                 //是否是新产品：1-是；0-否；
    private String img;                         //产品轮播图
    private String coverImg;                    //产品封面图
    private String coverVideo;                  //产品封面视频
    private String detailImg;                   //产品详情图
    private BigDecimal price;                   //产品价格
    private BigDecimal marketPrice;             //市场价格
    private Integer stock;                      //库存
    private Integer status;                     //产品状态：1-未上架；2-已上架；3-已下架；4-已删除；
    private Integer hot;                        //是否为热销
    private Integer onShelfType;                //上架类型：1-立即上架；2-自定义上架时间；3-暂不上架；
    private Date willOnShelfTime;               //自定义上架时间
    private Date onShelfTime;                   //上架时间
    private Date offShelfTime;                  //下架时间
    private BigDecimal logisticsFee;            //运费
    private String description;                 //产品描述
    private Integer sorts;                      //排序字段
    private String buyPermission;               //产品购买人群：逗号分隔
    private Integer minMoq;                     //最小起订量
    private Integer maxMoq;                     //最大限购数
    private Integer transportType;              //运输方式（运费模板）：1-包邮；2-货到付款；
    private Integer takeType;                   //提货方式：1-送货上门；2-到店自提；
    // private Integer incomeType;              //收益模板, 电子卡券多个,隔开
    private Integer activityType;               //活动类型：1-普通产品 2-折机产品 3-180产品；5-限时抢购
    private Integer saleCount;                  //售出量
    private String textDetail;
    private String creator;                     //创建人
    private Date createTime;                    //创建时间
    private String updater;                     //更新人
    private Date updateTime;                    //更新时间
    private String oldId;                       //老的产品id
    private String oldCategoryId;               //老的产品类型id

    public Product() {
    }

    /**
     * 用业务对象ProductDTO初始化数据库对象Product。
     *
     * @param dto 业务对象
     */
    public Product(ProductDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.code = dto.getCode();
        this.mode = dto.getMode();
        this.categoryId = dto.getCategoryId();
        this.categoryName = dto.getCategoryName();
        this.companyId = dto.getCompanyId();
        this.companyName = dto.getCompanyName();
        this.supplyCode = dto.getSupplyCode();
        this.newProduct = dto.getNewProduct();
        this.img = dto.getImg();
        this.coverImg = dto.getCoverImg();
        this.coverVideo = dto.getCoverVideo();
        this.detailImg = dto.getDetailImg();
        this.price = dto.getPrice();
        this.marketPrice = dto.getMarketPrice();
        this.stock = dto.getStock();
        this.status = dto.getStatus();
        this.hot = dto.getHot();
        this.onShelfType = dto.getOnShelfType();
        this.willOnShelfTime = dto.getWillOnShelfTime();
        this.onShelfTime = dto.getOnShelfTime();
        this.offShelfTime = dto.getOffShelfTime();
        this.logisticsFee = dto.getLogisticsFee();
        this.description = dto.getDescription();
        this.sorts = dto.getSorts();
        this.buyPermission = dto.getBuyPermission();
        this.minMoq = dto.getMinMoq();
        this.maxMoq = dto.getMaxMoq();
        this.transportType = dto.getTransportType();
        this.takeType = dto.getTakeType();
        this.activityType = dto.getActivityType();
        this.saleCount = dto.getSaleCount();
        this.textDetail = dto.getTextDetail();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
        this.oldId = dto.getOldId();
        this.oldCategoryId = dto.getOldCategoryId();

    }

    /**
     * 将数据库实体对象信息拷贝到业务对象ProductDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(ProductDTO dto) {
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setCode(this.code);
        dto.setMode(this.mode);
        dto.setCategoryId(this.categoryId);
        dto.setCategoryName(this.categoryName);
        dto.setCompanyId(this.companyId);
        dto.setCompanyName(this.companyName);
        dto.setSupplyCode(this.supplyCode);
        dto.setNewProduct(this.newProduct);
        dto.setImg(this.img);
        dto.setCoverImg(this.coverImg);
        dto.setCoverVideo(this.coverVideo);
        dto.setDetailImg(this.detailImg);
        dto.setPrice(this.price);
        dto.setMarketPrice(this.marketPrice);
        dto.setStock(this.stock);
        dto.setStatus(this.status);
        dto.setHot(this.hot);
        dto.setOnShelfType(this.onShelfType);
        dto.setWillOnShelfTime(this.willOnShelfTime);
        dto.setOnShelfTime(this.onShelfTime);
        dto.setOffShelfTime(this.offShelfTime);
        dto.setLogisticsFee(this.logisticsFee);
        dto.setDescription(this.description);
        dto.setSorts(this.sorts);
        dto.setBuyPermission(this.buyPermission);
        dto.setMinMoq(this.minMoq);
        dto.setMaxMoq(this.maxMoq);
        dto.setTransportType(this.transportType);
        dto.setTakeType(this.takeType);
        dto.setActivityType(this.activityType);
        dto.setSaleCount(this.saleCount);
        dto.setTextDetail(this.textDetail);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
        dto.setOldId(this.oldId);
        dto.setOldCategoryId(this.oldCategoryId);
    }
}
