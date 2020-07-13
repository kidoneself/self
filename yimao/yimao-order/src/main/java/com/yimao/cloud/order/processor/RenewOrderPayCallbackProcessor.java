package com.yimao.cloud.order.processor;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.RenewStatus;
import com.yimao.cloud.order.mapper.OrderRenewMapper;
import com.yimao.cloud.order.po.OrderRenew;
import com.yimao.cloud.order.po.PayRecord;
import com.yimao.cloud.order.service.OrderRenewService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 续费订单支付回调
 */
@Component
@Slf4j
public class RenewOrderPayCallbackProcessor {

    @Resource
    private OrderRenewService orderRenewService;
    @Resource
    private OrderRenewMapper orderRenewMapper;

    @RabbitListener(queues = RabbitConstant.RENEWORDER_PAY_CALLBACK)
    @RabbitHandler
    public void process(PayRecord record) {
        try {
            log.info("续费订单支付回调-开始");
            String tradeNo = record.getTradeNo();
            String renewOrderId = record.getMainOrderId();
	        if (StringUtils.isEmpty(renewOrderId)) {
		        log.error("续费订单支付回调失败。renewOrderId无值，tradeNo={}", tradeNo);
		        return;
	        }

	        log.info("3========================================================================,renewOrderId=" + renewOrderId);
	        OrderRenew renewOrder = orderRenewMapper.selectPayInfoById(renewOrderId.trim());
            if (renewOrder == null) {
                log.error("续费订单支付回调失败。renewOrderId={}", renewOrderId);
                return;
            }
            if (renewOrder.getPay()) {
                log.info("续费订单支付回调失败。重复调用已支付的续费订单");
                return;
            }
            int times = orderRenewMapper.countRenewTimes(renewOrder.getDeviceId());
            int renewTimes = times + 1;
            //更新续费订单支付信息
            OrderRenew renewUpdate = new OrderRenew();
            renewUpdate.setId(renewOrderId);
            renewUpdate.setPay(true);
            renewUpdate.setPayTime(record.getPayTime());
            renewUpdate.setTimes(renewTimes);
            renewUpdate.setTradeNo(tradeNo);
            renewUpdate.setStatus(RenewStatus.SUCCESS.value);
            renewUpdate.setStatusName(RenewStatus.SUCCESS.name);
            renewUpdate.setUpdateTime(new Date());
            orderRenewMapper.updateByPrimaryKeySelective(renewUpdate);

            orderRenewService.payCallback(renewOrderId);
            log.info("续费订单支付回调-结束");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
