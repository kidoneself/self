package com.yimao.cloud.order.po;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：财务支付审核（产品订单和续费订单的线下支付审核）
 *
 * @Author Zhang Bo
 * @Date 2019/8/23
 */
@Table(name = "order_pay_check")
@Getter
@Setter
public class OrderPayCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //订单类型：1-产品订单；2-水机续费单；
    private Integer orderType;
    private String orderId;
    //审核状态：1-通过；2-不通过；
    private Integer status;
    private String statusName;
    //审核不通过原因
    private String reason;
    private String creator;
    private Date createTime;

}
