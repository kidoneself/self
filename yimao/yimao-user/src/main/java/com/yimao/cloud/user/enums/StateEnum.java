package com.yimao.cloud.user.enums;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public enum StateEnum {
    BIND(1,"绑定"),
    UNBIND(0,"解绑"),
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
