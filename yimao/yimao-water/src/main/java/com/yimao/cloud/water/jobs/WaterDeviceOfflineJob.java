package com.yimao.cloud.water.jobs;

import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.framework.cache.RedisLock;
import com.yimao.cloud.water.mapper.WaterDeviceMapper;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 将最后在线时间为125分钟之前的设备置为离线状态
 *
 * @author Zhang Bo
 * @date 2019/10/31
 */
@Slf4j
@DisallowConcurrentExecution//该注解表示前一个任务执行结束再执行下一次任务
public class WaterDeviceOfflineJob extends QuartzJobBean {

    private static final String WATER_DEVICE_OFFLINE_LOCK_STR = "WATER_DEVICE_OFFLINE_LOCK_STR";

    @Resource
    private RedisLock redisLock;

    @Resource
    private WaterDeviceMapper waterDeviceMapper;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        log.info("将最后在线时间为125分钟之前的设备置为离线状态---任务开始执行");
        try {
            //获取锁
            if (!redisLock.lockWithTimeout(WATER_DEVICE_OFFLINE_LOCK_STR, 300L)) {
                return;
            }
            Date compareTime = DateUtil.minuteAfter(new Date(), -125);
            int count = waterDeviceMapper.updateToOffline(compareTime);
            log.info("下线了" + count + "台设备。");
        } catch (Exception e) {
            log.error("将最后在线时间为125分钟之前的设备置为离线状态---任务执行出错");
            log.error(e.getMessage(), e);
        } finally {
            log.info("将最后在线时间为125分钟之前的设备置为离线状态---任务结束执行");
            redisLock.unLock(WATER_DEVICE_OFFLINE_LOCK_STR);
        }
    }

}