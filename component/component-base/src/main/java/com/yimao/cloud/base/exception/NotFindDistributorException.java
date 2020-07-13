package com.yimao.cloud.base.exception;


/**
 * @description:
 * @author: yu chunlei
 * @create: 2019-09-05 11:32:06
 **/
public class NotFindDistributorException extends YimaoException{

    private static final long serialVersionUID = 3690020383957807547L;

    public NotFindDistributorException(String message) {
        super(410, message);
    }
}
