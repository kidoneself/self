package com.yimao.cloud.base.msg;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 共通响应主体对象
 *
 * @author Zhang Bo
 * @date 2018/8/3
 */
@ApiModel(description = "共通响应主体对象")
@Getter
@Setter
public class CommResult<T> {

    public static final int SUCCESS = 200;
    //错误码
    public static final int ERROR_400 = 400;
    //客户端需要做区分的错误码
    public static final int ERROR_400401 = 400401;

    @ApiModelProperty(position = 1, value = "状态码：200为正常；其它为异常")
    private int status;
    @ApiModelProperty(position = 3, value = "错误消息")
    private String errMsg;
    @ApiModelProperty(position = 1, value = "错误信息提示类型：false-文本；true-弹出对话框")
    private boolean dialog = false;
    @ApiModelProperty(position = 4, value = "响应体")
    private T data;

    private CommResult(T data) {
        this.status = 200;
        this.errMsg = null;
        this.data = data;
    }

    private CommResult(Integer status, String errMsg, boolean dialog, T data) {
        this.status = status;
        this.errMsg = errMsg;
        this.dialog = dialog;
        this.data = data;
    }

    /**
     * 客户端普通消息文本提示
     */
    public static <T> CommResult<T> msgError(String msg, T data) {
        return new CommResult<>(ERROR_400, msg, false, data);
    }

    /**
     * 客户端弹出对话框提示
     */
    public static <T> CommResult<T> dialogError(String msg) {
        return new CommResult<>(ERROR_400, msg, true, null);
    }

    /**
     * 客户端弹出对话框提示
     */
    public static <T> CommResult<T> dialogError(String msg, T data) {
        return new CommResult<>(ERROR_400, msg, true, data);
    }

    public static <T> CommResult<T> ok(T data) {
        return new CommResult<>(data);
    }

    public static <T> CommResult<T> ok() {
        return new CommResult<>(null);
    }

}
