package com.yimao.cloud.framework.aop;

public enum OperationType {

    SAVE("新建"), UPDATE("更新"), DELETE("删除");

    public String name;

    OperationType(String name) {
        this.name = name;
    }

    public String getType() {
        return name;
    }

}
