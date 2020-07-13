package com.yimao.cloud.water.configuration;

import com.yimao.cloud.water.jobs.UpdateDeviceCountTask;
import com.yimao.cloud.water.jobs.WaterDeviceOfflineJob;
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

    @Bean
    public JobDetail job1() {
        return JobBuilder.newJob(UpdateDeviceCountTask.class).withIdentity("UpdateDeviceCountTask", "UpdateDeviceCountGroup").storeDurably().build();
    }

    @Bean
    public Trigger trigger1() {
        //30分钟执行一次
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0 /30 * * * ?");
        return TriggerBuilder.newTrigger()
                .forJob(job1())
                .withIdentity("UpdateDeviceCountTrigger", "UpdateDeviceCountGroup")
                .withSchedule(scheduleBuilder)
                .build();
    }

    @Bean
    public JobDetail job2() {
        return JobBuilder.newJob(WaterDeviceOfflineJob.class).withIdentity("WaterDeviceOfflineJob", "WaterDeviceOfflineGroup").storeDurably().build();
    }

    @Bean
    public Trigger trigger2() {
        //3分钟执行一次
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0 0/1 * * * ?");
        return TriggerBuilder.newTrigger()
                .forJob(job2())
                .withIdentity("WaterDeviceOfflineTrigger", "WaterDeviceOfflineGroup")
                .withSchedule(scheduleBuilder)
                .build();
    }

}