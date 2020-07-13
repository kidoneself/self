package com.yimao.cloud.order.processor;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.order.service.OrderSubService;
import com.yimao.cloud.order.service.ServiceIncomeRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 服务收益分配
 */
@Component
@Slf4j
public class HraAllotProcessor {

    @Resource
    private ServiceIncomeRecordService serviceIncomeRecordService;
    @Resource
    private OrderSubService orderSubService;

    @RabbitListener(queues = RabbitConstant.HRA_ALLOT)
    @RabbitHandler
    public void process(Map<String, String> map) {
        try {
            String ticketNo = map.get("ticketNo");
            String deviceId = map.get("deviceId");
            Integer stationId = Integer.parseInt(map.get("stationId"));
            serviceIncomeRecordService.serviceAllot(ticketNo, stationId, deviceId);

            //设置M卡主体信息
            if (StringUtil.isNotEmpty(map.get("orderId"))) {
                Long orderId = Long.parseLong(map.get("orderId"));
                orderSubService.fillHraCardSalesObject(stationId, orderId);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
