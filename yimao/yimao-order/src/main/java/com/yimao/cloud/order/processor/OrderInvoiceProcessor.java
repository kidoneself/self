package com.yimao.cloud.order.processor;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.order.po.OrderInvoice;
import com.yimao.cloud.order.service.OrderInvoiceService;
import com.yimao.cloud.pojo.dto.order.OrderInvoiceDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 开票
 */
@Component
@Slf4j
public class OrderInvoiceProcessor {

    @Resource
    private OrderInvoiceService orderInvoiceService;

    @RabbitListener(queues = RabbitConstant.INSERT_WORK_ORDER_INVOICE)
    @RabbitHandler
    public void insertOrderInvoice(OrderInvoiceDTO orderInvoiceDTO) {
        log.info("保存开票信息-开始，订单子订单号为{}",orderInvoiceDTO.getOrderId());
        try {
            OrderInvoice orderInvoice = new OrderInvoice(orderInvoiceDTO);
            Boolean flag = orderInvoiceService.checkExistByOrderId(orderInvoice.getMainOrderId(), orderInvoice.getOrderId());
            if (!flag) {
                orderInvoiceService.save(null, orderInvoice);
                log.info("开票信息保存成功，订单子订单号为{}",orderInvoice.getOrderId());
            } else {
                log.info("子订单号为{}的开票信息已存在，修改开票信息",orderInvoice.getOrderId());
                //存在则修改
                orderInvoiceService.updateInvoice(orderInvoice);
                log.info("子订单号为{}的开票信息修改成功",orderInvoice.getOrderId());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
