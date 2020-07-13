package com.yimao.cloud.pojo.dto.order;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class QuotaChangeRecordDTO implements Serializable {

    private static final long serialVersionUID = -825260036336515551L;

    private Integer id;
    private Long orderId;        //子订单号
    private Integer type;           //1-下单 2-取消 3-续费
    private Integer count;          //数量
    private String reason;          //原因
    private Integer distributorId;  //经销商
    private Integer quota;          //总配额
    private Integer remainingQuota; //剩余配额
    private Integer userId;
}
