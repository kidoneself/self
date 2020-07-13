package com.yimao.cloud.base.exception;

import org.springframework.http.HttpStatus;

/**
 * @author Zhang Bo
 * @date 2018/11/22.
 */
public class RemoteCallException extends YimaoException {

    private static final long serialVersionUID = 6127210566140214971L;

    public RemoteCallException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务暂不可用，请稍后重试。");
    }

}
