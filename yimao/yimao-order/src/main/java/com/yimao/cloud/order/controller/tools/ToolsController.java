package com.yimao.cloud.order.controller.tools;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.RenewStatus;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.order.mapper.OrderRenewMapper;
import com.yimao.cloud.order.mapper.PayRecordMapper;
import com.yimao.cloud.order.po.OrderRenew;
import com.yimao.cloud.order.service.OrderRenewService;
import com.yimao.cloud.pojo.dto.order.PayRecordDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 描述：TODO
 *
 * @Author Zhang Bo
 * @Date 2019/11/30
 */
@RestController
@Slf4j
public class ToolsController {

    @Resource
    private OrderRenewService orderRenewService;
    @Resource
    private OrderRenewMapper orderRenewMapper;
    @Resource
    private PayRecordMapper payRecordMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @PostMapping(value = "/renewOrder/doBy")
    public void renewOrder(@RequestBody PayRecordDTO record) {
        try {
            log.info("手动处理续费订单支付回调失败-开始");
            String tradeNo = record.getTradeNo();
            String renewOrderId = record.getMainOrderId();
            OrderRenew renewOrder = orderRenewMapper.selectPayInfoById(renewOrderId);
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
            log.info("手动处理续费订单支付回调失败-结束");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @PostMapping(value = "/renewOrder/allotRenewIncome")
    public void allotRenewIncome(@RequestParam String renewOrderIdStr) {
        try {
            log.info("手动进行续费订单收益分配-开始");
            String[] split = renewOrderIdStr.split(",");
            for (String renewOrderId : split) {
                OrderRenew renewOrder = orderRenewMapper.selectByPrimaryKey(renewOrderId);
                if (renewOrder == null) {
                    throw new BadRequestException("续费订单号错误");
                }
                orderRenewService.allotRenewIncome(renewOrder);
            }
            log.info("手动进行续费订单收益分配-结束");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 手动进行订单支付回调操作
     */
    @PostMapping(value = "/manualTool/orderPayCallback")
    public void orderPayCallback(@RequestParam String mainOrderId) {
        try {
            log.info("手动进行订单支付回调操作-开始");
            PayRecordDTO payRecord = payRecordMapper.selectByOutTradeNo(mainOrderId);
            rabbitTemplate.convertAndSend(RabbitConstant.ORDER_PAY_CALLBACK, payRecord);
            log.info("手动进行订单支付回调操作-结束");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 手动进行工单支付回调操作
     */
    @PostMapping(value = "/manualTool/workOrderPayCallback")
    public void workOrderPayCallback(@RequestParam String mainOrderId) {
        try {
            log.info("手动进行工单支付回调操作-开始");
            PayRecordDTO payRecord = payRecordMapper.selectByOutTradeNo(mainOrderId);
            rabbitTemplate.convertAndSend(RabbitConstant.WORK_ORDER_PAY_CALLBACK, payRecord);
            log.info("手动进行工单支付回调操作-结束");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 手动进行续费回调操作
     */
    @PostMapping(value = "/manualTool/renewOrderPayCallback")
    public void renewOrderPayCallback(@RequestParam String mainOrderId) {
        try {
            log.info("手动进行续费回调操作-开始");
            PayRecordDTO payRecord = payRecordMapper.selectByOutTradeNo(mainOrderId);
            rabbitTemplate.convertAndSend(RabbitConstant.RENEWORDER_PAY_CALLBACK, payRecord);
            log.info("手动进行续费回调操作-结束");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
