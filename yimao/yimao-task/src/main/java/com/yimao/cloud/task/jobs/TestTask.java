package com.yimao.cloud.task.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @author Zhang Bo
 * @date 2019/01/17.
 */
@DisallowConcurrentExecution//该注解表示前一个任务执行结束再执行下一次任务
public class TestTask extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        System.out.println("任务1开始");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("任务2结束");
    }

}