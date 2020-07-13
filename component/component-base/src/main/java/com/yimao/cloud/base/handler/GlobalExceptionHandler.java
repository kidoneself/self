package com.yimao.cloud.base.handler;

import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.exception.YimaoRemoteException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理类
 *
 * @author Zhang Bo
 * @date 2018/8/6.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 程序出错时统一返回500系统错误
     *
     * @param e Exception
     */
    @ExceptionHandler(Exception.class)
    public Object exceptionHandler(Exception e) {
        log.error(e.getMessage(), e);
        // if (StringUtil.isNotBlank(e.getMessage())) {
        //     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        // } else {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("操作失败。");
        // }
    }

    /**
     * 程序出错返回信息处理
     *
     * @param e YimaoException
     */
    @ExceptionHandler(YimaoException.class)
    public Object yimaoExceptionHandler(YimaoException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(e.getStatus()).body(e.getMessage());
    }

    /**
     * 描述:远程异常会被Feign Hystrix拦截，HystrixBadRequestException不会被Feign Hystrix拦截
     *
     * @param e YimaoRemoteException
     */
    @ExceptionHandler(YimaoRemoteException.class)
    public Object yimaoHystrixExceptionHandler(YimaoRemoteException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(e.getStatus()).body(e.getMessage());
    }

    /**
     * 描述:捕获Feign Hystrix熔断异常。
     *
     * @param e RuntimeException
     */
    @ExceptionHandler({HystrixRuntimeException.class, FeignException.class})
    public Object yimaoHystrixExceptionHandler(RuntimeException e) {
        log.error(e.getMessage(), e);
        if (Constant.PRO_ENVIRONMENT) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("请求出错，请稍后重试。");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务正在重启中，请稍后重试。");
        }
    }

}
