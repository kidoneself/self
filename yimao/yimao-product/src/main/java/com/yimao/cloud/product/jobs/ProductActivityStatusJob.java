package com.yimao.cloud.product.jobs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.enums.ProductActivityStatusEnum;
import com.yimao.cloud.base.utils.CalcActivityStartTimeUtil;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.framework.cache.RedisLock;
import com.yimao.cloud.product.mapper.ProductActivityMapper;
import com.yimao.cloud.product.po.ProductActivity;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 商品活动状态变换
 *
 * @author Zhang Bo
 * @date 2020/03/13
 */
@Slf4j
@DisallowConcurrentExecution//该注解表示前一个任务执行结束再执行下一次任务
public class ProductActivityStatusJob extends QuartzJobBean {

    private static final String PRODUCT_ACTIVITY_STATUS_LOCK = "PRODUCT_ACTIVITY_STATUS_LOCK";

    @Resource
    private RedisLock redisLock;
    @Resource
    private RedisCache redisCache;

    @Resource
    private ProductActivityMapper productActivityMapper;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        log.info("商品活动状态变换---任务开始执行");
        try {
            //获取锁
            if (!redisLock.lockWithTimeout(PRODUCT_ACTIVITY_STATUS_LOCK, 300L)) {
                return;
            }
            ProductActivity record = new ProductActivity();
            record.setOpening(true);
            List<ProductActivity> list = productActivityMapper.select(record);
            if (CollectionUtil.isNotEmpty(list)) {
                for (ProductActivity pa : list) {
                    if (pa.getStatus() == ProductActivityStatusEnum.TERMINATED.value) {
                        continue;
                    }
                    //活动时间校验
                    Date now = new Date();
                    Date startDate = pa.getStartTime();
                    Date endDate = pa.getEndTime();
                    if (startDate == null || endDate == null) {
                        continue;
                    }
                    if (pa.getCycle() == null || !pa.getCycle()) {
                        //非周期重复活动
                        if (now.before(startDate)) {
                            pa.setStatus(ProductActivityStatusEnum.NOT_STARTED.value);
                        } else if (now.after(endDate)) {
                            pa.setStatus(ProductActivityStatusEnum.OVER.value);
                        } else {
                            pa.setStatus(ProductActivityStatusEnum.PROCESSING.value);
                        }
                        pa.setUpdateTime(now);
                        pa.setUpdater("定时器");
                        productActivityMapper.updateByPrimaryKey(pa);
                        redisCache.set(Constant.PRODUCT_ACTIVITY_CACHE + pa.getId(), pa, 3600L);
                    } else {
                        //当前活动开始时间
                        Date startTime = startDate;
                        //当前活动结束时间
                        Date endTime = endDate;
                        //周期重复活动
                        String cycleTime = pa.getCycleTime();
                        JSONObject json = JSON.parseObject(cycleTime);
                        String suffix1 = json.getString("startTime");
                        String suffix2 = json.getString("endTime");
                        String prefix = CalcActivityStartTimeUtil.getDayShortTime(now);
                        if (pa.getCycleType() == 1) {
                            //每天
                            prefix = CalcActivityStartTimeUtil.getDayShortTime(now);
                        } else if (pa.getCycleType() == 2) {
                            //每周几
                            prefix = CalcActivityStartTimeUtil.getWeekDayShortTime(json.getInteger("weekDay"));
                        } else if (pa.getCycleType() == 3) {
                            //每月几号
                            prefix = CalcActivityStartTimeUtil.getMonthDayShortTime(json.getInteger("day"));
                        }
                        startTime = DateUtil.transferStringToDate(prefix + " " + suffix1, DateUtil.defaultDateTimeFormat);
                        endTime = DateUtil.transferStringToDate(prefix + " " + suffix2, DateUtil.defaultDateTimeFormat);

                        if (now.before(startTime)) {
                            pa.setStatus(ProductActivityStatusEnum.NOT_STARTED.value);
                        } else if (now.after(endTime)) {
                            //下次活动开始时间
                            Date nextStartTime = CalcActivityStartTimeUtil.getNextTime(startTime, pa.getCycleType());
                            if (nextStartTime != null && nextStartTime.after(endDate)) {
                                //下次活动开始时间>活动总结束时间，则活动已结束
                                pa.setStatus(ProductActivityStatusEnum.OVER.value);
                            } else {
                                pa.setStatus(ProductActivityStatusEnum.NOT_STARTED.value);
                                pa.setRemainingStock(pa.getActivityStock());
                            }
                        } else {
                            pa.setStatus(ProductActivityStatusEnum.PROCESSING.value);
                        }
                        pa.setUpdateTime(now);
                        pa.setUpdater("定时器");
                        productActivityMapper.updateByPrimaryKey(pa);
                        redisCache.set(Constant.PRODUCT_ACTIVITY_CACHE + pa.getId(), pa, 3600L);
                    }
                }
            }
            log.info("商品活动状态变换---任务结束执行");
        } catch (Exception e) {
            log.error("商品活动状态变换---任务执行出错");
            log.error(e.getMessage(), e);
        } finally {
            redisLock.unLock(PRODUCT_ACTIVITY_STATUS_LOCK);
        }
    }

}