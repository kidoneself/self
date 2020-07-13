package com.yimao.cloud.base.exception;


/**
 * @description: 当订单不需要生成合同时返回该错误
 * @author: Liu long jie
 * @create: 2019-11-12
 **/
public class NoNeedSignException extends YimaoException{

    private static final long serialVersionUID = 3691020383957807557L;

    public NoNeedSignException(String message) {
        super(420, message);
    }
}
