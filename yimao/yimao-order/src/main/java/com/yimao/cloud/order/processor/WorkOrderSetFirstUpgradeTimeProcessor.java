package com.yimao.cloud.order.processor;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.order.service.WorkOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 体验版经销商升级之后给经销商下的工单“第一次升级时间”字段赋值
 *
 * @author Liu Long Jie
 */
@Component
@Slf4j
public class WorkOrderSetFirstUpgradeTimeProcessor {

    @Resource
    private WorkOrderService workOrderService;

    @RabbitListener(queues = RabbitConstant.WORK_ORDER_SET_FIRST_UPGRADE_TIME)
    @RabbitHandler
    public void process(Map<String,Object> info) {
        log.info("==================体验版经销商升级之后给经销商下的工单“第一次升级时间”字段赋值队列开始执行======" );
        try {
            workOrderService.setFirstUpgradeTime(info);
        } catch (Exception e) {
            log.error("==================体验版经销商升级之后给经销商下的工单“第一次升级时间”字段赋值失败=======" + e.getMessage());
        }
    }


}
