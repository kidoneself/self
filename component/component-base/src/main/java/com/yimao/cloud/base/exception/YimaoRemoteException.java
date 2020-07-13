package com.yimao.cloud.base.exception;

import com.netflix.hystrix.exception.HystrixBadRequestException;
import org.springframework.http.HttpStatus;

/**
 * 描述:远程异常会被Feign Hystrix拦截，HystrixBadRequestException不会被Feign Hystrix拦截
 *
 * @Author Zhang Bo
 * @Date 2019/1/10 16:37
 * @Version 1.0
 */
public class YimaoRemoteException extends HystrixBadRequestException {

    private static final long serialVersionUID = 5381999350575675912L;
    private int status = HttpStatus.INTERNAL_SERVER_ERROR.value();

    public YimaoRemoteException(String message) {
        super(message);
    }

    public YimaoRemoteException(int status, String message) {
        super(message);
        this.status = status;
    }

    public YimaoRemoteException(String message, Throwable cause) {
        super(message, cause);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
