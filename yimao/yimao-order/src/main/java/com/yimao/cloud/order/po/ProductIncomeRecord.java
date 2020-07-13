package com.yimao.cloud.order.po;

import com.yimao.cloud.pojo.dto.order.ProductIncomeRecordDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 产品收益记录
 *
 * @author Liu Yi
 * @date 2019-1-17
 */
@Table(name = "product_income_record")
@Getter
@Setter
public class ProductIncomeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Long mainOrderId;               //主订单id（商户交易单号）
    private String orderId;                 //子订单号
    private String renewOrderId;//续费单号
    private BigDecimal orderFee;            //订单金额

    private Integer productId;              //产品ID
    private String productName;             //产品名称
    private Integer productCompanyId;       //产品公司ID
    private String productCompanyName;      //产品公司名称
    private Integer productCategoryId;      //产品三级类目ID
    private String productCategoryName;     //产品三级类目名称
    private Integer productOneCategoryId;      //产品一级类目ID
    private String productOneCategoryName;     //产品一级类目名称
    private Integer productTwoCategoryId;      //产品二级类目ID
    private String productTwoCategoryName;     //产品二级类目名称

    private Integer userId;                 //用户id
    private Integer userType;               //用户类型
    private String userTypeName;            //用户类型名称
    private String userName;                //用户名称
    private String userPhone;               //用户手机号

    private Integer distributorId;          //经销商id
    private String distributorName;         //经销商姓名
    private String distributorTypeName;     //经销商类型名称
    private String distributorAccount;      //经销商账号
    private String distributorProvince;     //经销商省
    private String distributorCity;         //经销商市
    private String distributorRegion;       //经销商区县

    private Integer incomeRuleId;           //收益规则id
    private Integer incomeType;             //收益类型：1-产品收益，2-续费收益，3-服务收益，4-招商收益
    private Integer allotType;              //分配规则：1-按比例分配 2-按金额分配
    private BigDecimal settlementFee;       //结算金额
    private Integer settlementAmount;       //产品可结算数量
    private Integer status;                 //收益结算状态：0-不可结算（订单未完成）；1-可结算（订单已完成）；2-已结算；3-已失效(退单) 4-暂停结算；
    private Date orderCompleteTime;         //订单完成时间
    private String settlementMonth;         //结算月份
    private String engineerSettlementMonth; //安装工收益结算月份

    private Date createTime;                //创建时间
    private Date updateTime;                //更新时间


    public ProductIncomeRecord() {
    }

    /**
     * 用业务对象ProductIncomeRecordDTO初始化数据库对象ProductIncomeRecord。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public ProductIncomeRecord(ProductIncomeRecordDTO dto) {
        this.id = dto.getId();
        this.mainOrderId = dto.getMainOrderId();
        this.orderId = dto.getOrderId();
        this.orderFee = dto.getOrderFee();
        this.productId = dto.getProductId();
        this.productName = dto.getProductName();
        this.productCompanyId = dto.getProductCompanyId();
        this.productCompanyName = dto.getProductCompanyName();
        this.productCategoryId = dto.getProductCategoryId();
        this.productCategoryName = dto.getProductCategoryName();
        this.productOneCategoryId = dto.getProductOneCategoryId();
        this.productOneCategoryName = dto.getProductOneCategoryName();
        this.productTwoCategoryId = dto.getProductTwoCategoryId();
        this.productTwoCategoryName = dto.getProductTwoCategoryName();
        this.userId = dto.getUserId();
        this.userType = dto.getUserType();
        this.userTypeName = dto.getUserTypeName();
        this.userName = dto.getUserName();
        this.userPhone = dto.getUserPhone();
        this.distributorId = dto.getDistributorId();
        this.distributorName = dto.getDistributorName();
        this.distributorTypeName = dto.getDistributorTypeName();
        this.distributorAccount = dto.getDistributorAccount();
        this.distributorProvince = dto.getDistributorProvince();
        this.distributorCity = dto.getDistributorCity();
        this.distributorRegion = dto.getDistributorRegion();
        this.incomeRuleId = dto.getIncomeRuleId();
        this.incomeType = dto.getIncomeType();
        this.allotType = dto.getAllotType();
        this.settlementFee = dto.getSettlementFee();
        this.settlementAmount = dto.getSettlementAmount();
        this.status = dto.getStatus();
        this.orderCompleteTime = dto.getOrderCompleteTime();
        this.settlementMonth = dto.getSettlementMonth();
        this.engineerSettlementMonth = dto.getEngineerSettlementMonth();
        this.createTime = dto.getCreateTime();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象ProductIncomeRecordDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(ProductIncomeRecordDTO dto) {
        dto.setId(this.id);
        dto.setMainOrderId(this.mainOrderId);
        dto.setOrderId(this.orderId);
        dto.setOrderFee(this.orderFee);
        dto.setProductId(this.productId);
        dto.setProductName(this.productName);
        dto.setProductCompanyId(this.productCompanyId);
        dto.setProductCompanyName(this.productCompanyName);
        dto.setProductCategoryId(this.productCategoryId);
        dto.setProductCategoryName(this.productCategoryName);
        dto.setProductOneCategoryId(this.productOneCategoryId);
        dto.setProductOneCategoryName(this.productOneCategoryName);
        dto.setProductTwoCategoryId(this.productTwoCategoryId);
        dto.setProductTwoCategoryName(this.productTwoCategoryName);
        dto.setUserId(this.userId);
        dto.setUserType(this.userType);
        dto.setUserTypeName(this.userTypeName);
        dto.setUserName(this.userName);
        dto.setUserPhone(this.userPhone);
        dto.setDistributorId(this.distributorId);
        dto.setDistributorName(this.distributorName);
        dto.setDistributorTypeName(this.distributorTypeName);
        dto.setDistributorAccount(this.distributorAccount);
        dto.setDistributorProvince(this.distributorProvince);
        dto.setDistributorCity(this.distributorCity);
        dto.setDistributorRegion(this.distributorRegion);
        dto.setIncomeRuleId(this.incomeRuleId);
        dto.setIncomeType(this.incomeType);
        dto.setAllotType(this.allotType);
        dto.setSettlementFee(this.settlementFee);
        dto.setSettlementAmount(this.settlementAmount);
        dto.setStatus(this.status);
        dto.setOrderCompleteTime(this.orderCompleteTime);
        dto.setSettlementAmount(this.settlementAmount);
        dto.setEngineerSettlementMonth(this.engineerSettlementMonth);
        dto.setCreateTime(this.createTime);
        dto.setUpdateTime(this.updateTime);
    }
}