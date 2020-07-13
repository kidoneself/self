package com.yimao.cloud.order.po;

import com.yimao.cloud.pojo.dto.order.OrderMainDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 主订单（交易单）
 *
 * @author Zhang Bo
 * @date 2018/11/7.
 */
@Table(name = "order_main")
@Getter
@Setter
public class OrderMain {

    @Id
    private Long id;//主订单号/交易单号
    private Integer type;//订单类型：1-普通订单  2-续费订单
    private Integer subType;//子订单类型：1-为自己下单；2-为客户下单
    private Integer userId;//用户id
    //private BigDecimal productTotalFee;//商品总价productAmountFee
    private BigDecimal productAmountFee;//商品总价
    //private BigDecimal orderTotalFee;//订单总金额
    private BigDecimal orderAmountFee;//订单总金额
    private Integer count;//订单购买数量
    private BigDecimal logisticsFee;//订单运费
    private Integer payType;//支付类型：1-微信；2-支付宝；3-POS机；4-转账；
    private Boolean pay;//是否支付
    private Date payTime;//支付时间
    private String tradeNo;//订单支付单号，第三方支付流水号
    private String payCredential;//线下支付凭证
    private Date payCredentialSubmitTime;//支付凭证提交时间
    private Date createTime;//创建时间
    private Date updateTime;//更新时间
    private Boolean isReplacePay;// 是否他人代付：0、否；1、是
    private Boolean deleted;//是否删除

    public OrderMain() {
    }

    /**
     * 用业务对象OrderMainDTO初始化数据库对象OrderMain。
     *
     * @param dto 业务对象
     */
    public OrderMain(OrderMainDTO dto) {
        this.id = dto.getId();
        this.type = dto.getType();
        this.subType = dto.getSubType();
        this.userId = dto.getUserId();
        this.productAmountFee = dto.getProductAmountFee();
        this.orderAmountFee = dto.getOrderAmountFee();
        this.count = dto.getCount();
        this.logisticsFee = dto.getLogisticsFee();
        this.payType = dto.getPayType();
        this.pay = dto.getPay();
        this.payTime = dto.getPayTime();
        this.tradeNo = dto.getTradeNo();
        this.payCredential = dto.getPayCredential();
        this.payCredentialSubmitTime = dto.getPayCredentialSubmitTime();
        this.createTime = dto.getCreateTime();
        this.updateTime = dto.getUpdateTime();
        this.isReplacePay = dto.getIsReplacePay();
        this.deleted = dto.getDeleted();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象OrderMainDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(OrderMainDTO dto) {
        dto.setId(this.id);
        dto.setType(this.type);
        dto.setSubType(this.subType);
        dto.setUserId(this.userId);
        dto.setProductAmountFee(this.productAmountFee);
        dto.setOrderAmountFee(this.orderAmountFee);
        dto.setCount(this.count);
        dto.setLogisticsFee(this.logisticsFee);
        dto.setPayType(this.payType);
        dto.setPay(this.pay);
        dto.setPayTime(this.payTime);
        dto.setTradeNo(this.tradeNo);
        dto.setPayCredential(this.payCredential);
        dto.setPayCredentialSubmitTime(this.payCredentialSubmitTime);
        dto.setCreateTime(this.createTime);
        dto.setUpdateTime(this.updateTime);
        dto.setIsReplacePay(this.isReplacePay);
        dto.setDeleted(this.deleted);
    }
}