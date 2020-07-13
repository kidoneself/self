package com.yimao.cloud.user.configuration;

import com.yimao.cloud.user.jobs.DistributorOrderJob;
import com.yimao.cloud.user.jobs.ExamineOrderJob;
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
 * @author Liu long jie
 * @date 2019/10/15.
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
    public JobDetail distributorOrderJob() {
        return JobBuilder.newJob(DistributorOrderJob.class).withIdentity("distributorOrderJob", "testGroup").storeDurably().build();
    }

    @Bean
    public JobDetail examineOrderJob() {
        return JobBuilder.newJob(ExamineOrderJob.class).withIdentity("examineOrderJob", "testGroup").storeDurably().build();
    }

    @Bean
    public Trigger taskDistributorOrderJob() {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0 0/10 * * * ? *");
        return TriggerBuilder.newTrigger()
                .forJob(distributorOrderJob())
                .withIdentity("distributorOrderTrigger", "testGroup")
                .withSchedule(scheduleBuilder)
                .build();
    }

    @Bean
    public Trigger taskExamineOrderJob() {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0 0/15 * * * ? *");
        return TriggerBuilder.newTrigger()
                .forJob(examineOrderJob())
                .withIdentity("examineOrderTrigger", "testGroup")
                .withSchedule(scheduleBuilder)
                .build();
    }

}