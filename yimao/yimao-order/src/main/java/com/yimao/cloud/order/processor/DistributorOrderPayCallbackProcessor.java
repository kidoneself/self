package com.yimao.cloud.order.processor;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.order.feign.UserFeign;
import com.yimao.cloud.order.po.PayRecord;
import com.yimao.cloud.pojo.dto.order.PayRecordDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author Liu Yi
 * @description 经销商订单支付回调
 * @date 2019/9/21 11:28
 */
@Component
@Slf4j
public class DistributorOrderPayCallbackProcessor {

    @Resource
    private UserFeign userFeign;

    @RabbitListener(queues = RabbitConstant.DISTRIBUTOR_ORDER_PAY_CALLBACK)
    @RabbitHandler
    public void process(PayRecord record) {

        try {
            if (Objects.nonNull(record)) {
                log.info("经销商订单支付回调开始,经销商订单号："+record.getMainOrderId()+"，交易流水号："+record.getTradeNo());
                PayRecordDTO dto = new PayRecordDTO();
                record.convert(dto);

                userFeign.distributorOrderPayCallback(dto);
            }
        } catch (Exception e) {
            log.error("经销商订单支付回调异常：" + e.getMessage(), e);
        }
    }

}
