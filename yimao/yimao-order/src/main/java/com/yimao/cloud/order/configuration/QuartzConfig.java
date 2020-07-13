package com.yimao.cloud.order.configuration;

import com.yimao.cloud.order.jobs.SalesPerformRankJob;
import com.yimao.cloud.order.jobs.TimeoutOrderJob;
import com.yimao.cloud.order.jobs.TimeoutReceiptOrderJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * @author Zhang Bo
 * @date 2019/01/17.
 */
@Configuration
public class QuartzConfig implements SchedulerFactoryBeanCustomizer {

    // @Resource
    // private DataSource dataSource;

    /**
     * Customize the {@link SchedulerFactoryBean}.
     *
     * @param schedulerFactoryBean the scheduler to customize
     */
    @Override
    public void customize(SchedulerFactoryBean schedulerFactoryBean) {
        //延时启动，应用启动1秒后
        schedulerFactoryBean.setStartupDelay(5);
        schedulerFactoryBean.setAutoStartup(true);
        //用于quartz集群,QuartzScheduler启动时更新己存在的Job
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        // schedulerFactoryBean.setDataSource(dataSource);
    }

    /**
     * 将超时未支付的订单状态设置为【交易关闭】
     */
    @Bean
    public JobDetail timeoutOrderJob() {
        return JobBuilder.newJob(TimeoutOrderJob.class).withIdentity("TimeoutOrderTask", "TimeoutOrderGroup").storeDurably().build();
    }

    /**
     * 将超时待收货的订单状态设置为【交易成功】
     */
    @Bean
    public JobDetail timeoutReceiptOrderJob() {
        return JobBuilder.newJob(TimeoutReceiptOrderJob.class).withIdentity("TimeoutReceiptOrderTask", "TimeoutReceiptOrderGroup").storeDurably().build();
    }

    @Bean
    public Trigger timeoutOrderTrigger() {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0 0/1 * * * ?");//每1分钟执行一次
        return TriggerBuilder.newTrigger()
                .forJob(timeoutOrderJob())
                .withIdentity("TimeoutOrderTrigger", "TimeoutOrderGroup")
                .withSchedule(scheduleBuilder)
                .build();
    }

    @Bean
    public Trigger timeoutReceiptOrderTrigger() {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0 0/5 * * * ?");//每5分钟执行一次
        return TriggerBuilder.newTrigger()
                .forJob(timeoutReceiptOrderJob())
                .withIdentity("TimeoutReceiptOrderTrigger", "TimeoutReceiptOrderGroup")
                .withSchedule(scheduleBuilder)
                .build();
    }
    
    /**
     * 定时任务生成上个月的销售排行榜
     */
    @Bean
    public JobDetail salesPerformRankJob() {
        return JobBuilder.newJob(SalesPerformRankJob.class).withIdentity("SalesPerformRankTask", "SalesPerformRankJobGroup").storeDurably().build();
    }
    
    @Bean
    public Trigger salesPerformRankTrigger() {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0 0 0 1 * ? *");//每月一号0:0:0秒执行
        return TriggerBuilder.newTrigger()
                .forJob(salesPerformRankJob())
                .withIdentity("SalesPerformRankTrigger", "SalesPerformRankGroup")
                .withSchedule(scheduleBuilder)
                .build();
    }


}