package com.yimao.cloud.order.po;

import com.yimao.cloud.pojo.dto.order.OrderInvoiceDTO;
import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 发票
 *
 * @author liuhao@yimaokeji.com
 * @version v2.0
 * @date 2018/12/28
 */
@Data
@Table(name = "order_invoice")
public class OrderInvoice implements Serializable {

    private static final long serialVersionUID = 2507842437854835749L;

    private Integer id;
    private Long mainOrderId;             //主订单
    private Long orderId;                 //子订单号
    private Integer userId;               //用户ID
    private Integer productId;            //产品ID
    private String productName;           //产品名称
    private Integer applyStatus;          //开票状态：0-未申请  2-已申请
    private Date applyTime;               //申请开票时间
    private Integer invoiceType;          //发票类型1、普票；2、增票
    private Integer invoiceHead;          //发票抬头1、公司发票；2、个人发票
    private String companyName;           //公司名称
    private String bankAccount;           //开户号
    private String bankName;              //开户行
    private String dutyNo;                //税号
    private String companyAddress;        //公司地址
    private String companyPhone;          //公司电话
    private String applyUser;             //申请人名称
    private String applyPhone;            //申请用户手机号
    private String applyEmail;            //申请用户邮箱
    private String applyAddress;          //申请人地址
    private Integer type;                 //订单类型：1-普通订单  2-续费订单
    private String renewId;                 //续费单号
    private Date confirmTime;             //确认时间
    private Integer sourceType;           //来源：1-安装工 app;2-健康e家公众号

    public OrderInvoice() {
    }

    /**
     * 用业务对象OrderInvoiceDTO初始化数据库对象OrderInvoice。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public OrderInvoice(OrderInvoiceDTO dto) {
        this.id = dto.getId();
        this.mainOrderId = dto.getMainOrderId();
        this.orderId = dto.getOrderId();
        this.userId = dto.getUserId();
        this.productId = dto.getProductId();
        this.productName = dto.getProductName();
        this.applyStatus = dto.getApplyStatus();
        this.applyTime = dto.getApplyTime();
        this.invoiceType = dto.getInvoiceType();
        this.invoiceHead = dto.getInvoiceHead();
        this.companyName = dto.getCompanyName();
        this.bankAccount = dto.getBankAccount();
        this.bankName = dto.getBankName();
        this.dutyNo = dto.getDutyNo();
        this.companyAddress = dto.getCompanyAddress();
        this.companyPhone = dto.getCompanyPhone();
        this.applyUser = dto.getApplyUser();
        this.applyPhone = dto.getApplyPhone();
        this.applyEmail = dto.getApplyEmail();
        this.applyAddress = dto.getApplyAddress();
        this.renewId = dto.getRenewId();
        this.type = dto.getType();
        this.sourceType = dto.getSourceType();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象OrderInvoiceDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(OrderInvoiceDTO dto) {
        dto.setId(this.id);
        dto.setMainOrderId(this.mainOrderId);
        dto.setOrderId(this.orderId);
        dto.setUserId(this.userId);
        dto.setProductId(this.productId);
        dto.setProductName(this.productName);
        dto.setApplyStatus(this.applyStatus);
        dto.setApplyTime(this.applyTime);
        dto.setInvoiceType(this.invoiceType);
        dto.setInvoiceHead(this.invoiceHead);
        dto.setCompanyName(this.companyName);
        dto.setBankAccount(this.bankAccount);
        dto.setBankName(this.bankName);
        dto.setDutyNo(this.dutyNo);
        dto.setCompanyAddress(this.companyAddress);
        dto.setCompanyPhone(this.companyPhone);
        dto.setApplyUser(this.applyUser);
        dto.setApplyPhone(this.applyPhone);
        dto.setApplyEmail(this.applyEmail);
        dto.setApplyAddress(this.applyAddress);
        dto.setRenewId(this.renewId);
        dto.setType(this.type);
        dto.setSourceType(this.sourceType);
    }
}
