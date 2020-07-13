package com.yimao.cloud.order.po;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 配额记录表
 */
@Table(name = "quota_change_record")
@Getter
@Setter
public class QuotaChangeRecord implements Serializable {
    private static final long serialVersionUID = 2507842437854835767L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Long orderId;        //子订单号
    private Integer type;           //1-下单 2-取消 3-续费
    private Integer count;          //数量
    private String reason;          //原因
    private Integer distributorId;  //经销商
    private Integer quota;          //总配额
    private Integer remainingQuota; //剩余配额
    private BigDecimal replacementAmount;   //总金额
    private BigDecimal remainingReplacementAmount;//剩余金额
    private Date createTime;
    @Transient
    private Integer userId;
}
