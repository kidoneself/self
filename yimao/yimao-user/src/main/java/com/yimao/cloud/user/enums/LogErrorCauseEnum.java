package com.yimao.cloud.user.enums;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 后台管理系统登录失败原因枚举
 *
 */
@NoArgsConstructor
@AllArgsConstructor
public enum LogErrorCauseEnum {
    PASSWORD_ERROR(1,"密码输入错误"),
    ACCOUNT_LOCKOUT(2,"账户被锁定"),
    ;

    int code;
    String msg;


    public int value(){
        return this.code;
    }

    public String msg(){
        return this.msg;
    }
}
