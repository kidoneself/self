package com.yimao.cloud.base.enums;

public enum WorkOrderInstallTypeEnum {
    JXS_APP_ORDER(1, "经销商添加"),
    USER_SELF_HELP_ORDER(2, "用户自助下单");

    private int type;
    private String msg;

    private WorkOrderInstallTypeEnum(int type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
