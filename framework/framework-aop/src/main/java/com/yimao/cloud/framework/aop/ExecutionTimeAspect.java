package com.yimao.cloud.framework.aop;

import com.alibaba.fastjson.JSON;
import com.yimao.cloud.framework.aop.annotation.ExecutionTime;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author Zhang Bo
 * @date 2018/11/22.
 */
@Aspect
@Component
@Slf4j
public class ExecutionTimeAspect {

    @Around("@annotation(executionTime)")
    public Object doAround(ProceedingJoinPoint pjp, ExecutionTime executionTime) {
        Object result = null;
        try {
            long beginTime = System.currentTimeMillis();
            result = pjp.proceed();
            long endTime = System.currentTimeMillis();
            log.info(JSON.toJSONString(pjp.toString()) + "，执行耗时" + (endTime - beginTime) + "ms");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return result;
    }

    // @Before("@annotation(executionTime)")
    // public void doBefore(ProceedingJoinPoint pjp, ExecutionTime executionTime) {
    //     long beginTime = System.currentTimeMillis();
    //     BaseContextHandler.set("beginTime", beginTime);
    // }
    //
    // @AfterReturning(pointcut = "@annotation(executionTime)", returning = "object")
    // public void doAfterReturing(Object object, ExecutionTime executionTime) {
    //     long beginTime = (Long) BaseContextHandler.get("beginTime");
    //     long endTime = System.currentTimeMillis();
    //     log.info("查询耗时：" + (endTime - beginTime) + "ms");
    // }

}
