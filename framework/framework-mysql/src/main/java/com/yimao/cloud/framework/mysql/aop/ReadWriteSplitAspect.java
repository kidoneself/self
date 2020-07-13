// package com.yimao.cloud.framework.mysql.aop;
//
// import com.yimao.cloud.framework.mysql.annotation.ReadOnly;
// import com.yimao.cloud.framework.mysql.configuration.ReadWriteSplitRoutingDataSource;
// import lombok.extern.slf4j.Slf4j;
// import org.aspectj.lang.ProceedingJoinPoint;
// import org.aspectj.lang.annotation.Around;
// import org.aspectj.lang.annotation.Aspect;
// import org.springframework.stereotype.Component;
//
// /**
//  * 读写分离AOP逻辑
//  *
//  * @author Zhang Bo
//  * @date 2018/9/27.
//  */
// @Aspect
// @Component
// @Slf4j
// public class ReadWriteSplitAspect {
//
//     // @Override
//     // public int getOrder() {
//     //     return 0;
//     // }
//
//     @Around("@annotation(readOnly)")
//     public Object setRead(ProceedingJoinPoint joinPoint, ReadOnly readOnly) throws Throwable {
//         try {
//             ReadWriteSplitRoutingDataSource.setKey(null);
//             return joinPoint.proceed();
//         } finally {
//             // 清楚Key一方面为了避免内存泄漏，更重要的是避免对后续在本线程上执行的操作产生影响
//             ReadWriteSplitRoutingDataSource.clear();
//             log.info("ReadWriteSplitAspect---清除threadLocal");
//         }
//     }
//
// }
