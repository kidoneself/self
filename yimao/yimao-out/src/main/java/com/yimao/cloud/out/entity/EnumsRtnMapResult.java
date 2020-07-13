package com.yimao.cloud.out.entity;

public enum EnumsRtnMapResult {

    /* 00 系统 00 模块 00 功能 00 状态码 */

    SUCCESS("00000000", "操作成功"),
    FAILURE("00000001", "操作失败"),
    EXCEPTION("00000009", "操作异常"),
    UNLOGIN("99999999", "登录超时"),
    NO_PERMISSION("88888888", "没有权限访问");

    public final String code;
    public final String msg;

    EnumsRtnMapResult(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
