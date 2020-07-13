package com.yimao.cloud.base.exception;

/**
 * @author Zhang Bo
 * @date 2018/11/5.
 */
public class AuthException extends YimaoException {

    private static final long serialVersionUID = 1952540754624256774L;

    public AuthException(String message) {
        // 400 bad request，常用在参数校验。
        super(401, message);
    }

}
