// package com.yimao.cloud.water.configuration;
//
// import com.yimao.cloud.water.jobs.UpdateDeviceCountTask;
// import org.quartz.*;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
//
// /**
//  * @author Zhang Bo
//  * @date 2019/01/17.
//  */
// @Configuration
// public class TaskConfig {
//
//     @Bean
//     public JobDetail testJob() {
//         return JobBuilder.newJob(UpdateDeviceCountTask.class).withIdentity("updateDeviceCountTaskJob", "defaultGroup").storeDurably().build();
//     }
//
//     @Bean
//     public Trigger testTask() {
//         //0 /30 * * * ?   半个小时执行一次执行
//         CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0 /30 * * * ?");
//         return TriggerBuilder.newTrigger()
//                 .forJob(testJob())
//                 .withIdentity("updateDeviceCountTaskTrigger", "defaultGroup")
//                 .withSchedule(scheduleBuilder)
//                 .build();
//     }
//
// }