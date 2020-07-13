package com.yimao.cloud.base.exception;

/**
 * @description: 加密数据解密异常
 * @author: yu chunlei
 * @create: 2018-11-15 10:15:06
 **/
public class AESException extends YimaoException {

    public AESException(String message) {
        super(401, message);
    }
}
