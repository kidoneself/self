package com.yimao.cloud.order.jobs;

import com.yimao.cloud.base.enums.ProductActivityType;
import com.yimao.cloud.base.enums.ProductModeEnum;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.framework.cache.RedisLock;
import com.yimao.cloud.order.feign.ProductFeign;
import com.yimao.cloud.order.mapper.QuotaChangeRecordMapper;
import com.yimao.cloud.order.po.OrderSub;
import com.yimao.cloud.order.po.QuotaChangeRecord;
import com.yimao.cloud.order.service.OrderConfigService;
import com.yimao.cloud.order.service.OrderSubService;
import com.yimao.cloud.order.service.QuotaChangeRecordService;
import com.yimao.cloud.pojo.dto.order.OrderConfigDTO;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 将超时未支付的订单状态设置为【交易关闭】
 *
 * @author Zhang Bo
 * @date 2019/08/21
 */
@Slf4j
@DisallowConcurrentExecution//该注解表示前一个任务执行结束再执行下一次任务
public class TimeoutOrderJob extends QuartzJobBean {

    private static final String TIMEOUT_ORDER_LOCK_1 = "TIMEOUT_ORDER_LOCK_1";

    @Resource
    private RedisLock redisLock;

    @Resource
    private OrderConfigService orderConfigService;
    @Resource
    private OrderSubService orderSubService;
    @Resource
    private QuotaChangeRecordService quotaChangeRecordService;
    @Resource
    private QuotaChangeRecordMapper quotaChangeRecordMapper;
    @Resource
    private ProductFeign productFeign;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        log.info("将超时未支付的订单状态设置为已取消---任务开始执行");
        try {
            //获取锁
            if (!redisLock.lockWithTimeout(TIMEOUT_ORDER_LOCK_1, 300L)) {
                return;
            }
            OrderConfigDTO config = orderConfigService.getOrderConfig();
            if (config != null) {
                Integer hour = config.getOrderTimeOut();
                if (hour != null && hour > 0) {
                    Date date = DateUtil.hourAfter(new Date(), -hour);

                    //1-查询超时未支付的订单
                    List<OrderSub> subList = orderSubService.timeoutOrder(date);
                    if (CollectionUtil.isNotEmpty(subList)) {
                        for (OrderSub orderSub : subList) {
                            if (Objects.nonNull(orderSub.getProductType()) && Objects.equals(orderSub.getProductType(), ProductModeEnum.LEASE.value)) {
                                //2-还配额
                                Example example = new Example(QuotaChangeRecord.class);
                                Example.Criteria criteria = example.createCriteria();
                                criteria.andEqualTo("type", 1);
                                criteria.andEqualTo("orderId", orderSub.getId());
                                QuotaChangeRecord quotaChangeRecord = quotaChangeRecordMapper.selectOneByExample(example);
                                if (quotaChangeRecord != null) {
                                    if (quotaChangeRecord.getCount() != 0) {
                                        quotaChangeRecordService.quotaChange(orderSub.getId(), orderSub.getDistributorId(), "超时未支付取消订单-配额", 2, 1, null);
                                    } else {
                                        quotaChangeRecordService.quotaChange(orderSub.getId(), orderSub.getDistributorId(), "超时未支付取消订单-金额", 2, 0, orderSub.getFee());
                                    }
                                }
                            }

                            //活动商品还配额
                            if (orderSub.getActivityType() != null && orderSub.getActivityType() == ProductActivityType.PANIC_BUYING.value) {
                                productFeign.addProductActivityStock(orderSub.getActivityId(), orderSub.getCount());
                            }
                        }
                        //3-设置订单状态为【交易关闭】
                        orderSubService.timeout(date);
                    }
                }
            }
            log.info("将超时未支付的订单状态设置为已取消---任务结束执行");
        } catch (Exception e) {
            log.error("将超时未支付的订单状态设置为已取消---任务执行出错");
            log.error(e.getMessage(), e);
        } finally {
            redisLock.unLock(TIMEOUT_ORDER_LOCK_1);
        }
    }
}