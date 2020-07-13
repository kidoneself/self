package com.yimao.cloud.order.po;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：提现审核日志
 *
 * @Author Zhang Bo
 * @Date 2019/5/31
 */
@Table(name = "withdraw_audit_log")
@Getter
@Setter
public class WithdrawAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //提现订单号
    private Long tradeNo;
    //类型：1-退款；2-提现；
    private Integer type;
    //操作内容
    private String content;
    //IP
    private String ip;
    //操作人
    private String operator;
    //操作时间
    private Date createTime;

}
