package com.yimao.cloud.base.exception;

/**
 * @author Zhang Bo
 * @date 2018/8/6.
 */
public class YimaoException extends RuntimeException {

    private static final long serialVersionUID = -7908991293911710258L;
    // private int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
    private int status = 499;

    public YimaoException(String message) {
        super(message);
    }

    public YimaoException(int status, String message) {
        super(message);
        this.status = status;
    }

    public YimaoException(String message, Throwable cause) {
        super(message, cause);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
