package com.yimao.cloud.base.exception;

import org.springframework.http.HttpStatus;

/**
 * @author Zhang Bo
 * @date 2018/11/5.
 */
public class BadRequestException extends YimaoException {

    private static final long serialVersionUID = -7747566099019250764L;

    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST.value(), message);
    }

}
