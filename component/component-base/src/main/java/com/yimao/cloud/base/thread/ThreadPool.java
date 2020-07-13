package com.yimao.cloud.base.thread;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 描述：定义spring管理的线程池对象
 *
 * @Author Zhang Bo
 * @Date 2019/1/24 9:48
 * @Version 1.0
 */
@Configuration
public class ThreadPool {

    @Bean(destroyMethod = "shutdown")
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(20);
    }

}
