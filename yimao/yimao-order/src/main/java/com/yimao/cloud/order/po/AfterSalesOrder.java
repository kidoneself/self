package com.yimao.cloud.order.po;

import com.yimao.cloud.pojo.dto.order.AfterSalesOrderDTO;
import com.yimao.cloud.pojo.dto.order.OrderSalesInfoDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 售后订单
 *
 * @author liuhao@yimaokeji.com
 * @date 2018-12-28
 */
@Data
@Table(name = "after_sales_order")
public class AfterSalesOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;//售后单号
    private Long mainOrderId;//订单号
    private Long orderId;//子订单号
    private Integer status;//售后状态:0-待审核(经销商)  1-待审核(总部) 2-待退货入库  3-待退款(财务)  4-售后失败 5-售后成功；
    private Integer businessAuditStatus;//业务部门审核状态：1-审核通过；2-审核不通过
    private Integer buyAuditStatus;//物质确认审核状态：1-审核通过；2-审核不通过（1-收货确认；2-未确认）
    private Integer financeAuditStatus;//财务复核状态：1-审核通过；2-审核不通过
    private Integer customerServiceAuditStatus;//400客服审核状态或提交物流：1-审核通过；2-审核不通过（1-已提交物流，2-未提交）
    private Integer salesType;//售后类型：1.取消订单退款（未收货），2.申请退货退款（已收货）
    private Integer productType;//商品类型（大类）:1实物商品，2电子卡券，3租赁商品 对应产品:product: type
    private Integer productId;//产品id
    private String productName;//产品名称
    private String productCategoryName;//产品类目名称
    private String productCompanyName;//产品公司名称
    private Integer terminal;//售后发起端
    private Integer num;//数量
    private Integer userId;//用户id
    private Integer userType;//用户身份
    private Integer payType;//支付类型：1-微信；2-支付宝；3-POS机；4-转账；
    private BigDecimal payFee;//支付金额
    private BigDecimal refundFee;//退款金额
    private BigDecimal realRefundFee;//实退金额
    private BigDecimal formalitiesFee;//手续费
    private String refundReason;//退款原因
    private Date refundTime;//退款审核通过时间
    private String refundTradeNo;//企业付款成功，返回的流水号
    private Date accountTime;//退款到账时间
    private String logisticsNo;//物流单号
    private String logisticsCompanyNo;//物流公司编号
    private String logisticsCompanyName;//物流公司名称
    private Date createTime;//申请退单时间
    private Date businessAuditTime;//业务部门审核时间
    private String businessman;//业务部门审核人
    private String businessReason;//业务部门审核原因
    private Date buyAuditTime;//物资审核时间
    private String buyer;//物资审核人
    private String buyAuditReason; //物资审核原因
    private Date financeTime;//财务审核时间
    private String financer;//财务审核人
    private String financeReason;//财务审核原因
    private Date customerServiceTime;//400客服审核或提交物流时间
    private String customerService;//400客服审核人或提交物流人员
    private String customerServiceReason;//400客服审核或提交物流原因
    private Integer deleted;//是否关闭售后：0-未取消，1-取消
    private String remark;//备注
    private String auditee;//审核方:如：经销商、总部

    public AfterSalesOrder() {
    }

    /**
     * 用业务对象AfterSalesOrderDTO初始化数据库对象AfterSalesOrder。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public AfterSalesOrder(AfterSalesOrderDTO dto) {
        this.id = dto.getId();
        this.mainOrderId = dto.getMainOrderId();
        this.orderId = dto.getOrderId();
        this.status = dto.getStatus();
        this.productType = dto.getProductType();
        this.productId = dto.getProductId();
        this.productName = dto.getProductName();
        this.productCategoryName = dto.getProductCategoryName();
        this.productCompanyName = dto.getProductCompanyName();
        this.terminal = dto.getTerminal();
        this.num = dto.getNum();
        this.userId = dto.getUserId();
        this.userType = dto.getUserType();
        this.payType = dto.getPayType();
        this.payFee = dto.getPayFee();
        this.refundFee = dto.getRefundFee();
        this.realRefundFee = dto.getRealRefundFee();
        this.formalitiesFee = dto.getFormalitiesFee();
        this.refundReason = dto.getRefundReason();
        this.refundTime = dto.getRefundTime();
        this.refundTradeNo = dto.getRefundTradeNo();
        this.accountTime = dto.getAccountTime();
        this.logisticsNo = dto.getLogisticsNo();
        this.logisticsCompanyNo = dto.getLogisticsCompanyNo();
        this.logisticsCompanyName = dto.getLogisticsCompanyName();
        //this.receiveTime = dto.getReceiveTime();
        this.createTime = dto.getCreateTime();
        this.buyAuditTime = dto.getBuyAuditTime();
        this.buyer = dto.getBuyer();
        this.financeTime = dto.getFinanceTime();
        this.financer = dto.getFinancer();
        this.remark = dto.getRemark();
        this.auditee = dto.getAuditee();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象AfterSalesOrderDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(AfterSalesOrderDTO dto) {
        dto.setId(this.id);
        dto.setMainOrderId(this.mainOrderId);
        dto.setOrderId(this.orderId);
        dto.setStatus(this.status);
        dto.setProductType(this.productType);
        dto.setProductId(this.productId);
        dto.setProductName(this.productName);
        dto.setProductCategoryName(this.productCategoryName);
        dto.setProductCompanyName(this.productCompanyName);
        dto.setTerminal(this.terminal);
        dto.setNum(this.num);
        dto.setUserId(this.userId);
        dto.setUserType(this.userType);
        dto.setPayType(this.payType);
        dto.setPayFee(this.payFee);
        dto.setRefundFee(this.refundFee);
        dto.setRealRefundFee(this.realRefundFee);
        dto.setFormalitiesFee(this.formalitiesFee);
        dto.setRefundReason(this.refundReason);
        dto.setRefundTime(this.refundTime);
        dto.setRefundTradeNo(this.refundTradeNo);
        dto.setAccountTime(this.accountTime);
        dto.setLogisticsNo(this.logisticsNo);
        dto.setLogisticsCompanyNo(this.logisticsCompanyNo);
        dto.setLogisticsCompanyName(this.logisticsCompanyName);
        //dto.setReceiveTime(this.receiveTime);
        dto.setCreateTime(this.createTime);
        dto.setBuyAuditTime(this.buyAuditTime);
        dto.setBuyer(this.buyer);
        dto.setFinanceTime(this.financeTime);
        dto.setFinancer(this.financer);
        dto.setRemark(this.remark);
        dto.setAuditee(this.auditee);
    }

    /**
     * 用业务对象OrderSalesInfoDTO初始化数据库对象AfterSalesOrder。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public AfterSalesOrder(OrderSalesInfoDTO dto) {
        this.id = dto.getId();
        this.mainOrderId = dto.getMainOrderId();
        this.orderId = dto.getOrderId();
        this.status = dto.getStatus();
        this.businessAuditStatus = dto.getBusinessAuditStatus();
        this.buyAuditStatus = dto.getBuyAuditStatus();
        this.financeAuditStatus = dto.getFinanceAuditStatus();
        this.customerServiceAuditStatus = dto.getCustomerServiceAuditStatus();
        this.salesType = dto.getSalesType();
        this.productType = dto.getProductType();
        this.productId = dto.getProductId();
        this.productName = dto.getProductName();
        this.productCategoryName = dto.getProductCategoryName();
        this.productCompanyName = dto.getProductCompanyName();
        this.terminal = dto.getTerminal();
        this.num = dto.getNum();
        this.userId = dto.getUserId();
        this.userType = dto.getUserType();
        this.payType = dto.getPayType();
        this.payFee = dto.getPayFee();
        this.refundFee = dto.getRefundFee();
        this.realRefundFee = dto.getRealRefundFee();
        this.formalitiesFee = dto.getFormalitiesFee();
        this.refundReason = dto.getRefundReason();
        this.refundTime = dto.getRefundTime();
        this.refundTradeNo = dto.getRefundTradeNo();
        this.accountTime = dto.getAccountTime();
        this.logisticsNo = dto.getLogisticsNo();
        this.logisticsCompanyNo = dto.getLogisticsCompanyNo();
        this.logisticsCompanyName = dto.getLogisticsCompanyName();
        this.createTime = dto.getCreateTime();
        this.businessAuditTime = dto.getBusinessAuditTime();
        this.businessman = dto.getBusinessman();
        this.businessReason = dto.getBusinessReason();
        this.buyAuditTime = dto.getBuyAuditTime();
        this.buyer = dto.getBuyer();
        this.buyAuditReason = dto.getBuyAuditReason();
        this.financeTime = dto.getFinanceTime();
        this.financer = dto.getFinancer();
        this.financeReason = dto.getFinanceReason();
        this.customerServiceTime = dto.getCustomerServiceTime();
        this.customerService = dto.getCustomerService();
        this.customerServiceReason = dto.getCustomerServiceReason();
        this.deleted = dto.getDeleted();
        this.remark = dto.getRemark();
        this.auditee = dto.getAuditee();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象OrderSalesInfoDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(OrderSalesInfoDTO dto) {
        dto.setId(this.id);
        dto.setMainOrderId(this.mainOrderId);
        dto.setOrderId(this.orderId);
        dto.setStatus(this.status);
        dto.setBusinessAuditStatus(this.businessAuditStatus);
        dto.setBuyAuditStatus(this.buyAuditStatus);
        dto.setFinanceAuditStatus(this.financeAuditStatus);
        dto.setCustomerServiceAuditStatus(this.customerServiceAuditStatus);
        dto.setSalesType(this.salesType);
        dto.setProductType(this.productType);
        dto.setProductId(this.productId);
        dto.setProductName(this.productName);
        dto.setProductCategoryName(this.productCategoryName);
        dto.setProductCompanyName(this.productCompanyName);
        dto.setTerminal(this.terminal);
        dto.setNum(this.num);
        dto.setUserId(this.userId);
        dto.setUserType(this.userType);
        dto.setPayType(this.payType);
        dto.setPayFee(this.payFee);
        dto.setRefundFee(this.refundFee);
        dto.setRealRefundFee(this.realRefundFee);
        dto.setFormalitiesFee(this.formalitiesFee);
        dto.setRefundReason(this.refundReason);
        dto.setRefundTime(this.refundTime);
        dto.setRefundTradeNo(this.refundTradeNo);
        dto.setAccountTime(this.accountTime);
        dto.setLogisticsNo(this.logisticsNo);
        dto.setLogisticsCompanyNo(this.logisticsCompanyNo);
        dto.setLogisticsCompanyName(this.logisticsCompanyName);
        dto.setCreateTime(this.createTime);
        dto.setBusinessAuditTime(this.businessAuditTime);
        dto.setBusinessman(this.businessman);
        dto.setBusinessReason(this.businessReason);
        dto.setBuyAuditTime(this.buyAuditTime);
        dto.setBuyer(this.buyer);
        dto.setBuyAuditReason(this.buyAuditReason);
        dto.setFinanceTime(this.financeTime);
        dto.setFinancer(this.financer);
        dto.setFinanceReason(this.financeReason);
        dto.setCustomerServiceTime(this.customerServiceTime);
        dto.setCustomerService(this.customerService);
        dto.setCustomerServiceReason(this.customerServiceReason);
        dto.setDeleted(this.deleted);
        dto.setRemark(this.remark);
        dto.setAuditee(this.auditee);
    }
}
