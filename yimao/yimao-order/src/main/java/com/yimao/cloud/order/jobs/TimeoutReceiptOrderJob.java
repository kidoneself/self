package com.yimao.cloud.order.jobs;

import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.framework.cache.RedisLock;
import com.yimao.cloud.order.mapper.OrderSubMapper;
import com.yimao.cloud.order.po.OrderSub;
import com.yimao.cloud.order.service.OrderConfigService;
import com.yimao.cloud.order.service.OrderSubService;
import com.yimao.cloud.pojo.dto.order.OrderConfigDTO;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @description: 将超时待收货的订单状态设置为【交易成功】
 * @author: yu chunlei
 * @create: 2019-10-25 11:00:20
 **/
@Slf4j
@DisallowConcurrentExecution
public class TimeoutReceiptOrderJob extends QuartzJobBean {

    private static final String TIMEOUT_RECEIPT_ORDER_LOCK_1 = "TIMEOUT_RECEIPT_ORDER_LOCK_1";

    @Resource
    private RedisLock redisLock;

    @Resource
    private OrderConfigService orderConfigService;
    @Resource
    private OrderSubService orderSubService;
    @Resource
    private OrderSubMapper orderSubMapper;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        log.info("*******************将超时待收货的订单状态设置为交易成功---任务开始执行**********************");
        try {
            //获取锁
            if (!redisLock.lockWithTimeout(TIMEOUT_RECEIPT_ORDER_LOCK_1, 300L)) {
                return;
            }
            //只针对待收货的实物商品(并且已出库，这里的OrderSub的发货时间是order_delivery_record中的创建时间)
            List<OrderSub> orderSubList = orderSubMapper.selectWaitSendOrder();
            if (CollectionUtil.isEmpty(orderSubList)) {
                log.info("*******************将超时待收货的订单状态设置为交易成功---任务结束执行*******************");
                return;
            }

            OrderConfigDTO orderConfig = orderConfigService.getOrderConfig();
            if (null != orderConfig) {
                Integer day = orderConfig.getDeliveryDays();
                if (day != null && day > 0) {
                    for (OrderSub orderSub : orderSubList) {
                        Date date = DateUtil.dayAfter(orderSub.getDeliveryTime(), day);
                        Date currentTime = new Date();
                        if (currentTime.getTime() > date.getTime()) {
                            orderSubService.completeOrder(orderSub.getId());
                        }
                    }
                }
            }
            log.info("*******************将超时待收货的订单状态设置为交易成功---任务结束执行*******************");
        } catch (Exception e) {
            log.info("将超时待收货的订单状态设置为交易成功---任务执行出错");
            log.error(e.getMessage(), e);
        } finally {
            redisLock.unLock(TIMEOUT_RECEIPT_ORDER_LOCK_1);
        }
    }
}
