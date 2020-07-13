package com.yimao.cloud.order.po;

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
 * 描述：退款记录
 *
 * @Author Zhang Bo
 * @Date 2019/10/14
 */
@Table(name = "finance_refund_record")
@Getter
@Setter
public class RefundRecord implements Serializable {

    private static final long serialVersionUID = 1288313662925651235L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String outTradeNo;       //原始订单号，对应PayRecord表mainOrderId
    private String tradeNo;          //支付交易流水号
    private BigDecimal totalFee;     //原始订单支付金额
    private String outRefundNo;      //退款订单号
    private String refundTradeNo;    //退款交易流水号
    private BigDecimal refundFee;    //退款金额
    private Integer platform;        //平台：1-微信；2-支付宝；
    private Date refundTime;         //平台退款时间
    private Integer status;          //退款状态：1-成功；2-失败；
    private Date createTime;         //退款记录创建时间

}
