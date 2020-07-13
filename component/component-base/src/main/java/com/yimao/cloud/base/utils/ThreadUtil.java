package com.yimao.cloud.base.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Zhang Bo
 * @date 2018/11/12.
 */
public class ThreadUtil {

    // 通用线程
    public static ExecutorService executor = Executors.newFixedThreadPool(20);

}
