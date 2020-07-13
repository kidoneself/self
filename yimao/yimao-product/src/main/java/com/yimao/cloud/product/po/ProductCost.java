package com.yimao.cloud.product.po;

import com.yimao.cloud.pojo.dto.product.ProductCostDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 水机计费方式模版
 *
 * @author Liu Yi
 * @date 2018/11/29.
 */
@Table(name = "product_cost")
@Getter
@Setter
public class ProductCost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;                   //计费方式名称
    private Integer type;                  //计费方式：1-流量计费；2-时长计费；
    private Integer value;                 //套餐流量或者套餐天数
    private BigDecimal unitPrice;          //单价（流量单位为升；包年单位为天）
    private BigDecimal rentalFee;          //租凭费
    private BigDecimal installationFee;    //安装费（开户费）
    private BigDecimal totalFee;           //商品总价格
    private Integer productCategoryId;     //产品二级类目ID（后台类目）
    private Integer threshold1;            //阀值1
    private Integer threshold2;            //阀值2
    private Integer threshold3;            //阀值3

    private Boolean deleted;               //删除标识：0-未删除；1-已删除；
    private Integer sorts;                 //排序

    private String creator;                //创建人
    private Date createTime;               //创建时间
    private String updater;                //更新人
    private Date updateTime;               //更新时间
    private String oldId;                  //老的计费方式id
    private Integer modelType;             //模板类型：1-首年 2-续费
    private Integer incomeRuleId;          //收益模板名id（续费才有）
    private String incomeRuleName;         //收益模板名称（续费才有）

    private String renewRemark;            //续费套餐对应的计费方式描述（PAD端续费展示用到）

    public ProductCost() {
    }

    /**
     * 用业务对象ProductCostDTO初始化数据库对象ProductCost。
     *
     * @param dto 业务对象
     */
    public ProductCost(ProductCostDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.type = dto.getType();
        this.value = dto.getValue();
        this.unitPrice = dto.getUnitPrice();
        this.rentalFee = dto.getRentalFee();
        this.installationFee = dto.getInstallationFee();
        this.totalFee = dto.getTotalFee();
        this.productCategoryId = dto.getProductCategoryId();
        this.threshold1 = dto.getThreshold1();
        this.threshold2 = dto.getThreshold2();
        this.threshold3 = dto.getThreshold3();
        this.deleted = dto.getDeleted();
        this.sorts = dto.getSorts();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
        this.oldId = dto.getOldId();
        this.modelType = dto.getModelType();
        this.incomeRuleId = dto.getIncomeRuleId();
        this.incomeRuleName = dto.getIncomeRuleName();
        this.renewRemark = dto.getRenewRemark();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象ProductCostDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(ProductCostDTO dto) {
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setType(this.type);
        dto.setValue(this.value);
        dto.setUnitPrice(this.unitPrice);
        dto.setRentalFee(this.rentalFee);
        dto.setInstallationFee(this.installationFee);
        dto.setTotalFee(this.totalFee);
        dto.setProductCategoryId(this.productCategoryId);
        dto.setThreshold1(this.threshold1);
        dto.setThreshold2(this.threshold2);
        dto.setThreshold3(this.threshold3);
        dto.setDeleted(this.deleted);
        dto.setSorts(this.sorts);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
        dto.setOldId(this.oldId);
        dto.setModelType(this.modelType);
        dto.setIncomeRuleId(this.incomeRuleId);
        dto.setIncomeRuleName(this.incomeRuleName);
        dto.setRenewRemark(this.getRenewRemark());
    }
}