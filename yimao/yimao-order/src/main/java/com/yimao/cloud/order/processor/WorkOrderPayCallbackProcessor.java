package com.yimao.cloud.order.processor;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.mail.MailSender;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.framework.cache.RedisLock;
import com.yimao.cloud.order.po.PayRecord;
import com.yimao.cloud.order.service.WorkOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 安装工APP工单支付回调
 */
@Component
@Slf4j
public class WorkOrderPayCallbackProcessor {

    private static final String WORK_ORDER_PAY_CALLBACK_LOCK = "WORK_ORDER_PAY_CALLBACK_LOCK";

    @Resource
    private RedisLock redisLock;

    @Resource
    private WorkOrderService workOrderService;
    @Resource
    private MailSender mailSender;
    @Resource
    private DomainProperties domainProperties;

    @RabbitListener(queues = RabbitConstant.WORK_ORDER_PAY_CALLBACK)
    @RabbitHandler
    public void process(PayRecord record) {
        try {
            //获取锁
            if (!redisLock.lock(WORK_ORDER_PAY_CALLBACK_LOCK)) {
                return;
            }
            log.info("安装工APP工单支付回调队列接收开始执行---tradeNo={}", record.getTradeNo());
            workOrderService.payCallback(record);
        } catch (Exception e) {
            log.info("安装工APP工单支付回调队列接收执行出错---tradeNo={}", record.getTradeNo());
            log.error(e.getMessage(), e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String subject = "安装工APP工单支付回调队列执行失败提醒" + domainProperties.getApi();
            String content = "安装工APP工单支付回调队列执行出错。tradeNo=" + record.getTradeNo() + "\n" + sw.toString();
            mailSender.send(null, subject, content);
        } finally {
            redisLock.unLock(WORK_ORDER_PAY_CALLBACK_LOCK);
        }
    }

}
