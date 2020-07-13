package com.yimao.cloud.product.configuration;

import com.yimao.cloud.product.jobs.ProductActivityStatusJob;
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
 * @date 2020/03/13
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
    public JobDetail ProductActivityStatusJob() {
        return JobBuilder.newJob(ProductActivityStatusJob.class).withIdentity("ProductActivityStatusTask", "ProductActivityStatusGroup").storeDurably().build();
    }

    @Bean
    public Trigger ProductActivityStatusTrigger() {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0/10 * * * * ?");//每1分钟执行一次
        return TriggerBuilder.newTrigger()
                .forJob(ProductActivityStatusJob())
                .withIdentity("ProductActivityStatusTrigger", "ProductActivityStatusGroup")
                .withSchedule(scheduleBuilder)
                .build();
    }

}