package com.yimao.cloud.base.exception;

import org.springframework.http.HttpStatus;

/**
 * 用户需要为客户端提供错误提示类型的报错场景
 *
 * @author Zhang Bo
 * @date 2019/10/22
 */
public class DialogException extends YimaoException {

    private static final long serialVersionUID = 5590023018484012360L;

    public DialogException(String message) {
        super(HttpStatus.BAD_REQUEST.value(), message);
    }

}
