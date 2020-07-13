package com.yimao.cloud.base.exception;

import org.springframework.http.HttpStatus;

/**
 * @author Zhang Bo
 * @date 2018/11/5.
 */
public class NotFoundException extends YimaoException {

    private static final long serialVersionUID = 3690020383957807547L;

    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND.value(), message);
    }

}
