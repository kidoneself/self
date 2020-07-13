package com.yimao.cloud.order.processor;

import com.alibaba.fastjson.JSON;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.order.po.RefundRecord;
import com.yimao.cloud.order.service.AfterSalesOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 退款回调
 */
@Component
@Slf4j
public class RefundCallbackProcessor {

    @Resource
    private AfterSalesOrderService afterSalesOrderService;

    @RabbitListener(queues = RabbitConstant.REFUND_CALLBACK)
    @RabbitHandler
    public void process(RefundRecord record) {
        try {
            log.info("退款回调-开始");
            //退款成功才处理后续逻辑            
            if (record.getStatus() == 1) {
                afterSalesOrderService.refundUpdateAfterSaleStatus(record);
            }
            log.info("退款回调-结束");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
