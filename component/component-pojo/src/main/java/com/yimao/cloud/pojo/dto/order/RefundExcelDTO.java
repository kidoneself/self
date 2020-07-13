package com.yimao.cloud.pojo.dto.order;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author zhilin.he
 * @description 工单批量退单导入数据
 * @date 2019/4/29 10:18
 **/
@Getter
@Setter
public class RefundExcelDTO {

    private String workOrderId;            //工单号
    private BigDecimal refundMoney;        //实际退款金额
    private String chargeBackMoneyMethod;  //退款方式
    private String msg;                    //消息

}
