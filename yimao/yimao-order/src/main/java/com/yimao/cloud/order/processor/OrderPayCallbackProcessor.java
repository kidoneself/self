package com.yimao.cloud.order.processor;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.mail.MailSender;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.framework.cache.RedisLock;
import com.yimao.cloud.order.po.PayRecord;
import com.yimao.cloud.order.service.OrderSubService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 产品订单支付回调
 */
@Component
@Slf4j
public class OrderPayCallbackProcessor {

    private static final String ORDER_PAY_CALLBACK_LOCK = "ORDER_PAY_CALLBACK_LOCK";

    @Resource
    private RedisLock redisLock;

    @Resource
    private OrderSubService orderSubService;
    @Resource
    private MailSender mailSender;
    @Resource
    private DomainProperties domainProperties;

    @RabbitListener(queues = RabbitConstant.ORDER_PAY_CALLBACK)
    @RabbitHandler
    public void process(PayRecord record) {
        try {
            //获取锁
            if (!redisLock.lock(ORDER_PAY_CALLBACK_LOCK)) {
                return;
            }
            log.info("订单支付回调队列接收开始执行---tradeNo={}", record.getTradeNo());
            orderSubService.payCallback(record);
        } catch (Exception e) {
            log.info("订单支付回调队列接收执行出错---tradeNo={}", record.getTradeNo());
            log.error(e.getMessage(), e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String subject = "订单支付回调队列执行失败提醒" + domainProperties.getApi();
            String content = "订单支付回调队列执行出错。tradeNo=" + record.getTradeNo() + "\n" + sw.toString();
            mailSender.send(null, subject, content);
        } finally {
            redisLock.unLock(ORDER_PAY_CALLBACK_LOCK);
        }
    }

}
