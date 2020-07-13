package com.yimao.cloud.user.processor;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.mail.MailSender;
import com.yimao.cloud.base.utils.yunSignUtil.YunSignApi;
import com.yimao.cloud.base.utils.yunSignUtil.YunSignConfig;
import com.yimao.cloud.base.utils.yunSignUtil.YunSignResult;
import com.yimao.cloud.user.mapper.DistributorOrderMapper;
import com.yimao.cloud.user.po.DistributorOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 经销商订单相关队列
 *
 * @author Liu long jie
 * @date 2019-12-31
 */
@Component
@Slf4j
public class DistributorOrderProcessor {

    @Resource
    private YunSignApi yunSignApi;

    @Resource
    private MailSender mailSender;

    @Resource
    private DistributorOrderMapper distributorOrderMapper;

    /**
     * 翼猫静默签署经销商订单合同
     *
     * @param distributorOrderId 经销商订单id
     */
    @RabbitListener(queues = RabbitConstant.DISTRIBUTOR_ORDER_PROTOCOL_YIMAO_SIGN)
    @RabbitHandler
    public void processor(Long distributorOrderId) {
        try {
            String orderId = "";
            DistributorOrder distributorOrder = distributorOrderMapper.selectByPrimaryKey(distributorOrderId);
            if (distributorOrder.getOldOrderId() != null) {
                orderId = distributorOrder.getOldOrderId();
            } else {
                orderId = distributorOrderId + "";
            }

            //翼猫静默签署经销商合同
            YunSignResult result = yunSignApi.autoSignContract(YunSignConfig.YUNSIGN_YIMAO_ACCOUNT, orderId);
            long t1 = System.currentTimeMillis();
            while (!result.getSuccess() && result.getCode().equals("4240")) {
                Thread.sleep(5000);
                //静默签署失败(报 ：合同正在操作，请稍后重试)，五秒后重试一次
                result = yunSignApi.autoSignContract(YunSignConfig.YUNSIGN_YIMAO_ACCOUNT, orderId + "");
                long t2 = System.currentTimeMillis();
                log.info("经销商订单合同翼猫静默签署逻辑已执行{}毫秒", (t2 - t1));
                if ((t2 - t1) > 3 * 60_000) {
                    //执行三分钟的循环未有结果则跳出循环
                    log.error("云签静默签署翼猫合同出错（报：合同正在操作，请稍后重试）超过三分钟，跳出死循环");
                    break;
                }
            }
            if (!result.getSuccess()) {
                log.error("经销商订单翼猫静默签署失败，订单号为{}", distributorOrderId);
                // 发送邮箱提示静默签署失败
                String subject = "经销商订单翼猫静默签署失败";
                String content = "经销商订单翼猫静默签署失败，订单号为：" + distributorOrderId;
                mailSender.send(null, subject, content);
            }
        } catch (Exception e) {
            log.error("翼猫静默签署经销商订单合同发生错误，" + e.getMessage(), e);
            // 发送邮箱提示静默签署失败
            String subject = "翼猫静默签署经销商订单合同发生错误";
            String content = "翼猫静默签署经销商订单合同发生错误，订单号为：" + distributorOrderId;
            mailSender.send(null, subject, content);
        }
    }
}
