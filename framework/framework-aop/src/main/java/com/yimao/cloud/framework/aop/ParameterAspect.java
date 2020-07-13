// package com.yimao.cloud.framework.aop;
//
// import com.alibaba.fastjson.JSON;
// import lombok.extern.slf4j.Slf4j;
// import org.aspectj.lang.ProceedingJoinPoint;
// import org.aspectj.lang.annotation.Around;
// import org.aspectj.lang.annotation.Aspect;
// import org.aspectj.lang.annotation.Pointcut;
// import org.springframework.stereotype.Component;
//
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;
//
//
// /**
//  * spring aop切面逻辑
//  *
//  * @author Zhang Bo
//  * @date 2018/7/22.
//  */
// @Aspect
// @Component
// @Slf4j
// //@ConditionalOnProperty(value = "true", havingValue = "true")
// public class ParameterAspect {
//
//     @Pointcut("execution (* com.yimao.cloud.*.controller..*.*(..))")
//     public void controllerAspect() {
//     }
//
//     @Pointcut("execution (* com.yimao.cloud.*.rest..*.*(..))")
//     public void controllerAspect2() {
//     }
//
//     @Around("controllerAspect()")
//     public Object doLog(ProceedingJoinPoint pjp) throws Throwable {
//         log.info("==============================================================================");
//         long begin = System.currentTimeMillis();
//         log.info("request : {}", JSON.toJSONString(pjp.toString()));
//         // log.info("request : {}", JSON.toJSONString(pjp.getArgs()));
//         Object[] args = pjp.getArgs();
//         if (args != null) {
//             StringBuffer sb = new StringBuffer("{");
//             int len = args.length;
//             for (int i = 0; i < len; i++) {
//                 Object obj = args[i];
//                 if (obj instanceof HttpServletRequest || obj instanceof HttpServletResponse) {
//                     continue;
//                 }
//                 sb.append(JSON.toJSONString(obj) + ",");
//             }
//             String s = sb.substring(0, sb.length() - 1);
//             log.info("request : {}", s + "}");
//         }
//         Object result = null;
//         try {
//             result = pjp.proceed();
//         } catch (Throwable e) {
//             //logger.error(JSON.toJSONString(((MethodSignature) pjp.getSignature()).getMethod().getName()), e);
//             log.error("catch exception.");
//             long end = System.currentTimeMillis();
//             log.info("timeMillis : {}", JSON.toJSONString(end - begin));
//             log.info("==============================================================================");
//             throw e;
//         }
//         log.info("return : {}", JSON.toJSONString(result));
//         long end = System.currentTimeMillis();
//         log.info("timeMillis : {}", JSON.toJSONString(end - begin));
//         log.info("==============================================================================");
//         return result;
//     }
//
//     @Around("controllerAspect2()")
//     public Object doLog2(ProceedingJoinPoint pjp) throws Throwable {
//         log.info("==============================================================================");
//         long begin = System.currentTimeMillis();
//         log.info("request : {}", JSON.toJSONString(pjp.toString()));
//         // log.info("request : {}", JSON.toJSONString(pjp.getArgs()));
//         Object[] args = pjp.getArgs();
//         if (args != null) {
//             StringBuffer sb = new StringBuffer("{");
//             int len = args.length;
//             for (int i = 0; i < len; i++) {
//                 Object obj = args[i];
//                 if (obj instanceof HttpServletRequest || obj instanceof HttpServletResponse) {
//                     continue;
//                 }
//                 sb.append(JSON.toJSONString(obj) + ",");
//             }
//             String s = sb.substring(0, sb.length() - 1);
//             log.info("request : {}", s + "}");
//         }
//         Object result = null;
//         try {
//             result = pjp.proceed();
//         } catch (Throwable e) {
//             //logger.error(JSON.toJSONString(((MethodSignature) pjp.getSignature()).getMethod().getName()), e);
//             log.error("catch exception.");
//             long end = System.currentTimeMillis();
//             log.info("timeMillis : {}", JSON.toJSONString(end - begin));
//             log.info("==============================================================================");
//             throw e;
//         }
//         log.info("return : {}", JSON.toJSONString(result));
//         long end = System.currentTimeMillis();
//         log.info("timeMillis : {}", JSON.toJSONString(end - begin));
//         log.info("==============================================================================");
//         return result;
//     }
//
// }
