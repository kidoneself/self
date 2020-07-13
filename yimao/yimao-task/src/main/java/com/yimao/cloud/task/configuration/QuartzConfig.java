package com.yimao.cloud.task.configuration;

import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
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

    // @Bean
    // public JobDetail job1() {
    //     return JobBuilder.newJob(Task1.class).withIdentity("job1", "Group1").storeDurably().build();
    // }
    //
    // @Bean
    // public Trigger trigger1() {
    //     CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("*/5 * * * * ?");
    //     return TriggerBuilder.newTrigger()
    //             .forJob(job1())
    //             .withIdentity("trigger1", "Group1")
    //             .withSchedule(scheduleBuilder)
    //             .build();
    // }
    //
    // @Bean
    // public JobDetail job2() {
    //     return JobBuilder.newJob(Task2.class).withIdentity("job2", "Group1").storeDurably().build();
    // }
    //
    // @Bean
    // public Trigger trigger2() {
    //     CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("*/5 * * * * ?");
    //     return TriggerBuilder.newTrigger()
    //             .forJob(job2())
    //             .withIdentity("trigger2", "Group1")
    //             .withSchedule(scheduleBuilder)
    //             .build();
    // }

}