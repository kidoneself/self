package com.yimao.cloud.base.exception;

import org.springframework.http.HttpStatus;

/**
 * @author Zhang Bo
 * @date 2018/11/5.
 */
public class NoLoginException extends YimaoException {

    private static final long serialVersionUID = 4261854982772169747L;

    public NoLoginException() {
        super(HttpStatus.UNAUTHORIZED.value(), "请先登录。");
    }

}
