package com.yimao.cloud.order.po;

import com.yimao.cloud.pojo.dto.order.PayRecordDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 支付记录
 *
 * @author Zhang Bo
 * @date 2018/11/12.
 */
@Table(name = "finance_pay_record")
@Getter
@Setter
public class PayRecord implements Serializable {

    private static final long serialVersionUID = 7965048537359628923L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;                        //主订单号
    private String mainOrderId;               //主订单ID
    private Integer orderType;              //订单类型1、普通订单  2、续费订单 3、经销商订单 4、安装工单
    private String openid;
    private Integer userId;                   //支付用户
    private String tradeNo;                 //订单支付单号，第三方支付流水号
    private Integer payType;                //支付类型：1-微信；2-支付宝；3-POS机；4-转账；
    private String tradeType;               //JSAPI -JSAPI支付、NATIVE -Native支付、APP -APP支付
    private Integer terminal;               //支付端
    private BigDecimal amountTotal;        //总金额
    private Date createTime;                //创建时间
    private Date payTime;                   //支付时间

    //公司编号
    private Integer companyId;
    //支付平台：1-微信；2-支付宝；3-银行；
    private Integer platform;
    //客户端：1-健康e家公众号；4-经销商APP；5-安装工APP；6-水机PAD；10-H5页面；
    private Integer clientType;
    //款项收取类型（默认1）：1-商品费用；2-经销代理费用
    private Integer receiveType;

    public PayRecord() {
    }

    /**
     * 用业务对象PayRecordDTO初始化数据库对象PayRecord。
     *
     * @param dto 业务对象
     */
    public PayRecord(PayRecordDTO dto) {
        this.id = dto.getId();
        this.mainOrderId = dto.getMainOrderId();
        this.orderType = dto.getOrderType();
        this.openid = dto.getOpenid();
        this.userId = dto.getUserId();
        this.tradeNo = dto.getTradeNo();
        this.payType = dto.getPayType();
        this.tradeType = dto.getTradeType();
        this.terminal = dto.getTerminal();
        this.amountTotal = dto.getAmountTotal();
        this.createTime = dto.getCreateTime();
        this.payTime = dto.getPayTime();
        this.companyId = dto.getCompanyId();
        this.platform = dto.getPlatform();
        this.clientType = dto.getClientType();
        this.receiveType = dto.getReceiveType();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象PayRecordDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(PayRecordDTO dto) {
        dto.setId(this.id);
        dto.setMainOrderId(this.mainOrderId);
        dto.setOrderType(this.orderType);
        dto.setOpenid(this.openid);
        dto.setUserId(this.userId);
        dto.setTradeNo(this.tradeNo);
        dto.setPayType(this.payType);
        dto.setTradeType(this.tradeType);
        dto.setTerminal(this.terminal);
        dto.setAmountTotal(this.amountTotal);
        dto.setCreateTime(this.createTime);
        dto.setPayTime(this.payTime);
        dto.setCompanyId(this.companyId);
        dto.setPlatform(this.platform);
        dto.setClientType(this.clientType);
        dto.setReceiveType(this.receiveType);
    }
}
