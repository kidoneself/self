package com.yimao.cloud.order.processor;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.order.mapper.WithdrawAuditLogMapper;
import com.yimao.cloud.order.po.WithdrawAuditLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * 描述：提现审核日志
 *
 * @Author Zhang Bo
 * @Date 2019/5/31
 */
@Component
@Slf4j
public class WithdrawAuditLogProcessor {

    @Resource
    private WithdrawAuditLogMapper withdrawAuditLogMapper;

    @RabbitListener(queues = RabbitConstant.WITHDRAW_AUDIT)
    @RabbitHandler
    public void process(Map<String, Object> map) {
        try {
            Long tradeNo = (Long) map.get("tradeNo");
            Integer type = (Integer) map.get("type");
            String content = (String) map.get("content");
            String ip = (String) map.get("ip");
            String operator = (String) map.get("operator");
            WithdrawAuditLog auditLog = new WithdrawAuditLog();
            auditLog.setTradeNo(tradeNo);
            //1-退款 2-提现
            auditLog.setType(type);
            auditLog.setContent(content);
            auditLog.setIp(ip);
            auditLog.setOperator(operator);
            auditLog.setCreateTime(new Date());
            withdrawAuditLogMapper.insert(auditLog);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
